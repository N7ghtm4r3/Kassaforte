package com.tecknobit.kassaforte.services.helpers

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties.PURPOSE_AGREE_KEY
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import android.security.keystore.KeyProperties.PURPOSE_VERIFY
import android.security.keystore.KeyProperties.PURPOSE_WRAP_KEY
import com.tecknobit.equinoxcore.annotations.Returner
import com.tecknobit.kassaforte.key.usages.KeyOperation
import com.tecknobit.kassaforte.key.usages.KeyOperation.AGREE
import com.tecknobit.kassaforte.key.usages.KeyOperation.DECRYPT
import com.tecknobit.kassaforte.key.usages.KeyOperation.ENCRYPT
import com.tecknobit.kassaforte.key.usages.KeyOperation.OBTAIN_KEY
import com.tecknobit.kassaforte.key.usages.KeyOperation.SIGN
import com.tecknobit.kassaforte.key.usages.KeyOperation.UNWRAP
import com.tecknobit.kassaforte.key.usages.KeyOperation.VERIFY
import com.tecknobit.kassaforte.key.usages.KeyOperation.WRAP
import java.security.Key
import java.security.KeyFactory
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory

// TODO: TO DOCU SINCE
fun Key.canPerform(
    keyOperation: KeyOperation,
): Boolean {
    if (this is SecretKey)
        return this.canPerform(keyOperation)

    val keyFactory = KeyFactory.getInstance(this.algorithm)
    val keyInfo = keyFactory.getKeySpec(this, KeyInfo::class.java)

    return checkResult(
        purposes = keyInfo.purposes,
        keyOperation = keyOperation
    )
}

private fun SecretKey.canPerform(
    keyOperation: KeyOperation,
): Boolean {
    val keyFactory = SecretKeyFactory.getInstance(this.algorithm)
    val keyInfo = keyFactory.getKeySpec(this, KeyInfo::class.java) as KeyInfo

    return checkResult(
        purposes = keyInfo.purposes,
        keyOperation = keyOperation
    )
}

@Returner
private fun checkResult(
    purposes: Int,
    keyOperation: KeyOperation,
): Boolean {
    return when (keyOperation) {
        ENCRYPT -> purposes and PURPOSE_ENCRYPT != 0
        DECRYPT -> purposes and PURPOSE_DECRYPT != 0
        SIGN -> purposes and PURPOSE_SIGN != 0
        VERIFY -> purposes and PURPOSE_VERIFY != 0
        AGREE -> if (SDK_INT >= Build.VERSION_CODES.S)
            purposes and PURPOSE_AGREE_KEY != 0
        else
            false

        WRAP, UNWRAP -> if (SDK_INT >= P)
            purposes and PURPOSE_WRAP_KEY != 0
        else
            false

        OBTAIN_KEY -> true
    }
}