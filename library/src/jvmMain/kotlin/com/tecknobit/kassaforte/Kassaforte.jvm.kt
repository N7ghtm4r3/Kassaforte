package com.tecknobit.kassaforte

import com.github.javakeyring.Keyring
import com.github.javakeyring.PasswordAccessException

/**
 * The `Kassaforte` class allows safeguarding sensitive data by leveraging the native APIs of each operating system:
 *
 * - On `Windows` → [Windows Credentials](https://learn.microsoft.com/en-us/windows/win32/secauthn/credentials-management)
 * - On `Linux` → [DBus Secret Service](https://specifications.freedesktop.org/secret-service-spec/0.2/description.html)
 * - On `macOS` → [Keychain](https://developer.apple.com/documentation/security/keychain-services)
 *
 * This target leverages the [java-keyring](https://github.com/javakeyring/java-keyring) library under the hood
 *
 * @param name A representative name to identify the safeguarded data (e.g., the application name using Kassaforte). This name will be
 * properly used by each platform to identify the application owner of the safeguarded data
 *
 * @author Tecknobit - N7ghtm4r3
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Kassaforte actual constructor(
    private val name: String
) {

    /**
     * `keyring` the service which natively operate to provide the safeguarding, withdrawing, refreshing and remotion
     * of the sensitive data
     */
    private val keyring = Keyring.create()

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
        keyring.setPassword(
            name,
            key,
            data.toString()
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
        return unsuspendedWithdraw(
            key = key
        )
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
        val storedPassword = unsuspendedWithdraw(
            key = key
        )
        if(storedPassword != null) {
            safeguard(
                key = key,
                data = data
            )
        }
    }

    /**
     * Method used to withdraw safeguarded data.
     *
     * This method implements the real logic of the withdrawal without to be `suspend`, the wrapper [withdraw] method is
     * required to be `suspend` to respect the `expect/actual` implementation
     *
     * @param key The key of the safeguarded data to withdraw
     *
     * @return the safeguarded data specified by the [key] as nullable [String]
     */
    internal fun unsuspendedWithdraw(
        key: String,
    ): String? {
        return try {
            keyring.getPassword(
                name,
                key
            )
        } catch (e: PasswordAccessException) {
            null
        }
    }

    /**
     * Method used to remove safeguarded data
     *
     * @param key The key of the safeguarded data to remove
     */
    actual fun remove(
        key: String
    ) {
        keyring.deletePassword(
            name,
            key
        )
    }

}