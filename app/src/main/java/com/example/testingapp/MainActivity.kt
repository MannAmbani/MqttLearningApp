package com.example.testingapp

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.testingapp.databinding.ActivityMainBinding
import com.example.testingapp.network.ResponseManager
import com.example.testingapp.tools.Constants
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var thumbView: View
    private var isOneOn = false
    private var isTwoOn = false
    private var isThreeOn = false
    private var isFourOn = false
    private var isFanOn = false
    private var isSceneOn = false
    private var dim = 0


    private lateinit var broadcast: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        BaseApplication.getApp().initMqtt()
        BaseApplication.getApp().subscribeToPublishTopic()
        thumbView = LayoutInflater.from(this).inflate(R.layout.layout_seekbar_thumb, null, false)

        binding.seekbar.thumb = getThumb(0)
        broadcastListener()


        binding.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                binding.seekbar.thumb = getThumb(progress)
                if (isFanOn) {
                    when (progress) {
//                    0 ->{
//                        dim = 0
//                    }
                        1 -> {
                            val message =
                                "" + BaseApplication.securityCode + BaseApplication.constantString + "A1D001Q"
                            BaseApplication.getApp()
                                .sendData(BaseApplication.getAppContext(), message)
                            dim = 1

                        }
                        2 -> {
                            val message =
                                "" + BaseApplication.securityCode + BaseApplication.constantString + "A1D002Q"
                            BaseApplication.getApp()
                                .sendData(BaseApplication.getAppContext(), message)
                            dim = 2
                        }
                        3 -> {
                            val message =
                                "" + BaseApplication.securityCode + BaseApplication.constantString + "A1D003Q"
                            BaseApplication.getApp()
                                .sendData(BaseApplication.getAppContext(), message)
                            dim = 3
                        }
                        4 -> {
                            val message =
                                "" + BaseApplication.securityCode + BaseApplication.constantString + "A1D004Q"
                            BaseApplication.getApp()
                                .sendData(BaseApplication.getAppContext(), message)
                            dim = 4
                        }
                        5 -> {
                            val message =
                                "" + BaseApplication.securityCode + BaseApplication.constantString + "A1D005Q"
                            BaseApplication.getApp()
                                .sendData(BaseApplication.getAppContext(), message)
                            dim = 5
                        }

                    }
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    var msg = ""

    private fun broadcastListener() {
        broadcast = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.M)
            @SuppressLint("ResourceAsColor")
            override fun onReceive(context: Context, intent: Intent) {
                val message = intent.getStringExtra(Constants.Broadcast.MESSAGE)
                if (message == "update"){

                    val data = intent.getStringExtra(Constants.Broadcast.DATA)
                    msg= data.toString()

                    var security_code = data?.substring(0, 16)
                    try {
                        if (security_code?.contains("R01M01")!!) {
                            security_code = security_code.replace("R01M01", "").trim()
                           msg =msg.replace("R01M01", "").trim()
                            Log.d("TAG", "msg 1 "+ msg)
                        }
//                    201803108F R01M01 UPDATE A 0 0 015 B00003C10020D00000E10000F10000G10000H10000I00000J00000Q
                       msg =msg.replace(security_code + "UPDATE", "")
                        Log.d("TAG","data "+  data.toString())

                        for (i in msg.indices) {
                            if (msg.length > 6) {
                                var str =msg.substring(0, 6)
                                val applianceNumber = str.substring(0, 1)
                                val onOff1: Int = ResponseManager.getNumberFromString(str, 1)
                                //val d_static: Int = ResponseManager.getNumberFromString(str, 2)//D word static
                                val dim: Int = ResponseManager.getNumberFromSubString(str, 3, 6)


                                when(applianceNumber){
                                    "A" ->{
                                        if (onOff1 == 0){
                                            isOneOn = false
                                            binding.one.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))

                                            //fan added 14-04-2023
                                            isFanOn = false
                                            binding.fan.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))
                                        }else {
                                            isOneOn = true
                                            binding.one.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))

                                            //fan added 14-04-2023

                                            isFanOn = true
                                            binding.fan.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                                        }

                                        if (dim<=5){
                                            this@MainActivity.dim = dim
                                            binding.seekbar.thumb = getThumb(dim)
                                            binding.seekbar.progress = dim
                                        }

                                    }
                                    "B" ->{
                                        if (onOff1 == 0){
                                            isTwoOn = false
                                            binding.two.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))
                                        }else {
                                            isTwoOn = true
                                            binding.two.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                                        }
                                    }
                                    "C" ->{
                                        if (onOff1 == 0){
                                            isThreeOn = false
                                            binding.three.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))
                                        }else {
                                            isThreeOn = true
                                            binding.three.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                                        }
                                    }
                                    "D" ->{
                                        if (onOff1 == 0){
                                            isFourOn = false
                                            binding.four.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))
                                        }else {
                                            isFourOn = true
                                            binding.four.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                                        }
                                    }
