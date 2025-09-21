@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.enums.ExportFormat.PKCS8
import com.tecknobit.kassaforte.enums.ExportFormat.SPKI
import com.tecknobit.kassaforte.enums.NamedCurve.Companion.toNamedCurve
import com.tecknobit.kassaforte.enums.RsaAlgorithmName.Companion.toRsaAlgorithmName
import com.tecknobit.kassaforte.helpers.asPlainText
import com.tecknobit.kassaforte.helpers.toByteArray
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm.EC
import com.tecknobit.kassaforte.key.genspec.Algorithm.RSA
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.helpers.KassaforteAsymmetricServiceManager
import com.tecknobit.kassaforte.util.checkIfIsSupportedType
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.EcKeyGenParams
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.RsaHashedKeyGenParams
import com.tecknobit.kassaforte.wrappers.crypto.key.raw.RawCryptoKeyPair
import com.tecknobit.kassaforte.wrappers.crypto.rsaOaepParams
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec>() {

    // TODO: PROVIDE ALSO ECDH WHEN INTEGRATED THE AGREEMENT
    private const val DEFAULT_EC_NAME = "ECDSA"

    private val serviceManager = KassaforteAsymmetricServiceManager()

    actual override fun generateKey(
        algorithm: Algorithm,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        serviceManager.generateKey(
            alias = alias,
            genSpec = {
                resolveKeyGenSpec(
                    algorithm = algorithm,
                    keyGenSpec = keyGenSpec
                )
            },
            purposes = purposes
        )
    }

    @Returner
    private fun resolveKeyGenSpec(
        algorithm: Algorithm,
        keyGenSpec: AsymmetricKeyGenSpec,
    ): KeyGenSpec {
        return when (algorithm) {
            RSA -> {
                val digest = keyGenSpec.digest ?: throw IllegalArgumentException("The digest must be specified")
                resolveRsaHashedKeyGenParams(
                    name = keyGenSpec.encryptionPadding
                        .toRsaAlgorithmName()
                        .value,
                    modulusLength = keyGenSpec.keySize.bitCount,
                    hash = digest.value
                )
            }

            EC -> {
                resolveEcKeyGenParams(
                    name = DEFAULT_EC_NAME,
                    namedCurve = keyGenSpec.keySize
                        .toNamedCurve()
                        .value
                )
            }

            else -> throw IllegalArgumentException(INVALID_ASYMETRIC_ALGORITHM)
        }
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean = true

    actual suspend fun encrypt(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        data: Any,
    ): String {
        checkIfIsSupportedType(
            data = data
        )
        val rawCryptoKeyPair: RawCryptoKeyPair = serviceManager.retrieveKeyData(
            alias = alias
        )
        val encryptedData = serviceManager.useKey(
            rawKey = rawCryptoKeyPair.publicKey,
            rawKeyData = rawCryptoKeyPair,
            format = SPKI,
            usages = rawCryptoKeyPair.publicKeyUsages,
            usage = { key ->
                val encryptedData = serviceManager.encrypt(
                    algorithm = rsaOaepParams(),
                    key = key,
                    data = data
                )
                Base64.encode(encryptedData.toByteArray())
            }
        )
        return encryptedData
    }

    actual suspend fun decrypt(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        data: String,
    ): String {
        val rawCryptoKeyPair: RawCryptoKeyPair = serviceManager.retrieveKeyData(
            alias = alias
        )
        val decryptedData = serviceManager.useKey(
            rawKey = rawCryptoKeyPair.privateKey,
            rawKeyData = rawCryptoKeyPair,
            format = PKCS8,
            usage = { key ->
                val dataToDecrypt = Base64.decode(data)
                val decryptedData = serviceManager.decrypt(
                    algorithm = rsaOaepParams(),
                    key = key,
                    data = dataToDecrypt
                )
                decryptedData.asPlainText()
            }
        )
        return decryptedData
    }

    actual override fun deleteKey(
        alias: String,
    ) {
        serviceManager.removeKey(
            alias = alias
        )
    }

}

@JsFun(
    """
    (name, modulusLength, hash) => ({
        name: name,
        modulusLength: modulusLength,
        publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
        hash: hash
    })
    """
)
@Returner
private external fun resolveRsaHashedKeyGenParams(
    name: String,
    modulusLength: Int,
    hash: String,
): RsaHashedKeyGenParams

@JsFun(
    """
    (name, namedCurve) => ({
        name: name,
        namedCurve: namedCurve
    })
    """
)
@Returner
private external fun resolveEcKeyGenParams(
    name: String,
    namedCurve: String,
): EcKeyGenParams