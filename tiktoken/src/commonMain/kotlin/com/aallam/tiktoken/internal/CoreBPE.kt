package com.aallam.tiktoken.internal

import com.aallam.tiktoken.internal.extension.slide
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
) {

    fun decode(tokens: List<Int>): String {
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

    fun decode(token: Int): String {
        val piece =  decoder[token] ?: specialTokensDecoder[token] ?: error("token missing from the vocabulary: $token")
        return piece.utf8()
    }

    fun encode(text: String, allowedSpecial: Set<ByteString>): List<Int> {
        val encodedTokens = mutableListOf<Int>()
        val textBytes = text.encodeUtf8()

        var start = 0
        loop@ while (true) {
            var nextSpecial: IntRange?
            var startFind = start
            while (true) {
                val temp = cutBytes(textBytes, startFind..textBytes.size)
                nextSpecial = findIndex(temp, tlSpecialRegex)
                if (nextSpecial != null) {
                    val token = cutBytes(textBytes, nextSpecial.slide(startFind))
                    if (allowedSpecial.contains(token)) {
                        break
                    }
                    startFind += nextSpecial.last
                } else {
                    break
                }
            }

            var end = textBytes.size
            if (nextSpecial != null) {
                end = start + nextSpecial.first
            }

            val bytes = cutBytes(textBytes, start..end)
            val matchIndex = findAllIndexes(bytes, tlRegex)
            for (match in matchIndex) {
                val piece = cutBytes(textBytes, match.slide(start))
                val token = encoder[piece]
                if (token != null) {
                    encodedTokens.add(token)
                    continue
                }
                val tokens = bytePairEncode(piece, encoder)
                encodedTokens.addAll(tokens.toList())
            }

            if (nextSpecial != null) {
                val temp = cutBytes(textBytes, nextSpecial.slide(start))
                val token = specialTokensEncoder[temp] ?: error("token missing from the vocabulary: $temp")
                encodedTokens.add(token)
                start += nextSpecial.last
            } else {
                break@loop
            }
        }
        return encodedTokens
    }

    fun encodeSingleToken(text: String): Int {
        val piece = text.encodeUtf8()
        return encoder[piece] ?: specialTokensEncoder[piece] ?: error("token missing from the vocabulary: $text")
    }

    private fun cutBytes(byteString: ByteString, range: IntRange): ByteString {
        val string = byteString.utf8()
        val startIndex = range.first.coerceAtLeast(0)
        val endIndex = range.last.coerceAtMost(string.length)
        val substring = string.substring(startIndex, endIndex)
        return substring.encodeUtf8()
    }

    private fun cutBytes2(byteString: ByteString, range: IntRange): ByteString {
        val startIndex = range.first.coerceAtLeast(0)
        val endIndex = range.last.coerceAtMost(byteString.size - 1)
        return byteString.substring(startIndex, endIndex)
    }

    companion object {

        fun create(
            encoder: Map<ByteString, Int>,
            specialTokensEncoder: Map<ByteString, Int>,
            pattern: Regex
        ): CoreBPE {
            val specialRegexStrs = specialTokensEncoder.keys.map { Regex.escape(it.utf8()) }
            val specialRegex = Regex(specialRegexStrs.joinToString("|"))
            val decoder = encoder.map { (key, value) -> value to key }.toMap()
            val specialTokensDecoder = specialTokensEncoder.entries.associateBy({ it.value }, { it.key })
            return CoreBPE(
                encoder = encoder,
                specialTokensEncoder = specialTokensEncoder,
                tlRegex = pattern,
                tlSpecialRegex = specialRegex,
                decoder = decoder,
                specialTokensDecoder = specialTokensDecoder,
            )
        }
    }
}
