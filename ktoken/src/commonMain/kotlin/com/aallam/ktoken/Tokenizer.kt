package com.aallam.ktoken

import com.aallam.ktoken.internal.CoreBPE
import com.aallam.ktoken.internal.TokenEncoder
import com.aallam.ktoken.internal.defaultPbeLoader
import com.aallam.ktoken.loader.BpeLoader

/**
 * A public interface for tokenization and de-tokenization tasks, especially tailored for handling text encoding and decoding.
 * The primary operations include [encode] to convert text to a sequence of integers (tokens), and [decode] to convert a sequence of integers back to text.
 *
 * The companion object provides methods to obtain an instance of [Tokenizer] with specified encodings, either by encoding name or model name.
 */
public interface Tokenizer {

    /**
     *  Encodes a string into tokens.
     *
     *  Special tokens are artificial tokens used to unlock capabilities from a model,
     *  such as fill-in-the-middle. So we want to be careful about accidentally encoding special
     *  tokens, since they can be used to trick a model into doing something we don't want it to do.
     *
     *  Hence, by default, encode will raise an error if it encounters text that corresponds
     *  to a special token. This can be controlled on a per-token level using the [allowedSpecial]
     *  and `disallowed_special` parameters. In particular:
     *  - Setting [disallowedSpecial] to empty set will prevent this function from raising exceptions and
     *    cause all text corresponding to special tokens to be encoded as natural text.
     *  - Setting [allowedSpecial] to "all" will cause this function to treat all text
     *    corresponding to special tokens to be encoded as special tokens.
     *
     *  ```
     *  >>> tokenizer.encode("hello world")
     *  [31373, 995]
     *  >>> tokenizer.encode("<|endoftext|>", allowedSpecial = setOf("<|endoftext|>"))
     *  [50256]
     *  >>> tokenizer.encode("<|endoftext|>", allowedSpecial= setOf("all"))
     *  [50256]
     *  >>> tokenizer.encode("<|endoftext|>")
     *  # Raises exception
     *  >>> tokenizer.encode("<|endoftext|>", disallowedSpecial= emptySet())
     *  [27, 91, 437, 1659, 5239, 91, 29]
     *  ```
     *
     * @param text The text to be encoded.
     * @param allowedSpecial A set of special tokens that are permissible during the encoding process.
     * @param disallowedSpecial A set of special tokens that are not allowed during the encoding process.
     * @return An array of integers representing the encoded text.
     */
    public fun encode(
        text: String,
        allowedSpecial: Set<String> = emptySet(),
        disallowedSpecial: Set<String> = setOf("all"),
    ): List<Int>

    /**
     * Encodes text corresponding to a single token to its token value.
     *
     * NOTE: this will encode all special tokens.
     *
     * Raises an exception if the token is not in the vocabulary.
     *
     * ```
     * >>> tokenizer.encodeSingleToken("hello")
     * 31373
     * ```
     */
    public fun encodeSingleToken(text: String): Int

    /**
     * Decodes the given sequence of integers (tokens) back into text based on the underlying encoding scheme.
     *
     * @param tokens The array of integers to be decoded.
     * @return The decoded text.
     */
    public fun decode(tokens: List<Int>): String

    /**
     * Decodes a token into bytes.
     *
     * NOTE: this will decode all special tokens.
     *
     * Raises an exception if the token is not in the vocabulary.
     */
    public fun decode(token: Int): String

    public companion object {

        /**
         * Asynchronously obtains an instance of [Tokenizer] with the specified encoding scheme.
         *
         * @param encodingName The name of the encoding scheme to be used.
         * @param loader The loader to be used for obtaining the encoding scheme.
         * @return An instance of [Tokenizer] with the specified encoding.
         */
        public suspend fun encoding(encodingName: EncodingName, loader: BpeLoader = defaultPbeLoader()): Tokenizer {
            val encoding = Encoding.getEncoding(encodingName, loader)
            return create(encoding)
        }

        /**
         * Asynchronously obtains an instance of [Tokenizer] for the specified model name.
         *
         * @param model The name of the model whose encoding scheme is to be used.
         * @param loader The loader to be used for obtaining the encoding scheme.
         * @return An instance of [Tokenizer] with the encoding scheme for the specified model.
         */
        public suspend fun encodingForModel(model: String, loader: BpeLoader = defaultPbeLoader()): Tokenizer {
            val encoding = Encoding.getEncodingForModel(model, loader)
            return create(encoding)
        }

        /**
         * Builds and returns an instance of [Tokenizer] based on the specified [Encoding].
         *
         * @param encoding The [Encoding] object representing the encoding scheme to be used.
         * @return An instance of [Tokenizer].
         */
        public fun create(encoding: Encoding): Tokenizer {
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
