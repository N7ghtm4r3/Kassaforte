package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.key.KeyPurposes
import com.teckonobit.kassaforte.key.genspec.SymmetricKeyGenSpec

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class KassaforteSymmetricService(
    alias: String
): KassaforteKeysService<SymmetricKeyGenSpec> {

    override fun generateKey(
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes
    )

    override fun encrypt(
        data: Any
    ): String
    
    override fun decrypt(
        data: String
    ): Any

    override fun deleteKey()

}


