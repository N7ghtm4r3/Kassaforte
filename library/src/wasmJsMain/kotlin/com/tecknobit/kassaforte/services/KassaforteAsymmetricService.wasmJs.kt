@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.RequiresDocumentation
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.enums.ExportFormat.PKCS8
import com.tecknobit.kassaforte.enums.ExportFormat.SPKI
import com.tecknobit.kassaforte.enums.NamedCurve.Companion.toNamedCurve
import com.tecknobit.kassaforte.enums.RsaAlgorithmName.Companion.toRsaAlgorithmName
import com.tecknobit.kassaforte.enums.RsaAlgorithmName.RSASSA_PKCS1_v1_5
import com.tecknobit.kassaforte.helpers.asPlainText
import com.tecknobit.kassaforte.helpers.toByteArray
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm.EC
import com.tecknobit.kassaforte.key.genspec.Algorithm.RSA
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.helpers.KassaforteAsymmetricServiceManager
import com.tecknobit.kassaforte.util.decode
import com.tecknobit.kassaforte.util.encode
import com.tecknobit.kassaforte.wrappers.crypto.ecdsaParams
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.EcKeyGenParams
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.RsaHashedKeyGenParams
import com.tecknobit.kassaforte.wrappers.crypto.key.raw.RawCryptoKeyPair
import com.tecknobit.kassaforte.wrappers.crypto.params.EncryptionParams
import com.tecknobit.kassaforte.wrappers.crypto.rsaOaepParams
import com.tecknobit.kassaforte.wrappers.crypto.rsaPKCS1Params

/**
 * The `KassaforteAsymmetricService` class allows to generate and to use asymmetric keys and managing their persistence.
 *
 * It is based on the [SubtleCrypto](https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto)
 * API for the generation of the key pairs, and for their secure storage uses the
 * [IndexedDB](https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API) APIs
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeysService
 * @see AsymmetricKeyGenSpec
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec>() {

    /**
     * `DEFAULT_EC_NAME` constant value to represent the [Algorithm.EC]'s `ECDSA` type
     */
    // TODO: PROVIDE ALSO ECDH WHEN INTEGRATED THE AGREEMENT
    private const val DEFAULT_EC_NAME = "ECDSA"

    /**
     * `serviceManager` instance of the manager which helps the service to perform the operations with the keys
     */
    private val serviceManager = KassaforteAsymmetricServiceManager()

    /**
     * Method used to generate a new asymmetric key
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
        serviceManager.generateKey(
            alias = alias,
            genSpec = {
                resolveKeyGenSpec(
                    algorithm = algorithm,
                    keyGenSpec = keyGenSpec
                )
            },
            purposes = purposes
        )
    }

    /**
     * Method used to resolve the spec to generate a new asymmetric key
     *
     * @param algorithm The algorithm the key will use
     * @param keyGenSpec The generation spec to use to generate the key
     *
     * @return the gen spec as [KeyGenSpec]
     */
    @Returner
    private fun resolveKeyGenSpec(
        algorithm: Algorithm,
        keyGenSpec: AsymmetricKeyGenSpec,
    ): KeyGenSpec {
        return when (algorithm) {
            RSA -> {
                val digest = keyGenSpec.digest ?: throw IllegalArgumentException("The digest must be specified")
                resolveRsaHashedKeyGenParams(
                    name = keyGenSpec.encryptionPadding
                        .toRsaAlgorithmName()
                        .value,
                    modulusLength = keyGenSpec.keySize.bitCount,
                    hash = digest.value
                )
            }

            EC -> {
                resolveEcKeyGenParams(
                    name = DEFAULT_EC_NAME,
                    namedCurve = keyGenSpec.keySize
                        .toNamedCurve()
                        .value
                )
            }

            else -> throw IllegalArgumentException(INVALID_ASYMETRIC_ALGORITHM)
        }
    }

    /**
     * Unused method.
     *
     * It is required to avoid breaking the `expect/actual` implementation
     */
    actual override fun aliasExists(
        alias: String,
    ): Boolean = true

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
        val rawCryptoKeyPair: RawCryptoKeyPair = serviceManager.retrieveKeyData(
            alias = alias
        )
        val encryptedData = serviceManager.useKey(
            rawKey = rawCryptoKeyPair.publicKey,
            rawKeyData = rawCryptoKeyPair,
            format = SPKI,
            usages = rawCryptoKeyPair.publicKeyUsages,
            usage = { key ->
                val encryptedData = serviceManager.encrypt(
                    algorithm = rsaOaepParams(),
                    key = key,
                    data = data
                )
                encode(encryptedData.toByteArray())
            }
        )
        return encryptedData
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
        val rawCryptoKeyPair: RawCryptoKeyPair = serviceManager.retrieveKeyData(
            alias = alias
        )
        val decryptedData = serviceManager.useKey(
            rawKey = rawCryptoKeyPair.privateKey,
            rawKeyData = rawCryptoKeyPair,
            format = PKCS8,
            usage = { key ->
                val dataToDecrypt = decode(data)
                val decryptedData = serviceManager.decrypt(
                    algorithm = rsaOaepParams(),
                    key = key,
                    data = dataToDecrypt
                )
                decryptedData.asPlainText()
            }
        )
        return decryptedData
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
        val rawCryptoKeyPair: RawCryptoKeyPair = serviceManager.retrieveKeyData(
            alias = alias
        )
        return serviceManager.useKey(
            rawKey = rawCryptoKeyPair.privateKey,
            rawKeyData = rawCryptoKeyPair,
            format = PKCS8,
            usage = { key ->
                val signedMessage = serviceManager.sign(
                    algorithm = key.resolveSignatureParams(
                        digest = digest
                    ),
                    key = key,
                    message = message
                ).toByteArray()
                encode(signedMessage)
            }
        )
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
        TODO("Not yet implemented")
    }

    /**
     * Method used to resolve the signature params to use to sign or verify the messages
     *
     * @param digest The digest to apply to sign or verify messages
     *
     * @return the signature params as [EncryptionParams]
     */
    @Returner
    private fun CryptoKey.resolveSignatureParams(
        digest: Digest,
    ): EncryptionParams {
        val algorithm = algorithm.name
        return if (algorithm.contains(RSASSA_PKCS1_v1_5.value))
            rsaPKCS1Params()
        else {
            ecdsaParams(
                hash = digest.value
            )
        }
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
            alias = alias
        )
    }

}

/**
 * Method used to assemble a native [RsaHashedKeyGenParams] object
 *
 * @param name The algorithm which the key will use
 * @param modulusLength The length in bits of the RSA modulus
 * @param hash The public exponent
 *
 * @return the key gen params as [RsaHashedKeyGenParams]
 */
@JsFun(
    """
    (name, modulusLength, hash) => ({
        name: name,
        modulusLength: modulusLength,
        publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
        hash: hash
    })
    """
)
@Assembler
private external fun resolveRsaHashedKeyGenParams(
    name: String,
    modulusLength: Int,
    hash: String,
): RsaHashedKeyGenParams

/**
 * Method used to assemble a native [EcKeyGenParams] object
 *
 * @param name The algorithm which the key will use
 * @param namedCurve A string representing the name of the elliptic curve to use
 *
 * @return the key gen params as [EcKeyGenParams]
 */
@JsFun(
    """
    (name, namedCurve) => ({
        name: name,
        namedCurve: namedCurve
    })
    """
)
@Assembler
private external fun resolveEcKeyGenParams(
    name: String,
    namedCurve: String,
): EcKeyGenParams