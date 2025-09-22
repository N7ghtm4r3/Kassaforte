@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.annotations.Structure
import com.tecknobit.kassaforte.enums.ExportFormat
import com.tecknobit.kassaforte.helpers.IndexedDBManager
import com.tecknobit.kassaforte.helpers.prepareToDecrypt
import com.tecknobit.kassaforte.helpers.prepareToEncrypt
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.params.EncryptionParams
import com.tecknobit.kassaforte.wrappers.crypto.subtleCrypto
import kotlinx.coroutines.*
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.coroutines.resume
import kotlin.io.encoding.Base64

/**
 * The `KassaforteServiceImplManager` class allows to perform operations that [KassaforteAsymmetricServiceManager]
 * and [KassaforteSymmetricServiceManager] have in common
 *
 * It is particularly useful to avoid to break the `expect/actual` implementation and clean implement shared code avoiding
 * duplication
 *
 * @param K The type of the key the managers handles
 * @param RK The type of the raw key the managers have to retrive from the secure storage
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceManager
 */
@Structure
internal abstract class KassaforteServiceImplManager<K : JsAny, RK : CryptoKey> : KassaforteServiceManager<K> {

    /**
     * `subtleCrypto` the instance which handles the keys generation and usages
     */
    private val subtleCrypto = subtleCrypto()

    /**
     * `managerScope` the scope of the manager to perform routines in background
     */
    protected val managerScope = CoroutineScope(
        context = Dispatchers.Default
    )

    /**
     * Method used to generate a new key
     *
     * @param alias The alias used to identify the key
     * @param genSpec The callback to invoke to get the gen spec of the key to generate
     * @param purposes The purposes the key can be used
     */
    fun generateKey(
        alias: String,
        genSpec: () -> KeyGenSpec,
        purposes: KeyPurposes,
    ) {
        IndexedDBManager.checkIfAliasExists(
            alias = alias,
            onKeyExists = { return@checkIfAliasExists },
            onKeyNotFound = {
                managerScope.launch {
                    generateAndStore(
                        alias = alias,
                        genSpec = genSpec(),
                        usages = resolveUsages(
                            purposes = purposes
                        )
                    )
                }
            }
        )
    }

    /**
     * Method used to resolve the usages where the keys can be used from the specified [purposes]
     *
     * @param purposes The purposes the key can be used
     *
     * @return the usages for the keys as [JsArray] of [JsString]
     *
     * @throws IllegalStateException when the combination of the usages is not valid
     */
    // TODO: TO DOCU ABOUT THE COMBINATION WITH USAGES AND KEYGEN 
    @Assembler
    private fun resolveUsages(
        purposes: KeyPurposes,
    ): JsArray<JsString> {
        val keyUsages = mutableListOf<String>()
        if (purposes.canEncrypt)
            keyUsages.add("encrypt")
        if (purposes.canDecrypt)
            keyUsages.add("decrypt")
        if (purposes.canSign)
            keyUsages.add("sign")
        if (purposes.canVerify)
            keyUsages.add("verify")
        if (purposes.canWrapKey)
            keyUsages.add("wrapKey")
        if (purposes.canAgree)
            keyUsages.add("deriveKey")
        if (keyUsages.isEmpty())
            throw IllegalStateException("Key usages not valid")
        return keyUsages.map { it.toJsString() }.toJsArray()
    }

    /**
     * Method used to generate a new key and after secure store it
     *
     * @param alias The alias used to identify the key
     * @param genSpec The gen spec of the key to generate
     * @param usages The usages assigned to the key
     */
    private suspend fun generateAndStore(
        alias: String,
        genSpec: KeyGenSpec,
        usages: JsArray<JsString>,
    ) {
        val generatedKey: K = subtleCrypto.generateKey(
            algorithm = genSpec,
            extractable = true,
            keyUsages = usages
        ).await()
        store(
            alias = alias,
            algorithm = genSpec,
            generatedKey = generatedKey
        )
    }

    /**
     * Method used to secure store a new generated key
     *
     * @param alias The alias used to identify the key
     * @param algorithm The gen spec of the key to generate
     * @param generatedKey The newly generated key
     */
    protected abstract fun store(
        alias: String,
        algorithm: KeyGenSpec,
        generatedKey: K,
    )

