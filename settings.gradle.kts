rootProject.name = "ktoken"

include(":ktoken")
include(":ktoken-bom")
include(":sample")
include(":benchmark")

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
