@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.util.UNSUPPORTED_TYPE
import com.tecknobit.kassaforte.util.deleteFromKeychain
import com.tecknobit.kassaforte.util.kassaforteDictionary
import com.tecknobit.kassaforte.util.retrieveFromKeychain
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Security.*
import platform.darwin.NSObject

/**
 * The `Kassaforte` class allows safeguarding sensitive data by leveraging the
 * [Keychain](https://developer.apple.com/documentation/security/keychain-services) APIs
 *
 * @param name A representative name to identify the safeguarded data (e.g., the application name using Kassaforte).
 * This name will be used as [kSecAttrService](https://developer.apple.com/documentation/security/ksecattrservice/)
 *
 * @author Tecknobit - N7ghtm4r3
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Kassaforte actual constructor(
    private val name: String
) {

    /**
     * Method used to safeguard sensitive data
     *
     * @param key The key used to represent the data to safeguard
     * @param data The sensitive data to safeguard
     */
    actual fun safeguard(
        key: String,
        data: Any,
    ) {
        val attributes = addingDictionary(
            key = key,
            data = data
        )
        SecItemAdd(
            attributes = attributes,
            result = null
        )
    }

    /**
     * Method used to assemble the dictionary to use to add the data inside the keychain
     *
     * @param key The key used to represent the data to safeguard
     * @param data The sensitive data to safeguard
     *
     * @return the dictionary with the data to add as [CFMutableDictionaryRef]
     */
    @Assembler
    private fun addingDictionary(
        key: String,
        data: Any
    ) : CFMutableDictionaryRef{
        return kassaforteDictionary(
            capacity = 4,
            addEntries = {
                secAttrService()
                secAttrAccount(
                    key = key
                )
                secClassGenericPassword()
                secValueData(
                    data = data
                )
            }
        )
    }

    /**
     * Method used to refresh sensitive data previously safeguarded
     *
     * @param key The key used to represent the data to safeguard
     * @param data The refreshed sensitive data to safeguard and to replace the currently safeguarded
     */
    actual fun refresh(
        key: String,
        data: Any
    ) {
        val query = refreshingQueryDictionary(
            key = key
        )
        val refreshingAttributes = refreshingDictionary(
            key = key,
            data = data
        )
        SecItemUpdate(
            query = query,
            attributesToUpdate = refreshingAttributes
        )
    }

    /**
     * Method used to assemble the query dictionary to locate the current data stored in the keychain and to
     * replace it
     *
     * @param key The key used to represent the data to safeguard
     *
     * @return the query dictionary as [CFMutableDictionaryRef]
     */
    @Assembler
    private fun refreshingQueryDictionary(
        key: String,
    ) : CFMutableDictionaryRef {
        return kassaforteDictionary(
            capacity = 3,
            addEntries = {
                secAttrService()
                secAttrAccount(
                    key = key
                )
                secClassGenericPassword()
            }
        )
    }

    /**
     * Method used to assemble the dictionary with the refreshed data to update the current data stored inside the keychain
     *
     * @param key The key used to represent the data to safeguard
     * @param data The refreshed sensitive data to safeguard and to replace the currently safeguarded
     *
     * @return the dictionary with the refreshed data as [CFMutableDictionaryRef]
     */
    @Assembler
    private fun refreshingDictionary(
        key: String,
        data: Any
    ) : CFMutableDictionaryRef {
        return kassaforteDictionary(
            capacity = 3,
            addEntries = {
                secAttrService()
                secAttrAccount(
                    key = key
                )
                secValueData(
                    data = data
                )
            }
        )
    }

    /**
     * Method used to withdraw safeguarded data
     *
     * @param key The key of the safeguarded data to withdraw
     *
     * @return the safeguarded data specified by the [key] as nullable [String]
     */
    actual suspend fun withdraw(
        key: String,
    ): String? {
        return unsuspendedWithdraw(
            key = key
        )
    }

    /**
     * Method used to withdraw safeguarded data.
     *
     * This method implements the real logic of the withdrawal without to be `suspend`, the wrapper [withdraw] method is
     * required to be `suspend` to respect the `expect/actual` implementation
     *
     * @param key The key of the safeguarded data to withdraw
     *
     * @return the safeguarded data specified by the [key] as nullable [String]
     */
    internal fun unsuspendedWithdraw(
        key: String,
    ): String? {
        val query = searchingDictionary(
            key = key
        )
        return retrieveFromKeychain(
            query = query
        )
    }

    /**
     * Method used to assemble the searching dictionary to allow the retrieval of the data stored in the keychain
     *
     * @param key The key of the safeguarded data to withdraw
     *
     * @return the searching dictionary as [CFMutableDictionaryRef]
     */
    @Assembler
    private fun searchingDictionary(
        key: String
    ) : CFMutableDictionaryRef {
        return kassaforteDictionary(
            capacity = 5,
            addEntries = {
                secAttrService()
                secAttrAccount(
                    key = key
                )
                secClassGenericPassword()
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecMatchLimit,
                    value = kSecMatchLimitOne
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecReturnData,
                    value = kCFBooleanTrue
                )
            }
        )
    }

    /**
     * Method used to remove safeguarded data
     *
     * @param key The key of the safeguarded data to remove
     */
    actual fun remove(
        key: String
    ) {
        val query = deletingDictionary(
            key = key
        )
        deleteFromKeychain(
            query = query
        )
    }

    /**
     * Method used to assemble the deleting dictionary to allow the deletion of the data stored in the keychain
     *
     * @param key The key of the safeguarded data to remove
     *
     * @return the deleting dictionary as [CFMutableDictionaryRef]
     */
    @Assembler
    private fun deletingDictionary(
        key: String
    ) : CFMutableDictionaryRef {
        return kassaforteDictionary(
            capacity = 3,
            addEntries = {
                secAttrService()
                secAttrAccount(
                    key = key
                )
                secClassGenericPassword()
            }
        )
    }

    /**
     * Method used to add into a [CFMutableDictionaryRef] the
     * [kSecAttrService](https://developer.apple.com/documentation/security/ksecattrservice/) entry
     */
    private fun CFMutableDictionaryRef.secAttrService() {
        CFDictionaryAddValue(
            theDict = this,
            key = kSecAttrService,
            value = CFBridgingRetain(name)
        )
    }

    /**
     * Method used to add into a [CFMutableDictionaryRef] the
     * [kSecAttrAccount](https://developer.apple.com/documentation/security/kSecAttrAccount/) entry
     *
     * @param key The key associated with safeguarded data to assign to the `kSecAttrAccount` property
     */
    private fun CFMutableDictionaryRef.secAttrAccount(
        key: String
    ) {
        CFDictionaryAddValue(
            theDict = this,
            key = kSecAttrAccount,
            value = CFBridgingRetain(key)
        )
    }

    /**
     * Method used to add into a [CFMutableDictionaryRef] the
     * [kSecClass](https://developer.apple.com/documentation/security/kSecClass/) entry, associating to it, the
     * [kSecClass](https://developer.apple.com/documentation/security/kSecClassGenericPassword/) value
     */
    private fun CFMutableDictionaryRef.secClassGenericPassword() {
        CFDictionaryAddValue(
            theDict = this,
            key = kSecClass,
            value = kSecClassGenericPassword
        )
    }

    /**
     * Method used to add into a [CFMutableDictionaryRef] the
     * [kSecValueData](https://developer.apple.com/documentation/security/kSecValueData/) entry
     *
     * @param data The data to safeguard to assign to the `kSecValueData` property
     */
    private fun CFMutableDictionaryRef.secValueData(
        data: Any
    ) {
        CFDictionaryAddValue(
            theDict = this,
            key = kSecValueData,
            value = CFBridgingRetain(data.convert())
        )
    }

    /**
     * Method used to convert an [Any] value into a compatible [NSObject]
     *
     * @return the converted data as [NSObject]
     */
    @Returner
    private fun Any.convert() : NSObject {
        return when(this) {
            is Number, Boolean, String -> this.toNSString()
            else -> throw IllegalArgumentException(UNSUPPORTED_TYPE)
        }
    }

    /**
     * Method used to convert an [Any] value into a compatible [NSString]
     *
     * @return the converted data as [NSString]
     */
    @Returner
    private fun Any.toNSString(): NSString {
        val string = this.toString()
        return NSString.create(
            string = string
        )
    }

}