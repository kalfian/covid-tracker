package com.ppb2.kalfian.covidtracker.utils.fallservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ppb2.kalfian.covidtracker.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class FallService: Service(), SensorEventListener {

    companion object {

        lateinit var onSensorChanged: OnSensorChanged


        fun startService(context: Context, onSensorChanged: OnSensorChanged) {
            this.onSensorChanged = onSensorChanged
            context.startService(Intent(context, FallService::class.java))
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, FallService::class.java))
        }
    }


    private lateinit var sensorManager: SensorManager
    var mSensor: Sensor? = null
    private var lastMovementFall: Long = 0
    private var movementStart: Long = 0
    private var ct = 2

    private val CHANNEL_ID = "ChannelFallService"
    private val CHANNEL_ID_NOTIFICATIONS = "ChannelFallServiceNotifications"

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (mSensor == null) {
            mSensor = if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            } else {
                null
            }
        }

        startForeground()

        mSensor?.also { light ->
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder?  = null

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            movementStart = System.currentTimeMillis()

            val loX = event.values[0]
            val loY = event.values[1]
            val loZ = event.values[2]

            val loAccelerationReader = sqrt(
                loX.toDouble().pow(2.0)
                        + loY.toDouble().pow(2.0)
                        + loZ.toDouble().pow(2.0)
            )

            val precision = DecimalFormat("0.00")
            val ldAccRound = java.lang.Double.parseDouble(precision.format(loAccelerationReader))

            // precision/fall detection and more than 1000ms after last fall
            if (ldAccRound > 0.3 && ldAccRound < 1.2 && (movementStart - lastMovementFall) > 1000) {

                val timeStamp = SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss").format(Date(System.currentTimeMillis()))
                val duration = (System.currentTimeMillis() - movementStart).toString()

                lastMovementFall = System.currentTimeMillis()

                val fallObject = FallObject(timeStamp, duration)

                onSensorChanged.onFall(fallObject)
            }
        }
    }


    private fun startForeground(){
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Emergency Service"
            val chan = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan);

            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Emergency Started")
                .setContentText("Emergency service berjalan, alarm akan berbunyi jika device terjatuh")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build()
        } else {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FallService")
                .setContentText("Foreground service is running")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build()
        }
        try {
            startForeground(1, notification)
        } catch(e: Exception) {
            Log.d("ERROR_LOG", e.localizedMessage.toString())
        }

    }
}