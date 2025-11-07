package com.tecknobit.kassaforte.wrappers.crypto.key.genspec

import com.tecknobit.equinoxcore.annotations.RequiresDocumentation

@RequiresDocumentation(
    additionalNotes = "TO INSERT SINCE Revision Two"
)
external interface HmacKeyGenParams : SymmetricKeyGenSpec {

    val hash: String

}