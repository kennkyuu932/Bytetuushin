package com.example.bytetuushin

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

class ClientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        val ser_ip=intent.getStringExtra(MainActivity.intent_cli)

        val cli_test1= mutableListOf<ByteArray>()
        val cli_test2= mutableListOf<ByteArray>()
        cli_test1.add("World".toByteArray())
        cli_test1.add("Client".toByteArray())
        cli_test1.add("Receive".toByteArray())
        cli_test2.add("edc".toByteArray())
        cli_test2.add("rfv".toByteArray())
        val cli_send= mutableListOf<List<ByteArray>>()
        cli_send.add(cli_test1)
        cli_send.add(cli_test2)

        val cli_res_list= mutableListOf<List<ByteArray>>()

        lifecycleScope.launch {
            if (ser_ip!=null){
                Control.ClientConnect(ser_ip)
                var i=0
//                while (true){
//                    Control.ClientReceiveMessage()
//                    Log.d(TAG, "Client receive: ${Control.cli_res_mes}")
//                    if (Control.cli_res_flag=="start server"){
//                        Log.d(TAG, "onCreate: ${Control.cli_res_flag}")
//                    }
//                    if (Control.cli_res_flag=="end server"){
//                        Log.d(TAG,"onCreate: ${Control.cli_res_flag}")
//                    }

//                    Control.ClientReceiveSize()
//                    Log.d(TAG, "onCreate: ${Control.cli_res_size}")
//                    val size_first=Control.cli_res_size
//                    Log.d(TAG, "onCreate: $size_first")
//                    Control.ClientToServerOK()
//
//                }
                Control.ClientReceiveFlag()
                val firstlistsize=Control.cli_res_flag
                Control.cli_res_flag?.let { Control.ClientSendFlag(it) }
                Log.d(TAG, "onCreate: $firstlistsize loop")
                while (i< firstlistsize!!){
                    //フラグの初期化
                    Control.cli_res_flag=null
                    Control.ClientReceiveFlag()
                    val cli_res_second = mutableListOf<ByteArray>()
                    var j=0
                    val secondlistsize=Control.cli_res_flag
                    Control.cli_res_flag?.let { Control.ClientSendFlag(it) }
                    while (j<secondlistsize!!){
                        //フラグの初期化
                        Control.cli_res_flag=null
                        Control.ClientReceiveFlag()
                        val thirdlistsize=Control.cli_res_flag
                        Log.d(TAG, "onCreate: thirdlistsize ${thirdlistsize}")
                        Control.cli_res_flag?.let { Control.ClientSendFlag(it) }
                        if (thirdlistsize != null) {
                            Control.ClientReceive(thirdlistsize)
                        }
                        j++
                        Control.cli_res_mes.let { cli_res_second.add(it) }
                        val res_size= Control.cli_res_mes.size
                        Control.ClientSendFlag(res_size)
                    }
                    i++
                    cli_res_list.add(cli_res_second)
                }
            }
            for (test1 in cli_res_list){
                for (test2 in test1){
                    Log.d(TAG, "onCreate: test ${String(test2)}")
                }
            }
        }

    }
}