@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.enums.KeyType.Companion.toKeyType
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.DigestType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.util.kassaforteDictionary
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.Foundation.CFBridgingRetain
import platform.Security.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec>() {

    private const val PRIVATE_KEY_TAG = ".private"

    private const val PUBLIC_KEY_TAG = ".public"

    actual override fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        val privateKeyAttrs = keyAttrsDictionary(
            tag = "$alias$PRIVATE_KEY_TAG"
        )
        val publicKeyAttrs = keyAttrsDictionary(
            tag = "$alias$PUBLIC_KEY_TAG"
        )
        val dictionary = kassaforteDictionary(
            capacity = 4,
            addEntries = {
                val attrKeyType = algorithmType.toKeyType()
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeyType,
                    value = CFBridgingRetain(attrKeyType)
                )
                val keySize = keyGenSpec.keySize.bitCount
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeySizeInBits,
                    value = CFBridgingRetain(keySize)
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecPrivateKeyAttrs,
                    value = privateKeyAttrs
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecPublicKeyAttrs,
                    value = publicKeyAttrs
                )
            }
        )
    }

    // TODO: ANNOTATE WITH @Assembler
    private fun keyAttrsDictionary(
        tag: String,
    ): CFMutableDictionaryRef {
        return kassaforteDictionary(
            capacity = 2,
            addEntries = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrIsPermanent,
                    value = CFBridgingRetain(true)
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrApplicationTag,
                    value = CFBridgingRetain(tag)
                )
            }
        )
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean {
        TODO("Not yet implemented")
    }

    actual suspend fun encrypt(
        alias: String,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
        data: Any,
    ): String {
        TODO("Not yet implemented")
    }

    actual suspend fun decrypt(
        alias: String,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
        data: String,
    ): String {
        TODO("Not yet implemented")
    }

    actual override fun deleteKey(
        alias: String,
    ) {
    }

}