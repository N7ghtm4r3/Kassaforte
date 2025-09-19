package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

import org.khronos.webgl.Uint8Array

external interface RsaHashedKeyGenParams : KeyGenSpec {

    val modulusLength: Int

    val publicExponent: Uint8Array

    val hash: String

}

