package com.tecknobit.kassaforte.key.genspec

/**
 * These are the supported digests to use with generated keys
 *
 * @property value The name of the digest
 */
enum class DigestType(
    val value: String,
) {

    /**
     * `NONE` no digest is applied
     */
    NONE("NONE"),

    /**
     * `MD5` the MD5 digest algorithm
     *
     * Its usage is discouraged because it is considered cryptographically broken
     */
    MD5("MD5"),

    /**
     * `SHA1` the SHA-1 digest algorithm
     *
     * Its usage is discouraged due to known collision vulnerabilities
     */
    SHA1("SHA-1"),

    /**
     * `SHA224` the SHA-224 digest algorithm
     */
    SHA224("SHA-224"),

    /**
     * `SHA256` the SHA-256 digest algorithm
     */
    SHA256("SHA-256"),

    /**
     * `SHA384` the SHA-384 digest algorithm
     */
    SHA384("SHA-384"),

    /**
     * `SHA512` the SHA-512 digest algorithm
     */
    SHA512("SHA-512")

}