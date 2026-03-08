package com.aallam.ktoken.encoding

import com.aallam.ktoken.Encoding
import com.aallam.ktoken.EncodingConfig
import com.aallam.ktoken.internal.Patterns
import com.aallam.ktoken.internal.Tokens
import okio.ByteString

/**
 * Default configuration of `o200k_harmony` encoding.
 */
public data class O200KHarmony(
    override val file: String = "o200k_base.tiktoken"
) : Encoding {

    override fun encodingConfig(ranks: Map<ByteString, Int>): EncodingConfig {
        val specialTokens = mutableMapOf(
            Tokens.ENDOFTEXT to 199999,
            Tokens.ENDOFPROMPT to 200018,
            Tokens.STARTOFTEXT to 199998,
            Tokens.ENDOFTEXT to 199999,
            Tokens.RESERVED_200000 to 200000,
            Tokens.RESERVED_200001 to 200001,
            Tokens.RETURN to 200002,
            Tokens.CONSTRAIN to 200003,
            Tokens.RESERVED_200004 to 200004,
            Tokens.CHANNEL to 200005,
            Tokens.START to 200006,
            Tokens.END to 200007,
            Tokens.MESSAGE to 200008,
            Tokens.RESERVED_200009 to 200009,
            Tokens.RESERVED_200010 to 200010,
            Tokens.RESERVED_200011 to 200011,
            Tokens.CALL to 200012,
        )

        for (i in 200013..201087) {
            specialTokens[Tokens.reserved(i)] = i
        }

        return EncodingConfig(
            pattern = Patterns.O200K,
            mergeableRanks = ranks,
            specialTokens = specialTokens,
        )
    }
}
