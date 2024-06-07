package com.aallam.ktoken.internal

internal actual val regexP100K: Regex = Regex(
    pattern = """(?:'s|'t|'re|'ve|'m|'ll|'d)|[^\r\n\p{L}\p{N}]?\p{L}+|\p{N}{1,3}| ?[^\s\p{L}\p{N}]+[\r\n]*|\s*[\r\n]+|\s+(?!\S)|\s+""",
    options = setOf(RegexOption.IGNORE_CASE)
)

internal actual val regexO200K: Regex = Regex(
    pattern = """[^\r\nA-Za-z0-9]?[A-Z]*[a-z]+(?:'s|'t|'re|'ve|'m|'ll|'d)?|[^\r\nA-Za-z0-9]?[A-Z]+[a-z]*(?:'s|'t|'re|'ve|'m|'ll|'d)?|\d{1,3}| ?[^\sA-Za-z0-9]+[\r\n/]*|\s*[\r\n]+|\s+(?!\S)|\s+""",
    options = setOf(RegexOption.IGNORE_CASE)
)