//                                    "E" ->{
//                                        if (onOff1 == 0){
//                                            isFanOn = false
//                                            binding.fan.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))
//                                        }else {
//                                            isFanOn = true
//                                            binding.fan.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
//
//                                        }
//                                        if (dim<=5){
//                                            this@MainActivity.dim = dim
//                                            binding.seekbar.thumb = getThumb(dim)
//                                            binding.seekbar.progress = dim
//                                        }
//                                    }

                                }

                                Log.d("TAG", "$applianceNumber an $onOff1 onoff  $dim dim")

                               msg =msg.replace(str, "")
                            } else {
                                break
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
               else if (message != null && message.contains("DEVICELIVE")) {

                    setDeviceLiveStatus(true)

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(Constants.Broadcast.REVERSE_DATA)
        LocalBroadcastManager.getInstance(parent).registerReceiver(broadcast, intentFilter)
        setDeviceLiveStatus(false)

    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(parent).unregisterReceiver(broadcast)
    }

    private fun setDeviceLiveStatus(isLive: Boolean) {
        if (isLive) {
            binding.ivReverseUpdate.setBackgroundResource(R.drawable.bg_green_round)
            BaseApplication.getApp().sendData(this,BaseApplication.securityCode + getR01M01() + "OOBUPDATE")
        } else {
            binding.ivReverseUpdate.setBackgroundResource(R.drawable.bg_red_round)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseApplication.getApp().unsubscribePubTopic()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.one -> {
                if (!isOneOn) {
                    val message =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "A1D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message)
                    binding.one.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                    isOneOn = true
                } else {
                    val message =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "A0D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message)
                    binding.one.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))
                    isOneOn = false
                }
            }
            R.id.two -> {
                if (!isTwoOn) {
                    val message =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "B1D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message)
                    binding.two.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                    isTwoOn = true
                } else {
                    val message =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "B0D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message)
                    binding.two.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))
                    isTwoOn = false
                }
            }
            R.id.three -> {

                if (!isThreeOn) {
                    val message =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "C1D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message)
                    binding.three.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                    isThreeOn = true
                } else {
                    val message =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "C0D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message)
                    binding.three.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))
                    isThreeOn = false
                }
            }
            R.id.four -> {
                if (!isFourOn) {
                    val message =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "D1D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message)
                    binding.four.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                    isFourOn = true
                } else {
                    val message =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "D0D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message)
                    binding.four.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))
                    isFourOn = false
                }
            }
            R.id.fan -> {
                if (!isFanOn) {
//                    val message =
//                        "" + BaseApplication.securityCode + BaseApplication.constantString + "E1D005Q"
//                    BaseApplication.getApp()
//                        .sendData(BaseApplication.getAppContext(), message)


//                    if (dim == 0 ){
//                        dim  = 1
//                        binding.seekbar.thumb = getThumb(1)
//                        binding.seekbar.progress = 1
//                    }else{
                        binding.seekbar.thumb = getThumb(dim)
                        binding.seekbar.progress = dim
//                    }




                    binding.fan.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                    isFanOn = true
                } else {

//                    binding.seekbar.thumb = getThumb(0)
//                    binding.seekbar.progress = 0
                    binding.fan.setCardBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.gray))
                    isFanOn = false
                }
                val fanOnoff = if ( isFanOn) 1 else 0
                val message =
                    "" + BaseApplication.securityCode + BaseApplication.constantString + "A${fanOnoff}D00${dim}Q"
                BaseApplication.getApp()
                    .sendData(BaseApplication.getAppContext(), message)
            }
            R.id.on -> {


                    val message =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "A1D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message)


                Handler().postDelayed({
                    val message1 =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "B1D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message1)

                }, 500)
                Handler().postDelayed({
                    val message3 =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "C1D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message3)
                }, 1000)
                Handler().postDelayed({
                    val message4 =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "D1D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message4)
                }, 1500)

                Handler().postDelayed({
                val message5 =
                    "" + BaseApplication.securityCode + BaseApplication.constantString + "E1D005Q"
                BaseApplication.getApp()
                    .sendData(BaseApplication.getAppContext(), message5)
                }, 2000)
            }
            R.id.off -> {


                    val message =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "A0D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message)

                Handler().postDelayed({
                    val message1 =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "B0D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message1)
                }, 500)
                Handler().postDelayed({
                    val message3 =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "C0D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message3)
                }, 1000)
                Handler().postDelayed({
                    val message4 =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "D0D000Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message4)
                }, 1500)

                Handler().postDelayed({
                val message5 =
                    "" + BaseApplication.securityCode + BaseApplication.constantString + "E0D005Q"
                BaseApplication.getApp()
                    .sendData(BaseApplication.getAppContext(), message5)

                }, 2000)
            }
            R.id.scenePlay -> {
//                if (!isSceneOn) {
                        val message =
                            "" + BaseApplication.securityCode + BaseApplication.constantString + "A1D000Q"
                        BaseApplication.getApp()
                            .sendData(BaseApplication.getAppContext(), message)

                    Handler().postDelayed({
                        val message1 =
                            "" + BaseApplication.securityCode + BaseApplication.constantString + "B1D000Q"
                        BaseApplication.getApp()
                            .sendData(BaseApplication.getAppContext(), message1)
                    }, 400)
                    Handler().postDelayed({
                        val message3 =
                            "" + BaseApplication.securityCode + BaseApplication.constantString + "C1D000Q"
                        BaseApplication.getApp()
                            .sendData(BaseApplication.getAppContext(), message3)
                    }, 800)
                    Handler().postDelayed({
                        val message4 =
                            "" + BaseApplication.securityCode + BaseApplication.constantString + "D1D000Q"
                        BaseApplication.getApp()
                            .sendData(BaseApplication.getAppContext(), message4)
                    }, 1200)
                    Handler().postDelayed({
                    val message5 =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "E1D005Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message5)
                }, 1600)
                    isSceneOn = true

//                } else {

                Handler().postDelayed({
                        val message6 =
                            "" + BaseApplication.securityCode + BaseApplication.constantString + "A0D000Q"
                        BaseApplication.getApp()
                            .sendData(BaseApplication.getAppContext(), message6)
                }, 2000)
                    Handler().postDelayed({
                        val message1 =
                            "" + BaseApplication.securityCode + BaseApplication.constantString + "B0D000Q"
                        BaseApplication.getApp()
                            .sendData(BaseApplication.getAppContext(), message1)
                    }, 2400)
                    Handler().postDelayed({
                        val message3 =
                            "" + BaseApplication.securityCode + BaseApplication.constantString + "C0D000Q"
                        BaseApplication.getApp()
                            .sendData(BaseApplication.getAppContext(), message3)
                    }, 2800)
                    Handler().postDelayed({
                        val message4 =
                            "" + BaseApplication.securityCode + BaseApplication.constantString + "D0D000Q"
                        BaseApplication.getApp()
                            .sendData(BaseApplication.getAppContext(), message4)
                    }, 3200)

                    Handler().postDelayed({
                    val message5 =
                        "" + BaseApplication.securityCode + BaseApplication.constantString + "E0D005Q"
                    BaseApplication.getApp()
                        .sendData(BaseApplication.getAppContext(), message5)
                    }, 3600)
//                    isSceneOn = false
//                }

            }
        }
    }

    fun getThumb(progress: Int): Drawable? {
        (thumbView.findViewById(R.id.tvProgress) as TextView).text = progress.toString() + ""
        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(
            thumbView.measuredWidth,
            thumbView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        thumbView.layout(0, 0, thumbView.measuredWidth, thumbView.measuredHeight)
        thumbView.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }
}