package com.teckonobit.kassaforte

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class KassaforteProvider(
    alias: String
) {

    fun generateKeyPair()

    fun readAliases(): Collection<String>

    fun sign(
        data: Any
    )

    fun verify(
        data: Any
    )

}