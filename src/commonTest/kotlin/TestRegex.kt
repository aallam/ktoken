import com.aallam.kotoken.internal.findAllIndexes
import com.aallam.kotoken.internal.findIndex
import com.aallam.kotoken.internal.findMatch
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
            val find = findIndex(word, re)!!
            val reFind = re.find(word)!!
            assertEquals(reFind.range.first, find[0])
            assertEquals(reFind.range.last + 1, find[1])

            val findMatch = findMatch(word, re)
            val reFindMatch = re.find(word)!!
            assertEquals(reFindMatch.value, findMatch)

            val findAllIndexes = findAllIndexes(word, re)
            val reFindAllIndexes = re.findAll(word)
            reFindAllIndexes.forEachIndexed { index, matchResult ->
                val indexes = findAllIndexes[index]
                assertEquals(matchResult.range.first, indexes[0])
                assertEquals(matchResult.range.last + 1, indexes[1])
            }
        }
    }
}
