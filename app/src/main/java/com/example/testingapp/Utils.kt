package com.example.testingapp

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

@SuppressLint("HardwareIds")
fun getUDID(context: Context): String {
    return Settings.Secure.getString(
        context.contentResolver, Settings.Secure.ANDROID_ID
    ).substring(0, 5)
}


fun getR01M01(): String {
    return "R01M01"
}