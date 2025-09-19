@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.enums.NamedCurve.Companion.toNamedCurve
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AlgorithmType.EC
import com.tecknobit.kassaforte.key.genspec.AlgorithmType.RSA
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.DigestType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.RSA_OAEP
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.RSA_PKCS1
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.EcKeyGenParams
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.KeyGenSpec
import com.tecknobit.kassaforte.wrappers.crypto.key.genspec.RsaHashedKeyGenParams

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteAsymmetricService : KassaforteKeysService<AsymmetricKeyGenSpec>() {

    // TODO: PROVIDE ALSO ECDH WHEN INTEGRATED THE AGREEMENT
    private const val DEFAULT_EC_NAME = "ECDSA"

    private val serviceImplManager = KassaforteServiceImplManager()

    actual override fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        serviceImplManager.checkIfAliasAvailable(
            alias = alias,
            onAvailable = { return@checkIfAliasAvailable },
            onNotAvailable = {
                val genSpec = resolveGenSpec(
                    algorithmType = algorithmType,
                    keyGenSpec = keyGenSpec
                )
                val usages = serviceImplManager.resolveUsages(
                    purposes = purposes
                )
                serviceImplManager.generateKey(
                    genSpec = genSpec,
                    usages = usages
                )
            }
        )
    }

    @Returner
    private fun resolveGenSpec(
        algorithmType: AlgorithmType,
        keyGenSpec: AsymmetricKeyGenSpec,
    ): KeyGenSpec {
        return when (algorithmType) {
            RSA -> {
                val digest = keyGenSpec.digest ?: throw IllegalArgumentException("The digest must be specified")
                resolveRsaHashedKeyGenParams(
                    name = keyGenSpec.encryptionPadding.adaptRsaAlgorithmName(),
                    modulusLength = keyGenSpec.keySize.bitCount,
                    hash = digest.value
                )
            }

            EC -> {
                resolveEcKeyGenParams(
                    name = DEFAULT_EC_NAME,
                    namedCurve = keyGenSpec.keySize.toNamedCurve().value
                )
            }

            else -> {
                throw IllegalArgumentException("Invalid asymmetric algorithm")
            }
        }
    }

    @Returner
    private fun EncryptionPaddingType.adaptRsaAlgorithmName(): String {
        return RSA.value + "-" + when (this) {
            RSA_OAEP -> "OAEP"
            RSA_PKCS1 -> "PKCS1-v1_5"
            else -> {
                throw IllegalArgumentException("Invalid encryption padding value")
            }
        }
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean = true

    actual suspend fun encrypt(
        alias: String,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
        data: Any,
    ): String {
        TODO("Not yet implemented")
    }

    actual suspend fun decrypt(
        alias: String,
        paddingType: EncryptionPaddingType?,
        digestType: DigestType?,
        data: String,
    ): String {
        TODO("Not yet implemented")
    }

    actual override fun deleteKey(
        alias: String,
    ) {
    }

}

@JsFun(
    """
    (name, modulusLength, hash) => ({
        name: name,
        modulusLength: modulusLength,
        publicExponent: [0x01, 0x00, 0x01],
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