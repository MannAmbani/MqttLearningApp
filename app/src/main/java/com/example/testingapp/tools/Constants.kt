package com.example.testingapp.tools

object Constants {


    const val HINDI = "hi"
    const val BENGALI = "bn"
    const val MARATHI = "mr"
    const val TELUGU = "te"
    const val TAMIL = "ta"
    const val GUJARATI = "gu"
    const val URDU = "ur"
    const val ENGLISH = "en"
    const val OLD_USER_OTP: Int = 22
    const val KEY_IP_ADDRESS = "192.168.0.201"
    var isInitHeadset = false

    object Broadcast {
        val BROADCAST_ACTION_TV_LEARNING = "broadcast_tv_learning"
        const val CONFIG_MODULE = "config_module_via_wifi"
        const val MESSAGE = "msg"
        const val DATA = "data"
        const val REVERSE_DATA = "reverse_data"
        const val DOWNLOAD_ALL_DATA = "download_all_data"
        const val DOWNLOAD_SOME_DATA = "download_some_data"
    }

    const val EXTRA_AC_ID = "extra_ac_id"
    const val EXTRA_BUTTON_NUMBER = "extra_button_number"
    const val EXTRA_AC_BUTTON = "extra_ac_button"
    const val SEND_HI_OOB = "HIOOB"

    const val REQUEST_CODE_UPDATE_PIC = 0x1
    const val DAYS_ZERO_VALUES = "0000000"

    fun getTwoDigit(redclr: Int): String {
        var threeDigit = ""
        threeDigit = if (redclr < 10) {
            "0$redclr"
        } else {
            "" + redclr
        }
        return threeDigit
    }

    fun getThreeDigit(redclr: Int): String {
        var threeDigit = ""
        threeDigit = if (redclr < 10) {
            "00$redclr"
        } else if (redclr < 100) {
            "0$redclr"
        } else {
            "" + redclr
        }
        return threeDigit
    }

//    fun getThreeDigit(redclr: String): String {
//        val threeDigit = ""
//        if (TextUtils.isEmpty(redclr)) {
//            return threeDigit
//        }
//        val num = redclr.toInt()
//        return String.format(Locale.getDefault(), "%03d", num)
//    }
}