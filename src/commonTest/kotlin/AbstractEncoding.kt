import com.aallam.kotoken.Tiktoken
import com.aallam.kotoken.loader.BpeLoader
import com.aallam.kotoken.loader.RemoteBpeLoader
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFails

abstract class AbstractEncoding(private val loader: BpeLoader) {

    private lateinit var tiktoken: Tiktoken

    @BeforeTest
    fun init() = runTest {
        tiktoken = Tiktoken.getEncodingForModel(
            modelName = "gpt-3.5-turbo-16k",
            loader = loader
        )
    }

    @Test
    fun encodeUnicode() = runTest {
        val encode = tiktoken.encode(
            text = "hello world!你好，世界！",
            allowedSpecial = setOf("all"),
            disallowedSpecial = setOf("all"),
        )
        val sourceTokens = intArrayOf(15339, 1917, 0, 57668, 53901, 3922, 3574, 244, 98220, 6447)
        assertContentEquals(sourceTokens, encode, "Encoding should be equal")
    }

    @Test
    fun encodeAllowSpecial() = runTest {
        val encode = tiktoken.encode(
            text = "hello <|endoftext|>",
            allowedSpecial = setOf("<|endoftext|>")
        )
        val sourceTokens = intArrayOf(15339, 220, 100257)
        assertContentEquals(sourceTokens, encode, "Encoding should be equal")
    }

    @Test
    fun encodeDisallowAll() = runTest {
        val encode = tiktoken.encode(
            text = "hello <|endoftext|>",
            allowedSpecial = setOf("<|endoftext|>"),
            disallowedSpecial = setOf("all")
        )
        val sourceTokens = intArrayOf(15339, 220, 100257)
        assertContentEquals(sourceTokens, encode, "Encoding should be equal")
    }

    @Test
    fun encodeFail() = runTest {
        assertFails {
            tiktoken.encode(
                text = "hello <|endoftext|><|endofprompt|>",
                allowedSpecial = setOf("<|endoftext|>"),
                disallowedSpecial = setOf("all")
            )
        }
    }

    @Test
    fun encodeFail2() = runTest {
        assertFails {
            tiktoken.encode(
                text = "hello <|endoftext|>",
                allowedSpecial = setOf("<|endoftext|>"),
                disallowedSpecial = setOf("<|endoftext|>")
            )
        }
    }
}
