package com.aallam.ktoken.internal

internal actual val regexP100K: Regex
    get() = Regex(
        pattern = """'s|'t|'re|'ve|'m|'ll|'d|[^\r\nA-Za-z0-9]?[A-Za-z]+|[0-9]{1,3}| ?[^\sA-Za-z0-9]+[\r\n]*|\s*[\r\n]+|\s+(?!\S)|\s+""",
        option = RegexOption.IGNORE_CASE
    )

internal actual val regexO200K: Regex
    get() = Regex(
        pattern = """[^\r\nA-Za-z0-9]?[A-Z]*[a-z]+(?:'s|'t|'re|'ve|'m|'ll|'d)?|[^\r\nA-Za-z0-9]?[A-Z]+[a-z]*(?:'s|'t|'re|'ve|'m|'ll|'d)?|\d{1,3}| ?[^\sA-Za-z0-9]+[\r\n/]*|\s*[\r\n]+|\s+(?!\S)|\s+""",
        option = RegexOption.IGNORE_CASE
    )