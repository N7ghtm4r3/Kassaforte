package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.key.genspec.KassaforteKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes

abstract class KassaforteKeysService<KS: KassaforteKeyGenSpec> {

    companion object {

        const val ALIAS_ALREADY_TAKEN_ERROR = "This alias is already taken"

        const val IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR = "Impossible to retrieve the specified key"

        const val KEY_CANNOT_PERFORM_OPERATION_ERROR = "The key cannot perform this operation: %s"

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