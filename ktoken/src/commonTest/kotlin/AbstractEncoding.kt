import com.aallam.ktoken.Tokenizer
import com.aallam.ktoken.Encoding
import com.aallam.ktoken.loader.BpeLoader
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.Duration.Companion.minutes

abstract class AbstractEncoding(private val loader: BpeLoader) {

    @Test
    fun basicEncodeR50K() = runTest(timeout = 1.minutes) {
        val tokenizer = Tokenizer.of(Encoding.R50K_BASE, loader)
        assertContentEquals(listOf(31373, 995), tokenizer.encode("hello world"))
    }

    @Test
    fun basicEncodeP50K() = runTest(timeout = 1.minutes) {
        val tokenizer = Tokenizer.of(Encoding.P50K_BASE, loader)
        assertContentEquals(listOf(31373, 995), tokenizer.encode("hello world"))
    }

    @Test
    fun basicEncodeCL100K() = runTest(timeout = 1.minutes) {
        val tokenizer = Tokenizer.of(Encoding.CL100K_BASE, loader)
        assertContentEquals(listOf(15339, 1917), tokenizer.encode("hello world"))
    }

    @Test
    fun baseEncodeO200K() = runTest(timeout = 1.minutes) {
        val tokenizer = Tokenizer.of(Encoding.O200K_BASE, loader)
        assertContentEquals(listOf(24912, 2375), tokenizer.encode("hello world"))
    }

    @Test
    fun recentModelAliasesMapToO200K() = runTest(timeout = 1.minutes) {
        val reference = Tokenizer.of(Encoding.O200K_BASE, loader).encode("hello world")
        val models = listOf(
            "o1",
            "o3",
            "o4-mini",
            "gpt-4.1",
            "gpt-5",
            "ft:gpt-4o:example",
            "o1-2024-12-17",
            "o3-2025-02-01",
            "o4-mini-2025-02-01",
            "gpt-4.1-2025-04-14",
            "gpt-4.5-preview-2025-02-27",
            "gpt-5-2025-08-07",
            "chatgpt-4o-latest",
        )
        for (model in models) {
            val tokens = Tokenizer.of(model = model, loader = loader).encode("hello world")
            assertContentEquals(reference, tokens)
        }
    }

    @Test
    fun gptOssRemainsUnsupported() = runTest(timeout = 1.minutes) {
        assertFails {
            Tokenizer.of(model = "gpt-oss-120b", loader = loader)
        }
    }

    internal suspend fun tokenizer() = Tokenizer.of(
        model = "gpt-4o",
        loader = loader
    )

    @Test
    fun encodeUnicode() = runTest(timeout = 1.minutes) {
        val tokenizer = tokenizer()
        val input = "hello world!你好，世界！"
        val encode = tokenizer.encode(
            text = input,
            allowedSpecial = setOf("all"),
            disallowedSpecial = setOf("all"),
        )
        val decoded = tokenizer.decode(encode)
        assertEquals(input, decoded, "Encoding should be equal")
    }

    @Test
    fun encodeAllowSpecial() = runTest(timeout = 1.minutes) {
        val tokenizer = tokenizer()
        val encode = tokenizer.encode(
            text = "hello <|endoftext|>",
            allowedSpecial = setOf("<|endoftext|>")
        )
        val eot = tokenizer.encodeSingleToken("<|endoftext|>")
        assertContains(encode, eot)
    }

    @Test
    fun encodeDisallowAll() = runTest(timeout = 1.minutes) {
        val tokenizer = tokenizer()
        val encode = tokenizer.encode(
            text = "hello <|endoftext|>",
            allowedSpecial = setOf("<|endoftext|>"),
            disallowedSpecial = setOf("all")
        )
        val eot = tokenizer.encodeSingleToken("<|endoftext|>")
        assertContains(encode, eot)
    }

    @Test
    fun encodeRegex() = runTest(timeout = 1.minutes) {
        val tokenizer = tokenizer()
        assertEquals("rer", tokenizer.decode(tokenizer.encode("rer")))
        assertEquals("'rer", tokenizer.decode(tokenizer.encode("'rer")))
        assertEquals("today\n ", tokenizer.decode(tokenizer.encode("today\n ")))
        assertEquals("today\n \n", tokenizer.decode(tokenizer.encode("today\n \n")))
        assertEquals("today\n  \n", tokenizer.decode(tokenizer.encode("today\n  \n")))
    }

