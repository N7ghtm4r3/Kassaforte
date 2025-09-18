package com.tecknobit.kassaforte.services.impls

import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGen
import com.tecknobit.kassaforte.key.usages.KeyPurposes

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteAsymmetricServiceImpl actual constructor() : KassaforteServiceImpl() {

    actual fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: AsymmetricKeyGen,
        purposes: KeyPurposes,
    ) {
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun deleteKey(
        alias: String,
    ) {
    }

}