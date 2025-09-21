@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.enums

import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.Digest.*
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_OAEP
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_PKCS1
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.INVALID_ENCRYPTION_PADDING
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Security.*

enum class SecKeyAlgorithmType(
    val algorithm: SecKeyAlgorithm?,
) {

    rsaEncryptionPKCS1(kSecKeyAlgorithmRSAEncryptionPKCS1),

    rsaEncryptionOAEPSHA1(kSecKeyAlgorithmRSAEncryptionOAEPSHA1),

    rsaEncryptionOAEPSHA224(kSecKeyAlgorithmRSAEncryptionOAEPSHA224),

    rsaEncryptionOAEPSHA256(kSecKeyAlgorithmRSAEncryptionOAEPSHA256),

    rsaEncryptionOAEPSHA384(kSecKeyAlgorithmRSAEncryptionOAEPSHA384),

    rsaEncryptionOAEPSHA512(kSecKeyAlgorithmRSAEncryptionOAEPSHA512);

    companion object {

        // TODO: TO ANNOTATE WITH @Returner
        fun EncryptionPadding?.toSecKeyAlgorithm(
            digest: Digest? = null,
        ): SecKeyAlgorithmType {
            return when (this) {
                RSA_PKCS1 -> rsaEncryptionPKCS1
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

                else -> throw IllegalArgumentException(INVALID_ENCRYPTION_PADDING)
            }
        }

    }

}