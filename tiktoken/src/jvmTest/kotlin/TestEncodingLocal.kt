import com.aallam.tiktoken.loader.LocalPbeLoader
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class TestEncodingLocal : AbstractEncoding(
    loader = LocalPbeLoader(fileSystem = FileSystem.RESOURCES),
) {
    @Test
    fun unicode() = runTest(timeout = 1.minutes){
        val tokens = listOf(220, 126, 227, 15)
        val encode = tokenizer().encode( " \u00850")
        assertEquals(tokens, encode)
    }
}
