package com.aallam.kotoken

import kotlin.jvm.JvmInline

@JvmInline
public value class EncodingName(public val fileName: String) {
    public companion object {
        public val CL100K_BASE: EncodingName = EncodingName("cl100k_base.tiktoken")
        public val P50K_BASE: EncodingName = EncodingName("p50k_base.tiktoken")
        public val P50K_EDIT: EncodingName = EncodingName("p50k_base.tiktoken")
        public val R50K_BASE: EncodingName = EncodingName("r50k_base.tiktoken")
        public val GPT2: EncodingName = EncodingName("vocab.bpe")
    }
}
