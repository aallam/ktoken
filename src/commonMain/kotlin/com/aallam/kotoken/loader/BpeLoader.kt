package com.aallam.kotoken.loader

import com.aallam.kotoken.EncodingName
import okio.ByteString
import okio.ByteString.Companion.decodeBase64
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

sealed interface BpeLoader {

    suspend fun load(encodingName: EncodingName): Map<ByteString, Int>
}

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
