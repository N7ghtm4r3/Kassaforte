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
     * `AES` symmetric block cipher algorithm widely used for secure data encryption
     */
    AES("AES"),

    /**
     * `HMAC_SHA1` algorithm used to ensure message integrity and authentication,
     * implemented using the [Digest.SHA1] function
     *
     * @since Revision Two
     */
    HMAC_SHA1("HmacSHA1"),

    /**
     * `HMAC_SHA256` algorithm used to ensure message integrity and authentication,
     * implemented using the [Digest.SHA256] function
     *
     * @since Revision Two
     */
    HMAC_SHA256("HmacSHA256"),

    /**
     * `HMAC_SHA384` algorithm used to ensure message integrity and authentication,
     * implemented using the [Digest.SHA384] function
     *
     * @since Revision Two
     */
    HMAC_SHA384("HmacSHA384"),

    /**
     * `HmacSHA512` algorithm used to ensure message integrity and authentication,
     * implemented using the [Digest.SHA512] function
     *
     * @since Revision Two
     */
    HMAC_SHA512("HmacSHA512")

}