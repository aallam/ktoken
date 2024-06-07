package com.aallam.ktoken.encoding

import com.aallam.ktoken.Encoding
import com.aallam.ktoken.EncodingConfig
import com.aallam.ktoken.internal.Patterns
import com.aallam.ktoken.internal.Tokens
import okio.ByteString

/**
 * Default configuration of `cl100k_base` encoding.
 */
public data class O200KBase(
    override val file: String = "o200k_base.tiktoken"
) : Encoding {

    override fun encodingConfig(ranks: Map<ByteString, Int>): EncodingConfig {
        val specialTokens = mapOf(
            Tokens.ENDOFTEXT to 199999,
            Tokens.ENDOFPROMPT to 200018,
        )
        return EncodingConfig(
            pattern = Patterns.O200K,
            mergeableRanks = ranks,
            specialTokens = specialTokens,
        )
    }
}
