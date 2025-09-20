@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.util

import kotlinx.cinterop.*
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.CFBridgingRelease
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess

fun retrieveFromKeychain(
    query: CFMutableDictionaryRef,
): String? {
    return memScoped {
        val resultContainer = alloc<CFTypeRefVar>()
        val resultStatus = SecItemCopyMatching(
            query = query,
            result = resultContainer.ptr
        )
        val storedData = CFBridgingRelease(resultContainer.value)
        if (resultStatus == errSecSuccess)
            storedData.toString()
        else
            null
    }
}

fun deleteFromKeychain(
    query: CFMutableDictionaryRef,
) {
    SecItemDelete(
        query = query
    )
}