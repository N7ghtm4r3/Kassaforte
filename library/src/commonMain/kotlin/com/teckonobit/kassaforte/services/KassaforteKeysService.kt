package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.key.KeyPurposes
import com.teckonobit.kassaforte.key.genspec.KassaforteKeyGenSpec

abstract class KassaforteKeysService<KS: KassaforteKeyGenSpec> {

    abstract fun generateKey(
        alias: String,
        keyGenSpec: KS,
        purposes: KeyPurposes
    )

    abstract fun deleteKey(
        alias: String
    )

}