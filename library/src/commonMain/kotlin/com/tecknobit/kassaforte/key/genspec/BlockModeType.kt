package com.tecknobit.kassaforte.key.genspec

enum class BlockModeType(
    val value: String,
    val blockSize: Int,
) {

    CBC(
        value = "CBC",
        blockSize = 16
    ),

    CTR(
        value = "CTR",
        blockSize = 16
    ),

    GCM(
        value = "GCM",
        blockSize = 12
    );

}