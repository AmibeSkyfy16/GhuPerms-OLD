import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    id("maven-publish")
    id("java-library")
    id("signing")
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    id("fabric-loom") version "1.0-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.6.0"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("io.github.juuxel.loom-quiltflower") version "1.8.0"
    idea
}

allprojects {
    apply(plugin = "fabric-loom")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "io.github.juuxel.loom-quiltflower")
    apply(plugin = "idea")

    val transitiveInclude: Configuration by configurations.creating

    repositories {
        maven("https://maven.fabricmc.net") { name = "FabricMC" }
        maven("https://maven.quiltmc.org/repository/snapshot") { name = "Quilt Snapshots" }
        maven("https://maven.quiltmc.org/repository/release") { name = "Quilt" }
        maven("https://repo.repsy.io/mvn/amibeskyfy16/repo")
    }

    dependencies {
        minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
        mappings("net.fabricmc:yarn:${properties["yarn_mappings"]}:v2")

        modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"]}")
        modImplementation("net.fabricmc:fabric-language-kotlin:${properties["fabric_kotlin_version"]}")

        transitiveInclude(implementation("ch.skyfy.jsonconfiglib:json-config-lib:3.0.12")!!)
        transitiveInclude(implementation("org.mariadb.jdbc:mariadb-java-client:3.1.0")!!)
        transitiveInclude(implementation("org.ktorm:ktorm-core:3.5.0")!!)
        transitiveInclude(implementation("org.ktorm:ktorm-support-mysql:3.5.0")!!)

        handleIncludes(project, transitiveInclude)

        testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.0")
    }

    tasks {
        val javaVersion = JavaVersion.VERSION_17

        processResources {
            inputs.property("version", rootProject.version)
            filteringCharset = "UTF-8"
            filesMatching("fabric.mod.json") {
                expand(mutableMapOf("version" to rootProject.version))
            }
        }

        quiltflower { addToRuntimeClasspath.set(true) }

        java { withSourcesJar() }

//        named<Wrapper>("wrapper") {
//            gradleVersion = "7.6"
//            distributionType = Wrapper.DistributionType.BIN
//        }

        named<KotlinCompile>("compileKotlin") {
            kotlinOptions.jvmTarget = javaVersion.toString()
        }

        named<JavaCompile>("compileJava") {
            options.encoding = "UTF-8"
            options.release.set(javaVersion.toString().toInt())
        }

        named<Jar>("jar") {
            from("LICENSE") {
                rename { "${it}_${base.archivesName}" }
            }
        }

        named<Test>("test") { // https://stackoverflow.com/questions/40954017/gradle-how-to-get-output-from-test-stderr-stdout-into-console
            useJUnitPlatform()

            testLogging {
                outputs.upToDateWhen { false } // When the build task is executed, stderr-stdout of test classes will be show
                showStandardStreams = true
            }
        }

    }

}

configure(subprojects.filter { listOf("api").contains(it.name) }) {}

base {
    archivesName.set(properties["archives_name"].toString())
    group = property("maven_group")!!
    version = property("mod_version")!!
}

repositories {
    maven("https://maven.nucleoid.xyz/") {}
    maven("https://jitpack.io") { name = "JitPack" }
    maven("https://repo.repsy.io/mvn/amibeskyfy16/repo") // Use for my JsonConfig lib
}

dependencies {
    implementation(project(path = ":api", configuration = "namedElements"))
}

tasks {
    val copyJarToTestServer = register("copyJarToTestServer") {
        println("copy to server")
        copyFile("build/libs/ghuperms-${project.properties["mod_version"]}.jar", project.property("testServerModsFolder") as String)
        copyFile("api/build/libs/ghuperms-api-${project.properties["mod_version"]}.jar", project.property("testServerModsFolder") as String)
    }

    processResources { dependsOn(project(":api").tasks.processResources.get()) }

    publish { finalizedBy(project(":api").tasks.publish.get()) }

    build { doLast { copyJarToTestServer.get() } }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
//            groupId = project.group.toString()
//            artifactId = project.base.archivesName.get()
//            version = project.version.toString()

            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri("http://127.0.0.1:8080/releases")
            credentials {
                val properties = Properties()
                properties.load(file("E:\\reposilite.properties").inputStream())
                username = "${properties["username"]}"
                password = "${properties["password"]}"
            }
        }
    }
}

fun copyFile(src: String, dest: String) = copy { from(src);into(dest) }

fun DependencyHandlerScope.includeTransitive(
    root: ResolvedDependency?,
    dependencies: Set<ResolvedDependency>,
    fabricLanguageKotlinDependency: ResolvedDependency,
    checkedDependencies: MutableSet<ResolvedDependency> = HashSet()
) {
    dependencies.forEach {
        if (checkedDependencies.contains(it) || (it.moduleGroup == "org.jetbrains.kotlin" && it.moduleName.startsWith("kotlin-stdlib")) || (it.moduleGroup == "org.slf4j" && it.moduleName == "slf4j-api"))
            return@forEach

        if (fabricLanguageKotlinDependency.children.any { kotlinDep -> kotlinDep.name == it.name }) {
            println("Skipping -> ${it.name} (already in fabric-language-kotlin)")
        } else {
            include(it.name)
            println("Including -> ${it.name} from ${root?.name}")
        }
        checkedDependencies += it

        includeTransitive(root ?: it, it.children, fabricLanguageKotlinDependency, checkedDependencies)
    }
}

// from : https://github.com/StckOverflw/TwitchControlsMinecraft/blob/4bf406893544c3edf52371fa6e7a6cc7ae80dc05/build.gradle.kts
fun DependencyHandlerScope.handleIncludes(project: Project, configuration: Configuration) {
    includeTransitive(
        null,
        configuration.resolvedConfiguration.firstLevelModuleDependencies,
        project.configurations.getByName("modImplementation").resolvedConfiguration.firstLevelModuleDependencies
            .first { it.moduleGroup == "net.fabricmc" && it.moduleName == "fabric-language-kotlin" }
    )
}