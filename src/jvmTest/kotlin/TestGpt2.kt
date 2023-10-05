import com.aallam.kotoken.Tiktoken
import com.aallam.kotoken.loader.LocalPbeLoader
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TestGpt2 {

    @Test
    fun encode() = runTest {
        val tiktoken = Tiktoken.getEncodingForModel("gpt2", loader = LocalPbeLoader(FileSystem.RESOURCES))
        val encode = tiktoken.encode("hello world")
        assertContentEquals(intArrayOf(31373, 995), encode)
    }
}