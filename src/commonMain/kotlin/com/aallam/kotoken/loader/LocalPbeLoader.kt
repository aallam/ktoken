package com.aallam.kotoken.loader

import com.aallam.kotoken.EncodingName
import okio.Buffer
import okio.ByteString
import okio.FileSystem
import okio.Path.Companion.toPath

class LocalPbeLoader(private val fileSystem: FileSystem) : BpeLoader {

    override suspend fun load(encodingName: EncodingName): Map<ByteString, Int> {
        val data = readFile(encodingName.fileName, fileSystem)
        return loadTiktokenBpe(data)
    }

    private fun readFile(path: String, fileSystem: FileSystem): ByteArray {
        val buffer = Buffer()
        fileSystem.read(path.toPath()) { readAll(buffer) }
        return buffer.readByteArray()
    }
}
