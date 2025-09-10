package com.teckonobit.kassaforte

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Kassaforte actual constructor(
    name: String
) {

    actual fun safeguard(
        key: String,
        data: Any,
    ) {
    }

    actual fun <T> withdraw(
        key: String
    ): T? {
        TODO("Not yet implemented")
    }

    actual fun refresh(
        key: String,
        data: Any
    ) {
    }

    actual fun remove(
        key: String
    ) {
    }

}