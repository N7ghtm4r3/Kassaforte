package com.tecknobit.kassaforte.services.impls

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import android.security.keystore.KeyGenParameterSpec
import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.RequiresDocumentation
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.Algorithm.EC
import com.tecknobit.kassaforte.key.genspec.Algorithm.RSA
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.Digest.SHA1
import com.tecknobit.kassaforte.key.genspec.Digest.SHA256
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.NONE
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding.RSA_OAEP
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.Companion.checkIfRequiresPublicKey
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.ALIAS_ALREADY_TAKEN_ERROR
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager.Companion.ANDROID_KEYSTORE
import com.tecknobit.kassaforte.services.helpers.isStrongBoxAvailable
import java.security.Key
import java.security.KeyPairGenerator
import java.security.PublicKey

/**
 * The `KassaforteAsymmetricServiceImpl` class allows to implement a service to perform the operations
 * with asymmetric keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceImpl
 */
// TODO: TO WARN ABOUT SET allowBackup=false
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteAsymmetricServiceImpl actual constructor() : KassaforteServiceImpl() {

    /**
     * `serviceImplManager` instance of the manager which helps the service to perform the operations with the keys
     */
    private val serviceImplManager = KassaforteServiceImplManager()

    /**
     * Method used to generate an asymmetric new key
     *
     * @param algorithm The algorithm the key will use
     * @param alias The alias used to identify the key
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
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
            setupGenSpec(
                algorithm = algorithm,
                digest = keyGenSpec.digest,
                encryptionPadding = keyGenSpec.encryptionPadding
            )
        }
        keyPairGenerator.initialize(genSpec)
        keyPairGenerator.genKeyPair()
    }

    @RequiresDocumentation(
        additionalNotes = "TO INSERT SINCE Revision Two"
    )
    @Assembler
    private fun KeyGenParameterSpec.Builder.setupGenSpec(
        algorithm: Algorithm,
        digest: Digest?,
        encryptionPadding: EncryptionPadding,
    ): KeyGenParameterSpec {
        return when (algorithm) {
            EC -> {
                if (digest == null)
                    throw IllegalArgumentException("For Elliptic Curve algorithm the digest value is required")
                setDigests(digest.value)
            }

            RSA -> {
                if (encryptionPadding == NONE)
                    throw IllegalArgumentException("For RSA must be used PKCS1Padding or OAEPPadding padding type")
                if (encryptionPadding == RSA_OAEP) {
                    if (digest == null)
                        throw IllegalArgumentException("For RSA with OAEP algorithm the digest value is required")
                    if (digest != SHA1 && digest != SHA256)
                        throw IllegalArgumentException("Android supports only SHA-1 and in some devices SHA-256")
                    if (SDK_INT >= P && digest == SHA256 && isStrongBoxAvailable()) {
                        setIsStrongBoxBacked(true)
                        setDigests(SHA256.value)
                    } else
                        setDigests(SHA1.value)
                }
                setEncryptionPaddings(encryptionPadding.value)
            }

            else -> throw IllegalArgumentException("Invalid asymmetric algorithm")
        }.build()
    }

    /**
     * Method used to check whether the alias has been already taken to identify other key
     *
     * @param alias The alias to check
     *
     * @return whether the alias has been already taken as [Boolean]
     */
    actual override fun aliasExists(
        alias: String,
    ): Boolean {
        return serviceImplManager.isAliasTaken(
            alias = alias
        )
    }

    /**
     * Method used to get a key to perform a [keyOperation]
     *
     * @param alias The alias of the key to get
     * @param keyOperation The operation for what the key is being getting
     *
     * @return the specified key as [Key]
     */
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

    /**
     * Method used to retrieve a public from the specified alias
     *
     * @param alias The alias of the key to retrieve
     *
     * @return the specified key as [PublicKey]
     */
    @Returner
    private fun retrievePublicKey(
        alias: String,
    ): PublicKey {
        return serviceImplManager.performKeyStoreOpe { keystore ->
            val certificate = keystore.getCertificate(alias)
            certificate.publicKey
        }
    }

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    actual override fun deleteKey(
        alias: String,
    ) {
        serviceImplManager.removeKey(
            alias = alias
        )
    }

}