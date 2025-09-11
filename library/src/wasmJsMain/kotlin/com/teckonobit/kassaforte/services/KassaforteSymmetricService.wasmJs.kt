package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.keys.SymmetricKey

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class KassaforteSymmetricService actual constructor(
    alias: String
) : KassaforteKeysService<SymmetricKey>() {

    override fun generate(): SymmetricKey {
        TODO("Not yet implemented")
    }

    override fun encrypt(
        data: Any
    ): String {
        TODO("Not yet implemented")
    }

    override fun decrypt(
        data: String
    ): Any {
        TODO("Not yet implemented")
    }

}