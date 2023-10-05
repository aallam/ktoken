package com.aallam.kotoken

import com.aallam.kotoken.internal.RegexString
import com.aallam.kotoken.loader.BpeLoader
import com.aallam.kotoken.loader.RemoteBpeLoader
import okio.ByteString.Companion.encodeUtf8

data class Tiktoken(
    val bpe: CoreBPE,
    val bpeEncoding: Encoding,
    val specialTokensSet: Set<String>,
) {

    fun encode(
        text: String,
        allowedSpecial: Set<String> = emptySet(),
        disallowedSpecial: Set<String> = emptySet(),
    ): IntArray {
        val allowedSpecialSet = when {
            allowedSpecial.size == 1 && allowedSpecial.first() == "all" -> specialTokensSet
            else -> allowedSpecial
        }

        val disallowedSpecialSet = when {
            disallowedSpecial.size == 1 && disallowedSpecial.first() == "all" -> specialTokensSet - allowedSpecialSet
            else -> disallowedSpecial
        }

        if (disallowedSpecialSet.isNotEmpty()) {
            val specialRegex = RegexString.regexSpecialTokens(disallowedSpecialSet)
            val match = RegexString.findMatch(text, specialRegex)
            require(match.isEmpty()) { "text contains disallowed special token: $match" }
        }
        return bpe.encodeNative(text, allowedSpecialSet.map { it.encodeUtf8() }.toSet())
    }

    fun decode(tokens: IntArray): String {
        return bpe.decodeNative(tokens)
    }

    companion object {
        suspend fun getEncoding(encodingName: EncodingName, loader: BpeLoader = RemoteBpeLoader()): Tiktoken {
            val encoding = Encoding.getEncoding(encodingName, loader)
            return build(encoding)
        }

        suspend fun getEncodingForModel(modelName: String, loader: BpeLoader = RemoteBpeLoader()): Tiktoken {
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
            return Tiktoken(bpe = coreBPE, bpeEncoding = encoding, specialTokensSet = specialTokensSet)
        }
    }
}
