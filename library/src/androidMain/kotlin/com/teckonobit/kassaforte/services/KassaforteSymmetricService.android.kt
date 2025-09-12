package com.teckonobit.kassaforte.services

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.teckonobit.kassaforte.checkIfIsSupportedType
import com.teckonobit.kassaforte.key.KeyPurposes
import com.teckonobit.kassaforte.key.genspec.BlockModeType
import com.teckonobit.kassaforte.key.genspec.EncryptionPaddingType
import com.teckonobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.teckonobit.kassaforte.key.genspec.convert
import java.security.Key
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KassaforteSymmetricService : KassaforteKeysService<SymmetricKeyGenSpec>() {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    actual override fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes
    ) {
        val keyGenerator = KeyGenerator.getInstance(
            keyGenSpec.algorithm.value,
            ANDROID_KEYSTORE
        )
        val genSpec = KeyGenParameterSpec.Builder(
            alias,
            resolvePurposes(
                keyPurposes = purposes
            )
        ).run {
            setBlockModes(*keyGenSpec.blockModes.convert())
            setDigests(*keyGenSpec.digests.convert())
            setEncryptionPaddings(*keyGenSpec.encryptionPaddings.convert())
            keyGenSpec.keySize?.let { keySize ->
                setKeySize(keySize)
            }
            build()
        }
        keyGenerator.init(genSpec)
        keyGenerator.generateKey()
    }

    @Assembler
    private fun resolvePurposes(
        keyPurposes: KeyPurposes
    ): Int {
        var purposes = 0
        if(keyPurposes.canEncrypt)
            purposes = purposes or PURPOSE_ENCRYPT
        if(keyPurposes.canDecrypt)
            purposes = purposes or PURPOSE_DECRYPT
        if(keyPurposes.canSign)
            purposes = purposes or PURPOSE_SIGN
        if(keyPurposes.canVerify)
            purposes = purposes or PURPOSE_VERIFY
        if(keyPurposes.canWrapKey && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            purposes = purposes or PURPOSE_WRAP_KEY
        if(keyPurposes.canAgree && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            purposes = purposes or PURPOSE_AGREE_KEY
        if(purposes == 0)
            throw IllegalStateException("Key purposes not valid")
        return purposes
    }

    actual fun encrypt(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        data: Any
    ): String {
        checkIfIsSupportedType(
            data = data
        )
        val encryptedData = useCipher(
            alias = alias,
            blockModeType = blockModeType,
            paddingType = paddingType,
        ) { cipher, key ->
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val dataToEncrypt = cipher.iv + data.toString().encodeToByteArray()
            cipher.doFinal(dataToEncrypt)
        }
        return Base64.encode(encryptedData)
    }

    @Returner
    private fun initCustomIv(
        ivSeed: ByteArray
    ): IvParameterSpec {
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(ivSeed)
        return IvParameterSpec(ivSeed)
    }

    actual fun decrypt(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        data: String
    ): String {
        val decryptedData = useCipher(
            alias = alias,
            blockModeType = blockModeType,
            paddingType = paddingType,
        ) { cipher, key ->
            // TODO: TO ADAPT THE GCM USAGE
            val dataToDecrypt = Base64.decode(data)
            val iv = IvParameterSpec(dataToDecrypt.copyOfRange(0, 16))
            val cypherText = dataToDecrypt.copyOfRange(16, dataToDecrypt.size)
            cipher.init(Cipher.DECRYPT_MODE, key, iv)
            cipher.doFinal(cypherText)
        }
        return decryptedData.decodeToString()
    }

    private inline fun useCipher(
        alias: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
        cypherUsage: (Cipher, Key) -> ByteArray
    ): ByteArray {
        val keyStore = getKeystore()
        val key = keyStore.getKey(alias, null)
        val transformation = resolveTransformation(
            algorithm = key.algorithm,
            blockModeType = blockModeType,
            paddingType = paddingType
        )
        val cipher = Cipher.getInstance(transformation)
        return cypherUsage(cipher, key)
    }

    @Assembler
    private fun resolveTransformation(
        algorithm: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?
    ): String {
        var transformation = algorithm
        blockModeType?.let { blockMode ->
            transformation += "/${blockMode.value}"
        }
        paddingType?.let { padding ->
            transformation += "/${padding.value}"
        }
        return transformation
    }

    actual override fun deleteKey(
        alias: String
    ) {
        val keyStore = getKeystore()
        keyStore.deleteEntry(alias)
    }

    private fun getKeystore(): KeyStore {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return keyStore
    }

}