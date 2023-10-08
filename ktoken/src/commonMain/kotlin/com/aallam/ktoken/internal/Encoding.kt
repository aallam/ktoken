package com.aallam.ktoken.internal

import com.aallam.ktoken.Encoding
import com.aallam.ktoken.EncodingName
import com.aallam.ktoken.loader.BpeLoader
import okio.ByteString.Companion.encodeUtf8

internal suspend fun BpeLoader.getEncoding(encodingName: EncodingName): Encoding {
    return when (encodingName) {
        EncodingName.CL100K_BASE -> cl100kBase()
        EncodingName.P50K_BASE -> p50kBase()
        EncodingName.R50K_BASE -> r50kBase()
        EncodingName.P50K_EDIT -> p50kEdit()
        else -> error("Unknown encoding: $encodingName")
    }
}

internal suspend fun BpeLoader.cl100kBase(): Encoding {
    val ranks = loadEncoding(EncodingName.CL100K_BASE)
    val specialTokens = mapOf(
        Tokens.ENDOFTEXT to 100257,
        Tokens.FIM_PREFIX to 100258,
        Tokens.FIM_MIDDLE to 100259,
        Tokens.FIM_SUFFIX to 100260,
        Tokens.ENDOFPROMPT to 100276,
    )
    return Encoding(
        name = EncodingName.CL100K_BASE,
        pattern = Patterns.P100K,
        mergeableRanks = ranks,
        specialTokens = specialTokens,
    )
}

internal expect val regexP100K: Regex

internal object Patterns {
    val P50K: Regex = Regex("""'s|'t|'re|'ve|'m|'ll|'d| ?[A-Za-z]+| ?[0-9]+| ?[^\sA-Za-z0-9]+|\s+(?!\S)|\s+""")
    val P100K: Regex = regexP100K
}

internal suspend fun BpeLoader.p50kEdit(): Encoding {
    val ranks = loadEncoding(EncodingName.P50K_EDIT)
    val specialTokens = mapOf(
        Tokens.ENDOFTEXT to 50256, Tokens.FIM_PREFIX to 50281, Tokens.FIM_MIDDLE to 50282, Tokens.FIM_SUFFIX to 50283
    )
    return Encoding(
        name = EncodingName.P50K_EDIT,
        pattern = Patterns.P50K,
        mergeableRanks = ranks,
        specialTokens = specialTokens,
    )
}

internal suspend fun BpeLoader.p50kBase(): Encoding {
    val ranks = loadEncoding(EncodingName.P50K_BASE)
    val specialTokens = mapOf(Tokens.ENDOFTEXT to 50256)
    require(ranks.size + specialTokens.size == 50281)
    return Encoding(
        name = EncodingName.P50K_BASE,
        pattern = Patterns.P50K,
        mergeableRanks = ranks,
        specialTokens = mapOf(Tokens.ENDOFTEXT to 50256),
        explicitNVocab = 50281,
    )
}

internal suspend fun BpeLoader.r50kBase(): Encoding {
    val ranks = loadEncoding(EncodingName.R50K_BASE)
    return Encoding(
        name = EncodingName.R50K_BASE,
        mergeableRanks = ranks,
        pattern = Patterns.P50K,
        specialTokens = mapOf(Tokens.ENDOFTEXT to 50256),
        explicitNVocab = 50257,
    )
}

private object Tokens {
    val ENDOFTEXT = "<|endoftext|>".encodeUtf8()
    val FIM_PREFIX = "<|fim_prefix|>".encodeUtf8()
    val FIM_MIDDLE = "<|fim_middle|>".encodeUtf8()
    val FIM_SUFFIX = "<|fim_suffix|>".encodeUtf8()
    val ENDOFPROMPT = "<|endofprompt|>".encodeUtf8()
}

internal val modelToEncoding: Map<String, EncodingName> = mapOf(
    // chat
    "gpt-4" to EncodingName.CL100K_BASE,
    "gpt-3.5-turbo" to EncodingName.CL100K_BASE,
    "gpt-35-turbo" to EncodingName.CL100K_BASE,  // Azure deployment name
    // base
    "davinci-002" to EncodingName.CL100K_BASE,
    "babbage-002" to EncodingName.CL100K_BASE,
    // embeddings
    "text-embedding-ada-002" to EncodingName.CL100K_BASE,
    // DEPRECATED MODELS
    // text (DEPRECATED)
    "text-davinci-003" to EncodingName.R50K_BASE,
    "text-davinci-002" to EncodingName.R50K_BASE,
    "text-davinci-001" to EncodingName.R50K_BASE,
    "text-curie-001" to EncodingName.R50K_BASE,
    "text-babbage-001" to EncodingName.R50K_BASE,
    "text-ada-001" to EncodingName.R50K_BASE,
    "davinci" to EncodingName.R50K_BASE,
    "curie" to EncodingName.R50K_BASE,
    "babbage" to EncodingName.R50K_BASE,
    "ada" to EncodingName.R50K_BASE,
    // code (DEPRECATED)
    "code-davinci-002" to EncodingName.R50K_BASE,
    "code-davinci-001" to EncodingName.R50K_BASE,
    "code-cushman-002" to EncodingName.R50K_BASE,
    "code-cushman-001" to EncodingName.R50K_BASE,
    "davinci-codex" to EncodingName.R50K_BASE,
    "cushman-codex" to EncodingName.R50K_BASE,
    // edit (DEPRECATED)
    "text-davinci-edit-001" to EncodingName.P50K_EDIT,
    "code-davinci-edit-001" to EncodingName.P50K_EDIT,
    // old embeddings (DEPRECATED)
    "text-similarity-davinci-001" to EncodingName.R50K_BASE,
    "text-similarity-curie-001" to EncodingName.R50K_BASE,
    "text-similarity-babbage-001" to EncodingName.R50K_BASE,
    "text-similarity-ada-001" to EncodingName.R50K_BASE,
    "text-search-davinci-doc-001" to EncodingName.R50K_BASE,
    "text-search-curie-doc-001" to EncodingName.R50K_BASE,
    "text-search-babbage-doc-001" to EncodingName.R50K_BASE,
    "text-search-ada-doc-001" to EncodingName.R50K_BASE,
    "code-search-babbage-code-001" to EncodingName.R50K_BASE,
    "code-search-ada-code-001" to EncodingName.R50K_BASE,
)

internal val modelPrefixToEncoding = mapOf(
    // chat
    "gpt-4-" to EncodingName.CL100K_BASE,  // e.g., gpt-4-0314, etc., plus gpt-4-32k
    "gpt-3.5-turbo-" to EncodingName.CL100K_BASE,  // e.g, gpt-3.5-turbo-0301, -0401, etc.
    "gpt-35-turbo-" to EncodingName.CL100K_BASE,  // Azure deployment name
    // fine-tuned
    "ft:gpt-4" to EncodingName.CL100K_BASE,
    "ft:gpt-3.5-turbo" to EncodingName.CL100K_BASE,
    "ft:davinci-002" to EncodingName.CL100K_BASE,
    "ft:babbage-002" to EncodingName.CL100K_BASE,
)
