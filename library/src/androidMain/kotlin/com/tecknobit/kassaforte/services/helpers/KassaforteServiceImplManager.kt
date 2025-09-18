package com.tecknobit.kassaforte.services.helpers

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.kassaforte.key.genspec.KassaforteKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import java.security.Key
import java.security.KeyStore

internal class KassaforteServiceImplManager {

    internal companion object {

        const val ANDROID_KEYSTORE = "AndroidKeyStore"

    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

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

    fun isAliasTaken(
        alias: String,
    ): Boolean {
        return keyStore.isKeyEntry(alias)
    }

    fun retrieveKey(
        alias: String,
    ): Key {
        return keyStore.getKey(alias, null)
    }

    fun removeKey(
        alias: String,
    ) {
        keyStore.deleteEntry(alias)
    }

    fun <T> performKeyStoreOpe(
        keyStoreOpe: (KeyStore) -> T,
    ): T {
        return keyStoreOpe(keyStore)
    }

}