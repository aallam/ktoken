package com.aallam.kotoken

import kotlin.jvm.JvmInline

@JvmInline
value class EncodingName(val fileName: String) {
    companion object {
        val CL100K_BASE = EncodingName("cl100k_base.tiktoken")
        val P50K_BASE = EncodingName("p50k_base.tiktoken")
        val P50K_EDIT = EncodingName("p50k_base.tiktoken")
        val R50K_BASE = EncodingName("r50k_base.tiktoken")
    }
}
