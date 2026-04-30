@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.util

import kotlinx.cinterop.*
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess

/**
 * Utility method used to retrieve an item from the [Keychain](https://developer.apple.com/documentation/security/keychain-services)
 *
 * @param query The query to retrieve the requested item
 *
 * @return the retrieved item as nullable [String]
 */
@Suppress("UNCHECKED_CAST")
fun retrieveFromKeychain(
    query: CFMutableDictionaryRef,
): String? {
    return memScoped {
        val resultContainer = alloc<CFTypeRefVar>()
        val resultStatus = SecItemCopyMatching(
            query = query,
            result = resultContainer.ptr
        )
        if (resultStatus != errSecSuccess)
            return null

        val storedData = CFBridgingRelease(resultContainer.value) as NSData

        NSString.create(
            data = storedData,
            encoding = NSUTF8StringEncoding
        )?.toString()
    }
}

/**
 * Utility method used to delete an item from the [Keychain](https://developer.apple.com/documentation/security/keychain-services)
 *
 * @param query The query to delete the requested item
 */
fun deleteFromKeychain(
    query: CFMutableDictionaryRef,
) {
    SecItemDelete(
        query = query
    )
}