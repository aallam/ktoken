package com.aallam.tiktoken.internal

private const val patternP100K = """'s|'t|'re|'ve|'m|'ll|'d|[^\r\n\p{L}\p{N}]?\p{L}+|\p{N}{1,3}| ?[^\s\p{L}\p{N}]+[\r\n]*|\s*[\r\n]+|\s+(?!\S)|\s+"""
private const val patternP100KUnicode = "(?U)$patternP100K"

internal actual val regexP100K: Regex
    get() = try {
        Regex(patternP100KUnicode, RegexOption.IGNORE_CASE)
    } catch (e: IllegalArgumentException) {
        Regex(patternP100K, RegexOption.IGNORE_CASE)
    }
