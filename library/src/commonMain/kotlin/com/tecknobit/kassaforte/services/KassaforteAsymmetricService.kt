package com.tecknobit.kassaforte.services

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


    // TODO: CREATE THE WRAPPER UTIL LIKE KassaforteSymmetricService.decrypt

}