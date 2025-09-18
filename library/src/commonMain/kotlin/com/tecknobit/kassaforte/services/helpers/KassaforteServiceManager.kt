package com.tecknobit.kassaforte.services.helpers

interface KassaforteServiceManager<K> {

    fun isAliasTaken(
        alias: String,
    ): Boolean

    fun retrieveKey(
        alias: String,
    ): K

    fun removeKey(
        alias: String,
    )

}