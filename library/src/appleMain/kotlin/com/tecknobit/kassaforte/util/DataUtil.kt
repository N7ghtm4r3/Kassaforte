@file:OptIn(ExperimentalForeignApi::class, ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.util

import kotlinx.cinterop.*
import platform.CoreFoundation.*

// TODO: TO ANNOTATE WITH @Returner
fun ByteArray?.toCFData(): CFDataRef? {
    return if (this == null || this.isEmpty()) {
        CFDataCreate(
            allocator = kCFAllocatorDefault,
            bytes = null,
            length = 0
        )
    } else this.usePinned { pinned ->
        CFDataCreate(
            allocator = kCFAllocatorDefault,
            bytes = pinned.addressOf(0).reinterpret(),
            length = this.size.toLong()
        )
    }
}

// TODO: TO ANNOTATE WITH @Returner
fun String.toCFData(): CFDataRef? {
    val dataToConvert = this.encodeToByteArray()
    return dataToConvert.toCFData()
}

// TODO: TO ANNOTATE WITH @Returner
fun CFDataRef?.toByteArray(): ByteArray {
    val emptyData = ByteArray(0)
    if (this == null)
        return emptyData
    val length = CFDataGetLength(
        theData = this
    ).toInt()
    if (length == 0)
        return emptyData
    val ptr = CFDataGetBytePtr(
        theData = this
    ) ?: return emptyData
    return ByteArray(length) { i -> ptr[i].toByte() }
}
