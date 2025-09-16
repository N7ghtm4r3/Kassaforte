package com.tecknobit.kassaforte.helpers

import com.tecknobit.equinoxcore.annotations.Returner
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

@Returner
fun ArrayBuffer.toByteArray(): ByteArray {
    val uInt8 = Uint8Array(this)
    return ByteArray(uInt8.length) { index -> uInt8[index] }
}

@Returner
fun ByteArray.toUint8Array(): Uint8Array {
    val uint8Array = Uint8Array(this.size)
    val array = this.map { it.toInt().toJsNumber() }.toJsArray()
    uint8Array.set(
        array = array
    )
    return uint8Array
}

@Returner
fun ByteArray.toArrayBuffer(): ArrayBuffer {
    return this.toUint8Array().buffer
}