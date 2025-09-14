package com.tecknobit.kassaforte.services

internal external object SubtleCrypto {

    fun generateKey(
        algorithm: KeyGenSpec,
        extractable: Boolean,
        keyUsages: JsArray<JsString>,
    ): JsAny

}