package com.tecknobit.kassaforte.wrappers.crypto.params

import com.tecknobit.equinoxcore.annotations.RequiresDocumentation

@RequiresDocumentation(
    additionalNotes = "INSERT SINCE Revision Two"
)
external interface EcdsaParams : EncryptionParams {

    val hash: String

}