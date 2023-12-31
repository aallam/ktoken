package com.aallam.ktoken.encoding

import com.aallam.ktoken.Encoding
import com.aallam.ktoken.EncodingConfig
import com.aallam.ktoken.internal.Patterns
import com.aallam.ktoken.internal.Tokens
import okio.ByteString

/**
 * Default configuration of `p50k_base` encoding.
 */
public data class P50KBase(
    override val file: String = "p50k_base.tiktoken"
) : Encoding {
    override fun encodingConfig(ranks: Map<ByteString, Int>): EncodingConfig {
        val specialTokens = mapOf(Tokens.ENDOFTEXT to 50256)
        require(ranks.size + specialTokens.size == 50281)
        return EncodingConfig(
            pattern = Patterns.P50K,
            mergeableRanks = ranks,
            specialTokens = mapOf(Tokens.ENDOFTEXT to 50256),
            explicitNVocab = 50281,
        )
    }
}
