package com.teckonobit.kassaforte

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Kassaforte(
    name: String
) {

    fun safeguard(
        key: String,
        data: Any
    )

    fun refresh(
        key: String,
        data: Any
    )

    fun withdraw(
        key: String
    ): String?

    fun remove(
        key: String
    )

}