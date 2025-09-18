package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import java.security.Key

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class KassaforteAsymmetricServiceImpl() : KassaforteServiceImpl {

    fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    )

    override fun aliasExists(
        alias: String,
    ): Boolean

    override fun getKey(
        alias: String,
        keyOperation: KeyOperation,
    ): Key

    @Assembler
    fun resolveTransformation(
        algorithm: String,
        paddingType: EncryptionPaddingType?,
    ): String

    override fun deleteKey(
        alias: String,
    )

}
