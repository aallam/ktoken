package com.aallam.ktoken.internal

private const val patternP100K = """'s|'t|'re|'ve|'m|'ll|'d|[^\r\n\p{L}\p{N}]?\p{L}+|\p{N}{1,3}| ?[^\s\p{L}\p{N}]+[\r\n]*|\s*[\r\n]+|\s+(?!\S)|\s+"""
private const val patternP100KUnicode = "(?U)$patternP100K"

internal actual val regexP100K: Regex
    get() = try {
        Regex(patternP100KUnicode, RegexOption.IGNORE_CASE)
    } catch (e: IllegalArgumentException) {
        Regex(patternP100K, RegexOption.IGNORE_CASE)
    }

private const val patternO200K = """[^\r\n\p{L}\p{N}]?[\p{Lu}\p{Lt}\p{Lm}\p{Lo}\p{M}]*[\p{Ll}\p{Lm}\p{Lo}\p{M}]+(?i:'s|'t|'re|'ve|'m|'ll|'d)?|[^\r\n\p{L}\p{N}]?[\p{Lu}\p{Lt}\p{Lm}\p{Lo}\p{M}]+[\p{Ll}\p{Lm}\p{Lo}\p{M}]*(?i:'s|'t|'re|'ve|'m|'ll|'d)?|\p{N}{1,3}| ?[^\s\p{L}\p{N}]+[\r\n/]*|\s*[\r\n]+|\s+(?!\S)|\s+"""
private const val patternO200KUnicode = "(?U)$patternO200K"

internal actual val regexO200K: Regex
    get() = try {
        Regex(patternO200KUnicode, RegexOption.IGNORE_CASE)
    } catch (e: IllegalArgumentException) {
        Regex(patternO200K, RegexOption.IGNORE_CASE)
    }
