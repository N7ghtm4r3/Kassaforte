package com.tecknobit.kassaforte.enums

enum class ExportFormat(
    val value: String,
) {

    RAW("raw"),

    PKCS8("pkcs8"),

    SPKI("spki"),

    JWK("jwk")

}