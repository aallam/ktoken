package com.aallam.tiktoken.internal

internal fun findIndex(text: String, startIndex: Int, regex: Regex): IntRange? {
    return regex.find(input = text, startIndex = startIndex)?.range
}

internal fun findAllRanges(text: String, regex: Regex): List<IntRange> {
    return regex.findAll(text)
        .map { it.range }
        .toList()
}

internal fun findMatch(text: String, regex: Regex): String {
    val matchResult = regex.find(text)
    return matchResult?.value.orEmpty()
}

internal fun regexSpecialTokens(specialTokens: Set<String>): Regex {
    val specialRegexStrings = specialTokens.map { Regex.escape(it) }
    val specialRegexPattern = specialRegexStrings.joinToString(separator = "|")
    return Regex(specialRegexPattern)
}
