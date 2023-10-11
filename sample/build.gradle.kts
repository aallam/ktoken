plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

dependencies {
    implementation(project(":ktoken")) //"com.aallam.ktoken:ktoken:<version>"
    implementation(libs.openai.client)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.dataframe)
}

application {
    mainClass.set("com.aallam.ktoken.sample.AppKt")
}
