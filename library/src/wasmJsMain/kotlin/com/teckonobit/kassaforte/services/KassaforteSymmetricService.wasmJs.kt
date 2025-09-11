package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.keyspec.SymmetricKeyGenSpec

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class KassaforteSymmetricService actual constructor(
    alias: String
) : KassaforteKeysService<SymmetricKeyGenSpec>() {

    actual override fun generate(
        keyGenSpec: SymmetricKeyGenSpec
    ) {
        TODO("Not yet implemented")
    }

    actual override fun encrypt(
        data: Any
    ): String {
        TODO("Not yet implemented")
    }

    actual override fun decrypt(
        data: String
    ): Any {
        TODO("Not yet implemented")
    }

    actual override fun delete() {
        TODO("Not yet implemented")
    }

}