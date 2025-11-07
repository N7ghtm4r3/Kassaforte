@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.RequiresDocumentation
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.enums.ExportFormat.RAW
import com.tecknobit.kassaforte.helpers.asPlainText
import com.tecknobit.kassaforte.helpers.toArrayBuffer
import com.tecknobit.kassaforte.helpers.toByteArray
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm.*
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.BlockMode.CBC
import com.tecknobit.kassaforte.key.genspec.BlockMode.CTR
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.helpers.KassaforteSymmetricServiceManager
import com.tecknobit.kassaforte.util.decode
import com.tecknobit.kassaforte.util.encode
import com.tecknobit.kassaforte.wrappers.crypto.aesCbcParams
import com.tecknobit.kassaforte.wrappers.crypto.aesCtrParams
import com.tecknobit.kassaforte.wrappers.crypto.aesGcmParams
import com.tecknobit.kassaforte.wrappers.crypto.hmacParams
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.key.raw.RawCryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.params.AesCbcParams
import com.tecknobit.kassaforte.wrappers.crypto.params.AesCtrParams
import com.tecknobit.kassaforte.wrappers.crypto.params.AesGcmParams
import com.tecknobit.kassaforte.wrappers.crypto.params.EncryptionParams
import org.khronos.webgl.ArrayBuffer

