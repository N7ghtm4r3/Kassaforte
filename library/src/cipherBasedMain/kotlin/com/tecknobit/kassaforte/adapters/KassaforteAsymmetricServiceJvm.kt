package com.tecknobit.kassaforte.adapters

import com.tecknobit.equinoxcore.annotations.Wrapper
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.AsymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.genspec.Digest
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteAsymmetricService
import com.tecknobit.kassaforte.services.KassaforteKeysService
import kotlinx.coroutines.runBlocking
import java.security.KeyPairGenerator
import javax.crypto.Cipher

/**
 * The `KassaforteAsymmetricServiceJvm` object allows to generate and to use asymmetric keys and managing their persistence,
 * integrating in a `JVM` environment without any boilerplate and `suspend` tricky handling
 *
 * It is based on the [Cipher] API to handling the encryption and decryption of the data and on the [KeyPairGenerator]
 * API to generate the pairs of keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeysService
 * @see KassaforteAsymmetricService
 * @see AsymmetricKeyGenSpec
 *
 * @since Revision Two
 */
object KassaforteAsymmetricServiceJvm {

    /**
     * Method used to generate an asymmetric new key
     *
     * @param algorithm The algorithm the key will use
     * @param alias The alias used to identify the key
     * @param keyGenSpec The generation spec to use to generate the key
     * @param purposes The purposes the key can be used
     */
    @Wrapper
    @JvmStatic
    fun generateKey(
        algorithm: Algorithm,
        alias: String,
        keyGenSpec: AsymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) = KassaforteAsymmetricService.generateKey(
        alias = alias,
        algorithm = algorithm,
        keyGenSpec = keyGenSpec,
        purposes = purposes
    )

    /**
     * Method used to encrypt data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param padding The padding to apply to encrypt data
     * @param digest The digest to apply to encrypt data
     * @param data The data to encrypt
     *
     * @return the encrypted data as [String]
     */
    @Wrapper
    @JvmStatic
    fun encrypt(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        data: Any,
    ) = runBlocking {
        KassaforteAsymmetricService.encrypt(
            alias = alias,
            padding = padding,
            digest = digest,
            data = data
        )
    }

    /**
     * Method used to decrypt the encrypted data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param padding The padding to apply to decrypt data
     * @param digest The digest to apply to decrypt data
     * @param data The data to decrypt
     *
     * @return the decrypted data as [String]
     */
    @Wrapper
    @JvmStatic
    fun decrypt(
        alias: String,
        padding: EncryptionPadding?,
        digest: Digest?,
        data: String,
    ) = runBlocking {
        KassaforteAsymmetricService.decrypt(
            alias = alias,
            padding = padding,
            digest = digest,
            data = data
        )
    }

    /**
     * Method used to sign messages with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param digest The digest to apply to sign messages
     * @param message The message to sign
     *
     * @return the signed message as [String]
     *
     * @since Revision Two
     */
    @Wrapper
    @JvmStatic
    fun sign(
        alias: String,
        digest: Digest,
        message: Any,
    ) = runBlocking {
        KassaforteAsymmetricService.sign(
            alias = alias,
            digest = digest,
            message = message
        )
    }

    /**
     * Method used to verify the validity of the messages previously signed with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param digest The digest applied to sign [message]
     * @param message The message to verify
     * @param signature The signature previously computed
     *
     * @return whether the message matches to [signature] as [Boolean]
     *
     * @since Revision Two
     */
    @Wrapper
    @JvmStatic
    fun verify(
        alias: String,
        digest: Digest,
        message: Any,
        signature: String,
    ) = runBlocking {
        KassaforteAsymmetricService.verify(
            alias = alias,
            digest = digest,
            message = message,
            signature = signature
        )
    }

    /**
     * Method used to delete a generated key
     *
     * @param alias The alias of the key to delete
     */
    @Wrapper
    @JvmStatic
    fun deleteKey(
        alias: String,
    ) = KassaforteAsymmetricService.deleteKey(
        alias = alias
    )

}