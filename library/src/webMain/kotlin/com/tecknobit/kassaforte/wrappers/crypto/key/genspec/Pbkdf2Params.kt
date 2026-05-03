package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

import org.khronos.webgl.ArrayBuffer

// TODO: TO DOCU SINCE
external interface Pbkdf2Params : KeyGenSpec {

    val hash: String

    val salt: ArrayBuffer

    val iterations: Int

}