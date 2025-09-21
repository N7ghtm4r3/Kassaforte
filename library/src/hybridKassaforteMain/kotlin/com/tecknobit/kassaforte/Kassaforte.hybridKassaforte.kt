package com.tecknobit.kassaforte

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.BlockModeType.CBC
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.PKCS7
import com.tecknobit.kassaforte.key.genspec.KeySize.S256
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteSymmetricService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The `Kassaforte` class allows safeguarding sensitive data by leveraging the native APIs of each platform.
 * The `hybrid` definition means that is not provided a native secure storage so, before saving the data, will be
 * automatically encrypted.
 *
 * - `Android` uses the [SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences) APIs, encrypting the data before storing it
 * - `Web` uses the [LocalStorage](https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage) APIs, encrypting the data before storing it
 *
 * @param name A representative name to identify the safeguarded data (e.g., the application name using Kassaforte). This name will be
 * properly used by each platform to identify the application owner of the safeguarded data
 *
 * @author Tecknobit - N7ghtm4r3
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Kassaforte actual constructor(
    name: String
) {

    private companion object {

        /**
         * `SECRET_KEY` the key used to represent the secret key used to encrypt and decrypt the stored data
         */
        const val SECRET_KEY = "kassaforte"

    }

    /**
     * `kassaforteScope` the coroutine scope used by the kassaforte to perform background routines
     */
    private val kassaforteScope = CoroutineScope(
        context = Dispatchers.Main
    )

    /**
     * `manager` the manager which perform the operation of the kassaforte
     */
    private val manager = KassaforteManager(
        kassaforteName = name
    )

    init {
        generateSecretKeyIfMissing()
    }

    /**
     * Method used to generate the secret key to handle the encryption and decryption of the data
     */
    private fun generateSecretKeyIfMissing() {
        try {
            KassaforteSymmetricService.generateKey(
                algorithmType = AlgorithmType.AES,
                alias = SECRET_KEY,
                keyGenSpec = SymmetricKeyGenSpec(
                    keySize = S256,
                    encryptionPadding = PKCS7,
                    blockMode = CBC
                ),
                purposes = KeyPurposes(
                    canEncrypt = true,
                    canDecrypt = true
                )
            )
        } catch (_: Exception) {
        }
    }

    /**
     * Method used to safeguard sensitive data
     *
     * @param key The key used to represent the data to safeguard
     * @param data The sensitive data to safeguard
     */
    actual fun safeguard(
        key: String,
        data: Any,
    ) {
        safelyStore(
            key = key,
            data = data
        )
    }

    /**
     * Method used to withdraw safeguarded data
     *
     * @param key The key of the safeguarded data to withdraw
     *
     * @return the safeguarded data specified by the [key] as nullable [String]
     */
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

    /**
     * Method used to decrypt the data before return it
     *
     * @param data The encrypted data to decrypt
     *
     * @return the decrypted data as [String]
     */
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

    /**
     * Method used to refresh sensitive data previously safeguarded
     *
     * @param key The key used to represent the data to safeguard
     * @param data The refreshed sensitive data to safeguard and to replace the currently safeguarded
     */
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

    /**
     * Method used to safely store the data inside the kassaforte
     *
     * @param key The key used to represent the data to store
     * @param data The sensitive data to store
     */
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

    /**
     * Method used to encrypt the data before store it
     *
     * @param data The data to encrypt
     *
     * @return the encrypted data as [String]
     */
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

    /**
     * Method used to remove safeguarded data
     *
     * @param key The key of the safeguarded data to remove
     */
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

    /**
     * Method used to perform an action only when the key is already stored
     *
     * @param key The key which must be already stored
     * @param then The action to perform if the key is stored
     */
    private inline fun whenKeyIsStored(
        key: String,
        crossinline then: () -> Unit,
    ) {
        if (!manager.hasKeyStored(key))
            throw IllegalStateException("This key is currently not stored")
        then()
    }
    
}