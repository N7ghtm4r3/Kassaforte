package com.tecknobit.kassaforte

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class KassaforteManager actual constructor(
    kassaforteName: String
) {

    actual fun store(
        key: String,
        data: String,
    ) {
        localStorage.set(
            key = key,
            value = data
        )
    }

    actual fun retrieve(
        key: String
    ): String? {
        return localStorage.getItem(
            key = key
        )
    }

    actual fun remove(
        key: String
    ) {
        localStorage.removeItem(
            key = key
        )
    }

    actual fun hasKeyStored(
        key: String
    ): Boolean {
        return localStorage[key] != null
    }

}