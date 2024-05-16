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

    internal suspend fun tokenizer() = Tokenizer.of(
        model = "gpt-3.5-turbo-16k",
        loader = loader
    )

    @Test
    fun encodeUnicode() = runTest(timeout = 1.minutes) {
        val encode = tokenizer().encode(
            text = "hello world!你好，世界！",
            allowedSpecial = setOf("all"),
            disallowedSpecial = setOf("all"),
        )
        val sourceTokens = listOf(15339, 1917, 0, 57668, 53901, 3922, 3574, 244, 98220, 6447)
        assertContentEquals(sourceTokens, encode, "Encoding should be equal")
    }

    @Test
    fun encodeAllowSpecial() = runTest(timeout = 1.minutes) {
        val encode = tokenizer().encode(
            text = "hello <|endoftext|>",
            allowedSpecial = setOf("<|endoftext|>")
        )
        val sourceTokens = listOf(15339, 220, 100257)
        assertContentEquals(sourceTokens, encode, "Encoding should be equal")
    }

    @Test
    fun encodeDisallowAll() = runTest(timeout = 1.minutes) {
        val encode = tokenizer().encode(
            text = "hello <|endoftext|>",
            allowedSpecial = setOf("<|endoftext|>"),
            disallowedSpecial = setOf("all")
        )
        val sourceTokens = listOf(15339, 220, 100257)
        assertContentEquals(sourceTokens, encode, "Encoding should be equal")
    }

    @Test
    fun encodeRegex() = runTest(timeout = 1.minutes) {
        assertContentEquals(listOf(38149), tokenizer().encode("rer"))
        assertContentEquals(listOf(2351, 81), tokenizer().encode("'rer"))
        assertContentEquals(listOf(31213, 198, 220), tokenizer().encode("today\n "))
        assertContentEquals(listOf(31213, 27907), tokenizer().encode("today\n \n"))
        assertContentEquals(listOf(31213, 14211), tokenizer().encode("today\n  \n"))
    }

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
    fun emptyEncode() = runTest(timeout = 1.minutes) {
        assertContentEquals(emptyList(), tokenizer().encode(""))
    }

    @Test
    fun encodeSurrogatePairs() = runTest(timeout = 1.minutes) {
        assertContentEquals(listOf(9468, 239, 235), tokenizer().encode("👍"))
        assertContentEquals(listOf(9468, 239, 235), tokenizer().encode("\ud83d\udc4d"))
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
        val fip = tokenizer.encodeSingleToken("<|fim_prefix|>")
        val fim = tokenizer.encodeSingleToken("<|fim_middle|>")
        var text = "<|endoftext|> hello <|fim_prefix|>"
        var tokens = tokenizer.encode(text, disallowedSpecial = emptySet())
        assertNotContains(tokens, eot)
        assertFails {
            tokenizer.encode(text, disallowedSpecial = setOf("all"))
        }
        assertFails {
            tokenizer.encode(text, disallowedSpecial = setOf("<|endoftext|>"))
        }
        assertFails {
            tokenizer.encode(text, disallowedSpecial = setOf("<|fim_prefix|>"))
        }

        text = "<|endoftext|> hello <|fim_prefix|> there <|fim_middle|>"

        tokens = tokenizer.encode(text, disallowedSpecial = emptySet())
        assertNotContains(tokens, eot, fip, fim)

        tokens = tokenizer.encode(text, allowedSpecial = setOf("all"), disallowedSpecial = emptySet())
        assertContains(tokens, eot, fip, fim)

        tokens = tokenizer.encode(text, allowedSpecial = setOf("all"), disallowedSpecial = setOf("all"))
        assertContains(tokens, eot, fip, fim)

        tokens = tokenizer.encode(text, allowedSpecial = setOf("<|fim_prefix|>"), disallowedSpecial = emptySet())
        assertContains(tokens, fip)
        assertNotContains(tokens, eot, fim)

        tokens = tokenizer.encode(text, allowedSpecial = setOf("<|endoftext|>"), disallowedSpecial = emptySet())
        assertContains(tokens, eot)
        assertNotContains(tokens, fip, fim)

        tokens = tokenizer.encode(text, allowedSpecial = setOf("<|fim_middle|>"), disallowedSpecial = emptySet())
        assertContains(tokens, fim)
        assertNotContains(tokens, fip, eot)
    }
}
