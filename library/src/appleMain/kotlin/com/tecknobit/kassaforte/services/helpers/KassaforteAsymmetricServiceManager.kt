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

/**
 * The `KassaforteAsymmetricServiceManager` class allows to perform keychain's operations on asymmetric keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceManager
 * @see KassaforteServiceImplManager
 */
internal class KassaforteAsymmetricServiceManager : KassaforteServiceImplManager<SecKeyRef>() {

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
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

    /**
     * Method used to retrieve from the secure storage the specified key
     *
     * @param alias The alias used to store the key
     *
     * @return the key as [SecKeyRef]
     *
     * @throws RuntimeException when the alias does not indicate a private or public key
     */
    override fun retrieveKey(
        alias: String,
    ): SecKeyRef {
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

    /**
     * Method used to assemble the searching dictionary to allow the retrieval of the key stored in the keychain
     *
     * @param alias The alias which identify the key
     * @param keyClass The class of the key to retrieve ([kSecAttrKeyClassPrivate] or [kSecAttrKeyClassPublic])
     *
     * @return the searching dictionary as [CFMutableDictionaryRef]
     */
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

    /**
     * Method used to remove from the secure store the specified key
     *
     * @param alias The alias of the key to remove
     */
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

    /**
     * Method used to assemble the deleting dictionary to allow the deletion of the keypair stored in the keychain
     *
     * @param alias The alias which identify the key
     *
     * @return the deleting dictionary as [CFMutableDictionaryRef]
     */
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