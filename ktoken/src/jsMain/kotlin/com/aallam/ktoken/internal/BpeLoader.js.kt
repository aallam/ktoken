package com.aallam.ktoken.internal

import com.aallam.ktoken.loader.BpeLoader
import com.aallam.ktoken.loader.RemoteBpeLoader

internal actual fun defaultPbeLoader(): BpeLoader = RemoteBpeLoader()
