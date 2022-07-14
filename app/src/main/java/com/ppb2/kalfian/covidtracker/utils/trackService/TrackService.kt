package com.ppb2.kalfian.covidtracker.utils.trackService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ppb2.kalfian.covidtracker.R

class TrackService: Service(), LocationListener {

    companion object {
        fun startService(context: Context) {
            Log.d("LOG_NEARBY", "Start Scanning Bluetooth")
            context.startService(Intent(context, TrackService::class.java))
        }

        fun stopService(context: Context) {
            Log.d("LOG_NEARBY", "Stop Scanning Bluetooth")
            context.stopService(Intent(context, TrackService::class.java))
        }
    }

    private val CHANNEL_ID = "ChannelTrackService"

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        startForeground()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder?  = null


    private fun startForeground(){
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Emergency Service"
            val chan = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracker Started")
                .setContentText("Tracker service berjalan, perjalanan anda akan dipantau hingga dinyatakan negatif covid")
                .setSmallIcon(R.drawable.logo)
                .build()
        } else {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracker Started")
                .setContentText("Tracker service berjalan, perjalanan anda akan dipantau hingga dinyatakan negatif covid")
                .setSmallIcon(R.drawable.logo)
                .build()
        }
        try {
            startForeground(1, notification)
        } catch(e: Exception) {
            Log.d("ERROR_LOG", e.localizedMessage.toString())
        }

    }

    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }
}
