package com.tecknobit.kassaforte.services.helpers

import com.tecknobit.kassaforte.key.usages.KeyDetailsSheet
import kotlinx.serialization.KSerializer

internal class KassaforteServiceWinImplManager<KI : KeyDetailsSheet<*>>(
    serializer: KSerializer<KI>,
) : KassaforteServiceImplManager<KI>(
    serializer = serializer
) {

    override fun storeKeyData(
        alias: String,
        keyInfo: KI,
    ) {
        TODO("Not yet implemented")
    }

    override fun isAliasTaken(
        alias: String,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun retrieveKey(
        alias: String,
    ): KI {
        TODO("Not yet implemented")
    }

    override fun removeKey(
        alias: String,
    ) {
        TODO("Not yet implemented")
    }

}
