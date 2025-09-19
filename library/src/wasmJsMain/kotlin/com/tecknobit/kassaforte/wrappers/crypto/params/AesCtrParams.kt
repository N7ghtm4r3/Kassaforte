package com.tecknobit.kassaforte.wrappers.crypto.params

import org.khronos.webgl.ArrayBuffer

external interface AesCtrParams : EncryptionParams {

    val counter: ArrayBuffer

    val length: Int

}