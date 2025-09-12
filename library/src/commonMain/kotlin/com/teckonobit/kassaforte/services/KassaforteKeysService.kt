package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.key.KeyPurposes
import com.teckonobit.kassaforte.key.genspec.KassaforteKeyGenSpec

abstract class KassaforteKeysService<KS: KassaforteKeyGenSpec> {

    protected companion object {

        const val ALIAS_ALREADY_TAKEN_ERROR = "This alias is already taken"

    }

    abstract fun generateKey(
        alias: String,
        keyGenSpec: KS,
        purposes: KeyPurposes
    )

    protected abstract fun aliasExists(
        alias: String
    ): Boolean

    abstract fun deleteKey(
        alias: String
    )

}