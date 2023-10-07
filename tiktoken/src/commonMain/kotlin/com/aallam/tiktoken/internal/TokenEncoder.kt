package com.aallam.tiktoken.internal

import com.aallam.tiktoken.Tokenizer
import okio.ByteString.Companion.encodeUtf8

internal class TokenEncoder(
    val bpe: CoreBPE,
    val specialTokensSet: Set<String>,
) : Tokenizer {

    override fun encode(
        text: String,
        allowedSpecial: Set<String>,
        disallowedSpecial: Set<String>,
    ): List<Int> {
        val allowedSpecialSet = when {
            allowedSpecial.size == 1 && allowedSpecial.first() == "all" -> specialTokensSet
            else -> allowedSpecial
        }

        val disallowedSpecialSet = when {
            disallowedSpecial.size == 1 && disallowedSpecial.first() == "all" -> specialTokensSet - allowedSpecialSet
            else -> disallowedSpecial
        }

        if (disallowedSpecialSet.isNotEmpty()) {
            val specialRegex = regexSpecialTokens(disallowedSpecialSet)
            val match = findMatch(text, specialRegex)
            require(match.isEmpty()) { "text contains disallowed special token: $match" }
        }
        return bpe.encodeNative(text, allowedSpecialSet.map { it.encodeUtf8() }.toSet())
    }

    override fun decode(tokens: List<Int>): String {
        return bpe.decodeNative(tokens)
    }
}
