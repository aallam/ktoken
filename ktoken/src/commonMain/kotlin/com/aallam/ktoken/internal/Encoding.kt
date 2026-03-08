package com.aallam.ktoken.internal

import com.aallam.ktoken.Encoding
import okio.ByteString.Companion.encodeUtf8

/**
 * Regular expression pattern for matching P100K tokens.
 */
internal expect val regexP100K: Regex

/**
 * Regular expression pattern for matching O200K tokens.
 */
internal expect val regexO200K: Regex

/**
 * Regular expression patterns for common tokenization tasks.
 */
internal object Patterns {
    val P50K: Regex = Regex("""'s|'t|'re|'ve|'m|'ll|'d| ?[A-Za-z]+| ?[0-9]+| ?[^\sA-Za-z0-9]+|\s+(?!\S)|\s+""")
    val P100K: Regex = regexP100K
    val O200K = regexO200K
}

/**
 * Constants representing various tokens.
 */
internal object Tokens {
    /**
     * Represents the end of the text.
     */
    val ENDOFTEXT = "<|endoftext|>".encodeUtf8()

    /**
     * Represents a prefix token.
     */
    val FIM_PREFIX = "<|fim_prefix|>".encodeUtf8()

    /**
     * Represents a middle token.
     */
    val FIM_MIDDLE = "<|fim_middle|>".encodeUtf8()

    /**
     * Represents a suffix token.
     */
    val FIM_SUFFIX = "<|fim_suffix|>".encodeUtf8()

    /**
     * Represents the end of a prompt.
     */
    val ENDOFPROMPT = "<|endofprompt|>".encodeUtf8()

    /**
     * Represents the start of the text.
     */
    val STARTOFTEXT = "<|startoftext|>".encodeUtf8()

    /**
     * Represents the harmony return token.
     */
    val RETURN = "<|return|>".encodeUtf8()

    /**
     * Represents the harmony constrain token.
     */
    val CONSTRAIN = "<|constrain|>".encodeUtf8()

    /**
     * Represents the harmony channel token.
     */
    val CHANNEL = "<|channel|>".encodeUtf8()

    /**
     * Represents the harmony start token.
     */
    val START = "<|start|>".encodeUtf8()

    /**
     * Represents the harmony end token.
     */
    val END = "<|end|>".encodeUtf8()

    /**
     * Represents the harmony message token.
     */
    val MESSAGE = "<|message|>".encodeUtf8()

    /**
     * Represents the harmony call token.
     */
    val CALL = "<|call|>".encodeUtf8()

    /**
     * Represents reserved harmony token 200000.
     */
    val RESERVED_200000 = "<|reserved_200000|>".encodeUtf8()

    /**
     * Represents reserved harmony token 200001.
     */
    val RESERVED_200001 = "<|reserved_200001|>".encodeUtf8()

    /**
     * Represents reserved harmony token 200004.
     */
    val RESERVED_200004 = "<|reserved_200004|>".encodeUtf8()

    /**
     * Represents reserved harmony token 200009.
     */
    val RESERVED_200009 = "<|reserved_200009|>".encodeUtf8()

    /**
     * Represents reserved harmony token 200010.
     */
    val RESERVED_200010 = "<|reserved_200010|>".encodeUtf8()

    /**
     * Represents reserved harmony token 200011.
     */
    val RESERVED_200011 = "<|reserved_200011|>".encodeUtf8()

    /**
     * Represents a reserved harmony token with the given [id].
     */
    fun reserved(id: Int) = "<|reserved_$id|>".encodeUtf8()
}

/**
 * Mapping of model names to their corresponding encoding settings.
 */
