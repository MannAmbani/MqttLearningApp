package com.example.testingapp.tools

import android.os.Environment
import android.util.Log
import com.example.testingapp.BuildConfig
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * <h1>Debug.java</h1>
 * <p>
 * The Debug class implements Log functionality which filters
 * Application logs with the TAG provided by the developer.
 * <p>
 * developer can easily filter logs by using this class.
 *
 * @author OOB  Automation
 * @version 1.1
 * @Created By MONA
 * @since 2020-12-12
 */
object Debug {
    private val TAG = "TAG"
    var isDebug: Boolean = BuildConfig.DEBUG
    var isPersistant = false

    /**
     * This method is used to print log in "Android Monitor".
     * This is a simplest method to print log , message will be defined
     * by the developer.
     *
     *
     * If developer wants to save log on sdcard, then it is possible
     * using the append method.
     *
     *
     * It will print log if isPersistant is true.
     *
     * @param msg : This is the message developer wants to print.
     * @return Nothing
     */
    fun trace(msg: Any) {
        if (isDebug) {
            Log.i(TAG, msg.toString())
            if (isPersistant) {
                appendLog(msg.toString())
            }
        }
    }

    /**
     * This method is used print log in "Android Monitor".
     * This is a simplest method to print log , message will be defined
     * by the developer.
     *
     * @param tag : This is the filter tag,by which user can filter logs.
     * @param msg : This is the message developer wants to print.
     * @return Nothing
     */
    fun trace(tag: String?, msg: Any?) {
        if (isDebug) {
            Log.i(tag, msg.toString())
        }
    }

    /**
     * This is the method used to save log into sdcard.
     * path will be defined in file object.
     *
     * @param text : Message, which requires to save append string.
     * @return Nothing
     */
    fun longTrace(tag: String?, text: String) {
        if (isDebug) {
            val maxLogSize = 4055
            for (i in 0..text.length / maxLogSize) {
                val start = i * maxLogSize
                var end = (i + 1) * maxLogSize
                end = if (end > text.length) text.length else end
                trace(tag, text.substring(start, end))
            }
        }
    }

    fun longTrace(text: String) {
        if (isDebug) {
            val maxLogSize = 4055
            for (i in 0..text.length / maxLogSize) {
                val start = i * maxLogSize
                var end = (i + 1) * maxLogSize
                end = if (end > text.length) text.length else end
                trace(TAG, text.substring(start, end))
            }
        }
    }

    /**
     * This is the method used to save log into sdcard.
     * path will be defined in file object.
     *
     * @param text : Message, which requires to save in .txt file.
     * @return Nothing
     */
    private fun appendLog(text: String) {
        val logFile = File(Environment.getExternalStorageDirectory().toString() + "/tajr.txt")
        if (!logFile.exists()) try {
            logFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            // BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}