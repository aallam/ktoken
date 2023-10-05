package com.aallam.kotoken

import okio.ByteString

internal fun bytePairEncode(piece: ByteString, ranks: Map<ByteString, Int>): IntArray {
    if (piece.size == 1) {
        val value = ranks[piece] ?: 0
        return intArrayOf(value)
    }
    return bytePairMerge(piece, ranks) { start, end -> ranks[piece.substring(start, end)] ?: 0 }
}

private fun bytePairMerge(
    piece: ByteString,
    ranks: Map<ByteString, Int>,
    f: (start: Int, end: Int) -> Int
): IntArray {
    val parts = MutableList(piece.size + 1) { index ->
        IntArray(2) { if (it == 0) index else Int.MAX_VALUE }
    }

    fun getRank(startIdx: Int, skip: Int): Int {
        if (startIdx + skip + 2 < parts.size) {
            val b = piece.substring(parts[startIdx][0], parts[startIdx + skip + 2][0])
            return ranks[b] ?: -1
        }
        return -1 // use -1 to represent None
    }

    for (i in 0..<parts.size - 2) {
        val rank = getRank(i, 0)
        if (rank >= 0) {
            parts[i][1] = rank
        }
    }

    while (parts.size > 1) {
        var minRank = Int.MAX_VALUE
        var minIdx = -1
        for (i in 0..<parts.size - 1) {
            if (parts[i][1] < minRank) {
                minRank = parts[i][1]
                minIdx = i
            }
        }

        if (minRank < Int.MAX_VALUE) {
            val i = minIdx
            val rank = getRank(i, 1)
            parts[i][1] = if (rank >= 0) rank else Int.MAX_VALUE
            if (i > 0) {
                val rk = getRank(i - 1, 1)
                parts[i - 1][1] = if (rk >= 0) rk else Int.MAX_VALUE
            }
            parts.removeAt(i + 1)
        } else {
            break
        }
    }

    return Array(parts.size - 1) { i -> f(parts[i][0], parts[i + 1][0]) }.toIntArray()
}
