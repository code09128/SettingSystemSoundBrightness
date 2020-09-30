package com.example.setting

import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_wifi_scanner.*


class WifiScanner : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_scanner)

        wifi_enable.setOnClickListener(this)

        val wifiManager = getSystemService(WIFI_SERVICE) as WifiManager
    }

    override fun onClick(button: View?) {

        when(button){
            wifi_enable -> {

            }
            wifi_disable -> {

            }
        }
    }
}