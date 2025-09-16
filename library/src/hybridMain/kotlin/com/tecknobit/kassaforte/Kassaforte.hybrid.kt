package com.tecknobit.kassaforte

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.KeyPurposes
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.BlockModeType.CBC
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.PKCS7
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.services.KassaforteSymmetricService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Kassaforte actual constructor(
    name: String
) {

    private companion object {

        const val SECRET_KEY = "kassaforte"

    }

    private val kassaforteScope = CoroutineScope(
        context = Dispatchers.Main
    )

    private val manager = KassaforteManager(
        kassaforteName = name
    )

    init {
        generateSecretKeyIfMissing()
    }

    private fun generateSecretKeyIfMissing() {
        try {
            KassaforteSymmetricService.generateKey(
                alias = SECRET_KEY,
                keyGenSpec = SymmetricKeyGenSpec(
                    algorithm = AlgorithmType.AES,
                    keySize = 256,
                    blockMode = CBC,
                    encryptionPadding = PKCS7
                ),
                purposes = KeyPurposes(
                    canEncrypt = true,
                    canDecrypt = true
                )
            )
        } catch (_: Exception) {
        }
    }

    actual fun safeguard(
        key: String,
        data: Any,
    ) {
        safelyStore(
            key = key,
            data = data
        )
    }

    actual suspend fun withdraw(
        key: String,
    ): String? {
        val storedData = manager.retrieve(
            key = key
        )
        if (storedData == null)
            return null
        return decryptData(
            data = storedData
        )
    }

    @Returner
    private suspend fun decryptData(
        data: String,
    ): String {
        return withContext(
            context = Dispatchers.Main
        ) {
            KassaforteSymmetricService.decrypt(
                alias = SECRET_KEY,
                blockModeType = CBC,
                paddingType = PKCS7,
                data = data
            )
        }
    }

    actual fun refresh(
        key: String,
        data: Any
    ) {
        whenKeyIsStored(
            key = key,
            then = {
                safelyStore(
                    key = key,
                    data = data
                )
            }
        )
    }

    private fun safelyStore(
        key: String,
        data: Any,
    ) {
        kassaforteScope.launch {
            val encryptedData = encryptData(
                data = data
            )
            manager.store(
                key = key,
                data = encryptedData
            )
        }
    }

    @Returner
    private suspend fun encryptData(
        data: Any,
    ): String {
        val encryptedData = KassaforteSymmetricService.encrypt(
            alias = SECRET_KEY,
            blockModeType = CBC,
            paddingType = PKCS7,
            data = data
        )
        return encryptedData
    }

    actual fun remove(
        key: String
    ) {
        whenKeyIsStored(
            key = key,
            then = {
                manager.remove(
                    key = key
                )
            }
        )
    }

    private inline fun whenKeyIsStored(
        key: String,
        crossinline then: () -> Unit,
    ) {
        if (!manager.hasKeyStored(key))
            throw IllegalStateException("This key is currently not stored")
        then()
    }
    
}