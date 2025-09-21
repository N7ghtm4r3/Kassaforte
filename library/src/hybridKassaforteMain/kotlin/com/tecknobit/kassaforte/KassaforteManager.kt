package com.tecknobit.kassaforte

/**
 * The `KassaforteManager` class allows to perform the operations that the [Kassaforte] provides when is not provided
 * a native secure storage by default so, before saving the data, those data require to be automatically encrypted.
 *
 * - `Android` uses the [SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences) APIs
 * - `Web` uses the [LocalStorage](https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage) APIs
 *
 * This manager is particularly useful to avoid breaking the `expect/actual` implementation and leverage the native system
 * to store the data
 *
 * @param kassaforteName The name of the [Kassaforte]
 *
 * @author Tecknobit - N7ghtm4r3
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class KassaforteManager(
    kassaforteName: String
) {

    /**
     * Method used to store the encrypted data
     *
     * @param key The key used to represent the data
     * @param data The encrypted data to store
     */
    fun store(
        key: String,
        data: String,
    )

    /**
     * Method used to retrieve the decrypted data
     *
     * @param key The key used to represent the data
     *
     * @return the decrypted data as [String] if exists, `null` if otherwise
     */
    fun retrieve(
        key: String
    ): String?

    /**
     * Method used to remove the encrypted data from the secure storage
     *
     * @param key The key used to represent the data
     */
    fun remove(
        key: String,
    )

    /**
     * Method used to check whether the [key] is currently stored
     *
     * @param key The key to check
     *
     * @return whether the key is currently stored as [Boolean]
     */
    fun hasKeyStored(
        key: String
    ): Boolean

}