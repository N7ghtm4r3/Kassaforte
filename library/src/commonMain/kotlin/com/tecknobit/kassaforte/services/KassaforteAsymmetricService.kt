package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.usages.KeyPurposes

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec> {

    override fun generateKey(
        algorithm: Algorithm,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    )

    suspend fun encrypt(
        alias: String,
        padding: EncryptionPadding? = null,
        digest: Digest? = null,
        data: Any,
    ): String

    suspend fun decrypt(
        alias: String,
        padding: EncryptionPadding? = null,
        digest: Digest? = null,
        data: String,
    ): String

    override fun aliasExists(
        alias: String,
    ): Boolean

    override fun deleteKey(
        alias: String,
    )

}