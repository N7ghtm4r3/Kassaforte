package com.tecknobit.kassaforte.wrappers.crypto.params

import org.khronos.webgl.ArrayBuffer

external interface AesGcmParams : AesParams {

    val iv: ArrayBuffer

}