package com.tecknobit.kassaforte.services.impls

import com.tecknobit.kassaforte.key.usages.KeyOperation
import java.security.Key

internal abstract class KassaforteServiceImpl {

    abstract fun aliasExists(
        alias: String,
    ): Boolean

    abstract fun getKey(
        alias: String,
        keyOperation: KeyOperation,
    ): Key

    abstract fun deleteKey(
        alias: String,
    )

}