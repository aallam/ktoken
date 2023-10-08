rootProject.name = "ktoken"

include(":ktoken")
include(":ktoken-bom")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
