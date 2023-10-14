package com.aallam.ktoken.internal

/**
 * Finds the first occurrence of the specified regular expression within the given text,
 * starting the search at the specified startIndex.
 */
internal fun findIndex(text: String, startIndex: Int, regex: Regex): IntRange? {
    return regex.find(input = text, startIndex = startIndex)?.range
}

/**
 * Finds all non-overlapping occurrences of the specified regular expression within the given text
 * and returns their start and end indices as a list of [IntRange].
 */
internal fun findAllRanges(text: String, regex: Regex): List<IntRange> {
    return regex.findAll(text)
        .map { it.range }
        .toList()
}

/**
 * Finds the first occurrence of the specified regular expression within the given text
 * and returns it as a [String].
 */
internal fun findMatch(text: String, regex: Regex): String {
    val matchResult = regex.find(text)
    return matchResult?.value.orEmpty()
}

/**
 * Generates a regular expression pattern from a set of special tokens
 * and returns a [Regex] object for matching any of these special tokens.
 */
internal fun regexSpecialTokens(specialTokens: Set<String>): Regex {
    val specialRegexStrings = specialTokens.map { Regex.escape(it) }
    val specialRegexPattern = specialRegexStrings.joinToString(separator = "|")
    return Regex(specialRegexPattern)
}
