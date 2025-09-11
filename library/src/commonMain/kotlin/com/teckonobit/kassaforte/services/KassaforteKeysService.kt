package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.keys.KassaforteKey

abstract class KassaforteKeysService<K : KassaforteKey> {

    abstract fun generate(): K

    abstract fun encrypt(
        data: Any
    ): String

    abstract fun decrypt(
        data: String
    ): Any

}