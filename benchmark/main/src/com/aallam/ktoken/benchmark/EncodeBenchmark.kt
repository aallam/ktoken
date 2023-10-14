package com.aallam.ktoken.benchmark

import com.aallam.ktoken.Tokenizer
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.*
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

@State(Scope.Benchmark)
@Fork(2)
@BenchmarkMode(Mode.SingleShotTime)
class EncodeBenchmark {
    private lateinit var text: String
    private lateinit var tokenizer: Tokenizer

    @Setup
    fun setUp() = runBlocking {
        val url = URL("https://unicode.org/udhr/assemblies/full_all.txt")
        Scanner(url.openStream(), StandardCharsets.UTF_8.name())
            .use { scanner -> text = scanner.useDelimiter("\\A").next() }
        tokenizer = Tokenizer.of("gpt-4")
    }

    @Benchmark
    fun encode(): Int {
        return tokenizer.encode(text).size
    }
}
