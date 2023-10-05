import com.aallam.kotoken.EncodingName
import com.aallam.kotoken.Tiktoken
import com.aallam.kotoken.loader.BpeLoader
import com.aallam.kotoken.loader.RemoteBpeLoader
import kotlinx.coroutines.test.runTest
import kotlin.test.*

abstract class AbstractDecoding(
    private val encodingName: EncodingName,
    private val loader: BpeLoader = RemoteBpeLoader()
) {

    @Test
    fun decode() = runTest {
        val tiktoken = Tiktoken.getEncoding(
            encodingName = encodingName,
            loader = loader
        )
        val sourceTokens = intArrayOf(15339, 1917, 0, 57668, 53901, 3922, 3574, 244, 98220, 6447)
        val decoded = tiktoken.decode(sourceTokens)
        assertEquals("hello world!你好，世界！", decoded)
    }
}
