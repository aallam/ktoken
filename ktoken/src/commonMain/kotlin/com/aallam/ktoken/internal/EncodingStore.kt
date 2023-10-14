package com.aallam.ktoken.internal

import com.aallam.ktoken.Encoding
import com.aallam.ktoken.EncodingConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A thread-safe store for managing [EncodingConfig] objects associated with [Encoding] keys.
 * This store uses a [Mutex] to ensure thread-safety when accessing the underlying map.
 */
internal class EncodingStore {

    /** A [MutableMap] to hold [EncodingConfig] objects keyed by [Encoding]. */
    private val map = mutableMapOf<Encoding, EncodingConfig>()

    /** A [Mutex] to ensure thread-safety when accessing [map]. */
    private val mutex = Mutex()

    /**
     * Retrieves the [EncodingConfig] associated with the given [key] from the store.
     *
     * This method acquires a lock on [mutex] to ensure thread-safety when accessing [map].
     *
     * @param key The [Encoding] key to look up.
     * @return The [EncodingConfig] associated with the given [key], or `null` if no such [EncodingConfig] exists.
     */
    suspend fun get(key: Encoding): EncodingConfig? {
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
     * @param value The [EncodingConfig] value to store.
     * @return The previous [EncodingConfig] associated with the given [key], or `null` if there was no mapping for [key].
     */
    suspend fun put(key: Encoding, value: EncodingConfig): EncodingConfig? {
        return mutex.withLock {
            map.put(key, value)
        }
    }
}
