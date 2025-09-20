@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.kassaforte.enums.KeyType.Companion.toKeyType
import com.tecknobit.kassaforte.enums.SecKeyAlgorithmType.Companion.toSecKeyAlgorithm
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.DigestType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.helpers.KassaforteAsymmetricServiceManager
import com.tecknobit.kassaforte.util.*
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRetain
import platform.Security.*
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec>() {

    internal const val PRIVATE_KEY_TAG = ".private"

    internal const val PUBLIC_KEY_TAG = ".public"

    private val serviceManager = KassaforteAsymmetricServiceManager()

    actual override fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        if (aliasExists(alias))
            throw RuntimeException(ALIAS_ALREADY_TAKEN_ERROR)
        val usages = resolveUsages(
            alias = alias,
            purposes = purposes
        )
        val genSpec = resolveKeyGenSpec(
            algorithmType = algorithmType,
            keyGenSpec = keyGenSpec,
            usages = usages
        )
        errorScoped { errorVar ->
            SecKeyCreateRandomKey(
                parameters = genSpec,
                error = errorVar.ptr
            )
        }
    }

    // TODO: ANNOTATE WITH @Assembler
    private fun resolveUsages(
        alias: String,
        purposes: KeyPurposes,
    ): Pair<CFMutableDictionaryRef, CFMutableDictionaryRef> {
        val privateKeyAttrs = keyAttrsDictionary(
            tag = "$alias$PRIVATE_KEY_TAG",
            usages = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrCanDecrypt,
                    value = CFBridgingRetain(
                        X = purposes.canDecrypt
                    )
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrCanSign,
                    value = CFBridgingRetain(
                        X = purposes.canSign
                    )
                )
                keyAttrsCipheringDictionary(
                    key = kSecAttrCanUnwrap,
                    purposes = purposes
                )
            }
        )
        val publicKeyAttrs = keyAttrsDictionary(
            tag = "$alias$PUBLIC_KEY_TAG",
            usages = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrCanEncrypt,
                    value = CFBridgingRetain(
                        X = purposes.canEncrypt
                    )
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrCanVerify,
                    value = CFBridgingRetain(
                        X = purposes.canVerify
                    )
                )
                keyAttrsCipheringDictionary(
                    key = kSecAttrCanWrap,
                    purposes = purposes
                )
            }
        )
        return Pair(privateKeyAttrs, publicKeyAttrs)
    }

    // TODO: ANNOTATE WITH @Assembler
    private fun resolveKeyGenSpec(
        algorithmType: AlgorithmType,
        keyGenSpec: AsymmetricKeyGenSpec,
        usages: Pair<CFMutableDictionaryRef, CFMutableDictionaryRef>,
    ): CFMutableDictionaryRef {
        val privateKeyAttrs = usages.first
        val publicKeyAttrs = usages.second
        val genSpec = kassaforteDictionary(
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
        return genSpec
    }

    // TODO: ANNOTATE WITH @Assembler
    private fun keyAttrsDictionary(
        tag: String,
        usages: CFMutableDictionaryRef.() -> Unit,
    ): CFMutableDictionaryRef {
        return kassaforteDictionary(
            capacity = 6,
            addEntries = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrIsPermanent,
                    value = kCFBooleanTrue
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrApplicationTag,
                    value = CFBridgingRetain(tag)
                )
                usages()
            }
        )
    }

    // TODO: ANNOTATE WITH @Assembler
    private fun CFMutableDictionaryRef.keyAttrsCipheringDictionary(
        key: CValuesRef<*>?,
        purposes: KeyPurposes,
    ) {
        CFDictionaryAddValue(
            theDict = this,
            key = key,
            value = CFBridgingRetain(
                X = purposes.canWrapKey
            )
        )
        CFDictionaryAddValue(
            theDict = this,
            key = kSecAttrCanDerive,
            value = CFBridgingRetain(
                X = purposes.canAgree
            )
        )
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean {
        val keychainAlias = resolvePrivateKeyAlias(
            alias = alias
        )
        return serviceManager.isAliasTaken(
            alias = keychainAlias
        )
    }

    actual suspend fun encrypt(
        alias: String,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
        data: Any,
    ): String {
        checkIfIsSupportedType(
            data = data
        )
        val encryptedData = useKey(
            alias = resolvePublicKeyAlias(
                alias = alias
            ),
            paddingType = paddingType,
            digestType = digestType,
            usage = { publicKey, algorithm ->
                val dataToEncrypt = data.toString().toCFData()
                val encryptedData = errorScoped { errorVar ->
                    SecKeyCreateEncryptedData(
                        key = publicKey,
                        algorithm = algorithm,
                        plaintext = dataToEncrypt,
                        error = errorVar.ptr
                    )
                }
                encryptedData.toByteArray()
            }
        )
        return Base64.encode(encryptedData)
    }

    actual suspend fun decrypt(
        alias: String,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
        data: String,
    ): String {
        val decryptedData = useKey(
            alias = resolvePrivateKeyAlias(
                alias = alias
            ),
            paddingType = paddingType,
            digestType = digestType,
            usage = { privateKey, algorithm ->
                val dataToDecrypt = Base64.decode(data).toCFData()
                val decryptedData = errorScoped { errorVar ->
                    SecKeyCreateDecryptedData(
                        key = privateKey,
                        algorithm = algorithm,
                        ciphertext = dataToDecrypt,
                        error = errorVar.ptr
                    )
                }
                decryptedData.toByteArray()
            }
        )
        return decryptedData.decodeToString()
    }

    private inline fun useKey(
        alias: String,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
        usage: (SecKeyRef, SecKeyAlgorithm) -> ByteArray,
    ): ByteArray {
        val key = serviceManager.retrieveKey(
            alias = alias
        )
        val algorithmType = paddingType.toSecKeyAlgorithm(
            digestType = digestType
        ).algorithm!!
        return usage(key, algorithmType)
    }

    actual override fun deleteKey(
        alias: String,
    ) {
        serviceManager.removeKey(
            alias = resolvePrivateKeyAlias(
                alias = alias
            )
        )
        serviceManager.removeKey(
            alias = resolvePublicKeyAlias(
                alias = alias
            )
        )
    }

    // TODO: TO ANNOTATE WITH @Wrapper
    // TODO: TO ANNOTATE WITH @Returner
    private fun resolvePrivateKeyAlias(
        alias: String,
    ): String {
        return resolveAlias(
            alias = alias,
            tag = PRIVATE_KEY_TAG
        )
    }

    // TODO: TO ANNOTATE WITH @Wrapper
    // TODO: TO ANNOTATE WITH @Returner
    private fun resolvePublicKeyAlias(
        alias: String,
    ): String {
        return resolveAlias(
            alias = alias,
            tag = PUBLIC_KEY_TAG
        )
    }

    // TODO: TO ANNOTATE WITH @Returner
    private fun resolveAlias(
        alias: String,
        tag: String,
    ): String {
        return alias + tag
    }

}