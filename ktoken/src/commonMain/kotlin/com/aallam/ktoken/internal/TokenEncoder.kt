package com.aallam.ktoken.internal

import com.aallam.ktoken.Tokenizer

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
        return bpe.encode(text, allowedSpecialSet)
    }

    override fun encodeSingleToken(text: String): Int {
        return bpe.encodeSingleToken(text)
    }

    override fun decode(tokens: List<Int>): String {
        return bpe.decode(tokens)
    }

    override fun decode(token: Int): String {
        return bpe.decode(token)
    }
}
