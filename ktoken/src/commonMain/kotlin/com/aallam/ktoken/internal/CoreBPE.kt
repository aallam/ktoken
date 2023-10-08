package com.aallam.ktoken.internal

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
        val piece = decoder[token] ?: specialTokensDecoder[token] ?: error("token missing from the vocabulary: $token")
        return piece.utf8()
    }

    fun encode(text: String, allowedSpecial: Set<String>): List<Int> {
        val encodedTokens = mutableListOf<Int>()
        var start = 0
        while (true) {
            val nextSpecial = findNextSpecialToken(start, text, allowedSpecial)
            val end = nextSpecial?.first ?: text.length
            val section = text.substring(start, end)
            val ranges = findAllRanges(section, tlRegex)

            // add encoded tokens
            for (range in ranges) {
                val piece = section.substring(range).encodeUtf8()
                val token = encoder[piece]
                if (token != null) {
                    encodedTokens.add(token)
                } else {
                    val tokens = bytePairEncode(piece, encoder)
                    encodedTokens.addAll(tokens.toList())
                }
            }

            // add special tokens; end looping if no special token
            if (nextSpecial != null) {
                val specialToken = text.substring(nextSpecial)
                val encoded = specialToken.encodeUtf8()
                val token = specialTokensEncoder[encoded] ?: error("token missing from the vocabulary: $specialToken")
                encodedTokens.add(token)
                start = nextSpecial.last + 1
            } else {
                break
            }
        }
        return encodedTokens
    }

    /**
     * Find the next allowed special token, if any.
     */
    private fun findNextSpecialToken(
        start: Int,
        text: String,
        allowedSpecial: Set<String>
    ): IntRange? {
        var nextSpecial: IntRange?
        var startFind = start
        while (true) {
            nextSpecial = findIndex(text, startFind, tlSpecialRegex)
            if (nextSpecial != null) {
                val token = text.substring(nextSpecial)
                if (allowedSpecial.contains(token)) {
                    break
                }
                startFind = nextSpecial.first + 1
            } else {
                break
            }
        }
        return nextSpecial
    }

    fun encodeSingleToken(text: String): Int {
        val piece = text.encodeUtf8()
        return encoder[piece] ?: specialTokensEncoder[piece] ?: error("token missing from the vocabulary: $text")
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
