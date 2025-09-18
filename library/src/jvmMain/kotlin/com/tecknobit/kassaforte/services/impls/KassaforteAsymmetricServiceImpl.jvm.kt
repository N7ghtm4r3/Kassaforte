package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.DigestType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import java.security.Key

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteAsymmetricServiceImpl actual constructor() : KassaforteServiceImpl() {

    actual fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun getKey(
        alias: String,
        keyOperation: KeyOperation,
    ): Key {
        TODO("Not yet implemented")
    }

    @Assembler
    actual fun resolveTransformation(
        algorithm: String,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
    ): String {
        TODO("Not yet implemented")
    }

    actual override fun deleteKey(
        alias: String,
    ) {
    }

}