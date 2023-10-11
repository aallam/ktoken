package com.aallam.ktoken.loader

import com.aallam.ktoken.internal.loadTiktokenBpe
import okio.Buffer
import okio.ByteString
import okio.FileSystem
import okio.Path.Companion.toPath

public class LocalPbeLoader(private val fileSystem: FileSystem, private val directory: String? = null) : BpeLoader {

    override suspend fun loadEncoding(encodingPath: String): Map<ByteString, Int> {
        val data = readFile(encodingPath, fileSystem)
        return loadTiktokenBpe(data)
    }

    private fun readFile(file: String, fileSystem: FileSystem): ByteArray {
        val buffer = Buffer()
        val filePath = file.toPath()
        val path = directory?.toPath()?.let { it / filePath } ?: filePath
        fileSystem.read(path) { readAll(buffer) }
        return buffer.readByteArray()
    }
}
