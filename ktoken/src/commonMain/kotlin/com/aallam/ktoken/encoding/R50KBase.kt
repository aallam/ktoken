package com.aallam.ktoken.encoding

import com.aallam.ktoken.Encoding
import com.aallam.ktoken.TokenEncoding
import com.aallam.ktoken.internal.Patterns
import com.aallam.ktoken.internal.Tokens
import okio.ByteString

/**
 * Default configuration of `r50k_base` encoding.
 */
public data class R50KBase(
    override val file: String = "r50k_base.tiktoken"
) : Encoding {

    override fun encoding(ranks: Map<ByteString, Int>): TokenEncoding {
        return TokenEncoding(
            mergeableRanks = ranks,
            pattern = Patterns.P50K,
            specialTokens = mapOf(Tokens.ENDOFTEXT to 50256),
            explicitNVocab = 50257,
        )
    }
}
