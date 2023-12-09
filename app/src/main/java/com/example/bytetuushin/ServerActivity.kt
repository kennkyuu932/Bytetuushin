package com.example.bytetuushin

import android.content.ContentValues.TAG
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Inet4Address

class ServerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)

        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val server_ip_text=findViewById<TextView>(R.id.ipser)

        val networkCallback = object : ConnectivityManager.NetworkCallback(){
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)
                server_ip_text.text=linkProperties.linkAddresses.filter { it.address is Inet4Address
                }[0].toString()
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        val ser_test1= mutableListOf<ByteArray>()
        val ser_test2= mutableListOf<ByteArray>()
        val ser_send= mutableListOf<List<ByteArray>>()

        ser_test1.add("Hello".toByteArray())
        ser_test1.add("Server".toByteArray())
        ser_test1.add("Send".toByteArray())
        ser_test2.add("ABC".toByteArray())
        ser_test2.add("xyz".toByteArray())
        ser_send.add(ser_test1)
        ser_send.add(ser_test2)
        //変換確認
//        Log.d(TAG, "onCreate: Hello to byte: ${String(ser_test1[0])}")
//        Log.d(TAG, "onCreate: Server to byte: ${String(ser_test1[1])}")
//        Log.d(TAG, "onCreate: Send to byte: ${String(ser_test1[2])}")
//        Log.d(TAG, "onCreate: ABC to byte: ${String(ser_test2[0])}")
//        Log.d(TAG, "onCreate: xyz to byte: ${String(ser_test2[1])}")

        Log.d(TAG, "onCreate: create send data")

        lifecycleScope.launch {
            Log.d(TAG, "onCreate: server connect start")
            Control.ServerConnect()
            Log.d(TAG, "onCreate: server message send start")
            Control.ServerSend(ser_send)
//            Control.ServerSendMessage(ser_send)
//            while (true) {
//                //Log.d(TAG, "onCreate: server receive while")
//                Control.ServerReceiveMessage()
//                withContext(Dispatchers.Main) {
////                    res_text.setText(Control.ser_res_mes)
//                    //Log.d(TAG, "onCreate: receive message")
//                }
//            }
        }

//        server_send_button.setOnClickListener {
//            lifecycleScope.launch {
////                Control.ServerSendMessage(edit_mes.text.toString())
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Control.ServerDisConnect()
    }
}