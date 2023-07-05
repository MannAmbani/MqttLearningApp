package com.example.testingapp.network

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.testingapp.BaseApplication
import com.example.testingapp.tools.Constants

class ResponseManager {

    companion object {
        val TAG = "MQTT"
        var msg = ""
        var messageKey = ""
        fun responseHandle(message: String) {
            this.msg = message
            if (msg.contains("DEVICELIVE")) {
                sendBroadcastReverseData(msg)
            }  else if (msg.contains("UPDATE")) {
                messageKey = "update"

                var security_code = msg.substring(0, 16)
                try {
                    if (security_code.contains("R01M01")) {
                        security_code = security_code.replace("R01M01", "").trim()
                        msg = msg.replace("R01M01", "").trim()
                    }
//                    201803108F R01M01 UPDATE A 0 0 015 B00003C10020D00000E10000F10000G10000H10000I00000J00000Q
                    msg = msg.replace(security_code + "UPDATE", "")

                    for (i in msg.indices) {
                        if (msg.length > 6) {
                            var str = msg.substring(0, 6)
                            val applianceNumber = str.substring(0, 1)
                            val onOff1: Int = getNumberFromString(str, 1)
                            val cl: Int = getNumberFromString(str, 2)
                            val dim: Int = getNumberFromSubString(str, 3, 6)

                            msg = msg.replace(str, "")
                        } else {
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                sendBroadcastReverseData(messageKey, message)
            }
        }

        private fun sendBroadcastModuleConfig(msg: String) {
            val intent = Intent(Constants.Broadcast.CONFIG_MODULE)
            intent.putExtra(Constants.Broadcast.MESSAGE, msg)
            LocalBroadcastManager.getInstance(BaseApplication.getAppContext())
                .sendBroadcast(intent)
        }

        private fun sendBroadcastReverseData(msg: String) {
            val intent = Intent(Constants.Broadcast.REVERSE_DATA)
            intent.putExtra(Constants.Broadcast.MESSAGE, msg)
            LocalBroadcastManager.getInstance(BaseApplication.getAppContext())
                .sendBroadcast(intent)
        }

        private fun sendBroadcastReverseData(msgKey: String, msg: String) {
            val intent = Intent(Constants.Broadcast.REVERSE_DATA)
            intent.putExtra(Constants.Broadcast.MESSAGE, msgKey)
            intent.putExtra(Constants.Broadcast.DATA, msg)
            LocalBroadcastManager.getInstance(BaseApplication.getAppContext())
                .sendBroadcast(intent)
        }

    fun getNumberFromString(str: String, poss: Int): Int {
            try {
                return (str[poss].toString() + "").toInt()

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return 0
        }

     fun getNumberFromSubString(str: String, poss1: Int, poss2: Int): Int {
            try {
                return (str.substring(poss1, poss2) + "").toInt()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return 0
        }


    }
}

