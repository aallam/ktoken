package com.aallam.kotoken.loader

import com.aallam.kotoken.EncodingName
import okio.ByteString

public sealed interface BpeLoader {

    public suspend fun loadEncoding(encodingName: EncodingName): Map<ByteString, Int>
}
