package com.aallam.tiktoken.loader

import com.aallam.tiktoken.EncodingName
import com.aallam.tiktoken.internal.loadTiktokenBpe
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.*
import okio.ByteString

public class RemoteBpeLoader(
    private val client: HttpClient = defaultHttpClient(),
    private val url: String = "https://openaipublic.blob.core.windows.net/encodings"
) : BpeLoader {

    override suspend fun loadEncoding(encodingName: EncodingName): Map<ByteString, Int> {
        val data = downloadFile("$url/${encodingName.fileName}")
        return loadTiktokenBpe(data)
    }

    private suspend fun downloadFile(url: String): ByteArray {
        return client.get(url).body()
    }

    public companion object {
        private fun defaultHttpClient() = HttpClient {
            install(HttpCache)
        }
    }
}
