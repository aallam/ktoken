import kotlinx.benchmark.gradle.JvmBenchmarkTarget
import kotlinx.benchmark.gradle.benchmark
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("org.jetbrains.kotlinx.benchmark")
}

sourceSets.all {
    java.setSrcDirs(listOf("$name/src"))
    resources.setSrcDirs(listOf("$name/resources"))
}

configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

dependencies {
    implementation(project(":ktoken"))
    implementation(libs.kotlinx.benchmark)
}

benchmark {
    configurations {
        named("main") {
            warmups = 1
            iterations = 5
        }
    }
    targets {
        register("main") {
            this as JvmBenchmarkTarget
            jmhVersion = "1.37"
        }
    }
}
