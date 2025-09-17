@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte

import com.tecknobit.kassaforte.util.UNSUPPORTED_TYPE
import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Security.*
import platform.darwin.NSObject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Kassaforte actual constructor(
    private val name: String
) {

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

    // TODO TO ANNOTATE WITH @Assembler
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

    // TODO TO ANNOTATE WITH @Assembler
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

    // TODO TO ANNOTATE WITH @Assembler
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

    actual suspend fun withdraw(
        key: String,
    ): String? {
        return unsuspendedWithdraw(
            key = key
        )
    }

    // TODO: TO INDICATE WHY AT THE MOMENT THIS METHOD IS REQUIRED
    internal fun unsuspendedWithdraw(
        key: String,
    ): String? {
        val query = searchingDictionary(
            key = key
        )
        return memScoped {
            val resultContainer = alloc<CFTypeRefVar>()
            val resultStatus = SecItemCopyMatching(
                query = query,
                result = resultContainer.ptr
            )
            val storedData = CFBridgingRelease(resultContainer.value)
            if(resultStatus == errSecSuccess)
                storedData.toString()
            else
                null
        }
    }

    // TODO TO ANNOTATE WITH @Returner
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
                    value = CFBridgingRetain(true)
                )
            }
        )
    }

    actual fun remove(
        key: String
    ) {
        val query = deletingDictionary(
            key = key
        )
        SecItemDelete(
            query = query
        )
    }

    // TODO TO ANNOTATE WITH @Returner
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

    // TODO TO ANNOTATE WITH @Returner
    private fun kassaforteDictionary(
        capacity: Long,
        addEntries: CFMutableDictionaryRef.() -> Unit
    ) : CFMutableDictionaryRef {
        val dictionary = CFDictionaryCreateMutable(
            allocator = null,
            capacity = capacity,
            keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
            valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr
        )!!
        dictionary.addEntries()
        return dictionary
    }

    private fun CFMutableDictionaryRef.secAttrService() {
        CFDictionaryAddValue(
            theDict = this,
            key = kSecAttrService,
            value = CFBridgingRetain(name)
        )
    }

    private fun CFMutableDictionaryRef.secAttrAccount(
        key: String
    ) {
        CFDictionaryAddValue(
            theDict = this,
            key = kSecAttrAccount,
            value = CFBridgingRetain(key)
        )
    }

    private fun CFMutableDictionaryRef.secClassGenericPassword() {
        CFDictionaryAddValue(
            theDict = this,
            key = kSecClass,
            value = kSecClassGenericPassword
        )
    }

    private fun CFMutableDictionaryRef.secValueData(
        data: Any
    ) {
        CFDictionaryAddValue(
            theDict = this,
            key = kSecValueData,
            value = CFBridgingRetain(data.convert())
        )
    }

    // TODO TO ANNOTATE WITH @Returner
    private fun Any.convert() : NSObject {
        return when(this) {
            is Number, Boolean, String -> this.toNSString()
            else -> throw IllegalArgumentException(UNSUPPORTED_TYPE)
        }
    }

    // TODO TO ANNOTATE WITH @Returner
    private fun Any.toNSString(): NSString {
        val string = this.toString()
        return NSString.create(
            string = string
        )
    }

}