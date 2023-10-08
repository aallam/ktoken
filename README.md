# Ktoken

[![Maven Central](https://img.shields.io/maven-central/v/com.aallam.ktoken/ktoken?color=blue&label=Download)](https://central.sonatype.com/namespace/com.aallam.ktoken)
[![License](https://img.shields.io/github/license/aallam/ktoken?color=yellow)](LICENSE.md)

**Ktoken**, a BPE tokenizer for use with OpenAI's models.

## ⚡️ Getting Started

1. Install **ktoken** by adding the following dependency to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.aallam.ktoken:ktoken:0.1.0"
}
```

The library can be used in two modes: **Remote** (*default*) and **Local**.

### Local

Use `LocalPbeLoader` to load encodings from local files:

```kotlin
val tokenizer = Tokenizer.encoding(encodingName = EncodingName.CL100K_BASE, loader = LocalPbeLoader(FileSystem.SYSTEM))
// To get the tokeniser corresponding to a specific model in the OpenAI API:
val tokenizer = Tokenizer.encodingForModel(model = "gpt-4", loader = LocalPbeLoader(FileSystem.SYSTEM))

val tokens = tokenizer.encode("hello world")
val text = tokenizer.decode(listOf(15339, 1917))
```

#### JVM

JVM artifacts include encoding files. You can use `LocalPbeLoader` with `FileSystem.RESOURCES` to load them:

```kotlin
val tokenizer = Tokenizer.encoding(encodingName = EncodingName.CL100K_BASE, loader = LocalPbeLoader(FileSystem.RESOURCES))
```

### Remote (default)

1. Choose and add one of [Ktor's engines](https://ktor.io/docs/http-client-engines.html) to your dependencies in the `build.gradle` file.
2. Use `RemoteBpeLoader` to load encoding from remote sources:

```kotlin
val tokenizer = Tokenizer.encoding(encodingName = EncodingName.CL100K_BASE, loader = RemoteBpeLoader())
// To get the tokeniser corresponding to a specific model in the OpenAI API:
val tokenizer = Tokenizer.encodingForModel(model = "gpt-4", loader = RemoteBpeLoader())

val tokens = tokenizer.encode("hello world")
val text = tokenizer.decode(listOf(15339, 1917))
```

#### BOM

Alternatively, you can use [ktoken-bom](/ktoken-bom) by adding the following dependency to your `build.gradle` file:

```groovy
dependencies {
    // import Kotlin API client BOM
    implementation platform('com.aallam.ktoken:ktoken-bom:0.1.0')

    // define dependencies without versions
    implementation 'com.aallam.ktoken:ktoken'
    runtimeOnly 'io.ktor:ktor-client-okhttp'
}
```

#### Multiplaform

In multiplatform projects, add the **ktoken** dependency to `commonMain`, and choose an [engine](https://ktor.io/docs/http-client-engines.html) for each target.

## 📄 License

Ktoken is open-source software licensed under the [MIT license](LICENSE.md).
**This is not affiliated with nor endorsed by OpenAI**.
