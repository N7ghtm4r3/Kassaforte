package com.teckonobit.kassaforte.key.genspec

enum class AlgorithmType(
    val value: String
) {

    RSA("RSA"),

    EC("EC"),

    XDH("XDH"),

    AES("AES"),

    HMAC_SHA1("HmacSHA1"),

    HMAC_SHA224("HmacSHA224"),

    HMAC_SHA256("HmacSHA256"),

    HMAC_SHA384("HmacSHA384"),

    HMAC_SHA512("HmacSHA512");

}