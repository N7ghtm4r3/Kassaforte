package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.DigestType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.usages.KeyPurposes

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec> {

    override fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    )

    suspend fun encrypt(
        alias: String,
        paddingType: EncryptionPaddingType? = null,
        digestType: DigestType? = null,
        data: Any,
    ): String

    suspend fun decrypt(
        alias: String,
        paddingType: EncryptionPaddingType? = null,
        digestType: DigestType? = null,
        data: String,
    ): String

    override fun aliasExists(
        alias: String,
    ): Boolean

    override fun deleteKey(
        alias: String,
    )

}