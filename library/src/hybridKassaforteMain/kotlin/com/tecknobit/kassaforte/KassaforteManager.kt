package com.tecknobit.kassaforte

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class KassaforteManager(
    kassaforteName: String
) {

    fun store(
        key: String,
        data: String
    )

    fun retrieve(
        key: String
    ): String?

    fun remove(
        key: String
    )

    fun hasKeyStored(
        key: String
    ): Boolean

}