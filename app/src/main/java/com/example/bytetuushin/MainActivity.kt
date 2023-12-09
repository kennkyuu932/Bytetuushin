package com.example.bytetuushin

import android.content.Intent
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.bytetuushin.databinding.ActivityMainBinding
import java.net.Inet4Address

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
//        binding.sampleText.text = stringFromJNI()

        val ipedit=findViewById<EditText>(R.id.ipaddr)
        val ser=findViewById<Button>(R.id.ser)
        val cli=findViewById<Button>(R.id.cli)
        val ip=findViewById<TextView>(R.id.ip)

        val connectivityManager = getSystemService(ConnectivityManager::class.java)

        val networkCallback = object : ConnectivityManager.NetworkCallback(){
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)
                ip.text=linkProperties.linkAddresses.filter {
                    it.address is Inet4Address
                }[0].toString()
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        ser.setOnClickListener {
            val intent= Intent(this,ServerActivity::class.java)
            startActivity(intent)
        }

        cli.setOnClickListener {
            val intent = Intent(this,ClientActivity::class.java)
            intent.putExtra(intent_cli,ipedit.text.toString())
            startActivity(intent)
        }

    }

    /**
     * A native method that is implemented by the 'bytetuushin' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'bytetuushin' library on application startup.
        init {
            System.loadLibrary("bytetuushin")
        }
        val intent_cli="IPADDRESS"
    }
}