package com.aallam.kotoken.internal

import com.aallam.kotoken.EncodingName
import com.aallam.kotoken.loader.BpeLoader
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8

internal class Encoding(
    /**
     * The name of the encoding. It should be clear from the name of the encoding
     * what behaviour to expect, in particular, encodings with different special tokens
     * should have different names.
     */
    val name: EncodingName,

    /**
     * A regex pattern string that is used to split the input text.
     */
    val pattern: String,

    /**
     * A dictionary mapping mergeable token bytes to their ranks. The ranks must correspond to merge priority.
     */
    val mergeableRanks: Map<ByteString, Int>,

    /**
     * A dictionary mapping special token strings to their token values.
     */
    val specialTokens: Map<ByteString, Int>,

    /**
     * The number of tokens in the vocabulary.
     * If provided, it is checked that the number of mergeable tokens and special tokens is equal to this number.
     */
    val explicitNVocab: Int? = null,
) {
    init {
        if (explicitNVocab != null) {
            val totalCount = mergeableRanks.size + specialTokens.size
            require(totalCount == explicitNVocab) { "the expected number of tokens in the vocabulary is incorrect, expected: $explicitNVocab, actual: $totalCount" }
        }
    }

    companion object {
        private val encodingMap = mutableMapOf<EncodingName, Encoding>()
        private val mutex = Mutex()
        suspend fun getEncoding(encodingName: EncodingName, loader: BpeLoader): Encoding {
            return mutex.withLock {
                encodingMap[encodingName] ?: loader.getEncoding(encodingName).also { encodingMap[encodingName] = it }
            }
        }

        suspend fun getEncodingForModel(modelName: String, loader: BpeLoader): Encoding {
            val encodingName = modelToEncoding[modelName]
                ?: modelPrefixToEncoding.entries
                    .firstOrNull { (prefix, _) -> modelName.startsWith(prefix) }
                    ?.value
                ?: error("no encoding for model $modelName")
            return getEncoding(encodingName, loader)
        }
    }
}

private suspend fun BpeLoader.getEncoding(encodingName: EncodingName): Encoding {
    return when (encodingName) {
        EncodingName.CL100K_BASE -> cl100kBase()
        EncodingName.P50K_BASE -> p50kBase()
        EncodingName.R50K_BASE -> r50kBase()
        EncodingName.P50K_EDIT -> p50kEdit()
        EncodingName.GPT2 -> gpt2()
        else -> error("Unknown encoding: $encodingName")
    }
}

private suspend fun BpeLoader.cl100kBase(): Encoding {
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
        pattern = """(?i:'s|'t|'re|'ve|'m|'ll|'d)|[^\r\n\p{L}\p{N}]?\p{L}+|\p{N}{1,3}| ?[^\s\p{L}\p{N}]+[\r\n]*|\s*[\r\n]+|\s+(?!\S)|\s+""",
        mergeableRanks = ranks,
        specialTokens = specialTokens,
    )
}

private const val pattern50k = """'s|'t|'re|'ve|'m|'ll|'d| ?\p{L}+| ?\p{N}+| ?[^\s\p{L}\p{N}]+|\s+(?!\S)|\s+"""

private suspend fun BpeLoader.p50kEdit(): Encoding {
    val ranks = loadEncoding(EncodingName.P50K_EDIT)
    val specialTokens = mapOf(
        Tokens.ENDOFTEXT to 50256,
        Tokens.FIM_PREFIX to 50281,
        Tokens.FIM_MIDDLE to 50282,
        Tokens.FIM_SUFFIX to 50283
    )
    return Encoding(
        name = EncodingName.P50K_EDIT,
        pattern = pattern50k,
        mergeableRanks = ranks,
        specialTokens = specialTokens,
    )
}

private suspend fun BpeLoader.p50kBase(): Encoding {
    val ranks = loadEncoding(EncodingName.P50K_BASE)
    val specialTokens = mapOf(Tokens.ENDOFTEXT to 50256)
    require(ranks.size + specialTokens.size == 50281)
    return Encoding(
        name = EncodingName.P50K_BASE,
        pattern = pattern50k,
        mergeableRanks = ranks,
        specialTokens = mapOf(Tokens.ENDOFTEXT to 50256),
        explicitNVocab = 50281,
    )
}

private suspend fun BpeLoader.r50kBase(): Encoding {
    val ranks = loadEncoding(EncodingName.R50K_BASE)
    return Encoding(
        name = EncodingName.R50K_BASE,
        mergeableRanks = ranks,
        pattern = pattern50k,
        specialTokens = mapOf(Tokens.ENDOFTEXT to 50256),
        explicitNVocab = 50257,
    )
}

private suspend fun BpeLoader.gpt2(): Encoding {
    val mergeableRanks = loadVocab("vocab.bpe")
    return Encoding(
        name = EncodingName.GPT2,
        mergeableRanks = mergeableRanks,
        pattern = pattern50k,
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

private val modelToEncoding: Map<String, EncodingName> = mapOf(
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
    // open source
    "gpt2" to EncodingName.GPT2,
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
