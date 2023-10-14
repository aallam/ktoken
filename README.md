# Ktoken

[![Maven Central](https://img.shields.io/maven-central/v/com.aallam.ktoken/ktoken?color=blue&label=Download)](https://central.sonatype.com/namespace/com.aallam.ktoken)
[![License](https://img.shields.io/github/license/aallam/ktoken?color=yellow)](LICENSE.md)

**Ktoken** is a BPE tokenizer designed for seamless integration with OpenAI's models.

## ⚡️ Getting Started

### Installation
Install **Ktoken** by adding the dependency to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.aallam.ktoken:ktoken:0.2.0"
}
```
### Usage Modes

Ktoken operates in two modes: Local (default for JVM) and Remote (default for JS/Native).

#### 📍 Local Mode

Utilize LocalPbeLoader to retrieve encodings from local files:

```kotlin
val tokenizer = Tokenizer.of(
    encoding = Encoding.CL100K_BASE, 
    loader = LocalPbeLoader(FileSystem.SYSTEM)
)

// For a specific model in the OpenAI API:
val tokenizer = Tokenizer.of(
    model = "gpt-4", 
    loader = LocalPbeLoader(FileSystem.SYSTEM)
)

val tokens = tokenizer.encode("hello world")
val text = tokenizer.decode(listOf(15339, 1917))
```

##### JVM Specifics:

Artifacts for JVM include encoding files. Use `FileSystem.RESOURCES` to load them:

```kotlin
val tokenizer = Tokenizer.of(
    encoding = Encoding.CL100K_BASE, 
    loader = LocalPbeLoader(FileSystem.RESOURCES)
)
```

*Note: this is the default behavior for JVM.*

#### 🌐 Remote Mode

1. Add Engine: Include one of [Ktor's engines](https://ktor.io/docs/http-client-engines.html) to your dependencies.
2. Use `RemoteBpeLoader`: To load encoding from remote sources:

```kotlin
val tokenizer = Tokenizer.of(
    encoding = Encoding.CL100K_BASE, 
    loader = RemoteBpeLoader()
)

// For a specific model in the OpenAI API:
val tokenizer = Tokenizer.of(
    model = "gpt-4", 
    loader = RemoteBpeLoader()
)

val tokens = tokenizer.encode("hello world")
val text = tokenizer.decode(listOf(15339, 1917))

```

##### BOM Usage

You might alternatively use [ktoken-bom](/ktoken-bom) by adding the following dependency to your `build.gradle` file:

```groovy
dependencies {
    // Import Kotlin API client BOM
    implementation platform('com.aallam.ktoken:ktoken-bom:0.1.0')

    // Define dependencies without versions
    implementation 'com.aallam.ktoken:ktoken'
    runtimeOnly 'io.ktor:ktor-client-okhttp'
}
```

### 🌟 Multiplatform Projects

For multiplatform projects, add the **ktoken** dependency to `commonMain`, and select an [engine](https://ktor.io/docs/http-client-engines.html) for each target.

## 🔐 License
Ktoken is open-source software and distributed under the [MIT license](LICENSE.md).
**This project is not affiliated with nor endorsed by OpenAI**.
