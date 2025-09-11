package com.teckonobit.kassaforte.services

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class KassaforteAsymmetricService(
    alias: String
) {

    fun sign(
        data: Any
    ): String

    fun verify(
        data: String
    ): Boolean

}