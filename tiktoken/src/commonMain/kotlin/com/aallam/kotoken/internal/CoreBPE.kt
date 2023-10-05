package com.aallam.kotoken.internal

import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8

internal class CoreBPE(
    val encoder: Map<ByteString, Int>,
    val decoder: Map<Int, ByteString>,
    val specialTokensEncoder: Map<ByteString, Int>,
    val specialTokensDecoder: Map<Int, ByteString>,
    val tlRegex: Regex,
    val tlSpecialRegex: Regex,
    val sortedTokenBytes: List<ByteString>
) {

    fun decodeNative(tokens: IntArray): String {
        val buffer = Buffer()
        for (token in tokens) {
            val tokenString = decoder[token] ?: specialTokensDecoder[token]
            if (tokenString != null) {
                buffer.write(tokenString)
            }
        }
        val byteString = buffer.readByteString()
        return byteString.utf8()
    }

    fun encodeNative(text: String, allowedSpecial: Set<ByteString>): IntArray {
        val ret = mutableListOf<Int>()
        val textBytes = text.encodeUtf8()

        var start = 0
        loop@ while (true) {
            var nextSpecial: IntArray?
            var startFind = start
            while (true) {
                val temp = cutBytes(textBytes, startFind, textBytes.size).utf8()
                nextSpecial = findIndex(temp, tlSpecialRegex)
                if (nextSpecial != null) {
                    val token = cutBytes(textBytes, startFind + nextSpecial[0], startFind + nextSpecial[1])
                    if (allowedSpecial.contains(token)) {
                        break
                    }
                    startFind += nextSpecial[1]
                } else {
                    break
                }
            }

            var end = textBytes.size
            if (nextSpecial != null) {
                end = start + nextSpecial[0]
            }

            val bytes = cutBytes(textBytes, start, end)
            val matchIndex = findAllIndexes(bytes.utf8(), tlRegex)
            for (mat in matchIndex) {
                val piece = cutBytes(textBytes, start + mat[0], start + mat[1])
                val token = encoder[piece]
                if (token != null) {
                    ret.add(token)
                    continue
                }
                val tokens = bytePairEncode(piece, encoder)
                ret.addAll(tokens.toList())
            }

            if (nextSpecial != null) {
                val temp = cutBytes(textBytes, start + nextSpecial[0], start + nextSpecial[1])
                val token = specialTokensEncoder.getValue(temp)
                ret.add(token)
                start += nextSpecial[1]
            } else {
                break@loop
            }
        }

        return ret.toIntArray()
    }

    private fun cutBytes(byteString: ByteString, start: Int, end: Int): ByteString {
        val string = byteString.utf8()
        val startIndex = start.coerceAtLeast(0)
        val endIndex = end.coerceAtMost(string.length)
        val substring = string.substring(startIndex, endIndex)
        return substring.encodeUtf8()
    }

    companion object {

        fun create(
            encoder: Map<ByteString, Int>,
            specialTokensEncoder: Map<ByteString, Int>,
            pattern: String
        ): CoreBPE {
            val regex = Regex(pattern)
            val specialRegexStrs = specialTokensEncoder.keys.map { Regex.escape(it.utf8()) }
            val specialRegex = Regex(specialRegexStrs.joinToString("|"))
            val decoder = encoder.map { (key, value) -> value to key }.toMap()
            val specialTokensDecoder = specialTokensEncoder.entries.associateBy({ it.value }, { it.key })
            val sortedTokenBytes = encoder.keys
                .sortedWith { a, b ->
                    val minLength = minOf(a.size, b.size)
                    for (index in 0..<minLength) {
                        val cmp = a[index].compareTo(b[index])
                        if (cmp != 0) return@sortedWith cmp
                    }
                    a.size - b.size
                }
            return CoreBPE(
                encoder = encoder,
                specialTokensEncoder = specialTokensEncoder,
                tlRegex = regex,
                tlSpecialRegex = specialRegex,
                decoder = decoder,
                specialTokensDecoder = specialTokensDecoder,
                sortedTokenBytes = sortedTokenBytes
            )
        }
    }
}
