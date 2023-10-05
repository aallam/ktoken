package com.aallam.kotoken.loader

import com.aallam.kotoken.EncodingName
import com.aallam.kotoken.internal.loadTiktokenBpe
import com.aallam.kotoken.internal.loadVocabBpe
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.*
import okio.ByteString

public class RemoteBpeLoader(private val client: HttpClient = defaultHttpClient()) : BpeLoader {

    override suspend fun loadEncoding(encodingName: EncodingName): Map<ByteString, Int> {
        val data = downloadFile("$URL_PREFIX/${encodingName.fileName}")
        return loadTiktokenBpe(data)
    }

    override suspend fun loadVocab(vocabFile: String): Map<ByteString, Int> {
        val data = downloadFile(vocabFile)
        return loadVocabBpe(data)
    }

    private suspend fun downloadFile(url: String): ByteArray {
        return client.get(url).body()
    }

    public companion object {
        private const val URL_PREFIX = "https://openaipublic.blob.core.windows.net/encodings"
        private fun defaultHttpClient(): HttpClient {
            return HttpClient {
                install(HttpCache)
            }
        }
    }
}
