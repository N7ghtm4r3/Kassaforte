package com.tecknobit.kassaforte.key.genspec

import com.tecknobit.equinoxcore.annotations.Returner

// TODO: CHECK TO KEEP
@Returner
fun Array<DigestType>.convert(): Array<String> {
    return Array(this.size) { index -> this[index].value }
}

// TODO: CHECK TO KEEP
@Returner
fun Array<EncryptionPaddingType>.convert(): Array<String> {
    return Array(this.size) { index -> this[index].value }
}