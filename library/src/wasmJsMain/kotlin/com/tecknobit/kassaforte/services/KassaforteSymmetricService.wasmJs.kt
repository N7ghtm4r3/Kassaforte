@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.helpers.*
import com.tecknobit.kassaforte.key.genspec.AlgorithmType.AES
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.BlockModeType.CBC
import com.tecknobit.kassaforte.key.genspec.BlockModeType.CTR
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.util.checkIfIsSupportedType
import com.tecknobit.kassaforte.wrappers.crypto.*
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.key.RawCryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.params.AesCbcParams
import com.tecknobit.kassaforte.wrappers.crypto.params.AesCtrParams
import com.tecknobit.kassaforte.wrappers.crypto.params.AesGcmParams
import com.tecknobit.kassaforte.wrappers.crypto.params.AesParams
import kotlinx.coroutines.*
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.coroutines.resume
import kotlin.io.encoding.Base64


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    private val subtleCrypto = subtleCrypto()

    private val serviceScope = CoroutineScope(Dispatchers.Main)

    actual override fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        performIfAliasAvailable(
            alias = alias,
            onNotAvailable = { return@performIfAliasAvailable },
            onAvailable = {
                val genSpec = resolveKeyGenSpec(
                    algorithm = AES.value,
                    blockType = keyGenSpec.blockMode.value,
                    size = keyGenSpec.keySize.bitCount
                )
                val keyUsages = resolveUsages(
                    purposes = purposes
                )
                serviceScope.launch {
                    val key: CryptoKey = subtleCrypto.generateKey(
                        algorithm = genSpec,
                        extractable = true,
                        keyUsages = keyUsages
                    ).await()
                    storeKey(
                        alias = alias,
                        key = key
                    )
                }
            }
        )
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean = true

    private fun performIfAliasAvailable(
        alias: String,
        onNotAvailable: () -> Unit,
        onAvailable: () -> Unit,
    ) {
        IndexedDBManager.checkIfAliasExists(
            alias = alias,
            onKeyExists = { onNotAvailable() },
            onKeyNotFound = { onAvailable() }
        )
    }

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

    private fun storeKey(
        alias: String,
        key: CryptoKey,
    ) {
        serviceScope.launch {
            val exportedKey: ArrayBuffer = subtleCrypto.exportKey(
                format = RAW_EXPORT_FORMAT,
                key = key
            ).await()
            IndexedDBManager.addKey(
                alias = alias,
                key = key,
                keyData = exportedKey
            )
        }
    }

    actual suspend fun encrypt(
        alias: String,
        blockModeType: BlockModeType,
        paddingType: EncryptionPaddingType,
        data: Any,
    ): String {
        checkIfIsSupportedType(
            data = data
        )
        val rawKey = obtainRawKey(
            alias = alias
        )
        val encryptedData = useKey(
            rawKey = rawKey,
            usage = { key ->
                val aesParams = key.resolveAesParams()
                val encryptedData: ArrayBuffer = subtleCrypto.encrypt(
                    algorithm = aesParams.first,
                    key = key,
                    data = data.prepareToEncrypt(
                        blockModeType = blockModeType
                    )
                ).await()
                val iv = aesParams.second.toByteArray()
                val encryptedDataBytes = encryptedData.toByteArray()
                Base64.encode(iv + encryptedDataBytes)
            }
        )
        return encryptedData
    }

    actual suspend fun decrypt(
        alias: String,
        blockModeType: BlockModeType,
        paddingType: EncryptionPaddingType,
        data: String,
    ): String {
        val rawKey = obtainRawKey(
            alias = alias
        )
        val decryptedData = useKey(
            rawKey = rawKey,
            usage = { key ->
                val blockSize = blockModeType.blockSize
                val dataToDecrypt = Base64.decode(data)
                val iv = dataToDecrypt.copyOfRange(0, blockSize)
                val cipherText = dataToDecrypt.copyOfRange(blockSize, dataToDecrypt.size)
                val aesParams = key.resolveAesParams(
                    iv = iv.toArrayBuffer()
                )
                val decryptedData: ArrayBuffer = subtleCrypto.decrypt(
                    algorithm = aesParams.first,
                    key = key,
                    data = cipherText.prepareToDecrypt()
                ).await()
                val plainText = decryptedData.asPlainText(
                    blockModeType = blockModeType
                )
                plainText
            }
        )
        return decryptedData
    }

    @Returner
    private suspend fun obtainRawKey(
        alias: String,
    ): RawCryptoKey {
        return suspendCancellableCoroutine { continuation ->
            IndexedDBManager.getAndUseKeyData(
                alias = alias,
                onSuccess = { _, rawKey -> continuation.resume(rawKey) },
                onError = { eventError -> throw RuntimeException(eventError.type) }
            )
        }
    }

    private suspend fun useKey(
        rawKey: RawCryptoKey,
        usage: suspend (CryptoKey) -> String,
    ): String {
        val keyData = rawKey.keyData.toDecodedKeyData()
        val key: CryptoKey = subtleCrypto.importKey(
            format = RAW_EXPORT_FORMAT,
            keyData = keyData,
            algorithm = rawKey.algorithm,
            extractable = rawKey.extractable,
            keyUsages = rawKey.usages.unsafeCast()
        ).await()
        return usage(key)
    }

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

    @Returner
    private fun CryptoKey.resolveAesParams(
        iv: ArrayBuffer = ArrayBuffer(0),
    ): Pair<AesParams, ArrayBuffer> {
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

    actual override fun deleteKey(
        alias: String,
    ) {
        IndexedDBManager.removeKey(
            alias = alias
        )
    }

}

@JsFun(
    """
    (algorithm, blockType, bitCount) => ({
        name: `${'$'}{algorithm}-${'$'}{blockType}`,
        length: bitCount
    })
    """
)
@Returner
private external fun resolveKeyGenSpec(
    algorithm: String,
    blockType: String,
    size: Int,
): KeyGenSpec