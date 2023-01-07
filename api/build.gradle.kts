import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

base {
    archivesName.set(properties["archives_name"].toString())
    group = property("maven_group")!!
    version = rootProject.version
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.base.archivesName.get()
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