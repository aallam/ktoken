package com.aallam.tiktoken.internal

import okio.ByteString

internal fun findIndex(bytes: ByteString, regex: Regex): IntRange? {
    val text = bytes.utf8()
    val matchResult = regex.find(text) ?: return null
    return matchResult.range.first..matchResult.range.last + 1
}

internal fun findAllIndexes(bytes: ByteString, regex: Regex): List<IntRange> {
    val text = bytes.utf8()
    return regex.findAll(text)
        .map { matchResult -> matchResult.range.first..matchResult.range.last + 1 }
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
