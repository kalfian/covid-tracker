package com.ppb2.kalfian.covidtracker.modules.setting

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ppb2.kalfian.covidtracker.databinding.ActivityNotificationBinding
import com.ppb2.kalfian.covidtracker.utils.Constant
import com.ppb2.kalfian.covidtracker.utils.fallservice.FallObject
import com.ppb2.kalfian.covidtracker.utils.fallservice.FallService
import com.ppb2.kalfian.covidtracker.utils.fallservice.OnSensorChanged
import com.ppb2.kalfian.covidtracker.utils.isAuthorize

class NotificationActivity : AppCompatActivity(), OnSensorChanged {

    private lateinit var b: ActivityNotificationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    private lateinit var sharedPref : SharedPreferences

    private var userUID = ""
    private var isActivateAlarm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityNotificationBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        sharedPref = this.getSharedPreferences(Constant.PREF_CONF_NAME, Constant.PREF_CONF_MODE)
        isActivateAlarm = sharedPref.getBoolean(Constant.PREF_ALERT_ACTIVATE, false)
        if (isActivateAlarm) {
            b.switchEmergency.isChecked = true
        }

        val v = b.root

        setContentView(v)

        this.userUID = isAuthorize(auth)


        b.nav.titleNav.text = "Notifikasi"
        b.nav.backButton.setOnClickListener {
            finish()
        }

        b.switchEmergency.setOnClickListener {
            isActivateAlarm = !isActivateAlarm
            val editor:SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean(Constant.PREF_ALERT_ACTIVATE, isActivateAlarm)
            editor.apply()

            if (isActivateAlarm) {
                FallService.startService(applicationContext, this)
            } else {
                FallService.stopService(applicationContext)
            }

        }
    }

    override fun onFall(fallObject: FallObject) {
        val intent = Intent(this, EmergencyActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}