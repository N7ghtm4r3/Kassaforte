package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.key.KeyPurposes
import com.teckonobit.kassaforte.key.genspec.BlockModeType
import com.teckonobit.kassaforte.key.genspec.EncryptionPaddingType
import com.teckonobit.kassaforte.key.genspec.SymmetricKeyGenSpec

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KassaforteSymmetricService: KassaforteKeysService<SymmetricKeyGenSpec> {

    override fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes
    )

    override fun aliasExists(
        alias: String
    ): Boolean

    fun encrypt(
        alias: String,
        blockModeType: BlockModeType? = null,
        paddingType: EncryptionPaddingType? = null,
        data: Any
    ): String
    
    fun decrypt(
        alias: String,
        blockModeType: BlockModeType? = null,
        paddingType: EncryptionPaddingType? = null,
        data: String
    ): String

    override fun deleteKey(
        alias: String
    )

}


