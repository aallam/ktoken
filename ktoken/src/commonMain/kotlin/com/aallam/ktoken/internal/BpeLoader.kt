package com.aallam.ktoken.internal

import okio.ByteString
import okio.ByteString.Companion.decodeBase64
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
