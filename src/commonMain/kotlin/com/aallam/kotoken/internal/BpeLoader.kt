package com.aallam.kotoken.internal

import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.encodeUtf8
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

internal fun loadTiktokenBpe(data: ByteArray): Map<ByteString, Int> {
    val bpeRanks = mutableMapOf<ByteString, Int>()
    val lines = data.decodeToString().split("\n")
    for (line in lines) {
        if (line.isEmpty()) continue
        val (encodedToken, rankString) = line.split(" ")
        val token = encodedToken.decodeBase64() ?: error("can't decode $encodedToken")
        val rank = rankString.toInt()
        bpeRanks[token] = rank
    }
    return bpeRanks
}

internal fun loadVocabBpe(data: ByteArray): Map<ByteString, Int> {
    val rankToIntByte = List(256) { it }.filter { it.toChar().isPrintable() && it.toChar() != ' ' }.toMutableList()
    var n = 0
    for (b in 0..<256) {
        if (b !in rankToIntByte) {
            rankToIntByte.add(b)
            n++
        }
    }
    require(rankToIntByte.size == 256)

    val vocabBpe = data.decodeToString()
        .split("\n")
        .drop(1)
        .dropLast(1)
        .map { it.split(" ") }
        .map { it[0] to it[1] }

    val bpeRanks = rankToIntByte
        .withIndex()
        .associate { ByteString.of(it.value.toByte()) to it.index }
        .toMutableMap()

    var rank = bpeRanks.size
    for ((first, second) in vocabBpe) {
        val concatenated = Buffer().write(first.encodeUtf8()).write(second.encodeUtf8()).readByteString()
        bpeRanks[concatenated] = rank
        rank++
    }

    return bpeRanks
}


private fun Char.isPrintable(): Boolean {
    return this.category !in setOf(
        CharCategory.CONTROL,
        CharCategory.FORMAT,
        CharCategory.SURROGATE,
        CharCategory.PRIVATE_USE,
        CharCategory.UNASSIGNED
    )
}
