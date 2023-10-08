package com.aallam.ktoken.loader

import com.aallam.ktoken.EncodingName
import okio.ByteString

public sealed interface BpeLoader {

    public suspend fun loadEncoding(encodingName: EncodingName): Map<ByteString, Int>
}
