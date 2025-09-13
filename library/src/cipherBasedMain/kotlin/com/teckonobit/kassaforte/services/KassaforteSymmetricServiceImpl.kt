package com.teckonobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.teckonobit.kassaforte.key.KeyPurposes
import com.teckonobit.kassaforte.key.genspec.BlockModeType
import com.teckonobit.kassaforte.key.genspec.EncryptionPaddingType
import com.teckonobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import java.security.Key

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class KassaforteSymmetricServiceImpl() {

    fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes
    )

    fun aliasExists(
        alias: String
    ): Boolean

    fun getKey(
        alias: String
    ): Key

    @Assembler
    internal fun resolveTransformation(
        algorithm: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?
    ): String

    fun deleteKey(
        alias: String
    )

}