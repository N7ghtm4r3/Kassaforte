package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm.EC
import com.tecknobit.kassaforte.key.genspec.Algorithm.RSA
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.NONE
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.Companion.checkIfRequiresPublicKey
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
        algorithm: Algorithm,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        if (aliasExists(alias))
            throw IllegalAccessException(ALIAS_ALREADY_TAKEN_ERROR)
        val keyPairGenerator = KeyPairGenerator.getInstance(
            algorithm.value,
            ANDROID_KEYSTORE
        )
        val genSpec = serviceImplManager.resolveGenSpec(
            alias = alias,
            keyGenSpec = keyGenSpec,
            purposes = purposes
        ).run {
            val digest = keyGenSpec.digest
            val encryptionPadding = keyGenSpec.encryptionPadding
            if (algorithm == EC && digest == null)
                throw IllegalArgumentException("For Elliptic Curve algorithm a digest value is required")
            if (algorithm != EC) {
                if (algorithm == RSA && encryptionPadding == NONE)
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