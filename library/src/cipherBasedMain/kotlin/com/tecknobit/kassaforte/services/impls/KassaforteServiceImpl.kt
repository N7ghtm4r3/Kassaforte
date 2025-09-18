package com.tecknobit.kassaforte.services.impls

internal abstract class KassaforteServiceImpl {

    abstract fun aliasExists(
        alias: String,
    ): Boolean

    abstract fun deleteKey(
        alias: String,
    )

}