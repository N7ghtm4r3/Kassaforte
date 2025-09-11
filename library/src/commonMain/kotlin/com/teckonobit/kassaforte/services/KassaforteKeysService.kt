package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.keyspec.KassaforteKeyGenSpec

abstract class KassaforteKeysService<KS: KassaforteKeyGenSpec> {

    abstract fun generate(
        keyGenSpec: KS
    )

    abstract fun encrypt(
        data: Any
    ): String

    abstract fun decrypt(
        data: String
    ): Any

    abstract fun delete()

}