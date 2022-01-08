package com.ppb2.kalfian.covidtracker.modules.setting

import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ActivityEmergencyBinding
import com.ppb2.kalfian.covidtracker.models.EmergencyModel
import com.ppb2.kalfian.covidtracker.modules.dashboard.DashboardActivity
import com.ppb2.kalfian.covidtracker.utils.DB
import com.ppb2.kalfian.covidtracker.utils.isAuthorize

private var locationManager : LocationManager? = null


class EmergencyActivity : AppCompatActivity() {

    private lateinit var b: ActivityEmergencyBinding
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var media: MediaPlayer
    private var userUID = ""
    private lateinit var countDownTimer: CountDownTimer

    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityEmergencyBinding.inflate(layoutInflater)

        db = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        this.userUID = isAuthorize(auth)
        b.pulsator.start()

        val v = b.root
        setContentView(v)

        media = MediaPlayer.create(applicationContext, R.raw.alarm)
        media.isLooping = true
        media.start()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val current = millisUntilFinished / 1000

                b.counterView.text = "Mengirim sinyal dalam $current detik"
            }

            override fun onFinish() {
                b.counterView.text = "Mengirim sinyal emergency"
                DB.getUserById(db, userUID) {
                    if (it != null) {
                        val user = it
                        try {

                            locationListener = LocationListener { location ->
                                val emergency = EmergencyModel()
                                emergency.lat = location.latitude
                                emergency.long = location.longitude
                                emergency.user_id = user.uid
                                emergency.user_name = user.name

                                DB.insertEmergency(db, emergency) { error, msg ->
                                    if (error) {
                                        b.counterView.text = msg
                                    }

                                    b.counterView.text = "Sinyal Emergency terkirim"
                                    locationManager?.removeUpdates(locationListener)
                                }
                            }

                            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                            return@getUserById
                        } catch(ex: SecurityException) {
                            Log.d("ErrorSendLocation", "Security Exception, no location available")
                        }
                    }
                }
            }
        }

        countDownTimer.start()

        b.stopEmergency.setOnClickListener {
            media.stop()
            countDownTimer.cancel()
            b.counterView.text = "Mematikan sinyal emergency"
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        countDownTimer.cancel()
        media.stop()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}