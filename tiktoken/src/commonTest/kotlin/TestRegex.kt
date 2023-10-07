import com.aallam.tiktoken.internal.findAllIndexes
import com.aallam.tiktoken.internal.findIndex
import com.aallam.tiktoken.internal.findMatch
import okio.ByteString.Companion.encodeUtf8
import kotlin.test.Test
import kotlin.test.assertEquals

class TestRegex {

    @Test
    fun regex() {
        val re = Regex("""[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,4}""")
        val words = listOf(
            "this is my email hi@google.com,and this is john's email world@outlook.com",
            "hi@google.com is email for google",
            "outlook email world@outlook.com is work for microsoft",
        )

        for (word in words) {
            val bytes = word.encodeUtf8()
            val find = findIndex(bytes, re)!!
            val reFind = re.find(word)!!
            assertEquals(reFind.range.first, find.first)
            assertEquals(reFind.range.last + 1, find.last)

            val findMatch = findMatch(word, re)
            val reFindMatch = re.find(word)!!
            assertEquals(reFindMatch.value, findMatch)

            val findAllIndexes = findAllIndexes(bytes, re)
            val reFindAllIndexes = re.findAll(word)
            reFindAllIndexes.forEachIndexed { index, matchResult ->
                val indexes = findAllIndexes[index]
                assertEquals(matchResult.range.first, indexes.first)
                assertEquals(matchResult.range.last + 1, indexes.last)
            }
        }
    }

    @Test
    fun unicodeJS() {
        val js = """(?:'s|'t|'re|'ve|'m|'ll|'d)|[^\r\n\p{L}\p{N}]?\p{L}+|\p{N}{1,3}| ?[^\s\p{L}\p{N}]+[\r\n]*|\s*[\r\n]+|\s+(?!\S)|\s+""".toRegex(RegexOption.IGNORE_CASE)
        val str = " \u00850"
        val matches = js.findAll(str).toList()
        matches.forEach { println(it.range) }
    }

    @Test
    fun unicodeJVM() {
        val jvm = """(?U)'s|'t|'re|'ve|'m|'ll|'d|[^\r\n\p{L}\p{N}]?\p{L}+|\p{N}{1,3}| ?[^\s\p{L}\p{N}]+[\r\n]*|\s*[\r\n]+|\s+(?!\S)|\s+""".toRegex(RegexOption.IGNORE_CASE)
        val str = " \u00850"
        val matches = jvm.findAll(str).toList()
        matches.forEach { println(it.range) }
    }

    @Test
    fun unicodeNative() {
        val native = """'s|'t|'re|'ve|'m|'ll|'d|[^\r\nA-Za-z0-9]?[A-Za-z]+|[0-9]{1,3}| ?[^\sA-Za-z0-9]+[\r\n]*|\s*[\r\n]+|\s+(?!\S)|\s+""".toRegex(RegexOption.IGNORE_CASE)
        val str = " \u00850"
        val matches = native.findAll(str).toList()
        matches.forEach { println(it.range) }
    }
}
