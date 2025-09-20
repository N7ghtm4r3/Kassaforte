package com.tecknobit.kassaforte

/**
 * The `Kassaforte` class allows safeguarding sensitive data by leveraging the native APIs of each platform.
 * When native secure storage is not available (e.g., on `Android` and the `Web`), it automatically encrypts
 * the data before saving it.
 *
 * - `Android` uses the [SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences) APIs, encrypting the data before storing it
 * - `iOS` and native `macOS` use the [Keychain](https://developer.apple.com/documentation/security/keychain-services) APIs
 * - `JVM` uses the native APIs provided by the OS:
 *    - On `Windows` → [Windows Credentials](https://learn.microsoft.com/en-us/windows/win32/secauthn/credentials-management)
 *    - On `Linux` → [DBus Secret Service](https://specifications.freedesktop.org/secret-service-spec/0.2/description.html)
 *    - On `macOS` → [Keychain](https://developer.apple.com/documentation/security/keychain-services)
 *
 *    This target leverages the [java-keyring](https://github.com/javakeyring/java-keyring) library under the hood
 *
 * - `Web` uses the [LocalStorage](https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage) APIs, encrypting the data before storing it
 *
 * @param name A representative name to identify the safeguarded data (e.g., the application name using Kassaforte). This name will be
 * properly used by each platform to identify the application owner of the safeguarded data
 *
 * @author Tecknobit - N7ghtm4r3
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Kassaforte(
    name: String
) {

    /**
     * Method used to safeguard sensitive data
     *
     * @param key The key used to represent the data to safeguard
     * @param data The sensitive data to safeguard
     */
    fun safeguard(
        key: String,
        data: Any,
    )

    /**
     * Method used to refresh sensitive data previously safeguarded
     *
     * @param key The key used to represent the data to safeguard
     * @param data The refreshed sensitive data to safeguard and to replace the currently safeguarded
     */
    fun refresh(
        key: String,
        data: Any,
    )

    /**
     * Method used to withdraw safeguarded data
     *
     * @param key The key of the safeguarded data to withdraw
     *
     * @return the safeguarded data specified by the [key] as nullable [String]
     */
    suspend fun withdraw(
        key: String,
    ): String?

    /**
     * Method used to remove safeguarded data
     *
     * @param key The key of the safeguarded data to remove
     */
    fun remove(
        key: String
    )

}