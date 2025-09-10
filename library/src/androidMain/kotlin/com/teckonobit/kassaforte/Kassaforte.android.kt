package com.teckonobit.kassaforte

import android.content.Context
import android.content.SharedPreferences
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.utilities.AppContext


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Kassaforte actual constructor(
    name: String,
) {

    private val appContext = AppContext.get()

    private val prefs: SharedPreferences = appContext.getSharedPreferences(
        name,
        Context.MODE_PRIVATE
    )

    actual fun safeguard(
        key: String,
        data: Any,
    ) {
        val encryptedKey = encryptKey(
            key = key
        )
        secureStore(
            encryptedKey = encryptedKey,
            data = data
        )
    }

    @Suppress("UNCHECKED_CAST")
    actual fun <T> withdraw(
        key: String,
    ): T? {
        val encryptedKey = encryptKey(
            key = key
        )
        val storedData = prefs.getString(
            encryptedKey,
            null
        )
        return if(storedData == null)
            null
        else decryptData(
            data = storedData
        ) as T?
    }

    @Returner
    private fun decryptData(
        data: String
    ) : Any {
        // TODO: TO USE THE Provider TO DECRYPT THEN
        return data
    }

    actual fun refresh(
        key: String,
        data: Any,
    ) {
        val encryptedKey = encryptKey(
            key = key
        )
        whenKeyIsStored(
            encryptedKey = encryptedKey,
            then = {
                secureStore(
                    encryptedKey = encryptedKey,
                    data = data
                )
            }
        )
    }

    private fun secureStore(
        encryptedKey: String,
        data: Any
    ) {
        val encryptedData = encryptData(
            data = data
        )
        prefs.edit().putString(
            encryptedKey,
            encryptedData
        ).apply()
    }

    @Returner
    private fun encryptData(
        data: Any
    ) : String {
        // TODO: TO USE THE Provider TO ENCRYPT THEN AND CHECK THE DATA
        return data.toString()
    }

    actual fun remove(
        key: String,
    ) {
        val encryptedKey = encryptKey(
            key = key
        )
        whenKeyIsStored(
            encryptedKey = encryptedKey,
            then = { prefs.edit().remove(encryptedKey).apply() }
        )
    }

    @Returner
    private fun encryptKey(
        key: String
    ) : String {
        // TODO: TO USE THE Provider TO ENCRYPT THEN
        return key
    }

    private inline fun whenKeyIsStored(
        encryptedKey: String,
        crossinline then: () -> Unit
    ) {
        if(!prefs.contains(encryptedKey))
            throw IllegalStateException("This key is currently not stored")
        then()
    }

}