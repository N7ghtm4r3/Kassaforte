package com.tecknobit.kassaforte.key.genspec

// TODO: CHECK TO KEEP
// TODO TO ANNOTATE WITH @Returner
fun Array<DigestType>.convert(): Array<String> {
    return Array(this.size) { index -> this[index].value }
}

// TODO: CHECK TO KEEP
// TODO TO ANNOTATE WITH @Returner
fun Array<EncryptionPaddingType>.convert(): Array<String> {
    return Array(this.size) { index -> this[index].value }
}