    @Test
    fun emptyEncode() = runTest(timeout = 1.minutes) {
        assertContentEquals(emptyList(), tokenizer().encode(""))
    }

    @Test
    fun encodeSurrogatePairs() = runTest(timeout = 1.minutes) {
        assertContentEquals(tokenizer().encode("👍"), tokenizer().encode("\ud83d\udc4d"))
    }

    @Test
    fun roundTrip() = runTest(timeout = 1.minutes) {
        val values =
            listOf("hello", "hello ", "hello  ", " hello", " hello ", " hello  ", "hello world", "请考试我的软件！12345")
        for (value in values) {
            val encoded = tokenizer().encode(value)
            val decoded = tokenizer().decode(encoded)
            assertEquals(value, decoded)
        }
    }

    @Test
    fun decode() = runTest(timeout = 1.minutes) {
        val tokenizer = Tokenizer.of(
            encoding = Encoding.CL100K_BASE,
            loader = loader
        )
        val sourceTokens = listOf(15339, 1917, 0, 57668, 53901, 3922, 3574, 244, 98220, 6447)
        val decoded = tokenizer.decode(sourceTokens)
        assertEquals("hello world!你好，世界！", decoded)
    }

    @Test
    fun encodeFail() = runTest(timeout = 1.minutes) {
        assertFails {
            tokenizer().encode(
                text = "hello <|endoftext|><|endofprompt|>",
                allowedSpecial = setOf("<|endoftext|>"),
                disallowedSpecial = setOf("all")
            )
        }
    }

    @Test
    fun encodeFailDisallowed() = runTest(timeout = 1.minutes) {
        assertFails {
            tokenizer().encode(
                text = "hello <|endoftext|>",
                allowedSpecial = setOf("<|endoftext|>"),
                disallowedSpecial = setOf("<|endoftext|>")
            )
        }
    }

    @Test
    fun singleToken() = runTest(timeout = 1.minutes) {
        val tokenizer = tokenizer()
        for (n in 0..<1000) {
            val token = n.toString()
            val encoded = tokenizer.encodeSingleToken(token)
            val decoded = tokenizer.decode(encoded)
            assertEquals(token, decoded)
        }
    }

    @Test
    fun specialTokens() = runTest(timeout = 1.minutes) {
        val tokenizer = tokenizer()
        val eot = tokenizer.encodeSingleToken("<|endoftext|>")
        val eop = tokenizer.encodeSingleToken("<|endofprompt|>")
        var text = "<|endoftext|> hello <|endofprompt|>"
        var tokens = tokenizer.encode(text, disallowedSpecial = emptySet())
        assertNotContains(tokens, eot)
        assertFails {
            tokenizer.encode(text, disallowedSpecial = setOf("all"))
        }
        assertFails {
            tokenizer.encode(text, disallowedSpecial = setOf("<|endoftext|>"))
        }
        assertFails {
            tokenizer.encode(text, disallowedSpecial = setOf("<|endofprompt|>"))
        }

        text = "<|endoftext|> hello <|endofprompt|>"

        tokens = tokenizer.encode(text, disallowedSpecial = emptySet())
        assertNotContains(tokens, eot, eop)

        tokens = tokenizer.encode(text, allowedSpecial = setOf("all"), disallowedSpecial = emptySet())
        assertContains(tokens, eot, eop)

        tokens = tokenizer.encode(text, allowedSpecial = setOf("all"), disallowedSpecial = setOf("all"))
        assertContains(tokens, eot, eop)

        tokens = tokenizer.encode(text, allowedSpecial = setOf("<|endofprompt|>"), disallowedSpecial = emptySet())
        assertContains(tokens, eop)
        assertNotContains(tokens, eot)

        tokens = tokenizer.encode(text, allowedSpecial = setOf("<|endoftext|>"), disallowedSpecial = emptySet())
        assertContains(tokens, eot)
        assertNotContains(tokens, eop)
    }
}
