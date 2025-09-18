package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.NONE
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KassaforteSymmetricService: KassaforteKeysService<SymmetricKeyGenSpec> {

    override fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    )

    override fun aliasExists(
        alias: String
    ): Boolean

    suspend fun encrypt(
        alias: String,
        blockModeType: BlockModeType,
        paddingType: EncryptionPaddingType = NONE,
        data: Any,
    ): String

    suspend fun decrypt(
        alias: String,
        blockModeType: BlockModeType,
        paddingType: EncryptionPaddingType = NONE,
        data: String,
    ): String

    override fun deleteKey(
        alias: String
    )

}


