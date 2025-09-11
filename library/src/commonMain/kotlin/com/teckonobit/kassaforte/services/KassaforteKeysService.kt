package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.key.KeyPurposes
import com.teckonobit.kassaforte.key.genspec.KassaforteKeyGenSpec

abstract class KassaforteKeysService<KS: KassaforteKeyGenSpec> {

    abstract fun generateKey(
        keyGenSpec: KS,
        purposes: KeyPurposes
    )

    abstract fun encrypt(
        data: Any
    ): String

    abstract fun decrypt(
        data: String
    ): Any

    abstract fun deleteKey()

}