@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.enums

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.Digest.*
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_OAEP
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_PKCS1
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.INVALID_ENCRYPTION_PADDING
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Security.*

/**
 * These are the supported types of the padding to use in combination with the
 * [com.tecknobit.kassaforte.key.genspec.Algorithm.RSA] algorithm
 *
 * @param algorithm The native algorithm combined with the padding value
 */
enum class SecKeyAlgorithmType(
    val algorithm: SecKeyAlgorithm?,
) {

    /**
     * `rsaEncryptionPKCS1` RSA encryption using PKCS#1 padding
     */
    rsaEncryptionPKCS1(kSecKeyAlgorithmRSAEncryptionPKCS1),

    /**
     * `rsaEncryptionOAEPSHA1` RSA encryption using OAEP with SHA-1
     */
    rsaEncryptionOAEPSHA1(kSecKeyAlgorithmRSAEncryptionOAEPSHA1),

    /**
     * `rsaEncryptionOAEPSHA224` RSA encryption using OAEP with SHA-224
     */
    rsaEncryptionOAEPSHA224(kSecKeyAlgorithmRSAEncryptionOAEPSHA224),

    /**
     * `rsaEncryptionOAEPSHA256` RSA encryption using OAEP with SHA-256
     */
    rsaEncryptionOAEPSHA256(kSecKeyAlgorithmRSAEncryptionOAEPSHA256),

    /**
     * `rsaEncryptionOAEPSHA384` RSA encryption using OAEP with SHA-384
     */
    rsaEncryptionOAEPSHA384(kSecKeyAlgorithmRSAEncryptionOAEPSHA384),

    /**
     * `rsaEncryptionOAEPSHA512` RSA encryption using OAEP with SHA-512
     */
    rsaEncryptionOAEPSHA512(kSecKeyAlgorithmRSAEncryptionOAEPSHA512),

    /**
     * `rsaSignatureMessagePKCS1v15SHA1` RSA signature using PKCS1v15 with SHA-1
     *
     * @since Revision Two
     */
    rsaSignatureMessagePKCS1v15SHA1(kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA1),

    /**
     * `rsaSignatureMessagePKCS1v15SHA224` RSA signature using PKCS1v15 with SHA-224
     *
     * @since Revision Two
     */
    rsaSignatureMessagePKCS1v15SHA224(kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA224),

    /**
     * `rsaSignatureMessagePKCS1v15SHA256` RSA signature using PKCS1v15 with SHA-256
     *
     * @since Revision Two
     */
    rsaSignatureMessagePKCS1v15SHA256(kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA256),

    /**
     * `rsaSignatureMessagePKCS1v15SHA384` RSA signature using PKCS1v15 with SHA-384
     *
     * @since Revision Two
     */
    rsaSignatureMessagePKCS1v15SHA384(kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA384),

    /**
     * `rsaSignatureMessagePKCS1v15SHA512` RSA signature using PKCS1v15 with SHA-512
     *
     * @since Revision Two
     */
    rsaSignatureMessagePKCS1v15SHA512(kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA512),

    /**
     * `ecdsaSignatureMessageX962SHA1` ECDSA signature using `X962` with SHA-1
     *
     * @since Revision Two
     */
    ecdsaSignatureMessageX962SHA1(kSecKeyAlgorithmECDSASignatureMessageX962SHA1),

    /**
     * `ecdsaSignatureMessageX962SHA224` ECDSA signature using `X962` with SHA-224
     *
     * @since Revision Two
     */
    ecdsaSignatureMessageX962SHA224(kSecKeyAlgorithmECDSASignatureMessageX962SHA224),

    /**
     * `ecdsaSignatureMessageX962SHA256` ECDSA signature using `X962` with SHA-256
     *
     * @since Revision Two
     */
    ecdsaSignatureMessageX962SHA256(kSecKeyAlgorithmECDSASignatureMessageX962SHA256),

    /**
     * `ecdsaSignatureMessageX962SHA256` ECDSA signature using `X962` with SHA-384
     *
     * @since Revision Two
     */
    ecdsaSignatureMessageX962SHA384(kSecKeyAlgorithmECDSASignatureMessageX962SHA384),

    /**
     * `ecdsaSignatureMessageX962SHA512` ECDSA signature using `X962` with SHA-512
     *
     * @since Revision Two
     */
    ecdsaSignatureMessageX962SHA512(kSecKeyAlgorithmECDSASignatureMessageX962SHA512);

    companion object {

        /**
         * Method used to convert an [EncryptionPadding] value to the related [SecKeyAlgorithmType]
         *
         * @param digest The digest value
         *
         * @return the security key algorithm type as [SecKeyAlgorithmType]
         *
         * @throws IllegalArgumentException when the encryption padding is not valid or, when required, the digest value
         * is not valid
         */
        @Returner
        fun EncryptionPadding?.toSecKeyAlgorithm(
            digest: Digest? = null,
        ): SecKeyAlgorithmType {
            return when (this) {
                RSA_PKCS1 -> {
                    when (digest) {
                        SHA1 -> rsaSignatureMessagePKCS1v15SHA1
                        SHA224 -> rsaSignatureMessagePKCS1v15SHA224
                        SHA256 -> rsaSignatureMessagePKCS1v15SHA256
                        SHA384 -> rsaSignatureMessagePKCS1v15SHA384
                        SHA512 -> rsaSignatureMessagePKCS1v15SHA512
                        else -> rsaEncryptionPKCS1
                    }
                }
                RSA_OAEP -> {
                    when (digest) {
                        SHA1 -> rsaEncryptionOAEPSHA1
                        SHA224 -> rsaEncryptionOAEPSHA224
                        SHA256 -> rsaEncryptionOAEPSHA256
                        SHA384 -> rsaEncryptionOAEPSHA384
                        SHA512 -> rsaEncryptionOAEPSHA512
                        else -> throw IllegalArgumentException("Invalid digest type")
                    }
                }

                EncryptionPadding.NONE -> {
                    when (digest) {
                        SHA1 -> ecdsaSignatureMessageX962SHA1
                        SHA224 -> ecdsaSignatureMessageX962SHA224
                        SHA256 -> ecdsaSignatureMessageX962SHA256
                        SHA384 -> ecdsaSignatureMessageX962SHA384
                        SHA512 -> ecdsaSignatureMessageX962SHA512
                        else -> throw IllegalArgumentException("Invalid digest type")
                    }
                }

                else -> {
                    throw IllegalArgumentException(INVALID_ENCRYPTION_PADDING)
                }
            }
        }

    }

}