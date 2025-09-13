package com.teckonobit.kassaforte.services

import com.tecknobit.equinoxcore.annotations.Assembler
import com.tecknobit.equinoxcore.annotations.Returner
import com.teckonobit.kassaforte.Kassaforte
import com.teckonobit.kassaforte.key.KeyPurposes
import com.teckonobit.kassaforte.key.genspec.BlockModeType
import com.teckonobit.kassaforte.key.genspec.EncryptionPaddingType
import com.teckonobit.kassaforte.key.genspec.EncryptionPaddingType.PKCS7
import com.teckonobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.teckonobit.kassaforte.services.KassaforteKeysService.Companion.ALIAS_ALREADY_TAKEN_ERROR
import com.teckonobit.kassaforte.services.KassaforteKeysService.Companion.IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR
import java.security.Key
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class KassaforteSymmetricServiceImpl actual constructor() {
    
    actual fun generateKey(
        alias: String,
        keyGenSpec: SymmetricKeyGenSpec,
        purposes: KeyPurposes,
    ) {
        if (aliasExists(alias))
            throw IllegalAccessException(ALIAS_ALREADY_TAKEN_ERROR)
        val algorithm = keyGenSpec.algorithm.value
        val keyGenerator = KeyGenerator.getInstance(algorithm)
        keyGenSpec.keySize?.let { keySize ->
            keyGenerator.init(keySize)
        }
        storeKeyData(
            alias = alias,
            key = keyGenerator.generateKey()
        )
    }

    actual fun aliasExists(
        alias: String
    ): Boolean {
        val kassaforte = Kassaforte(
            name = alias
        )
        return kassaforte.withdraw(
            key = alias
        ) != null
    }

    private fun storeKeyData(
        alias: String,
        key: Key
    ) {
        val keyData = formatKeyData(
            key = key
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
        key: Key
    ) : String {
        val encodedKey = Base64.encode(key.encoded)
        val keyData = "${key.algorithm}-$encodedKey".encodeToByteArray()
        return Base64.encode(keyData)
    }

    actual fun getKey(
        alias: String
    ): Key {
        val kassaforte = Kassaforte(
            name = alias
        )
        val keyData = kassaforte.withdraw(
            key = alias
        )
        if(keyData == null)
            throw IllegalAccessException(IMPOSSIBLE_TO_RETRIEVE_KEY_ERROR)
        val decodedKeyData = Base64.decode(
            source = keyData
        ).decodeToString().split("-")
        println(decodedKeyData)
        val algorithm = decodedKeyData[0]
        return SecretKeySpec(Base64.decode(decodedKeyData[1]), algorithm)
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
            PKCS7 -> "PKCS5Padding"
            else -> this.value
        }
    }

    actual fun deleteKey(
        alias: String
    ) {
        val kassaforte = Kassaforte(
            name = alias
        )
        kassaforte.remove(
            key = alias
        )
    }

}