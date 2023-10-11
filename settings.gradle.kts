rootProject.name = "ktoken"

include(":ktoken")
include(":ktoken-bom")
include(":sample")

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
