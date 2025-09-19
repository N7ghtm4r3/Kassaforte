package com.tecknobit.kassaforte.enums

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.RSA_PKCS1

enum class RsaAlgorithmName(
    val value: String,
) {

    RSASSA_PKCS1_v1_5("RSASSA-PKCS1-v1_5"),

    RSA_OAEP("RSA-OAEP");

    companion object {

        @Returner
        fun EncryptionPaddingType.toRsaAlgorithmName(): RsaAlgorithmName {
            return when (this) {
                EncryptionPaddingType.RSA_OAEP -> RSA_OAEP
                RSA_PKCS1 -> RSASSA_PKCS1_v1_5
                else -> {
                    throw IllegalArgumentException("Invalid encryption padding value")
                }
            }
        }

    }

}