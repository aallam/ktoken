package com.aallam.kotoken.loader

import com.aallam.kotoken.EncodingName
import com.aallam.kotoken.internal.loadTiktokenBpe
import com.aallam.kotoken.internal.loadVocabBpe
import okio.Buffer
import okio.ByteString
import okio.FileSystem
import okio.Path.Companion.toPath

public class LocalPbeLoader(private val fileSystem: FileSystem) : BpeLoader {

    override suspend fun loadEncoding(encodingName: EncodingName): Map<ByteString, Int> {
        val data = readFile(encodingName.fileName, fileSystem)
        return loadTiktokenBpe(data)
    }

    override suspend fun loadVocab(vocabFile: String): Map<ByteString, Int> {
        val data = readFile(vocabFile, fileSystem)
        return loadVocabBpe(data)
    }

    private fun readFile(path: String, fileSystem: FileSystem): ByteArray {
        val buffer = Buffer()
        fileSystem.read(path.toPath()) { readAll(buffer) }
        return buffer.readByteArray()
    }
}
