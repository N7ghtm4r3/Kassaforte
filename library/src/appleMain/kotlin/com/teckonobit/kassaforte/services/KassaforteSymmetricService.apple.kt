package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.key.KeyPurposes
import com.teckonobit.kassaforte.key.genspec.BlockModeType
import com.teckonobit.kassaforte.key.genspec.EncryptionPaddingType
import com.teckonobit.kassaforte.key.genspec.SymmetricKeyGenSpec

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    actual override fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        TODO("Not yet implemented")
    }

    actual override fun aliasExists(
        alias: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    actual fun encrypt(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        data: Any,
    ): String {
        TODO("Not yet implemented")
    }

    actual fun decrypt(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        data: String,
    ): String {
        TODO("Not yet implemented")
    }

    actual override fun deleteKey(
        alias: String
    ) {
        TODO("Not yet implemented")
    }

}