package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.keyspec.SymmetricKeyGenSpec


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class KassaforteSymmetricService(
    alias: String
): KassaforteKeysService<SymmetricKeyGenSpec> {

    override fun generate(
        keyGenSpec: SymmetricKeyGenSpec
    )

    override fun encrypt(
        data: Any
    ): String
    
    override fun decrypt(
        data: String
    ): Any

    override fun delete()

}


