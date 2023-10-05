package com.aallam.kotoken.loader

import com.aallam.kotoken.EncodingName
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.*
import okio.ByteString

class RemoteBpeLoader(private val client: HttpClient = HttpClient { install(HttpCache) }) : BpeLoader {

    override suspend fun load(encodingName: EncodingName): Map<ByteString, Int> {
        val data = downloadFile("$URL_PREFIX/${encodingName.fileName}")
        return loadTiktokenBpe(data)
    }

    private suspend fun downloadFile(url: String): ByteArray {
        return client.get(url).body()
    }

    companion object {
        private const val URL_PREFIX = "https://openaipublic.blob.core.windows.net/encodings"
    }
}
