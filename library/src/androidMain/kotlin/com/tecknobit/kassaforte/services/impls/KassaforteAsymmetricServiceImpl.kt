package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.AlgorithmType
import com.tecknobit.kassaforte.key.genspec.AlgorithmType.EC
import com.tecknobit.kassaforte.key.genspec.AlgorithmType.RSA
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.NONE
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.ENCRYPT
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.ALIAS_ALREADY_TAKEN_ERROR
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager.Companion.ANDROID_KEYSTORE
import java.security.Key
import java.security.KeyPairGenerator
import java.security.PublicKey

// TODO: TO WARN ABOUT SET allowBackup=false
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteAsymmetricServiceImpl actual constructor() : KassaforteServiceImpl() {

    private val serviceImplManager = KassaforteServiceImplManager()

    actual fun generateKey(
        algorithmType: AlgorithmType,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        if (aliasExists(alias))
            throw IllegalAccessException(ALIAS_ALREADY_TAKEN_ERROR)
        val keyPairGenerator = KeyPairGenerator.getInstance(
            algorithmType.value,
            ANDROID_KEYSTORE
        )
        val genSpec = serviceImplManager.resolveGenSpec(
            alias = alias,
            keyGenSpec = keyGenSpec,
            purposes = purposes
        ).run {
            val digest = keyGenSpec.digest
            val encryptionPadding = keyGenSpec.encryptionPadding
            if (algorithmType == EC && digest == null)
                throw IllegalArgumentException("For Elliptic Curve algorithm a digest value is required")
            if (algorithmType != EC) {
                if (algorithmType == RSA && encryptionPadding == NONE)
                    throw IllegalArgumentException("For RSA must be used PKCS1Padding or OAEPPadding padding type")
                setEncryptionPaddings(keyGenSpec.encryptionPadding.value)
            }
            digest?.let {
                setDigests(digest.value)
            }
            build()
        }
        keyPairGenerator.initialize(genSpec)
        keyPairGenerator.genKeyPair()
    }

    actual override fun aliasExists(
        alias: String,
    ): Boolean {
        return serviceImplManager.isAliasTaken(
            alias = alias
        )
    }

    actual override fun getKey(
        alias: String,
        keyOperation: KeyOperation,
    ): Key {
        val requiresPublicKey = keyOperation.checkIfRequiresPublicKey()
        val storedKey = serviceImplManager.retrieveKey(
            alias = alias
        )
        return if (requiresPublicKey) {
            retrievePublicKey(
                alias = alias
            )
        } else
            storedKey
    }

    @Returner
    private fun KeyOperation.checkIfRequiresPublicKey(): Boolean {
        return when (this) {
            ENCRYPT -> true
            else -> false
        }
    }

    @Returner
    private fun retrievePublicKey(
        alias: String,
    ): PublicKey {
        return serviceImplManager.performKeyStoreOpe { keystore ->
            val certificate = keystore.getCertificate(alias)
            certificate.publicKey
        }
    }

    actual override fun deleteKey(
        alias: String,
    ) {
        serviceImplManager.removeKey(
            alias = alias
        )
    }

}