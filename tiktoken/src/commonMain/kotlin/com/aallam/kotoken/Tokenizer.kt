package com.aallam.kotoken

import com.aallam.kotoken.internal.CoreBPE
import com.aallam.kotoken.internal.Encoding
import com.aallam.kotoken.internal.TokenEncoder
import com.aallam.kotoken.loader.BpeLoader
import com.aallam.kotoken.loader.RemoteBpeLoader

/**
 * A public interface for tokenization and de-tokenization tasks, especially tailored for handling text encoding and decoding.
 * The primary operations include [encode] to convert text to a sequence of integers (tokens), and [decode] to convert a sequence of integers back to text.
 *
 * The companion object provides methods to obtain an instance of [Tokenizer] with specified encodings, either by encoding name or model name.
 */
public interface Tokenizer {

    /**
     * Encodes the given text into a sequence of integers (tokens) based on the underlying encoding scheme.
     *
     * @param text The text to be encoded.
     * @param allowedSpecial A set of special tokens that are permissible during the encoding process.
     * @param disallowedSpecial A set of special tokens that are not allowed during the encoding process.
     * @return An array of integers representing the encoded text.
     */
    public fun encode(
        text: String,
        allowedSpecial: Set<String> = emptySet(),
        disallowedSpecial: Set<String> = emptySet(),
    ): List<Int>

    /**
     * Decodes the given sequence of integers (tokens) back into text based on the underlying encoding scheme.
     *
     * @param tokens The array of integers to be decoded.
     * @return The decoded text.
     */
    public fun decode(tokens: List<Int>): String

    public companion object {

        /**
         * Asynchronously obtains an instance of [Tokenizer] with the specified encoding scheme.
         *
         * @param encodingName The name of the encoding scheme to be used.
         * @param loader The loader to be used for obtaining the encoding scheme.
         * @return An instance of [Tokenizer] with the specified encoding.
         */
        public suspend fun getEncoding(encodingName: EncodingName, loader: BpeLoader = RemoteBpeLoader()): Tokenizer {
            val encoding = Encoding.getEncoding(encodingName, loader)
            return build(encoding)
        }

        /**
         * Asynchronously obtains an instance of `Tiktoken` for the specified model name.
         *
         * @param model The name of the model whose encoding scheme is to be used.
         * @param loader The loader to be used for obtaining the encoding scheme.
         * @return An instance of [Tokenizer] with the encoding scheme for the specified model.
         */
        public suspend fun getEncodingForModel(model: String, loader: BpeLoader = RemoteBpeLoader()): Tokenizer {
            val encoding = Encoding.getEncodingForModel(model, loader)
            return build(encoding)
        }

        /**
         * Builds and returns an instance of [Tokenizer] based on the specified [Encoding].
         *
         * @param encoding The [Encoding] object representing the encoding scheme to be used.
         * @return An instance of [Tokenizer].
         */
        private fun build(encoding: Encoding): Tokenizer {
            val coreBPE = CoreBPE.create(
                encoder = encoding.mergeableRanks,
                specialTokensEncoder = encoding.specialTokens,
                pattern = encoding.pattern
            )
            val specialTokensSet = encoding.specialTokens.keys.map { it.utf8() }.toSet()
            return TokenEncoder(bpe = coreBPE, specialTokensSet = specialTokensSet)
        }
    }
}
