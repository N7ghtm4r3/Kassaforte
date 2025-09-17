package com.tecknobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.equinoxcore.annotations.Validator
import com.tecknobit.kassaforte.Kassaforte
import com.tecknobit.kassaforte.key.KeyPurposes
import com.tecknobit.kassaforte.key.genspec.AlgorithmType.AES
import com.tecknobit.kassaforte.key.genspec.BlockModeType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType
import com.tecknobit.kassaforte.key.genspec.EncryptionPaddingType.PKCS7
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.ALIAS_ALREADY_TAKEN_ERROR
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR
import com.tecknobit.kassaforte.services.KassaforteKeysService.Companion.KEY_CANNOT_PERFORM_OPERATION_ERROR
import com.tecknobit.kassaforte.services.KeyOperation.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.Key
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteSymmetricServiceImpl actual constructor() {

    private companion object {

        const val PKCS5 = "PKCS5Padding"

    }

    actual fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        if (aliasExists(alias))
            throw IllegalAccessException(ALIAS_ALREADY_TAKEN_ERROR)
        val algorithm = AES.value
        val keyGenerator = KeyGenerator.getInstance(algorithm)
        keyGenerator.init(keyGenSpec.keySize.bitCount)
        val key = keyGenerator.generateKey()
        storeKeyData(
            alias = alias,
            keyInfo = KeyInfo(
                algorithm = algorithm,
                key = key,
                keyPurposes = purposes
            )
        )
    }

    actual fun aliasExists(
        alias: String,
    ): Boolean {
        val kassaforte = Kassaforte(
            name = alias
        )
        return kassaforte.unsuspendedWithdraw(
            key = alias
        ) != null
    }

    private fun storeKeyData(
        alias: String,
        keyInfo: KeyInfo,
    ) {
        val keyData = formatKeyData(
            keyInfo = keyInfo
        )
        val kassaforte = Kassaforte(
            name = alias
        )
        kassaforte.safeguard(
            key = alias,
            data = keyData
        )
    }

    @Returner
    private fun formatKeyData(
        keyInfo: KeyInfo,
    ) : String {
        val encodedKeyInfo = Json.encodeToString(keyInfo)
            .encodeToByteArray()
        return Base64.encode(encodedKeyInfo)
    }

    actual fun getKey(
        alias: String,
        keyOperation: KeyOperation,
    ): Key {
        val kassaforte = Kassaforte(
            name = alias
        )
        val encodedKeyData = kassaforte.unsuspendedWithdraw(
            key = alias
        )
        if(encodedKeyData == null)
            throw IllegalAccessException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
        val decodedKeyData = Base64.decode(encodedKeyData)
            .decodeToString()
        val keyInfo: KeyInfo = Json.decodeFromString(decodedKeyData)
        if(!keyInfo.canPerform(keyOperation))
            throw IllegalAccessException(KEY_CANNOT_PERFORM_OPERATION_ERROR.format(keyOperation))
        return keyInfo.resolveKey()
    }

    @Assembler
    internal actual fun resolveTransformation(
        algorithm: String,
        blockModeType: BlockModeType?,
        paddingType: EncryptionPaddingType?,
    ): String {
        var transformation = algorithm
        blockModeType?.let { blockMode ->
            transformation += "/${blockMode.value}"
        }
        paddingType?.let { padding ->
            transformation += padding.adapt()
        }
        return transformation
    }

    @Returner
    private fun EncryptionPaddingType.adapt() : String {
        return "/" + when(this) {
            PKCS7 -> PKCS5
            else -> this.value
        }
    }

    actual fun deleteKey(
        alias: String,
    ) {
        val kassaforte = Kassaforte(
            name = alias
        )
        kassaforte.remove(
            key = alias
        )
    }

    @Serializable
    private data class KeyInfo(
        val algorithm: String,
        val key: String,
        val keyPurposes: KeyPurposes
    ) {

        constructor(
            algorithm: String,
            key: Key,
            keyPurposes: KeyPurposes
        ) : this (
            algorithm = algorithm,
            key = Base64.encode(key.encoded),
            keyPurposes = keyPurposes
        )

        val canEncrypt = keyPurposes.canEncrypt

        val canDecrypt = keyPurposes.canDecrypt

        val canSign = keyPurposes.canSign

        val canVerify = keyPurposes.canVerify

        val canAgree = keyPurposes.canAgree

        val canWrapKey = keyPurposes.canWrapKey

        fun resolveKey(): Key = SecretKeySpec(
            Base64.decode(key.encodeToByteArray()),
            algorithm
        )

        @Validator
        fun canPerform(
            keyOperation: KeyOperation
        ) : Boolean {
            return when(keyOperation) {
                ENCRYPT -> canEncrypt
                DECRYPT -> canDecrypt
                SIGN -> canSign
                VERIFY -> canVerify
                AGREE -> canAgree
                WRAP -> canWrapKey
                OBTAIN_KEY -> true
            }
        }

    }

}