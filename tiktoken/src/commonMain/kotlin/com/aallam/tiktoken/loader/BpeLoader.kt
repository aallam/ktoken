package com.aallam.tiktoken.loader

import com.aallam.tiktoken.EncodingName
import okio.ByteString

public sealed interface BpeLoader {

    public suspend fun loadEncoding(encodingName: EncodingName): Map<ByteString, Int>
}
