package com.tecknobit.kassaforte.enums

/**
 * These are the supported format to export a key
 *
 * @param value The native value of the export format
 */
enum class ExportFormat(
    val value: String,
) {

    /**
     * `RAW` Can be used to import or export AES or HMAC secret keys, or Elliptic Curve public keys.
     *
     * In this format the key is supplied as an [org.khronos.webgl.ArrayBuffer] containing the raw bytes for the key
     */
    RAW("raw"),

    /**
     * `PKCS8` Can be used to import or export RSA or Elliptic Curve private keys
     */
    PKCS8("pkcs8"),

    /**
     * `SPKI` Can be used to import or export RSA or Elliptic Curve public keys
     */
    SPKI("spki"),

    /**
     * `JWK` Can be used to import or export RSA or Elliptic Curve public or private keys, as well as AES and HMAC
     * secret keys
     */
    JWK("jwk")

}