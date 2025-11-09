@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.RequiresDocumentation
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.annotations.Wrapper
import com.tecknobit.kassaforte.enums.KeyType.Companion.toKeyType
import com.tecknobit.kassaforte.enums.SecKeyAlgorithmType.Companion.toSecKeyAlgorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.NONE
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_PKCS1
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.helpers.KassaforteAsymmetricServiceManager
import com.tecknobit.kassaforte.util.*
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryGetValue
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRetain
import platform.Security.*

/**
 * The `KassaforteAsymmetricService` class allows to generate and to use asymmetric keys and managing their persistence.
 *
 * It is based on the [SecKeyCreateRandomKey](https://developer.apple.com/documentation/security/seckeycreaterandomkey(_:_:))
 * method for the generation of the key pairs, and for their secure storage uses the
 * [Keychain](https://developer.apple.com/documentation/security/keychain-services) APIs
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeysService
 * @see AsymmetricKeyGenSpec
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec>() {

    /**
     * `PRIVATE_KEY_TAG` the tag to apply to an alias to identify the private key
     */
    internal const val PRIVATE_KEY_TAG = ".private"

    /**
     * `PUBLIC_KEY_TAG` the tag to apply to an alias to identify the public key
     */
    internal const val PUBLIC_KEY_TAG = ".public"

    /**
     * `serviceManager` instance of the manager which helps the service to perform the operations with the keys
     */
    private val serviceManager = KassaforteAsymmetricServiceManager()

    /**
     * Method used to generate an asymmetric key
     *
     * @param alias The alias used to identify the key
     * @param algorithm The algorithm the key will use
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    actual override fun generateKey(
        alias: String,
        algorithm: Algorithm,
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
            algorithm = algorithm,
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

    /**
     * Method used to resolve the usages where the keys can be used from the specified [purposes]
     *
     * @param alias The alias used to identify the key
     * @param purposes The purposes the key can be used
     *
     * @return the usages for the private and public keys as [Pair] of [CFMutableDictionaryRef]
     */
    @Assembler
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

    /**
     * Method used to resolve the spec to generate a new asymmetric key
     *
     * @param algorithm The algorithm the key will use
     * @param keyGenSpec The generation spec to use to generate the key
     * @param usages The usages the key can be used
     *
     * @return the gen spec as [CFMutableDictionaryRef]
     */
    @Assembler
    private fun resolveKeyGenSpec(
        algorithm: Algorithm,
        keyGenSpec: AsymmetricKeyGenSpec,
        usages: Pair<CFMutableDictionaryRef, CFMutableDictionaryRef>,
    ): CFMutableDictionaryRef {
        val privateKeyAttrs = usages.first
        val publicKeyAttrs = usages.second
        val genSpec = kassaforteDictionary(
            capacity = 4,
            addEntries = {
                val attrKeyType = algorithm.toKeyType()
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

    /**
     * Method used to create the dictionary with the information of a key, private or public
     *
     * @param tag The tag which represent the key
     * @param usages The usages the key can be used
     *
     * @return the dictionary with the key information as [CFMutableDictionaryRef]
     */
    @Assembler
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

    /**
     * Method used to create the dictionary with the ciphering information
     * ([KeyPurposes.canWrapKey] and [KeyPurposes.canAgree]) related to a key
     *
     * @param key The key to assign the ciphering information
     * @param purposes The usages the key can be used
     */
    @Assembler
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

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
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

    /**
     * Method used to encrypt data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param padding The padding to apply to encrypt data
     * @param digest The digest to apply to encrypt data
     * @param data The data to encrypt
     *
     * @return the encrypted data as [String]
     */
    actual suspend fun encrypt(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        data: Any,
    ): String {
        val encryptedData = useKey(
            alias = resolvePublicKeyAlias(
                alias = alias
            ),
            padding = padding,
            digest = digest,
            usage = { publicKey, algorithm ->
                val dataToEncrypt = data.convertToCFData()
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
        return encode(encryptedData)
    }

    /**
     * Method used to decrypt the encrypted data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param padding The padding to apply to decrypt data
     * @param digest The digest to apply to decrypt data
     * @param data The data to decrypt
     *
     * @return the decrypted data as [String]
     */
    actual suspend fun decrypt(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        data: String,
    ): String {
        val decryptedData = useKey(
            alias = resolvePrivateKeyAlias(
                alias = alias
            ),
            padding = padding,
            digest = digest,
            usage = { privateKey, algorithm ->
                val dataToDecrypt = decode(data).toCFData()
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

    /**
     * Method used to sign messages with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param digest The digest to apply to sign messages
     * @param message The message to sign
     *
     * @return the signed message as [String]
     *
     * @since Revision Two
     */
    actual suspend fun sign(
        alias: String,
        digest: Digest,
        message: Any,
    ): String {
        val signature = useKey(
            alias = alias,
            padding = RSA_PKCS1,
            digest = digest,
            usage = { key, algorithm ->
                val dataToSign = message.encodeForKeyOperation()
                val signedMessage = errorScoped { error ->
                    SecKeyCreateSignature(
                        key = key,
                        algorithm = algorithm,
                        dataToSign = dataToSign.toCFData(),
                        error = error.ptr
                    )
                }
                signedMessage.toByteArray()
            }
        )
        return encode(signature)
    }

    @RequiresDocumentation(
        additionalNotes = "INSERT SINCE Revision Two"
    )
    actual suspend fun verify(
        alias: String,
        digest: Digest,
        signature: String,
        message: Any,
    ): Boolean {
        return useKey(
            alias = alias,
            padding = RSA_PKCS1,
            digest = digest,
            usage = { key, algorithm ->
                val signedData = message.encodeForKeyOperation()
                val result = errorScoped { error ->
                    SecKeyVerifySignature(
                        key = key,
                        algorithm = algorithm,
                        signedData = signedData.toCFData(),
                        signature = decode(signature).toCFData(),
                        error = error.ptr
                    )
                }
                result
            }
        )
    }

    /**
     * Method used to work and to use a key (private or public) to perform encryption or decryption of the data
     *
     * @param alias The alias which identify the key to use
     * @param padding The padding to apply to ciphering data
     * @param digest The digest to apply to ciphering data
     * @param usage The ciphering routine to perform
     *
     * @return the ciphered data as [ByteArray]
     *
     * @param T The type of the result obtained using the key
     */
    private inline fun <reified T> useKey(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        usage: (SecKeyRef, SecKeyAlgorithm) -> T,
    ): T {
        val key = serviceManager.retrieveKey(
            alias = alias
        )
        val attributes = SecKeyCopyAttributes(
            key = key
        )
        val keyType = CFDictionaryGetValue(
            theDict = attributes,
            key = kSecAttrKeyType
        )
        val encryptionPadding = if (keyType == kSecAttrKeyTypeEC)
            NONE
        else
            padding
        val algorithmType = encryptionPadding.toSecKeyAlgorithm(
            digest = digest
        ).algorithm!!
        return usage(key, algorithmType)
    }

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
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

    /**
     * Method used to resolve the alias of a private key
     *
     * @param alias The alias of the key to resolve
     *
     * @return the resolved alias of the private key as [String]
     */
    @Wrapper
    @Returner
    private fun resolvePrivateKeyAlias(
        alias: String,
    ): String {
        return resolveAlias(
            alias = alias,
            tag = PRIVATE_KEY_TAG
        )
    }

    /**
     * Method used to resolve the alias of a public key
     *
     * @param alias The alias of the key to resolve
     *
     * @return the resolved alias of the public key as [String]
     */
    @Wrapper
    @Returner
    private fun resolvePublicKeyAlias(
        alias: String,
    ): String {
        return resolveAlias(
            alias = alias,
            tag = PUBLIC_KEY_TAG
        )
    }

    /**
     * Method used to resolve the alias of an asymmetric key
     *
     * @param alias The alias of the key to resolve
     * @param tag The specific tag of the key to resolve
     *
     * @return the resolved alias of the asymmetric key as [String]
     */
    @Returner
    private fun resolveAlias(
        alias: String,
        tag: String,
    ): String {
        return alias + tag
    }

}