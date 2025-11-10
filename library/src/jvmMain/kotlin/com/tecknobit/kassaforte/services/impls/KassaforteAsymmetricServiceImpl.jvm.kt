package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.Companion.checkIfRequiresPublicKey
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.ALIAS_ALREADY_TAKEN_ERROR
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.KEY_CANNOT_PERFORM_OPERATION_ERROR
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager.Companion.encode64
import com.tecknobit.kassaforte.util.decode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

/**
 * The `KassaforteAsymmetricServiceImpl` class allows to implement a service to perform the operations
 * with asymmetric keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceImpl
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteAsymmetricServiceImpl actual constructor() : KassaforteServiceImpl() {

    /**
     * `serviceImplManager` instance of the manager which helps the service to perform the operations with the keys
     */
    private val serviceImplManager = KassaforteServiceImplManager.getInstance(
        serializer = KeyInfo.serializer()
    )

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
        val algorithmName = algorithm.value
        val keyPairGenerator = KeyPairGenerator.getInstance(algorithmName)
        keyPairGenerator.initialize(keyGenSpec.keySize.bitCount)
        val keyPair = keyPairGenerator.genKeyPair()
        serviceImplManager.storeKeyData(
            alias = alias,
            keyInfo = KeyInfo(
                algorithm = algorithmName,
                keyPurposes = purposes,
                keyPair = keyPair
            )
        )
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
     * Method used to get a key to perform a [keyOperation].
     *
     * Will be automatically returned the `private` or the `public` key based on the [keyOperation] to perform
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
        val keyInfo = serviceImplManager.retrieveKey(
            alias = alias
        )
        if (!keyInfo.canPerform(keyOperation))
            throw RuntimeException(KEY_CANNOT_PERFORM_OPERATION_ERROR.format(keyOperation))
        return if (keyOperation.checkIfRequiresPublicKey())
            keyInfo.resolvePublicKey()
        else
            keyInfo.resolvePrivateKey()
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

    /**
     * The `KeyInfo` data class allows to store extra information related to a generated key such as the usages the key
     * can be used
     *
     * @property algorithm The algorithm the key can use
     * @property keyPurposes The purposes the key can be used
     * @property key The generated key
     * @property publicKey The generated public key
     *
     * @author Tecknobit - N7ghtm4r3
     *
     * @see KeyDetailsSheet
     */
    @Serializable
    private data class KeyInfo(
        val algorithm: String,
        override val keyPurposes: KeyPurposes,
        override val key: String,
        val publicKey: String,
    ) : KeyDetailsSheet<String> {

        /**
         * `keyFactory` the factory used to resolve the keys from the stored raw data
         */
        @Transient
        private val keyFactory = KeyFactory.getInstance(algorithm)

        /**
         * Constructor used to init the data class
         *
         * @param algorithm The algorithm the key can use
         * @param keyPurposes The purposes the key can be used
         * @param keyPair The key pair containing the keys
         */
        constructor(
            algorithm: String,
            keyPurposes: KeyPurposes,
            keyPair: KeyPair,
        ) : this(
            algorithm = algorithm,
            keyPurposes = keyPurposes,
            key = keyPair.private.encode64(),
            publicKey = keyPair.public.encode64()
        )

        /**
         * Method used to resolve from the raw data the private key
         *
         * @return the key resolved as [PrivateKey]
         */
        @Returner
        fun resolvePrivateKey(): PrivateKey {
            val decodedKey = decodeKey(
                encodedKey = key
            )
            return keyFactory.generatePrivate(PKCS8EncodedKeySpec(decodedKey))
        }

        /**
         * Method used to resolve from the raw data the public key
         *
         * @return the key resolved as [PublicKey]
         */
        @Returner
        fun resolvePublicKey(): PublicKey {
            val decodedKey = decodeKey(
                encodedKey = publicKey
            )
            return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        }

        /**
         * Method used to decode the encoded stored key data
         *
         * @param encodedKey The data of the encoded key
         *
         * @return the decoded data as [ByteArray]
         */
        @Returner
        private fun decodeKey(
            encodedKey: String,
        ): ByteArray {
            return decode(encodedKey)
        }

    }

}