package com.tecknobit.kassaforte.enums

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm.*

/**
 * The `Hash` enum provide a way to map [Algorithm] `HMAC`'s algorithm with the native algorithms
 *
 * @property value The native value of the hash function
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @since Revision Two
 */
enum class Hash(
    val value: String,
) {

    /**
     * `SHA1` the SHA-1 hash function
     *
     * Its usage is discouraged due to known collision vulnerabilities
     */
    SHA1("SHA-1"),

    /**
     * `SHA256` the SHA-256 hash function
     */
    SHA256("SHA-256"),

    /**
     * `SHA384` the SHA-384 hash function
     */
    SHA384("SHA-384"),

    /**
     * `SHA512` the SHA-512 hash function
     */
    SHA512("SHA-512");

    companion object {

        /**
         * Method used as mapper from [Algorithm] to corresponding [Hash] instance
         *
         * @return the native hash value as [Hash]
         */
        @Returner
        fun Algorithm.resolveHash(): Hash {
            return when (this) {
                HMAC_SHA1 -> SHA1
                HMAC_SHA256 -> SHA256
                HMAC_SHA384 -> SHA384
                HMAC_SHA512 -> SHA512
                else -> throw IllegalArgumentException("No corresponding hash value with the provided algorithm")
            }
        }

    }

}