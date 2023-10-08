package com.aallam.ktoken

import com.aallam.ktoken.internal.getEncoding
import com.aallam.ktoken.internal.modelPrefixToEncoding
import com.aallam.ktoken.internal.modelToEncoding
import com.aallam.ktoken.loader.BpeLoader
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.ByteString

public class Encoding(
    /**
     * The name of the encoding. It should be clear from the name of the encoding
     * what behaviour to expect, in particular, encodings with different special tokens
     * should have different names.
     */
    public val name: EncodingName,

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
        private val encodingMap = mutableMapOf<EncodingName, Encoding>()
        private val mutex = Mutex()

        /**
         * Asynchronously obtains an instance of [Encoding].
         *
         * @param encodingName The name of the encoding scheme.
         * @param loader The loader to be used for obtaining the encoding scheme.
         * @return An instance of [Encoding].
         */
        public suspend fun getEncoding(encodingName: EncodingName, loader: BpeLoader): Encoding {
            return mutex.withLock {
                encodingMap[encodingName] ?: loader.getEncoding(encodingName).also { encodingMap[encodingName] = it }
            }
        }

        /**
         * Asynchronously obtains an instance of [Encoding].
         *
         * @param model The name of the model whose encoding scheme is to be used.
         * @param loader The loader to be used for obtaining the encoding scheme.
         * @return An instance of [Encoding] for the specified model.
         */
        public suspend fun getEncodingForModel(model: String, loader: BpeLoader): Encoding {
            val encodingName = modelToEncoding[model]
                ?: modelPrefixToEncoding
                    .entries.firstOrNull { (prefix, _) -> model.startsWith(prefix) }?.value
                ?: error("no encoding for model $model")
            return getEncoding(encodingName, loader)
        }
    }
}
