package com.aallam.ktoken.internal

import okio.ByteString

/**
 * Encode a given piece (ByteString) using byte pair encoding and return an array of integers representing the encoded tokens.
 *
 * @param piece The input piece to encode.
 * @param ranks A map that associates ByteStrings with their corresponding ranks.
 * @return An array of integers representing the encoded tokens.
 */
internal fun bytePairEncode(piece: ByteString, ranks: Map<ByteString, Int>): IntArray {
    if (piece.size == 1) {
        val value = ranks[piece] ?: 0
        return intArrayOf(value)
    }

    // Initialize `parts` to store pairs of [index, rank].
    val parts = MutableList(piece.size + 1) { index ->
        IntArray(2) { if (it == 0) index else Int.MAX_VALUE }
    }

    // Populate initial ranks for the first merge candidates.
    for (i in 0 until parts.size - 2) {
        parts[i][1] = getRank(piece, ranks, parts, i, 0) ?: Int.MAX_VALUE
    }

    // Continue merging until only one part remains.
    while (parts.size > 1) {
        // Find the index with the minimum rank for merging.
        val index = findMinRankIndex(parts) ?: break
        // Update ranks after merging.
        parts[index][1] = getRank(piece, ranks, parts, index, 1) ?: Int.MAX_VALUE
        if (index > 0) {
            val prevIndex = index - 1
            // Update rank of the previous part in case it needs to be merged in the next iterations.
            parts[prevIndex][1] = getRank(piece, ranks, parts, prevIndex, 1) ?: Int.MAX_VALUE
        }
        // Merge the part at `index` with the next part.
        parts.removeAt(index + 1)
    }

    // Generate the final encoded tokens based on remaining parts.
    return IntArray(parts.size - 1) { i ->
        val start = parts[i][0]
        val end = parts[i + 1][0]
        ranks[piece.substring(start, end)] ?: 0
    }
}

/**
 * Retrieves the rank for a specific part based on its starting index and skip value.
 */
private fun getRank(
    piece: ByteString,
    ranks: Map<ByteString, Int>,
    parts: MutableList<IntArray>,
    startIndex: Int,
    skip: Int
): Int? {
    if (startIndex + skip + 2 >= parts.size) return null
    val bytes = piece.substring(parts[startIndex][0], parts[startIndex + skip + 2][0])
    return ranks[bytes]
}

/**
 * Finds the minimum rank and its corresponding index from the parts.
 */
private fun findMinRankIndex(parts: List<IntArray>): Int? {
    var minRank = Int.MAX_VALUE
    var minIdx = -1
    for (i in parts.indices) {
        if (parts[i][1] < minRank) {
            minRank = parts[i][1]
            minIdx = i
        }
    }
    return if (minRank < Int.MAX_VALUE) minIdx else null
}
