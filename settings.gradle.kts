pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/"){
            name = "Fabric"
        }
        maven("https://maven.quiltmc.org/repository/release"){
            name = "Quilt"
        }
        maven("https://server.bbkr.space/artifactory/libs-release/"){
            name = "Cotton"
        }
        gradlePluginPortal()
    }
}

include("api")