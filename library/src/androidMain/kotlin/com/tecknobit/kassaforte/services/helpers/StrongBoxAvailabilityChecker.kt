package com.tecknobit.kassaforte.services.helpers

import android.content.pm.PackageManager.FEATURE_STRONGBOX_KEYSTORE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import com.tecknobit.equinoxcore.annotations.RequiresDocumentation
import com.tecknobit.equinoxcore.utilities.AppContext

@RequiresDocumentation(
    additionalNotes = "TO INSERT SINCE Revision Two"
)
fun isStrongBoxAvailable(): Boolean {
    val context = AppContext.get()
    val packageManager = context.packageManager
    return if (SDK_INT >= P)
        packageManager.hasSystemFeature(FEATURE_STRONGBOX_KEYSTORE)
    else
        false
}