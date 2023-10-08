package com.aallam.ktoken.internal.extension

internal fun IntRange.slide(value: Int): IntRange {
    return (first + value)..(last + value)
}
