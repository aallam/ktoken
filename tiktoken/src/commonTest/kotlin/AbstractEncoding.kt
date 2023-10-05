import com.aallam.kotoken.Tokenizer
import com.aallam.kotoken.loader.BpeLoader
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFails
import kotlin.time.Duration.Companion.minutes

abstract class AbstractEncoding(private val loader: BpeLoader) {

    private lateinit var tokenizer: Tokenizer

    @BeforeTest
    fun init() = runTest(timeout = 1.minutes) {
        tokenizer = Tokenizer.getEncodingForModel(
            model = "gpt-3.5-turbo-16k",
            loader = loader
        )
    }

    @Test
    fun encodeUnicode() = runTest(timeout = 1.minutes) {
        val encode = tokenizer.encode(
            text = "hello world!你好，世界！",
            allowedSpecial = setOf("all"),
            disallowedSpecial = setOf("all"),
        )
        val sourceTokens = listOf(15339, 1917, 0, 57668, 53901, 3922, 3574, 244, 98220, 6447)
        assertContentEquals(sourceTokens, encode, "Encoding should be equal")
    }

    @Test
    fun encodeAllowSpecial() = runTest(timeout = 1.minutes) {
        val encode = tokenizer.encode(
            text = "hello <|endoftext|>",
            allowedSpecial = setOf("<|endoftext|>")
        )
        val sourceTokens = listOf(15339, 220, 100257)
        assertContentEquals(sourceTokens, encode, "Encoding should be equal")
    }

    @Test
    fun encodeDisallowAll() = runTest(timeout = 1.minutes) {
        val encode = tokenizer.encode(
            text = "hello <|endoftext|>",
            allowedSpecial = setOf("<|endoftext|>"),
            disallowedSpecial = setOf("all")
        )
        val sourceTokens = listOf(15339, 220, 100257)
        assertContentEquals(sourceTokens, encode, "Encoding should be equal")
    }

    @Test
    fun encodeFail() = runTest(timeout = 1.minutes) {
        assertFails {
            tokenizer.encode(
                text = "hello <|endoftext|><|endofprompt|>",
                allowedSpecial = setOf("<|endoftext|>"),
                disallowedSpecial = setOf("all")
            )
        }
    }

    @Test
    fun encodeFail2() = runTest(timeout = 1.minutes) {
        assertFails {
            tokenizer.encode(
                text = "hello <|endoftext|>",
                allowedSpecial = setOf("<|endoftext|>"),
                disallowedSpecial = setOf("<|endoftext|>")
            )
        }
    }
}
