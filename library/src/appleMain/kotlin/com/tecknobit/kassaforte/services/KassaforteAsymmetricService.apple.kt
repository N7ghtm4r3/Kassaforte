@file:OptIn(ExperimentalForeignApi::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.annotations.Wrapper
import com.tecknobit.kassaforte.enums.KeyType.Companion.toKeyType
import com.tecknobit.kassaforte.enums.SecKeyAlgorithmType.Companion.toSecKeyAlgorithm
import com.tecknobit.kassaforte.key.genspec.*
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.NONE
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_PKCS1
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.helpers.KassaforteAsymmetricServiceManager
import com.tecknobit.kassaforte.util.*
import kotlinx.cinterop.*
import platform.CoreFoundation.*
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
            tag = resolvePrivateKeyAlias(alias),
            usages = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeyClass,
                    value = kSecAttrKeyClassPrivate
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrCanDecrypt,
                    value = if (purposes.canDecrypt)
                        kCFBooleanTrue
                    else
                        kCFBooleanFalse
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrCanSign,
                    value = if (purposes.canSign)
                        kCFBooleanTrue
                    else
                        kCFBooleanFalse
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrCanWrap,
                    value = if (purposes.canWrapKey)
                        kCFBooleanTrue
                    else
                        kCFBooleanFalse
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrAccessible,
                    value = kSecAttrAccessibleWhenUnlocked
                )
            }
        )

        val publicKeyAttrs = keyAttrsDictionary(
            tag = resolvePublicKeyAlias(alias),
            usages = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeyClass,
                    value = kSecAttrKeyClassPublic
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrCanEncrypt,
                    value = if (purposes.canEncrypt)
                        kCFBooleanTrue
                    else
                        kCFBooleanFalse
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrCanVerify,
                    value = if (purposes.canVerify)
                        kCFBooleanTrue
                    else
                        kCFBooleanFalse
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

        return kassaforteDictionary(
            capacity = 4,
            addEntries = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeyType,
                    value = algorithm.toKeyType().typeProvider()
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeySizeInBits,
                    value = CFBridgingRetain(keyGenSpec.keySize.bitCount)
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
                    value = tag.toCFData()
                )
                usages()
            }
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
        val signedMessage = useKey(
            alias = alias,
            padding = RSA_PKCS1,
            digest = digest,
            usage = { key, algorithm ->
                val dataToSign = message.encodeForKeyOperation()
                val signature = errorScoped { error ->
                    SecKeyCreateSignature(
                        key = key,
                        algorithm = algorithm,
                        dataToSign = dataToSign.toCFData(),
                        error = error.ptr
                    )
                }

                signature.toByteArray()
            }
        )

        return encode(signedMessage)
    }

    /**
     * Method used to verify the validity of the messages previously signed with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param digest The digest applied to sign [message]
     * @param message The message to verify
     * @param signature The signature previously computed
     *
     * @return whether the message matches to [signature] as [Boolean]
     *
     * @since Revision Two
     */
    actual suspend fun verify(
        alias: String,
        digest: Digest,
        message: Any,
        signature: String,
    ): Boolean {
        val result = useKey(
            alias = alias,
            padding = RSA_PKCS1,
            digest = digest,
            usage = { key, algorithm ->
                val signedData = message.encodeForKeyOperation()
                errorScoped { error ->
                    SecKeyVerifySignature(
                        key = key,
                        algorithm = algorithm,
                        signedData = signedData.toCFData(),
                        signature = decode(signature).toCFData(),
                        error = error.ptr
                    )
                }
            }
        )

        return result
    }

    /**
     * Method to perform an `Envelopment Encryption` for wrapping a `DEK` material
     *
     * @param kekAlias The alias which identify the `KEK` key to use
     * @param padding The padding to apply to wrap the material
     * @param digest The digest to apply to wrap material
     * @param dekBytes Arbitrary bytes representing the `DEK` material to wrap
     *
     * @return the [dekBytes] wrapped using the specified KEK key as `Base64` [String]
     *
     * @since Revision Three
     */
    actual suspend fun wrap(
        kekAlias: String,
        padding: EncryptionPadding,
        digest: Digest,
        dekBytes: ByteArray,
    ): String {
        return encrypt(
            alias = kekAlias,
            padding = padding,
            digest = digest,
            data = encode(dekBytes)
        )
    }

    /**
     * Method to perform an `Envelopment Decryption` for unwrapping a `DEK` material previously
     * wrapped
     *
     * @param kekAlias The alias which identify the `KEK` key to use
     * @param padding The padding to apply to unwrap the material
     * @param digest The digest to apply to unwrap the material
     * @param wrappedDek The wrapped material, `Base64` encoded, to unwrap
     *
     * @return the material unwrapped using the specified KEK key as [ByteArray]
     *
     * @since Revision Three
     */
    actual suspend fun unwrap(
        kekAlias: String,
        padding: EncryptionPadding,
        digest: Digest,
        wrappedDek: String,
    ): ByteArray {
        val unwrappedDek = decrypt(
            alias = kekAlias,
            padding = padding,
            digest = digest,
            data = wrappedDek
        )

        return decode(unwrappedDek)
    }

    // TODO: TO DOCU SINCE
    actual suspend fun agree(
        alias: String,
        peerPublicKey: ByteArray,
        publicKeyLength: KeySize,
        secretLength: KeySize,
    ): String {
        val publicKey = resolvePublicKey(
            peerPublicKey = peerPublicKey,
            keySize = publicKeyLength
        )

        val secret = useKey(
            alias = resolvePrivateKeyAlias(
                alias = alias
            ),
            padding = null,
            digest = null,
            usage = { privateKey, _ ->
                errorScoped { errorVar ->
                    SecKeyCopyKeyExchangeResult(
                        privateKey = privateKey,
                        algorithm = kSecKeyAlgorithmECDHKeyExchangeStandard,
                        publicKey = publicKey,
                        parameters = null,
                        error = errorVar.ptr
                    )
                }
            }
        )

        return encode(secret.toByteArray())
    }

    //TODO: TO DOCU SINCE
    @Returner
    private fun resolvePublicKey(
        peerPublicKey: ByteArray,
        keySize: KeySize,
    ): SecKeyRef {
        val data = peerPublicKey.usePinned { pinnedPublicKey ->
            CFDataCreate(
                allocator = kCFAllocatorDefault,
                bytes = pinnedPublicKey.addressOf(0).reinterpret(),
                length = peerPublicKey.size.toLong()
            )
        }

        val attributes = kassaforteDictionary(
            capacity = 3,
            addEntries = {
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeyType,
                    value = kSecAttrKeyTypeECSECPrimeRandom
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeyClass,
                    value = kSecAttrKeyClassPublic
                )
                CFDictionaryAddValue(
                    theDict = this,
                    key = kSecAttrKeySizeInBits,
                    value = CFBridgingRetain(keySize.bitCount)
                )
            }
        )

        return errorScoped { errorVar ->
            SecKeyCreateWithData(
                keyData = data,
                attributes = attributes,
                error = errorVar.ptr
            )
        }
    }

    /**
     * Method used to work and to use a key (private or public) to perform encryption or decryption of the data
     *
     * @param alias The alias which identify the key to use
     * @param padding The padding to apply to ciphering data
     * @param digest The digest to apply to ciphering data
     * @param usage The ciphering routine to perform
     *
     * @return the result from [usage] as [T]
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