package com.tecknobit.kassaforte.enums

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_PKCS1
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.INVALID_ENCRYPTION_PADDING

enum class RsaAlgorithmName(
    val value: String,
) {

    // TODO: TO WARN ABOUT THAT ON THE WEB THIS IS USED TO VERIFY AND SIGN ONLY
    RSASSA_PKCS1_v1_5("RSASSA-PKCS1-v1_5"),

    RSA_OAEP("RSA-OAEP");

    companion object {

        @Returner
        fun EncryptionPadding.toRsaAlgorithmName(): RsaAlgorithmName {
            return when (this) {
                EncryptionPadding.RSA_OAEP -> RSA_OAEP
                RSA_PKCS1 -> RSASSA_PKCS1_v1_5
                else -> throw IllegalArgumentException(INVALID_ENCRYPTION_PADDING)
            }
        }

    }

}