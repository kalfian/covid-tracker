package com.ppb2.kalfian.covidtracker.utils.nearbyService

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import java.util.*

class NearbyService: Service() {

    companion object {
        fun startService(context: Context) {
            Log.d("LOG_NEARBY", "Start Scanning Bluetooth")
            context.startService(Intent(context, NearbyService::class.java))
        }

        fun stopService(context: Context) {
            Log.d("LOG_NEARBY", "Stop Scanning Bluetooth")
            context.stopService(Intent(context, NearbyService::class.java))
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed(object : Runnable {
            override fun run() {


                handler.postDelayed(this, 1000)
            }
        }, 1000)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder?  = null


    private fun startForeground(){
        Log.d("LOG_NEARBY", "Nearby Service started")
    }
}
