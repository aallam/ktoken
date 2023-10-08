package com.aallam.ktoken.loader

import com.aallam.ktoken.EncodingName
import com.aallam.ktoken.internal.loadTiktokenBpe
import okio.Buffer
import okio.ByteString
import okio.FileSystem
import okio.Path.Companion.toPath

public class LocalPbeLoader(private val fileSystem: FileSystem) : BpeLoader {

    override suspend fun loadEncoding(encodingName: EncodingName): Map<ByteString, Int> {
        val data = readFile(encodingName.fileName, fileSystem)
        return loadTiktokenBpe(data)
    }

    private fun readFile(path: String, fileSystem: FileSystem): ByteArray {
        val buffer = Buffer()
        fileSystem.read(path.toPath()) { readAll(buffer) }
        return buffer.readByteArray()
    }
}