    /**
     * Method used to export a valid key allowing to use it.
     *
     * This process is required to correctly use the key, because it will be unusable using directly the [key] instance
     *
     * @param key The key to export
     * @param format The format which the key must be exported
     *
     * @return the exported key value as [ArrayBuffer]
     */
    @Returner
    protected suspend fun exportKey(
        key: CryptoKey,
        format: ExportFormat,
    ): ArrayBuffer {
        return subtleCrypto.exportKey(
            format = format.value,
            key = key
        ).await()
    }

    /**
     * Method used to retrieve from the secure storage the key data
     *
     * @param alias The alias used to identify the key
     *
     * @return the retrieved key data as [RK]
     */
    suspend fun retrieveKeyData(
        alias: String,
    ): RK {
        return suspendCancellableCoroutine { continuation ->
            IndexedDBManager.getAndUseKeyData<RK>(
                alias = alias,
                onSuccess = { _, rawKeyData -> continuation.resume(rawKeyData) },
                onError = { eventError -> throw RuntimeException(eventError.type) }
            )
        }
    }

    /**
     * Method used to work and to use a key to perform encryption or decryption of the data
     *
     * @param rawKey The raw key to use to encrypt or decrypt data
     * (when is an asymmetric algorithm must be specified which key to use)
     * @param rawKeyData The raw key data retrieved by the [retrieveKeyData] method
     * @param usages The usages assigned to the key (when is an asymmetric algorithm must be specified which usages are
     * assigned to the [rawKey])
     * @param format The format which the key has been previously exported
     * @param usage The ciphering routine to perform
     *
     * @return the ciphered data as [ByteArray]
     */
    suspend inline fun useKey(
        rawKey: String,
        rawKeyData: CryptoKey,
        usages: JsArray<JsString> = rawKeyData.usages,
        format: ExportFormat,
        usage: (CryptoKey) -> String,
    ): String {
        val keyData = rawKey.toDecodedKeyData()
        val key: CryptoKey = subtleCrypto.importKey(
            format = format.value,
            keyData = keyData,
            algorithm = rawKeyData.algorithm,
            extractable = rawKeyData.extractable,
            keyUsages = usages
        ).await()
        return usage(key)
    }

    /**
     * Method to decode the data of the key, encoded in [Base64] format, and obtain the usable bytes of the key
     *
     * @return the decoded usable bytes of the key as [ArrayBuffer]
     */
    @Returner
    private fun String.toDecodedKeyData(): ArrayBuffer {
        val encodedKeyData = Base64.decode(this)
        val uint8Array = Uint8Array(encodedKeyData.size)
        val mappedSourceArray = encodedKeyData
            .map { (it.toInt() and 0xFF).toJsNumber() }
            .toJsArray()
        uint8Array.set(
            array = mappedSourceArray
        )
        return uint8Array.buffer
    }

    /**
     * Method used to encrypt data with the specified key
     *
     * @param algorithm The algorithm to use to encrypt the data
     * @param key The key to use to encrypt the data
     * @param data The data to encrypt
     *
     * @return the encrypted data as [ArrayBuffer]
     */
    suspend fun encrypt(
        algorithm: EncryptionParams,
        key: CryptoKey,
        data: Any,
    ): ArrayBuffer {
        return subtleCrypto.encrypt(
            algorithm = algorithm,
            key = key,
            data = data.prepareToEncrypt()
        ).await()
    }

    /**
     * Method used to decrypt data with the specified key
     *
     * @param algorithm The algorithm to use to decrypt the data
     * @param key The key to use to decrypt the data
     * @param data The data to decrypt
     *
     * @return the decrypted data as [ArrayBuffer]
     */
    suspend fun decrypt(
        algorithm: EncryptionParams,
        key: CryptoKey,
        data: Any,
    ): ArrayBuffer {
        return subtleCrypto.decrypt(
            algorithm = algorithm,
            key = key,
            data = data.prepareToDecrypt()
        ).await()
    }

    /**
     * Method used to remove from the secure store the specified key
     *
     * @param alias The alias of the key to remove
     */
    override fun removeKey(
        alias: String,
    ) {
        IndexedDBManager.removeKey(
            alias = alias
        )
    }

    /**
     * Unused method.
     *
     * It is required to avoid breaking the `expect/actual` implementation
     */
    override fun isAliasTaken(
        alias: String,
    ): Boolean = true

    /**
     * Unused method.
     *
     * It is required to avoid breaking the `expect/actual` implementation
     */
    override fun retrieveKey(
        alias: String,
    ): K = TODO("UNUSED")

}