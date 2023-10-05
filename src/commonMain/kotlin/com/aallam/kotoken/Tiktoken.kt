package com.aallam.kotoken

import com.aallam.kotoken.internal.CoreBPE
import com.aallam.kotoken.internal.Encoding
import com.aallam.kotoken.internal.TiktokenEncoder
import com.aallam.kotoken.loader.BpeLoader
import com.aallam.kotoken.loader.RemoteBpeLoader

public interface Tiktoken {

    public fun encode(
        text: String,
        allowedSpecial: Set<String> = emptySet(),
        disallowedSpecial: Set<String> = emptySet(),
    ): IntArray

    public fun decode(tokens: IntArray): String

    public companion object {

        public suspend fun getEncoding(encodingName: EncodingName, loader: BpeLoader = RemoteBpeLoader()): Tiktoken {
            val encoding = Encoding.getEncoding(encodingName, loader)
            return build(encoding)
        }

        public suspend fun getEncodingForModel(modelName: String, loader: BpeLoader = RemoteBpeLoader()): Tiktoken {
            val encoding = Encoding.getEncodingForModel(modelName, loader)
            return build(encoding)
        }

        private fun build(encoding: Encoding): Tiktoken {
            val coreBPE = CoreBPE.create(
                encoder = encoding.mergeableRanks,
                specialTokensEncoder = encoding.specialTokens,
                pattern = encoding.pattern
            )
            val specialTokensSet = encoding.specialTokens.keys.map { it.utf8() }.toSet()
            return TiktokenEncoder(bpe = coreBPE, specialTokensSet = specialTokensSet)
        }
    }
}
