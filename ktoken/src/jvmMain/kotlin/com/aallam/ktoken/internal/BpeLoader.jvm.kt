package com.aallam.ktoken.internal

import com.aallam.ktoken.loader.BpeLoader
import com.aallam.ktoken.loader.LocalPbeLoader
import okio.FileSystem

internal actual fun defaultPbeLoader(): BpeLoader = LocalPbeLoader(FileSystem.RESOURCES)
