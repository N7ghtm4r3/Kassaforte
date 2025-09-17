package com.tecknobit.kassaforte.key.genspec

enum class KeySize(
    val bitCount: Int,
    val bytes: Int,
) {

    S128(
        bitCount = 128,
        bytes = 16
    ),

    S192(
        bitCount = 192,
        bytes = 24
    ),

    S256(
        bitCount = 256,
        bytes = 32
    )

}