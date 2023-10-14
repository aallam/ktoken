package com.aallam.ktoken

import com.aallam.ktoken.internal.EncodingStore
import com.aallam.ktoken.internal.modelPrefixToEncoding
import com.aallam.ktoken.internal.modelToEncoding
import com.aallam.ktoken.loader.BpeLoader
import okio.ByteString

/**
 * Manages configurations for token encoding, providing the settings and mappings needed to perform
 * byte pair encoding (BPE) and handle special tokens.
 */
public class EncodingConfig(

    /**
     * A regex pattern string that is used to split the input text.
     */
    public val pattern: Regex,

    /**
     * A dictionary mapping mergeable token bytes to their ranks. The ranks must correspond to merge priority.
     */
    public val mergeableRanks: Map<ByteString, Int>,

    /**
     * A dictionary mapping special token strings to their token values.
     */
    public val specialTokens: Map<ByteString, Int>,

    /**
     * The number of tokens in the vocabulary.
     * If provided, it is checked that the number of mergeable tokens and special tokens is equal to this number.
     */
    public val explicitNVocab: Int? = null,
) {
    init {
        if (explicitNVocab != null) {
            val totalCount = mergeableRanks.size + specialTokens.size
            require(totalCount == explicitNVocab) { "the expected number of tokens in the vocabulary is incorrect, expected: $explicitNVocab, actual: $totalCount" }
        }
    }

    public companion object {

        /**
         * Encodings cache store.
         */
        private val cache = EncodingStore()

        /**
         * Asynchronously obtains an instance of [EncodingConfig].
         *
         * @param encoding The encoding scheme.
         * @param loader The loader to be used for obtaining the encoding scheme.
         * @return An instance of [EncodingConfig].
         */
        public suspend fun of(encoding: Encoding, loader: BpeLoader): EncodingConfig {
            return cache.get(encoding) ?: run {
                val ranks = loader.loadEncoding(encoding.file)
                encoding.encodingConfig(ranks).also { cache.put(encoding, it) }
            }
        }

        /**
         * Asynchronously obtains an instance of [EncodingConfig].
         *
         * @param model The name of the model whose encoding scheme is to be used.
         * @param loader The loader to be used for obtaining the encoding scheme.
         * @return An instance of [EncodingConfig] for the specified model.
         */
        public suspend fun of(model: String, loader: BpeLoader): EncodingConfig {
            val encoding = modelToEncoding[model]
                ?: modelPrefixToEncoding
                    .entries.firstOrNull { (prefix, _) -> model.startsWith(prefix) }?.value
                ?: error("no encoding for model $model")
            return of(encoding, loader)
        }
    }
}
