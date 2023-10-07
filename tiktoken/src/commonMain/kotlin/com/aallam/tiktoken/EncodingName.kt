package com.aallam.tiktoken

import kotlin.jvm.JvmInline

/**
 * A value class representing an encoding name.
 */
@JvmInline
public value class EncodingName(public val fileName: String) {
    public companion object {
        public val CL100K_BASE: EncodingName = EncodingName("cl100k_base.tiktoken")
        public val P50K_BASE: EncodingName = EncodingName("p50k_base.tiktoken")
        public val P50K_EDIT: EncodingName = EncodingName("p50k_base.tiktoken")
        public val R50K_BASE: EncodingName = EncodingName("r50k_base.tiktoken")
    }
}
