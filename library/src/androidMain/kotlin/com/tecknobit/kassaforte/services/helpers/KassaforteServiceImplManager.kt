package com.tecknobit.kassaforte.services.helpers

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.KassaforteKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import java.security.Key
import java.security.KeyStore

/**
 * The `KassaforteServiceImplManager` class allows to perform operations that [com.tecknobit.kassaforte.services.impls.KassaforteSymmetricServiceImpl]
 * and [com.tecknobit.kassaforte.services.impls.KassaforteAsymmetricServiceImpl] have in common
 *
 * It is particularly useful to avoid to break the `expect/actual` implementation and clean implement shared code avoiding
 * duplication
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceManager
 */
internal class KassaforteServiceImplManager : KassaforteServiceManager<Key> {

    internal companion object {

        /**
         * `ANDROID_KEYSTORE` the name of the keystore provided by Android
         */
        const val ANDROID_KEYSTORE = "AndroidKeyStore"

    }

    /**
     * `keyStore` the keystore instance used to handle the operation on the keys
     */
    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    /**
     * Method used to resolve the specified gen spec to generate a new key
     *
     * @param alias The alias used to identify the key
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     *
     * @return the resolved gen spec as [KeyGenParameterSpec.Builder]
     */
    @Assembler
    fun resolveGenSpec(
        alias: String,
        keyGenSpec: KassaforteKeyGenSpec,
        purposes: KeyPurposes,
    ): KeyGenParameterSpec.Builder {
        return KeyGenParameterSpec.Builder(
            alias,
            resolvePurposes(
                keyPurposes = purposes
            )
        ).run {
            setKeySize(keyGenSpec.keySize.bitCount)
        }
    }

    /**
     * Method used to resolve the specified purposes value to generate the new key
     *
     * @param keyPurposes The specified purposes
     *
     * @return the resolved purposes values as [Int]
     *
     * @throws IllegalStateException when the specified purposes are not valid
     */
    @Assembler
    private fun resolvePurposes(
        keyPurposes: KeyPurposes,
    ): Int {
        var purposes = 0
        if (keyPurposes.canEncrypt)
            purposes = purposes or KeyProperties.PURPOSE_ENCRYPT
        if (keyPurposes.canDecrypt)
            purposes = purposes or KeyProperties.PURPOSE_DECRYPT
        if (keyPurposes.canSign)
            purposes = purposes or KeyProperties.PURPOSE_SIGN
        if (keyPurposes.canVerify)
            purposes = purposes or KeyProperties.PURPOSE_VERIFY
        if (keyPurposes.canWrapKey && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            purposes = purposes or KeyProperties.PURPOSE_WRAP_KEY
        if (keyPurposes.canAgree && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            purposes = purposes or KeyProperties.PURPOSE_AGREE_KEY
        if (purposes == 0)
            throw IllegalStateException("Key purposes not valid")
        return purposes
    }

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
    override fun isAliasTaken(
        alias: String,
    ): Boolean {
        return keyStore.isKeyEntry(alias)
    }

    /**
     * Method used to retrieve from the secure storage the specified key
     *
     * @param alias The alias used to store the key
     *
     * @return the key as [Key]
     */
    override fun retrieveKey(
        alias: String,
    ): Key {
        return keyStore.getKey(alias, null)
    }

    /**
     * Method used to remove from the secure store the specified key
     *
     * @param alias The alias of the key to remove
     */
    override fun removeKey(
        alias: String,
    ) {
        keyStore.deleteEntry(alias)
    }

    /**
     * Method used to perform an extra operation with the [keyStore] instance
     *
     * @param keyStoreOpe The operation to perform
     *
     * @param T the type of the result from the execution of the [keyStoreOpe]
     *
     * @return the result of the [keyStoreOpe] execution as [T]
     */
    fun <T> performKeyStoreOpe(
        keyStoreOpe: (KeyStore) -> T,
    ): T {
        return keyStoreOpe(keyStore)
    }

}