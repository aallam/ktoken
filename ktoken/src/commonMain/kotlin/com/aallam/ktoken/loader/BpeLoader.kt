package com.aallam.ktoken.loader

import okio.ByteString

public sealed interface BpeLoader {

    public suspend fun loadEncoding(encodingPath: String): Map<ByteString, Int>
}
