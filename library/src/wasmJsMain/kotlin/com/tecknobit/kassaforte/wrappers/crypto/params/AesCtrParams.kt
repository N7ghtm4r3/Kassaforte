package com.tecknobit.kassaforte.wrappers.crypto.params

import org.khronos.webgl.ArrayBuffer

external interface AesCtrParams : AesParams {

    val counter: ArrayBuffer

    val length: Int

}