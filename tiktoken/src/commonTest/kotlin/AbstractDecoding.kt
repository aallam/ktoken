import com.aallam.kotoken.EncodingName
import com.aallam.kotoken.Tiktoken
import com.aallam.kotoken.loader.BpeLoader
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class AbstractDecoding(
    private val loader: BpeLoader
) {

    @Test
    fun decode() = runTest {
        val tiktoken = Tiktoken.getEncoding(
            encodingName = EncodingName.CL100K_BASE,
            loader = loader
        )
        val sourceTokens = intArrayOf(15339, 1917, 0, 57668, 53901, 3922, 3574, 244, 98220, 6447)
        val decoded = tiktoken.decode(sourceTokens)
        assertEquals("hello world!你好，世界！", decoded)
    }

    @Test
    fun decode2() = runTest {
        val tiktoken = Tiktoken.getEncoding(
            encodingName = EncodingName.R50K_BASE,
            loader = loader
        )
        val sourceTokens = intArrayOf(15339, 1917, 0, 57668, 53901, 3922, 3574, 244, 98220, 6447)
        val decoded = tiktoken.decode(sourceTokens)
        assertEquals("hello world!你好，世界！", decoded)
    }
}
