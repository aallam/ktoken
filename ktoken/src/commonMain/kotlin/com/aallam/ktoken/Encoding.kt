package com.aallam.ktoken

import com.aallam.ktoken.encoding.CL100KBase
import com.aallam.ktoken.encoding.P50KBase
import com.aallam.ktoken.encoding.P50KEdit
import com.aallam.ktoken.encoding.R50KBase
import com.aallam.ktoken.loader.BpeLoader
import okio.ByteString

/**
 * Represents an encoding interface that provides essential encoding functionality
 * and access to predefined encoding types.
 */
public interface Encoding {

    /**
     * The encoding file path.
     */
    public val file: String

    /**
     * Retrieve token encoding utilizing the provided [BpeLoader].
     *
     * @param ranks byte pair encoding ranks.
     */
    public fun encodingConfig(ranks: Map<ByteString, Int>): EncodingConfig

    public companion object {
        /**
         * A predefined [Encoding] instance representing the [CL100KBase] encoding type.
         */
        public val CL100K_BASE: Encoding = CL100KBase()

        /**
         * A predefined [Encoding] instance representing the [P50KBase] encoding type.
         */
        public val P50K_BASE: Encoding = P50KBase()

        /**
         * A predefined [Encoding] instance representing the [R50KBase] encoding type.
         */
        public val R50K_BASE: Encoding = R50KBase()

        /**
         * A predefined [Encoding] instance representing the [P50KEdit] encoding type.
         */
        public val P50K_EDIT: Encoding = P50KEdit()
    }
}
