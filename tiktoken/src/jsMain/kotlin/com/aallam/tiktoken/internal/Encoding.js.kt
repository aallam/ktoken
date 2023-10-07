package com.aallam.tiktoken.internal

internal actual val regexP100K: Regex
    get() = Regex(
        pattern = """(?:'s|'t|'re|'ve|'m|'ll|'d)|[^\r\n\p{L}\p{N}]?\p{L}+|\p{N}{1,3}| ?[^\s\p{L}\p{N}]+[\r\n]*|\s*[\r\n]+|\s+(?!\S)|\s+""",
        options = setOf(RegexOption.IGNORE_CASE)
    )
