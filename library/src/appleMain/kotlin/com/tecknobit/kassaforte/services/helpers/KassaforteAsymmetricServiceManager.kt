@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.services.KassaforteAsymmetricService.PRIVATE_KEY_TAG
import com.tecknobit.kassaforte.services.KassaforteAsymmetricService.PUBLIC_KEY_TAG
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR
import com.tecknobit.kassaforte.util.deleteFromKeychain
import com.tecknobit.kassaforte.util.kassaforteDictionary
import com.tecknobit.kassaforte.util.retrieveFromKeychain
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.CoreFoundation.*
import platform.Foundation.CFBridgingRetain
import platform.Security.*

internal class KassaforteAsymmetricServiceManager : KassaforteServiceImplManager() {

    override fun isAliasTaken(
        alias: String,
    ): Boolean {
        val query = searchingDictionary(
            alias = alias,
            keyClass = kSecAttrKeyClassPrivate
        )
        return memScoped {
            val resultContainer = alloc<CFTypeRefVar>()
            val resultStatus = SecItemCopyMatching(
                query = query,
                result = resultContainer.ptr
            )
            resultStatus == errSecSuccess
        }
    }

    override fun retrieveKey(
        alias: String,
    ): String {
        val query = searchingDictionary(
            alias = alias,
            keyClass = when {
                alias.endsWith(PRIVATE_KEY_TAG) -> kSecAttrKeyClassPrivate
                alias.endsWith(PUBLIC_KEY_TAG) -> kSecAttrKeyClassPublic
                else -> throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
            }
        )
        return retrieveFromKeychain(
            query = query
        ) ?: throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
    }

    // TODO TO ANNOTATE WITH @Assembler
    private fun searchingDictionary(
        alias: String,
        keyClass: CFStringRef?,
    ): CFMutableDictionaryRef {
        return kassaforteDictionary(
            capacity = 3,
            addEntries = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrApplicationTag,
                    value = CFBridgingRetain(alias)
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecMatchLimit,
                    value = kSecMatchLimitOne
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecReturnRef,
                    value = kCFBooleanTrue
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeyClass,
                    value = keyClass
                )
            }
        )
    }

    override fun removeKey(
        alias: String,
    ) {
        val query = deletingDictionary(
            alias = alias
        )
        deleteFromKeychain(
            query = query
        )
    }

    // TODO TO ANNOTATE WITH @Assembler
    private fun deletingDictionary(
        alias: String,
    ): CFMutableDictionaryRef {
        return kassaforteDictionary(
            capacity = 1,
            addEntries = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrApplicationTag,
                    value = CFBridgingRetain(alias)
                )
            }
        )
    }

}