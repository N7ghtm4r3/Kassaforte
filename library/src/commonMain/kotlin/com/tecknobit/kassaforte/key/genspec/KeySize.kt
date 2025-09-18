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

    S224(
        bitCount = 224,
        bytes = 28
    ),

    S256(
        bitCount = 256,
        bytes = 32
    ),

    S384(
        bitCount = 384,
        bytes = 48
    ),

    S512(
        bitCount = 512,
        bytes = 64
    ),

    S521(
        bitCount = 521,
        bytes = 66
    ),

    S1024(
        bitCount = 1024,
        bytes = 128
    ),

    S2048(
        bitCount = 2048,
        bytes = 256
    ),

    S4096(
        bitCount = 4096,
        bytes = 512
    ),

    S8192(
        bitCount = 8192,
        bytes = 1024
    )

}