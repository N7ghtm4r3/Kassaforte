@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.services.KassaforteAsymmetricService.PRIVATE_KEY_TAG
import com.tecknobit.kassaforte.services.KassaforteAsymmetricService.PUBLIC_KEY_TAG
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR
import com.tecknobit.kassaforte.util.deleteFromKeychain
import com.tecknobit.kassaforte.util.kassaforteDictionary
import com.tecknobit.kassaforte.util.toCFData
import kotlinx.cinterop.*
import platform.CoreFoundation.*
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
            alias = alias
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
            keyClass = alias.resolveKeyClassByAlias()
        )

        return memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(
                query = query,
                result = result.ptr
            )
            if (status != errSecSuccess)
                throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)

            @Suppress("UNCHECKED_CAST")
            result.value as SecKeyRef
        }
    }

    /**
     * Method used to assemble the searching dictionary to allow the retrieval of the key stored in the keychain
     *
     * @param alias The alias which identify the key
     * @param keyClass The class of the key to retrieve ([kSecAttrKeyClassPrivate] or [kSecAttrKeyClassPublic])
     *
     * @return the searching dictionary as [CFMutableDictionaryRef]
     */
    @Assembler
    private fun searchingDictionary(
        alias: String,
        keyClass: CFStringRef? = null,
    ): CFMutableDictionaryRef {
        return kassaforteDictionary(
            capacity = 3,
            addEntries = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrApplicationTag,
                    value = alias.toCFData()
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
                    key = kSecClass,
                    value = kSecClassKey
                )
                keyClass?.let {
                    CFDictionaryAddValue(
                        theDict = this,
                        key = kSecAttrKeyClass,
                        value = keyClass
                    )
                }
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
            alias = alias,
            keyClass = alias.resolveKeyClassByAlias()
        )

        deleteFromKeychain(
            query = query
        )
    }

    /**
     * Method used to resolve the class type of the requested key using related alias
     *
     * @return The class type of the key as nullable [CFStringRef]
     *
     * @since Revision Three
     */
    @Returner
    private fun String.resolveKeyClassByAlias(): CFStringRef? {
        return when {
            endsWith(PRIVATE_KEY_TAG) -> kSecAttrKeyClassPrivate
            endsWith(PUBLIC_KEY_TAG) -> kSecAttrKeyClassPublic
            else -> throw RuntimeException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
        }
    }

    /**
     * Method used to assemble the deleting dictionary to allow the deletion of the keypair stored in the keychain
     *
     * @param alias The alias which identify the key
     *
     * @return the deleting dictionary as [CFMutableDictionaryRef]
     */
    @Assembler
    private fun deletingDictionary(
        alias: String,
        keyClass: CFStringRef?,
    ): CFMutableDictionaryRef {

        return kassaforteDictionary(
            capacity = 2,
            addEntries = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecClass,
                    value = kSecClassKey
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeyClass,
                    value = keyClass
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrApplicationTag,
                    value = alias.toCFData()
                )
            }
        )
    }

}