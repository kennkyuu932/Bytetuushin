package com.example.bytetuushin

import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.net.Socket
import kotlin.math.log

object Control {
//    var ser_res_flag:String?=null
    var ser_res_flag:Int?=null
//    var cli_res_flag:String?=null
    var cli_res_flag:Int?=null
    var cli_res_size:Int?=null
    var ser_res_size:Int?=null
    var ser_res_mes:ByteArray?=null
    lateinit var cli_res_mes:ByteArray
    private var ser_serversoc: ServerSocket?=null
    private var ser_socket: Socket?=null
    private var ser_Dis: DataInputStream?=null
    private var ser_Dos: DataOutputStream?=null
    private var cli_socket: Socket?=null
    private var cli_Dis: DataInputStream?=null
    private var cli_Dos: DataOutputStream?=null

    const val PORT: Int = 50000

    suspend fun ServerConnect()= withContext(Dispatchers.IO){
        Log.d(TAG, "ServerConnect: Start")
        try {
            ser_serversoc= ServerSocket(PORT)
            ser_serversoc?.reuseAddress=true
            Log.d(TAG, "ServerConnect: try")
            ser_socket= ser_serversoc?.accept()
            Log.d(TAG, "ServerConnect: finish")
        }catch (_:Exception){}
        Log.d(TAG, "ServerConnect: return")
    }

    suspend fun ClientConnect(ipaddr:String)= withContext(Dispatchers.IO){
        Log.d(TAG, "ClientConnect: Start")
        try {
            if (cli_socket==null){
                cli_socket= Socket(ipaddr, PORT)
                Log.d(TAG, "ClientConnect: finish")
            }
        }catch (_:Exception){}
        Log.d(TAG, "ClientConnect: return")
    }

    //テスト1
    suspend fun ServerSend(ser_mes:List<List<ByteArray>>)= withContext(Dispatchers.IO){
        Log.d(TAG, "ServerSend: Start")
        try {
            Log.d(TAG, "ServerSend: try")
            val first_size = ser_mes.size
            ser_Dos= DataOutputStream(BufferedOutputStream(ser_socket?.getOutputStream()))
            Log.d(TAG, "ServerSend: send frst list size $first_size")
            ser_Dos?.writeInt(first_size)
            ser_Dos?.flush()
            //クライアントからの応答を受け取る
            ServerReceiveNotice()
            if(ser_res_flag==first_size){
                //フラグの初期化
                ser_res_flag=null
                for (mes in ser_mes){
                    ser_Dos?.writeInt(mes.size)
                    ser_Dos?.flush()
                    ServerReceiveNotice()
                    if(ser_res_flag==mes.size){
                        //フラグの初期化
                        ser_res_flag=null
                        for (message in mes){
                            ser_Dos?.writeInt(message.size)
                            ser_Dos?.flush()
                            ServerReceiveNotice()
                            if(ser_res_flag==message.size){
                                Log.d(TAG, "ServerSend: 最小リストのサイズ ${message.size}")
                                ser_Dos?.write(message,0,message.size)
                                ser_Dos?.flush()
                                ServerReceiveNotice()
                                if (ser_res_flag!=message.size){
                                    Log.d(TAG, "ServerSend: miss send")
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }catch (_:Exception){}
        Log.d(TAG, "ServerSend: return")
    }

    suspend fun ServerReceiveNotice()= withContext(Dispatchers.IO){
        Log.d(TAG, "ServerNotice: Start")
        try {
            Log.d(TAG, "ServerNotice: try")
            ser_Dis= DataInputStream(BufferedInputStream(ser_socket?.getInputStream()))
            ser_res_flag=ser_Dis?.readInt()
            Log.d(TAG, "ServerNotice: $ser_res_flag")
        }catch (_:Exception){}
        Log.d(TAG, "ServerNotice: return")
    }


    suspend fun ClientReceiveFlag()= withContext(Dispatchers.IO){
        Log.d(TAG, "ClientReceiveFlag: Start")
        try {
            Log.d(TAG, "ClientReceiveFlag: try")
            cli_Dis= DataInputStream(BufferedInputStream(cli_socket?.getInputStream()))
            cli_res_flag=cli_Dis?.readInt()
            Log.d(TAG, "ClientReceiveFlag: $cli_res_flag")
        }catch(_:Exception){}
        Log.d(TAG, "ClientReceiveFlag: return")
    }

    suspend fun ClientSendFlag(flag:Int)= withContext(Dispatchers.IO){
        Log.d(TAG, "ClientSendFlag: Start")
        try {
//            sleep(1000)
            Log.d(TAG, "ClientSendFlag: try")
            cli_Dos= DataOutputStream(BufferedOutputStream(cli_socket?.getOutputStream()))
            cli_Dos?.writeInt(flag)
            cli_Dos?.flush()
        }catch (_:Exception){}
        Log.d(TAG, "ClientSendFlag: return")
    }



    suspend fun ClientReceive(res_size:Int)= withContext(Dispatchers.IO){
        Log.d(TAG, "ClientReceive: Start")
        try {
            Log.d(TAG, "ClientReceive: try")
            cli_res_mes=ByteArray(res_size)
            cli_Dis= DataInputStream(BufferedInputStream(cli_socket?.getInputStream()))
            cli_Dis?.read(cli_res_mes,0,res_size)//nullになってる?
            Log.d(TAG, "ClientReceive: message ${cli_res_mes?.let { String(it) }}")
        }catch (_:Exception){}
        Log.d(TAG, "ClientReceive: return")
    }


    //
    
    fun ServerDisConnect(){
        Log.d(TAG, "ServerDisConnect: Start")
        ser_serversoc?.close()
        ser_socket?.close()
        ser_Dis?.close()
        ser_Dos?.close()
        cli_socket?.close()
        cli_Dis?.close()
        cli_Dos?.close()
        Log.d(TAG, "ServerDisConnect: return")
    }
}