internal val modelToEncoding: Map<String, Encoding> = mapOf(
    // reasoning
    "o1" to Encoding.O200K_BASE,
    "o3" to Encoding.O200K_BASE,
    "o4-mini" to Encoding.O200K_BASE,
    // chat
    "gpt-5" to Encoding.O200K_BASE,
    "gpt-4.1" to Encoding.O200K_BASE,
    "gpt-4o" to Encoding.O200K_BASE,
    "gpt-4" to Encoding.CL100K_BASE,
    "gpt-3.5-turbo" to Encoding.CL100K_BASE,
    "gpt-35-turbo" to Encoding.CL100K_BASE,  // Azure deployment name
    // base
    "davinci-002" to Encoding.CL100K_BASE,
    "babbage-002" to Encoding.CL100K_BASE,
    // embeddings
    "text-embedding-ada-002" to Encoding.CL100K_BASE,
    // DEPRECATED MODELS
    // text (DEPRECATED)
    "text-davinci-003" to Encoding.R50K_BASE,
    "text-davinci-002" to Encoding.R50K_BASE,
    "text-davinci-001" to Encoding.R50K_BASE,
    "text-curie-001" to Encoding.R50K_BASE,
    "text-babbage-001" to Encoding.R50K_BASE,
    "text-ada-001" to Encoding.R50K_BASE,
    "davinci" to Encoding.R50K_BASE,
    "curie" to Encoding.R50K_BASE,
    "babbage" to Encoding.R50K_BASE,
    "ada" to Encoding.R50K_BASE,
    // code (DEPRECATED)
    "code-davinci-002" to Encoding.R50K_BASE,
    "code-davinci-001" to Encoding.R50K_BASE,
    "code-cushman-002" to Encoding.R50K_BASE,
    "code-cushman-001" to Encoding.R50K_BASE,
    "davinci-codex" to Encoding.R50K_BASE,
    "cushman-codex" to Encoding.R50K_BASE,
    // edit (DEPRECATED)
    "text-davinci-edit-001" to Encoding.P50K_EDIT,
    "code-davinci-edit-001" to Encoding.P50K_EDIT,
    // old embeddings (DEPRECATED)
    "text-similarity-davinci-001" to Encoding.R50K_BASE,
    "text-similarity-curie-001" to Encoding.R50K_BASE,
    "text-similarity-babbage-001" to Encoding.R50K_BASE,
    "text-similarity-ada-001" to Encoding.R50K_BASE,
    "text-search-davinci-doc-001" to Encoding.R50K_BASE,
    "text-search-curie-doc-001" to Encoding.R50K_BASE,
    "text-search-babbage-doc-001" to Encoding.R50K_BASE,
    "text-search-ada-doc-001" to Encoding.R50K_BASE,
    "code-search-babbage-code-001" to Encoding.R50K_BASE,
    "code-search-ada-code-001" to Encoding.R50K_BASE,
)

/**
 * Mapping of model prefixes to their corresponding encoding settings.
 */
internal val modelPrefixToEncoding = mapOf(
    // reasoning
    "o1-" to Encoding.O200K_BASE,
    "o3-" to Encoding.O200K_BASE,
    "o4-mini-" to Encoding.O200K_BASE,
    // chat
    "gpt-5-" to Encoding.O200K_BASE,
    "gpt-4.5-" to Encoding.O200K_BASE,
    "gpt-4.1-" to Encoding.O200K_BASE,
    "chatgpt-4o-" to Encoding.O200K_BASE,
    "gpt-4o-" to Encoding.O200K_BASE,
    "gpt-oss-" to Encoding.O200K_HARMONY,
    "gpt-4-" to Encoding.CL100K_BASE,  // e.g., gpt-4-0314, etc., plus gpt-4-32k
    "gpt-3.5-turbo-" to Encoding.CL100K_BASE,  // e.g, gpt-3.5-turbo-0301, -0401, etc.
    "gpt-35-turbo-" to Encoding.CL100K_BASE,  // Azure deployment name
    // fine-tuned
    "ft:gpt-4o" to Encoding.O200K_BASE,
    "ft:gpt-4" to Encoding.CL100K_BASE,
    "ft:gpt-3.5-turbo" to Encoding.CL100K_BASE,
    "ft:davinci-002" to Encoding.CL100K_BASE,
    "ft:babbage-002" to Encoding.CL100K_BASE,
)
