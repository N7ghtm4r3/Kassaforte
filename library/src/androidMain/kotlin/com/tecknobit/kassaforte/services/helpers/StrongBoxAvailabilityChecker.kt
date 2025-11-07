package com.tecknobit.kassaforte.services.helpers

import android.content.pm.PackageManager.FEATURE_STRONGBOX_KEYSTORE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import com.tecknobit.equinoxcore.utilities.AppContext

/**
 * Method used to verify the availability of the `android.hardware.strongbox_keystore` from the device
 *
 * @return the availability of the `StrongBox` as [Boolean]
 *
 * @since Revision Two
 */
fun isStrongBoxAvailable(): Boolean {
    val context = AppContext.get()
    val packageManager = context.packageManager
    return if (SDK_INT >= P)
        packageManager.hasSystemFeature(FEATURE_STRONGBOX_KEYSTORE)
    else
        false
}