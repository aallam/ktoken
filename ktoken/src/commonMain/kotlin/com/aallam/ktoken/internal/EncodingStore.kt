package com.aallam.ktoken.internal

import com.aallam.ktoken.Encoding
import com.aallam.ktoken.TokenEncoding
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A thread-safe store for managing [TokenEncoding] objects associated with [Encoding] keys.
 * This store uses a [Mutex] to ensure thread-safety when accessing the underlying map.
 */
internal class EncodingStore {

    /** A [MutableMap] to hold [TokenEncoding] objects keyed by [Encoding]. */
    private val map = mutableMapOf<Encoding, TokenEncoding>()

    /** A [Mutex] to ensure thread-safety when accessing [map]. */
    private val mutex = Mutex()

    /**
     * Retrieves the [TokenEncoding] associated with the given [key] from the store.
     *
     * This method acquires a lock on [mutex] to ensure thread-safety when accessing [map].
     *
     * @param key The [Encoding] key to look up.
     * @return The [TokenEncoding] associated with the given [key], or `null` if no such [TokenEncoding] exists.
     */
    suspend fun get(key: Encoding): TokenEncoding? {
        return mutex.withLock {
            map[key]
        }
    }

    /**
     * Associates the given [key] with the given [value] in the store.
     *
     * This method acquires a lock on [mutex] to ensure thread-safety when accessing [map].
     *
     * @param key The [Encoding] key with which to associate the given [value].
     * @param value The [TokenEncoding] value to store.
     * @return The previous [TokenEncoding] associated with the given [key], or `null` if there was no mapping for [key].
     */
    suspend fun put(key: Encoding, value: TokenEncoding): TokenEncoding? {
        return mutex.withLock {
            map.put(key, value)
        }
    }
}
