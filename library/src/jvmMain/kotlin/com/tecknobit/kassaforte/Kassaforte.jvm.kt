package com.tecknobit.kassaforte

import com.github.javakeyring.Keyring
import com.github.javakeyring.PasswordAccessException

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Kassaforte actual constructor(
    private val name: String
) {

    // TODO: INSERT INTO THE ARCHICTURE REFERENCE
    private val keyring = Keyring.create()

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

    actual suspend fun withdraw(
        key: String,
    ): String? {
        return unsuspendedWithdraw(
            key = key
        )
    }

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

    // TODO: TO INDICATE WHY AT THE MOMENT THIS METHOD IS REQUIRED
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

    actual fun remove(
        key: String
    ) {
        keyring.deletePassword(
            name,
            key
        )
    }

}