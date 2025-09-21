@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.enums.ExportFormat.RAW
import com.tecknobit.kassaforte.helpers.asPlainText
import com.tecknobit.kassaforte.helpers.toArrayBuffer
import com.tecknobit.kassaforte.helpers.toByteArray
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm.AES
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.BlockMode.CBC
import com.tecknobit.kassaforte.key.genspec.BlockMode.CTR
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.helpers.KassaforteSymmetricServiceManager
import com.tecknobit.kassaforte.util.checkIfIsSupportedType
import com.tecknobit.kassaforte.wrappers.crypto.aesCbcParams
import com.tecknobit.kassaforte.wrappers.crypto.aesCtrParams
import com.tecknobit.kassaforte.wrappers.crypto.aesGcmParams
import com.tecknobit.kassaforte.wrappers.crypto.key.CryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.key.raw.RawCryptoKey
import com.tecknobit.kassaforte.wrappers.crypto.params.AesCbcParams
import com.tecknobit.kassaforte.wrappers.crypto.params.AesCtrParams
import com.tecknobit.kassaforte.wrappers.crypto.params.AesGcmParams
import com.tecknobit.kassaforte.wrappers.crypto.params.EncryptionParams
import org.khronos.webgl.ArrayBuffer
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    private val serviceManager = KassaforteSymmetricServiceManager()

    actual override fun generateKey(
        algorithm: Algorithm,
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        serviceManager.generateKey(
            alias = alias,
            genSpec = {
                resolveKeyGenSpec(
                    algorithm = AES.value,
                    blockType = keyGenSpec.blockMode.value,
                    size = keyGenSpec.keySize.bitCount
                )
            },
            purposes = purposes
        )
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean = true

    actual suspend fun encrypt(
        alias: String,
        blockMode: BlockMode,
        padding: EncryptionPadding,
        data: Any,
    ): String {
        checkIfIsSupportedType(
            data = data
        )
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
                    key = key,
                    data = data
                )
                val iv = aesParams.second.toByteArray()
                val encryptedDataBytes = encryptedData.toByteArray()
                Base64.encode(iv + encryptedDataBytes)
            }
        )
        return encryptedData
    }

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
                val dataToDecrypt = Base64.decode(data)
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

    actual override fun deleteKey(
        alias: String,
    ) {
        serviceManager.removeKey(
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
): com.tecknobit.kassaforte.wrappers.crypto.key.genspec.SymmetricKeyGenSpec