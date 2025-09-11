package com.teckonobit.kassaforte.keyspec

import com.tecknobit.equinoxcore.annotations.Returner

@Returner
fun Array<BlockModeType>.convert(): Array<String> {
    return Array(this.size) { index -> this[index].value }
}

@Returner
fun Array<DigestType>.convert(): Array<String> {
    return Array(this.size) { index -> this[index].value }
}

@Returner
fun Array<EncryptionPaddingType>.convert(): Array<String> {
    return Array(this.size) { index -> this[index].value }
}