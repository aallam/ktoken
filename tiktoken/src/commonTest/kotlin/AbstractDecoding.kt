import com.aallam.kotoken.EncodingName
import com.aallam.kotoken.Tokenizer
import com.aallam.kotoken.loader.BpeLoader
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

abstract class AbstractDecoding(
    private val loader: BpeLoader
) {

    @Test
    fun decode() = runTest(timeout = 1.minutes) {
        val tokenizer = Tokenizer.getEncoding(
            encodingName = EncodingName.CL100K_BASE,
            loader = loader
        )
        val sourceTokens = listOf(15339, 1917, 0, 57668, 53901, 3922, 3574, 244, 98220, 6447)
        val decoded = tokenizer.decode(sourceTokens)
        assertEquals("hello world!你好，世界！", decoded)
    }
}
