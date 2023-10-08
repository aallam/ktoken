plugins {
    id("com.vanniktech.maven.publish")
    id("java-platform")
}

dependencies {
    constraints {
        api(project(":ktoken"))
        api(libs.ktor.client.apache)
        api(libs.ktor.client.cio)
        api(libs.ktor.client.java)
        api(libs.ktor.client.jetty)
        api(libs.ktor.client.okhttp)
    }
}
