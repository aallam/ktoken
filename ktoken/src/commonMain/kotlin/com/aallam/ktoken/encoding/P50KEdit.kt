package com.aallam.ktoken.encoding

import com.aallam.ktoken.Encoding
import com.aallam.ktoken.EncodingConfig
import com.aallam.ktoken.internal.Patterns
import com.aallam.ktoken.internal.Tokens
import okio.ByteString

/**
 * Default configuration of `p50k_edit` encoding.
 */
public data class P50KEdit(
    override val file: String = "p50k_base.tiktoken"
) : Encoding {

    override fun encodingConfig(ranks: Map<ByteString, Int>): EncodingConfig {
        val specialTokens = mapOf(
            Tokens.ENDOFTEXT to 50256,
            Tokens.FIM_PREFIX to 50281,
            Tokens.FIM_MIDDLE to 50282,
            Tokens.FIM_SUFFIX to 50283
        )
        return EncodingConfig(
            pattern = Patterns.P50K,
            mergeableRanks = ranks,
            specialTokens = specialTokens,
        )
    }
}
