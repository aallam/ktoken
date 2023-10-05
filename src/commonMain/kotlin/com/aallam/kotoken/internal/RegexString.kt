package com.aallam.kotoken.internal

internal object RegexString {

    fun findIndex(text: String, regex: Regex): IntArray? {
        val matchResult = regex.find(text) ?: return null
        return intArrayOf(matchResult.range.first, matchResult.range.last + 1)
    }

    fun findAllIndexes(text: String, regex: Regex): List<IntArray> {
        return regex.findAll(text)
            .map { matchResult -> intArrayOf(matchResult.range.first, matchResult.range.last + 1) }
            .toList()
    }

    fun findMatch(text: String, regex: Regex): String {
        val matchResult = regex.find(text)
        return matchResult?.value.orEmpty()
    }

    fun regexSpecialTokens(specialTokens: Set<String>): Regex {
        val specialRegexStrings = specialTokens.map { Regex.escape(it) }
        val specialRegexPattern = specialRegexStrings.joinToString(separator = "|")
        return Regex(specialRegexPattern)
    }
}
