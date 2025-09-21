package com.tecknobit.kassaforte

import android.content.Context
import android.content.SharedPreferences
import com.tecknobit.equinoxcore.utilities.AppContext

/**
 * The `KassaforteManager` class allows to perform the operations that the [Kassaforte] provides when is not provided
 * a native secure storage by default so, before saving the data, those data require to be automatically encrypted.
 *
 * The data will be safely stored on the [SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences)
 *
 * This manager is particularly useful to avoid breaking the `expect/actual` implementation and leverage the native system
 * to store the data
 *
 * @param kassaforteName The name of the [Kassaforte]
 *
 * @author Tecknobit - N7ghtm4r3
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class KassaforteManager actual constructor(
    kassaforteName: String
) {

    /**
     * `appContext` the context of the application which is using the [Kassaforte]
     */
    private val appContext = AppContext.get()

    /**
     * `prefs` the instance which provides the access to the `SharedPreferences`
     */
    private val prefs: SharedPreferences = appContext.getSharedPreferences(
        kassaforteName,
        Context.MODE_PRIVATE
    )

    /**
     * Method used to store the encrypted data
     *
     * @param key The key used to represent the data
     * @param data The encrypted data to store
     */
    actual fun store(
        key: String,
        data: String,
    ) {
        prefs.edit().putString(
            key,
            data
        ).apply()
    }

    /**
     * Method used to retrieve the decrypted data
     *
     * @param key The key used to represent the data
     *
     * @return the decrypted data as [String] if exists, `null` if otherwise
     */
    actual fun retrieve(
        key: String
    ): String? {
        return prefs.getString(
            key,
            null
        )
    }

    /**
     * Method used to remove the encrypted data from the secure storage
     *
     * @param key The key used to represent the data
     */
    actual fun remove(
        key: String
    ) {
        prefs.edit().remove(
            key
        ).apply()
    }

    /**
     * Method used to check whether the [key] is currently stored
     *
     * @param key The key to check
     *
     * @return whether the key is currently stored as [Boolean]
     */
    actual fun hasKeyStored(
        key: String
    ): Boolean {
        return prefs.contains(
            key
        )
    }

}