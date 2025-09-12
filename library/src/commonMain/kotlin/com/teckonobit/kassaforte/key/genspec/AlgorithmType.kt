package com.teckonobit.kassaforte.key.genspec

enum class AlgorithmType(
    val value: String
) {

    RSA("RSA"),

    EC("EC"),

    AES("AES"),

    HMAC_SHA256("HmacSHA256")
}