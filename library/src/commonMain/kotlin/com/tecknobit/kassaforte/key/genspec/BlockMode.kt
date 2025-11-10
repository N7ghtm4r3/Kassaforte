package com.tecknobit.kassaforte.key.genspec

/**
 * These are the supported block modes to use with the [Algorithm.AES] algorithm
 *
 * @property value The name of the block
 * @property blockSize The size in bytes of each block
 */
enum class BlockMode(
    val value: String,
    val blockSize: Int,
) {

    /**
     * `CBC` – Cipher Block Chaining mode
     *
     * Each block of plaintext is XORed with the previous ciphertext block before being encrypted.
     * Requires an initialization vector (IV) of the block size
     */
    CBC(
        value = "CBC",
        blockSize = 16
    ),

    /**
     * `CTR` – Counter mode
     *
     * Turns a block cipher into a stream cipher by encrypting successive values of a counter and
     * XORing them with the plaintext. Provides parallelizable encryption and decryption
     */
    CTR(
        value = "CTR",
        blockSize = 16
    ),

    /**
     * `GCM` – Galois/Counter Mode
     *
     * Based on CTR mode for encryption, but also provides authentication (AEAD) using Galois field multiplication.
     * Requires a nonce, typically 12 bytes for efficiency
     */
    GCM(
        value = "GCM",
        blockSize = 12
    ),

    /**
     * `NONE` - No block mode to adopt
     *
     * Useful to provide a default value when is not needed a block mode
     *
     * @since Revision Two
     */
    NONE(
        value = "",
        blockSize = 0
    )

}