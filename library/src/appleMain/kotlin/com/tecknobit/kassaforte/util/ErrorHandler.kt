@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.util

import kotlinx.cinterop.*
import platform.CoreFoundation.CFErrorCopyDescription
import platform.CoreFoundation.CFErrorRefVar

/**
 * Scoped method to handle an error if necessary
 *
 * @param routine The routine to perform to obtain a result.
 *
 * If an error occurred during its execution will be automatically handled by the [handleError] method
 *
 * @return the result of the [routine] if no error occurred
 */
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

/**
 * Method used to handle an occurred error
 *
 * @param errorVar The native error reference to handle
 */
private fun handleError(
    errorVar: CFErrorRefVar,
) {
    val cfError = errorVar.value
    val description = cfError?.let { error ->
        CFErrorCopyDescription(error)?.toString()
    } ?: "Unknown error"
    throw Exception(description)
}