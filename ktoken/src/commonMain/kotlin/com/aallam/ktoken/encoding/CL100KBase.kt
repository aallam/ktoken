package com.aallam.ktoken.encoding

import com.aallam.ktoken.Encoding
import com.aallam.ktoken.EncodingConfig
import com.aallam.ktoken.internal.Patterns
import com.aallam.ktoken.internal.Tokens
import okio.ByteString

/**
 * Default configuration of `cl100k_base` encoding.
 */
public data class CL100KBase(
    override val file: String = "cl100k_base.tiktoken"
) : Encoding {

    override fun encodingConfig(ranks: Map<ByteString, Int>): EncodingConfig {
        val specialTokens = mapOf(
            Tokens.ENDOFTEXT to 100257,
            Tokens.FIM_PREFIX to 100258,
            Tokens.FIM_MIDDLE to 100259,
            Tokens.FIM_SUFFIX to 100260,
            Tokens.ENDOFPROMPT to 100276,
        )
        return EncodingConfig(
            pattern = Patterns.P100K,
            mergeableRanks = ranks,
            specialTokens = specialTokens,
        )
    }
}
