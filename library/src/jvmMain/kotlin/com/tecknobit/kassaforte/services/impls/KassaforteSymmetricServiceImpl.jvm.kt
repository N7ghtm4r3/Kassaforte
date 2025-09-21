package com.tecknobit.kassaforte.services.impls

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteKeysService
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.KEY_CANNOT_PERFORM_OPERATION_ERROR
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager
import com.tecknobit.kassaforte.services.helpers.KassaforteServiceImplManager.Companion.encode64
import kotlinx.serialization.Serializable
import java.security.Key
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

/**
 * The `KassaforteSymmetricServiceImpl` class allows to implement a service to perform the operations
 * with symmetric keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteServiceImpl
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteSymmetricServiceImpl actual constructor() : KassaforteServiceImpl() {

    private companion object {

        /**
         * `PKCS5` the replacer of the [EncryptionPadding.PKCS7] value because are equivalent and on the `JVM` is
         * recognized the `PKCS5Padding` value
         */
        const val PKCS5 = "PKCS5Padding"

    }

    /**
     * `serviceImplManager` instance of the manager which helps the service to perform the operations with the keys
     */
    private val serviceImplManager = KassaforteServiceImplManager.getInstance(
        serializer = KeyInfo.serializer()
    )

    /**
     * Method used to generate an asymmetric new key
     *
     * @param alias The alias used to identify the key
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    actual fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        if (aliasExists(alias))
            throw IllegalAccessException(KassaforteKeysService.ALIAS_ALREADY_TAKEN_ERROR)
        val algorithm = Algorithm.AES.value
        val keyGenerator = KeyGenerator.getInstance(algorithm)
        keyGenerator.init(keyGenSpec.keySize.bitCount)
        val key = keyGenerator.generateKey()
        serviceImplManager.storeKeyData(
            alias = alias,
            keyInfo = KeyInfo(
                algorithm = algorithm,
                keyPurposes = purposes,
                key = key
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
        val keyInfo = serviceImplManager.retrieveKey(
            alias = alias
        )
        if (!keyInfo.canPerform(keyOperation))
            throw RuntimeException(KEY_CANNOT_PERFORM_OPERATION_ERROR.format(keyOperation))
        return keyInfo.resolveKey()
    }

    /**
     * Method used to resolve the transformation value to obtain a cipher instance
     *
     * @param algorithm The algorithm to use
     * @param blockMode The block mode to use to ciphering data
     * @param padding The padding to apply to ciphering data
     *
     * @return the transformation value as [String]
     */
    @Assembler
    actual fun resolveTransformation(
        algorithm: String,
        blockMode: BlockMode?,
        padding: EncryptionPadding?,
    ): String {
        var transformation = algorithm
        blockMode?.let {
            transformation += "/${blockMode.value}"
        }
        padding?.let {
            transformation += padding.adapt()
        }
        return transformation
    }

    /**
     * Method used to adapt the value of the [EncryptionPadding] and, if necessary, convert the [EncryptionPadding.PKCS7]
     * value with the [PKCS5] replacer
     *
     * @return the adapted encryption padding value as [String]
     */
    @Returner
    private fun EncryptionPadding.adapt(): String {
        return "/" + when (this) {
            EncryptionPadding.PKCS7 -> PKCS5
            else -> this.value
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

    /**
     * The `KeyInfo` data class allows to store extra information related to a generated key such as the usages the key
     * can be used
     *
     * @property algorithm The algorithm the key can use
     * @property keyPurposes The purposes the key can be used
     * @property key The generated symmetric key
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
    ) : KeyDetailsSheet<String> {

        /**
         * Constructor used to init the data class
         *
         * @param algorithm The algorithm the key can use
         * @param keyPurposes The purposes the key can be used
         * @param key The generated symmetric key
         */
        constructor(
            algorithm: String,
            keyPurposes: KeyPurposes,
            key: Key,
        ) : this(
            algorithm = algorithm,
            keyPurposes = keyPurposes,
            key = key.encode64()
        )

        /**
         * Method used to resolve from the raw data the secret key
         *
         * @return the secret key as [SecretKeySpec]
         */
        @Returner
        fun resolveKey(): Key = SecretKeySpec(
            Base64.decode(key.encodeToByteArray()),
            algorithm
        )

    }

}