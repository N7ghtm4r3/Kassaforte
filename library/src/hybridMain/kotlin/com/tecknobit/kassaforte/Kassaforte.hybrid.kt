package com.tecknobit.kassaforte

import com.tecknobit.equinoxcore.annotations.Returner

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Kassaforte actual constructor(
    name: String
) {

    private val manager = KassaforteManager(
        kassaforteName = name
    )

    actual fun safeguard(
        key: String,
        data: Any,
    ) {
        val encryptedKey = encryptKey(
            key = key
        )
        safelyStore(
            encryptedKey = encryptedKey,
            data = data
        )
    }

    actual fun withdraw(
        key: String
    ): String? {
        val encryptedKey = encryptKey(
            key = key
        )
        val storedData = manager.retrieve(
            key = encryptedKey
        )
        return if(storedData == null)
            null
        else decryptData(
            data = storedData
        )
    }

    @Returner
    private fun decryptData(
        data: String
    ): String {
        // TODO: TO USE THE Provider TO DECRYPT THEN
        val decryptedData = "" + data
        return decryptedData
    }

    actual fun refresh(
        key: String,
        data: Any
    ) {
        val encryptedKey = encryptKey(
            key = key
        )
        whenKeyIsStored(
            encryptedKey = encryptedKey,
            then = {
                safelyStore(
                    encryptedKey = encryptedKey,
                    data = data
                )
            }
        )
    }

    private fun safelyStore(
        encryptedKey: String,
        data: Any,
    ) {
        val encryptedData = encryptData(
            data = data
        )
        manager.store(
            key = encryptedKey,
            data = encryptedData
        )
    }

    @Returner
    private fun encryptData(
        data: Any
    ): String {
        checkIfIsSupportedType(
            data = data
        )
        // TODO: TO USE THE Provider TO ENCRYPT THEN AND CHECK THE DATA
        return data.toString()
    }

    actual fun remove(
        key: String
    ) {
        val encryptedKey = encryptKey(
            key = key
        )
        whenKeyIsStored(
            encryptedKey = encryptedKey,
            then = {
                manager.remove(
                    key = encryptedKey
                )
            }
        )
    }

    @Returner
    private fun encryptKey(
        key: String
    ): String {
        // TODO: TO USE THE Provider TO ENCRYPT THEN
        return key
    }

    private inline fun whenKeyIsStored(
        encryptedKey: String,
        crossinline then: () -> Unit
    ) {
        if(!manager.hasKeyStored(encryptedKey))
            throw IllegalStateException("This key is currently not stored")
        then()
    }
    
}