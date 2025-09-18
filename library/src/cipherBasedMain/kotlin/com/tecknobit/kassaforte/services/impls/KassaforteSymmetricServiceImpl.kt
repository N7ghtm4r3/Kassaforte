package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import java.security.Key

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class KassaforteSymmetricServiceImpl() : KassaforteServiceImpl {

    fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    )

    override fun aliasExists(
        alias: String,
    ): Boolean

    fun getKey(
        alias: String,
        keyOperation: KeyOperation,
    ): Key

    @Assembler
    fun resolveTransformation(
        algorithm: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
    ): String

    override fun deleteKey(
        alias: String,
    )

}