package com.tecknobit.kassaforte.key.genspec

/**
 * These are the supported size to generate a key, both symmetric or asymmetric generation
 *
 * @property bitCount The size of the key in bits
 * @property bytes The size of the key in bytes
 */
enum class KeySize(
    val bitCount: Int,
    val bytes: Int,
) {

    /**
     * `S128` Size to generate a key of `128` bits length.
     *
     * Widely used with [AlgorithmType.AES] algorithm
     */
    S128(
        bitCount = 128,
        bytes = 16
    ),

    /**
     * `S192` Size to generate a key of `192` bits length
     *
     * Widely used with [AlgorithmType.AES] algorithm
     */
    S192(
        bitCount = 192,
        bytes = 24
    ),

    /**
     * `S224` Size to generate a key of `224` bits length
     *
     * Widely used with [AlgorithmType.EC] algorithm
     */
    S224(
        bitCount = 224,
        bytes = 28
    ),

    /**
     * `S256` Size to generate a key of `256` bits length
     *
     * Widely used with [AlgorithmType.AES] algorithm
     */
    S256(
        bitCount = 256,
        bytes = 32
    ),

    /**
     * `S384` Size to generate a key of `384` bits length
     *
     * Widely used with [AlgorithmType.EC] algorithm
     */
    S384(
        bitCount = 384,
        bytes = 48
    ),

    /**
     * `S512` Size to generate a key of `512` bits length
     *
     * Widely used with [AlgorithmType.AES] algorithm
     */
    S512(
        bitCount = 512,
        bytes = 64
    ),

    /**
     * `S521` Size to generate a key of `521` bits length
     *
     * Widely used with [AlgorithmType.EC] algorithm
     */
    S521(
        bitCount = 521,
        bytes = 66
    ),

    /**
     * `S1024` Size to generate a key of `1024` bits length
     *
     * Widely used with [AlgorithmType.RSA] algorithm
     */
    S1024(
        bitCount = 1024,
        bytes = 128
    ),

    /**
     * `S2048` Size to generate a key of `2048` bits length
     *
     * Widely used with [AlgorithmType.RSA] algorithm
     */
    S2048(
        bitCount = 2048,
        bytes = 256
    ),

    /**
     * `S4096` Size to generate a key of `4096` bits length
     *
     * Widely used with [AlgorithmType.RSA] algorithm
     */
    S4096(
        bitCount = 4096,
        bytes = 512
    )

}