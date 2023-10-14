package com.aallam.ktoken.internal

import com.aallam.ktoken.loader.BpeLoader
import okio.ByteString
import okio.ByteString.Companion.decodeBase64
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * Platform-specific function that provides a default BPE loader.
 */
internal expect fun defaultPbeLoader(): BpeLoader

/**
 * Load Byte Pair Encoding (BPE) data from a byte array and return it as a map of ByteStrings to ranks.
 *
 * @param data The byte array containing BPE data.
 * @return A map of ByteStrings to their corresponding ranks.
 */
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
