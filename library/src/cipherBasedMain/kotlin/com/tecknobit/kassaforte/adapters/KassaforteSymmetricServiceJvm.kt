package com.tecknobit.kassaforte.adapters

import com.tecknobit.equinoxcore.annotations.Wrapper
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteKeysService
import com.tecknobit.kassaforte.services.KassaforteSymmetricService
import kotlinx.coroutines.runBlocking
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

/**
 * The `KassaforteSymmetricServiceJvm` object allows to generate and to use symmetric keys and managing their persistence,
 * integrating in a `JVM` environment without any boilerplate and `suspend` tricky handling
 *
 * It is based on the [Cipher] API to handling the encryption and decryption of the data and on the [KeyGenerator] API
 * to generate the keys
 *
 * @author Tecknobit - N7ghtm4r3
 *
 * @see KassaforteKeysService
 * @see KassaforteSymmetricService
 * @see SymmetricKeyGenSpec
 *
 * @since Revision Two
 */
object KassaforteSymmetricServiceJvm {

    /**
     * Method used to generate a new symmetric key
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
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) = KassaforteSymmetricService.generateKey(
        algorithm = algorithm,
        alias = alias,
        keyGenSpec = keyGenSpec,
        purposes = purposes
    )

    /**
     * Method used to encrypt data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param blockMode The block mode to use to encrypt data
     * @param padding The padding to apply to encrypt data
     * @param data The data to encrypt
     *
     * @return the encrypted data as [String]
     */
    @Wrapper
    @JvmStatic
    fun encrypt(
        alias: String,
        blockMode: BlockMode,
        padding: EncryptionPadding,
        data: Any,
    ) = runBlocking {
        KassaforteSymmetricService.encrypt(
            alias = alias,
            blockMode = blockMode,
            padding = padding,
            data = data
        )
    }

    /**
     * Method used to decrypt encrypted data with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param blockMode The block mode to use to decrypt data
     * @param padding The padding to apply to decrypt data
     * @param data The data to decrypt
     *
     * @return the decrypted data as [String]
     */
    @Wrapper
    @JvmStatic
    fun decrypt(
        alias: String,
        blockMode: BlockMode,
        padding: EncryptionPadding,
        data: String,
    ) = runBlocking {
        KassaforteSymmetricService.decrypt(
            alias = alias,
            blockMode = blockMode,
            padding = padding,
            data = data
        )
    }

    /**
     * Method used to sign messages with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param message The message to sign
     *
     * @return the signed message as [String]
     */
    @Wrapper
    @JvmStatic
    fun sign(
        alias: String,
        message: Any,
    ) = runBlocking {
        KassaforteSymmetricService.sign(
            alias = alias,
            message = message
        )
    }

    /**
     * Method used to verify the validity of the messages previously signed with the key specified by the [alias] value
     *
     * @param alias The alias which identify the key to use
     * @param message The message to verify
     * @param signature The signature previously computed
     *
     * @return whether the message matches to [signature] as [Boolean]
     */
    @Wrapper
    @JvmStatic
    fun verify(
        alias: String,
        message: Any,
        signature: String,
    ) = runBlocking {
        KassaforteSymmetricService.verify(
            alias = alias,
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
    ) = KassaforteSymmetricService.deleteKey(
        alias = alias
    )

}