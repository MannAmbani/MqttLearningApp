package com.example.testingapp

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.example.testingapp.network.ResponseManager
import com.example.testingapp.tools.Constants
import com.example.testingapp.tools.Debug
//import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import info.mqtt.android.service.MqttAndroidClient

class BaseApplication : MultiDexApplication() {
    private lateinit var client: MqttAndroidClient
    private lateinit var messageKey: String
    private  var position:Int = 0

    init {
        mInstance = this
    }

    fun initMqtt(){
        client = MqttAndroidClient(this.applicationContext, serverUrl, clientId)
        if (client.isConnected){
            Log.d("MQTT","Connected mqtt")
            subscribeToTopic()
            return
        }else {

            try {
                val options = MqttConnectOptions()
                options.isAutomaticReconnect = true
                options.isCleanSession = false
                options.userName = userName
                options.password = password.toCharArray()
                val token: IMqttToken = client.connect(options)
                token.actionCallback = object :IMqttActionListener{
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("MQTT","Connected mqtt")
                        messageKey = "Mqtt"
                        subscribeToTopic()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d("MQTT","failed to connect")
                    }

                }
            }catch (e:MqttException){
                e.printStackTrace()
            }
        }
    }

    private fun subscribeToTopic(){
        try {
            val token:IMqttToken = client.subscribe(subTopic,0)
            token.actionCallback = object:IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT","subscribed $subTopic")
                    sendData(applicationContext,
                        securityCode + Constants.SEND_HI_OOB
                    )
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTT","failed to subscribe $subTopic")
                }
            }
            client.setCallback(object :MqttCallback{
                override fun connectionLost(cause: Throwable?) {
                    Log.d("MQTT","Connection Lost Due to $cause")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val data = String(message!!.payload)
                    if (!data.contains(getUDID(applicationContext))) {
                        Debug.trace("TAG", "sb Incoming message: $data")
                        processData(data.split("Q")[0] + "Q")
                    }
                    Log.d("MQTT","sb $topic : $data")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("MQTT","delivery Completed")
                }
            })
        }catch (e:MqttException){
            e.printStackTrace()
        }
    }

    fun subscribeToPublishTopic(){
        try {
            val mqttToken = client.subscribe(publishTopic,0)
            mqttToken.actionCallback = object :IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT","p subscribed $publishTopic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTT","p failed to subscribe $publishTopic")
                }
            }
            client.setCallback(object :MqttCallback{
                override fun connectionLost(cause: Throwable?) {
                    Log.d("MQTT","p connection Lost")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val data = String(message!!.payload)


                    Log.d("MQTT","p $topic : $data")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("MQTT","p delivery completed")
                }
            })
        }catch (e:MqttException){
            System.err.println("Exception whilst subscribing")
            e.printStackTrace()
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    //new 13-4-2023
    fun processData(msg: String) {
        if (msg.isNotEmpty()) {
            if (msg.contains("Q")) {
                val sub = msg.substring(0, msg.indexOf("Q"))
                val message = sub.trim() + "Q"
                ResponseManager.responseHandle(message)
            }
        }
    }

    fun unsubscribePubTopic(){
        val mqttToken:IMqttToken
        try {
            mqttToken = client.unsubscribe(publishTopic)
            mqttToken.actionCallback = object :IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT","unSubscribed $publishTopic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTT","failed to unSubscribe")
                }

            }
        }catch (ex:java.lang.Exception){
            ex.printStackTrace()
        }
    }

    fun unSubscribeTopic(){
        val mqttToken: IMqttToken?
        try {
            mqttToken = client.unsubscribe(subTopic)
            mqttToken.actionCallback = object :IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT","unSubscribed $subTopic")
                    client.close()
                    try {
                        client.disconnect()
                    }catch (e:MqttException){
                        e.printStackTrace()
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTT","failed to unSubscribe")
                    client.close()
                    try {
                        client.disconnect()
                    }catch (e:MqttException){
                        e.printStackTrace()
                    }
                }
            }
        }catch (e:MqttException){
            e.printStackTrace()
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    private fun publishMessage(context: Context,publishMessage:String){
        try {
            val message = MqttMessage()
            message.payload = publishMessage.toByteArray()
            client.publish(publishTopic,message)
            Log.d("MQTT","Message published $publishMessage")
        }catch (e:MqttException){
            e.printStackTrace()
        }
    }

    fun sendData(context: Context,json:String){
        if (client.isConnected){
            publishMessage(context,json)
        }else{
            Log.d("MQTT","Not connected, Please try again.")
        }
    }
    fun stopMqtt(){
        if (client.isConnected){
            try {
                unSubscribeTopic()
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }
        }
    }

    companion object {
         var clientId = MqttAsyncClient.generateClientId() //"OOBCLIENT1609826917"
         var mInstance: BaseApplication? = null
        const val securityCode = "9714090305"
        const val subTopic = securityCode+ "SB"
        const val publishTopic = securityCode + "PB"
        const val constantString = "R01M01"
        const val serverUrl = "tcp://143.110.182.238:1883"
        const val userName = "oob1"
        const val password = "hs5vSgb8Tmt2nTV5"

        fun getApp(): BaseApplication {
            return if (mInstance != null && mInstance is BaseApplication) {
                mInstance as BaseApplication
            } else {
                mInstance = BaseApplication()
                mInstance!!.onCreate()
                mInstance as BaseApplication
            }
        }

        fun getAppActivity() :Activity{
            return Activity()
        }

        fun getAppContext():Context{
            if (mInstance == null){
                mInstance!!.applicationContext
            }
            return mInstance as BaseApplication
        }
    }
}