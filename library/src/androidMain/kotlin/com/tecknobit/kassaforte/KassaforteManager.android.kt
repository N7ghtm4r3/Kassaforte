package com.tecknobit.kassaforte

import android.content.Context
import android.content.SharedPreferences
import com.tecknobit.equinoxcore.utilities.AppContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class KassaforteManager actual constructor(
    kassaforteName: String
) {

    private val appContext = AppContext.get()

    private val prefs: SharedPreferences = appContext.getSharedPreferences(
        kassaforteName,
        Context.MODE_PRIVATE
    )
    
    actual fun store(
        key: String,
        data: String,
    ) {
        prefs.edit().putString(
            key,
            data
        ).apply()
    }

    actual fun retrieve(
        key: String
    ): String? {
        return prefs.getString(
            key,
            null
        )
    }
    
    actual fun remove(
        key: String
    ) {
        prefs.edit().remove(
            key
        ).apply()
    }

    actual fun hasKeyStored(
        key: String
    ): Boolean {
        return prefs.contains(
            key
        )
    }

}