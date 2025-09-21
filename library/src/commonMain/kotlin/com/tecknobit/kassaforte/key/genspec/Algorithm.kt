package com.tecknobit.kassaforte.key.genspec

/**
 * These are the supported algorithms used during ciphering and keys creation
 *
 * @property value The name of the algorithm
 */
enum class Algorithm(
    val value: String,
) {

    /**
     * `RSA` asymmetric encryption algorithm based on the hardness of factoring large integers
     */
    RSA("RSA"),

    /**
     * `EC` an asymmetric algorithm based on elliptic curve cryptography (ECC),
     * commonly used for digital signatures (ECDSA) and key exchange (ECDH)
     */
    EC("EC"),

    /**
     * `AES` symmetric block cipher algorithm widely used for secure data encryption.
     */
    AES("AES")
}