package com.tecknobit.kassaforte.key.genspec

enum class DigestType(
    val value: String,
) {

    NONE("NONE"),

    MD5("MD5"),

    SHA1("SHA-1"),

    SHA224("SHA-224"),

    SHA256("SHA-256"),

    SHA384("SHA-384"),

    SHA512("SHA-512");
}