@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.helpers.IndexedDBManager
import com.tecknobit.kassaforte.key.KeyPurposes
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.wrappers.RAW_EXPORT_FORMAT
import com.tecknobit.kassaforte.wrappers.cryptokey.CryptoKey
import com.tecknobit.kassaforte.wrappers.cryptokey.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.subtleCrypto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.khronos.webgl.ArrayBuffer


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    private val subtleCrypto = subtleCrypto()

    private val serviceScope = CoroutineScope(Dispatchers.Main)

    actual override fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        checkIfAliasExists(
            alias = alias,
            onExists = { throw IllegalStateException(ALIAS_ALREADY_TAKEN_ERROR) },
            onNotExists = {
                val genSpec = resolveKeyGenSpec(
                    algorithm = keyGenSpec.algorithm.value,
                    blockType = keyGenSpec.blockMode.value,
                    size = keyGenSpec.keySize ?: 128
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

    private fun checkIfAliasExists(
        alias: String,
        onExists: () -> Unit,
        onNotExists: () -> Unit,
    ) {
        IndexedDBManager.checkIfKeyExists(
            alias = alias,
            onKeyExists = { onExists() },
            onError = { onNotExists() }
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

    actual fun encrypt(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        data: Any,
    ): String {
        val subtleCrypto = subtleCrypto()
        TODO("Not yet implemented")
    }

    actual fun decrypt(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        data: String,
    ): String {
        TODO("Not yet implemented")
    }

    actual override fun deleteKey(
        alias: String
    ) {
        TODO("Not yet implemented")
    }

}

@JsFun(
    """
    (algorithm, blockType, size) => ({
        name: `${'$'}{algorithm}-${'$'}{blockType}`,
        length: size
    })
    """
)
@Returner
private external fun resolveKeyGenSpec(
    algorithm: String,
    blockType: String,
    size: Int,
): KeyGenSpec