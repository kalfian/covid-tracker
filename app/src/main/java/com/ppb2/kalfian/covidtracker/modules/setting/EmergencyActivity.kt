package com.ppb2.kalfian.covidtracker.modules.setting

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ActivityEmergencyBinding
import com.ppb2.kalfian.covidtracker.modules.dashboard.DashboardActivity
import com.ppb2.kalfian.covidtracker.utils.isAuthorize


class EmergencyActivity : AppCompatActivity() {

    private lateinit var b: ActivityEmergencyBinding
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var media: MediaPlayer
    private var userUID = ""
    private lateinit var countDownTimer: CountDownTimer

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

        countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val current = millisUntilFinished / 1000

                b.counterView.text = "Mengirim sinyal dalam $current detik"
            }

            override fun onFinish() {
                b.counterView.text = "Mengirim sinyal emergency"
            }
        }

        countDownTimer.start()

        b.stopEmergency.setOnClickListener {
            media.stop()
            countDownTimer.cancel()
            b.counterView.text = "Membatalkan sinyal emergency"
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