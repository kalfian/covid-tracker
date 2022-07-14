package com.ppb2.kalfian.covidtracker.utils.trackService

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.models.PostLocationResponse
import com.ppb2.kalfian.covidtracker.utils.RetrofitClient
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackService: Service(), LocationListener {

    companion object {
        fun startService(context: Context) {
            context.startService(Intent(context, TrackService::class.java))
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, TrackService::class.java))
        }
    }

    private val CHANNEL_ID = "ChannelTrackService"

    private lateinit var locationManager: LocationManager
    var mSensor: Location? = null
    private var userUID = ""


    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val INTERVAL = 1000.toLong() // In milliseconds
        val DISTANCE = 10.toFloat() // In meters
        val auth = FirebaseAuth.getInstance()
        userUID = auth.currentUser!!.uid

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager



        if (!EasyPermissions.hasPermissions(this,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            return START_NOT_STICKY
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 5F, this)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5F, this)



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

    override fun onLocationChanged(location: Location) {


        val params = HashMap<String, Any>()
        params["id_user"] = userUID
        params["lat"] = location.latitude
        params["long"] = location.longitude
        params["is_checkin"] = 0

        Log.d("DEBUGGf", params.toString())

        RetrofitClient.instance.postUserJourney(params).enqueue(object:
            Callback<PostLocationResponse> {
            override fun onResponse(
                call: Call<PostLocationResponse>,
                response: Response<PostLocationResponse>
            ) {

               Log.d("DEBUGG", "SUCCESS POST")
            }

            override fun onFailure(call: Call<PostLocationResponse>, t: Throwable) {
                Log.d("DEBUGG", "ERROR POST")
            }
        })
    }
}
