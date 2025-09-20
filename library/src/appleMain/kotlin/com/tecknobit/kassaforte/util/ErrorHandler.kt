@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.util

import kotlinx.cinterop.*
import platform.CoreFoundation.CFErrorCopyDescription
import platform.CoreFoundation.CFErrorRefVar

fun <T> errorScoped(
    routine: (CFErrorRefVar) -> T?,
): T {
    memScoped {
        val errorVar = nativeHeap.alloc<CFErrorRefVar>()
        val result = routine(errorVar)
        if (result == null) {
            handleError(
                errorVar = errorVar
            )
        }
        return result!!
    }
}

private fun handleError(
    errorVar: CFErrorRefVar,
) {
    val cfError = errorVar.value
    val description = cfError?.let { error ->
        CFErrorCopyDescription(error)?.toString()
    } ?: "Unknown error"
    throw Exception(description)
}