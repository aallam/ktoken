package com.aallam.tiktoken.internal.extension

internal fun IntRange.slide(value: Int): IntRange {
    return (first + value)..(last + value)
}