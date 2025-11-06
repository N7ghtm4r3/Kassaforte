package com.tecknobit.kassaforte.adapters

import com.tecknobit.equinoxcore.annotations.RequiresDocumentation
import com.tecknobit.equinoxcore.annotations.Wrapper
import com.tecknobit.kassaforte.key.genspec.Algorithm
import com.tecknobit.kassaforte.key.genspec.BlockMode
import com.tecknobit.kassaforte.key.genspec.EncryptionPadding
import com.tecknobit.kassaforte.key.genspec.SymmetricKeyGenSpec
import com.tecknobit.kassaforte.key.usages.KeyPurposes
import com.tecknobit.kassaforte.services.KassaforteSymmetricService
import kotlinx.coroutines.runBlocking

@RequiresDocumentation(
    additionalNotes = "TO ADD SINCE"
)
object KassaforteSymmetricServiceJvm {

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

    @Wrapper
    @JvmStatic
    fun deleteKey(
        alias: String,
    ) = KassaforteSymmetricService.deleteKey(
        alias = alias
    )

}