/**
 * The `KassaforteSymmetricService` class allows to generate and to use symmetric keys and managing their persistence.
 *
 * It is based on the [SubtleCrypto](https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto)
 * API for the generation of the keys, and for their secure storage uses the
 * [IndexedDB](https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API) APIs
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeysService
 * @see SymmetricKeyGenSpec
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    /**
     * `serviceManager` instance of the manager which helps the service to perform the operations with the keys
     */
    private val serviceManager = KassaforteSymmetricServiceManager()

    /**
     * Method used to generate a new symmetric key
     *
     * @param alias The alias used to identify the key
     * @param algorithm The algorithm the key will use
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    actual override fun generateKey(
        alias: String,
        algorithm: Algorithm,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        serviceManager.generateKey(
            alias = alias,
            genSpec = {
                resolveAESGenSpec(
                    algorithm = algorithm,
                    keyGenSpec = keyGenSpec
                )
            },
            purposes = purposes
        )
    }

    @RequiresDocumentation(
        additionalNotes = "TO INSERT SINCE Revision Two"
    )
    @Returner
    private fun resolveAESGenSpec(
        algorithm: Algorithm,
        keyGenSpec: SymmetricKeyGenSpec,
    ): KeyGenSpec {
        return when (algorithm) {
            AES -> {
                resolveAESKeyGenSpec(
                    algorithm = algorithm.value,
                    blockType = keyGenSpec.blockMode.value,
                    size = keyGenSpec.keySize.bitCount
                )
            }

            HMAC_SHA1 -> {
                resolveHMACKeyGenSpec(
                    hash = "SHA-1"
                )
            }

            HMAC_SHA256 -> {
                resolveHMACKeyGenSpec(
                    hash = "SHA-256"
                )
            }

            HMAC_SHA384 -> {
                resolveHMACKeyGenSpec(
                    hash = "SHA-384"
                )
            }

            HMAC_SHA512 -> {
                resolveHMACKeyGenSpec(
                    hash = "SHA-512"
                )
            }

            else -> throw IllegalArgumentException("Invalid symmetric algorithm to generate a symmetric key")
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
     * @param blockMode The block mode to use to encrypt data
     * @param padding The padding to apply to encrypt data
     * @param data The data to encrypt
     *
     * @return the encrypted data as [String]
     */
    actual suspend fun encrypt(
        alias: String,
        blockMode: BlockMode,
        padding: EncryptionPadding,
        data: Any,
    ): String {
        val rawKey: RawCryptoKey = serviceManager.retrieveKeyData(
            alias = alias
        )
        val encryptedData = serviceManager.useKey(
            rawKey = rawKey.key,
            rawKeyData = rawKey,
            format = RAW,
            usage = { key ->
                val aesParams = key.resolveAesParams()
                val encryptedData = serviceManager.encrypt(
                    algorithm = aesParams.first,
                    blockMode = blockMode,
                    key = key,
                    data = data
                )
                val iv = aesParams.second.toByteArray()
                val encryptedDataBytes = encryptedData.toByteArray()
                encode(iv + encryptedDataBytes)
            }
        )
        return encryptedData
    }

    /**
     * Method used to decrypt encrypted data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param blockMode The block mode to use to decrypt data
     * @param padding The padding to apply to decrypt data
     * @param data The data to decrypt
     *
     * @return the decrypted data as [String]
     */
    actual suspend fun decrypt(
        alias: String,
        blockMode: BlockMode,
        padding: EncryptionPadding,
        data: String,
    ): String {
        val rawKey: RawCryptoKey = serviceManager.retrieveKeyData(
            alias = alias
        )
        val decryptedData = serviceManager.useKey(
            rawKey = rawKey.key,
            rawKeyData = rawKey,
            format = RAW,
            usage = { key ->
                val blockSize = blockMode.blockSize
                val dataToDecrypt = decode(data)
                val iv = dataToDecrypt.copyOfRange(0, blockSize)
                val cipherText = dataToDecrypt.copyOfRange(blockSize, dataToDecrypt.size)
                val aesParams = key.resolveAesParams(
                    iv = iv.toArrayBuffer()
                )
                val decryptedData: ArrayBuffer = serviceManager.decrypt(
                    algorithm = aesParams.first,
                    key = key,
                    data = cipherText
                )
                val plainText = decryptedData.asPlainText(
                    blockMode = blockMode
                )
                plainText
            }
        )
        return decryptedData
    }

    /**
     * Method used to resolve which [EncryptionParams] it is required to perform the encryption or the decryption
     *
     * @param iv The initialization vector to adopt in the decryption, during the encryption will be automatically generated
     * by the library
     *
     * @return the encryption params and the related initialization vector as [Pair] of [EncryptionParams] and [ArrayBuffer]
     */
    @Returner
    private fun CryptoKey.resolveAesParams(
        iv: ArrayBuffer = ArrayBuffer(0),
    ): Pair<EncryptionParams, ArrayBuffer> {
        val algorithm = algorithm.name
        return when {
            algorithm.endsWith(CBC.value) -> {
                val aesCbcParams: AesCbcParams = aesCbcParams(algorithm, iv)
                Pair(aesCbcParams, aesCbcParams.iv)
            }

            algorithm.endsWith(CTR.value) -> {
                val aesCtrParams: AesCtrParams = aesCtrParams(algorithm, iv)
                Pair(aesCtrParams, aesCtrParams.counter)
            }

            else -> {
                val aesGcmParams: AesGcmParams = aesGcmParams(algorithm, iv)
                Pair(aesGcmParams, aesGcmParams.iv)
            }
        }
    }

    @RequiresDocumentation(
        additionalNotes = "TO INSERT SINCE Revision Two"
    )
    actual suspend fun sign(
        alias: String,
        message: Any,
    ): String {
        val rawKey: RawCryptoKey = serviceManager.retrieveKeyData(
            alias = alias
        )
        val signedData = serviceManager.useKey(
            rawKey = rawKey.key,
            rawKeyData = rawKey,
            format = RAW,
            usage = { key ->
                serviceManager.sign(
                    algorithm = hmacParams("SHA-512"),
                    key = key,
                    data = message
                ).asPlainText()
            }
        )
        return encode(signedData)
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
 * Method used to assemble a native [com.tecknobit.kassaforte.wrappers.crypto.key.genspec.SymmetricKeyGenSpec] object
 *
 * @param algorithm The algorithm which the key will use
 * @param blockType The type of the block the key will have to use during encryption and decryption
 * @param size The length in bits of the key to generate
 *
 * @return the key gen params as [com.tecknobit.kassaforte.wrappers.crypto.key.genspec.SymmetricKeyGenSpec]
 */
@JsFun(
    """
    (algorithm, blockType, bitCount) => ({
        name: `${'$'}{algorithm}-${'$'}{blockType}`,
        length: bitCount
    })
    """
)
@Assembler
private external fun resolveAESKeyGenSpec(
    algorithm: String,
    blockType: String,
    size: Int,
): com.tecknobit.kassaforte.wrappers.crypto.key.genspec.SymmetricKeyGenSpec

@RequiresDocumentation(
    additionalNotes = "TO INSERT SINCE Revision Two"
)
@JsFun(
    """
    (hash) => ({
        name: `HMAC`,
        hash: hash
    })
    """
)
@Assembler
private external fun resolveHMACKeyGenSpec(
    hash: String,
): com.tecknobit.kassaforte.wrappers.crypto.key.genspec.HmacKeyGenParams