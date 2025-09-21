package com.tecknobit.kassaforte.key.genspec

/**
 * These are the supported padding to use during encryption to use with generated keys
 *
 * @property value The name of the padding
 */
enum class EncryptionPadding(
    val value: String,
) {

    /**
     * `NONE` no padding is applied.
     */
    NONE("NoPadding"),

    /**
     * `PKCS7` padding scheme used with block ciphers such as [Algorithm.AES].
     *
     * It fills the last block with bytes all set to the value of the number of padding bytes
     */
    PKCS7("PKCS7Padding"),

    /**
     * `RSA_PKCS1` padding scheme defined in `PKCS#1` for use with [Algorithm.RSA].
     *
     * Commonly used in RSA encryption, but less secure compared to [RSA_OAEP].
     */
    RSA_PKCS1("PKCS1Padding"),

    /**
     * `RSA_OAEP` **Optimal Asymmetric Encryption Padding**, recommended scheme for [Algorithm.RSA].
     *
     * Provides better security than [RSA_PKCS1] by incorporating randomness and a hash function.
     */
    RSA_OAEP("OAEPPadding");

}