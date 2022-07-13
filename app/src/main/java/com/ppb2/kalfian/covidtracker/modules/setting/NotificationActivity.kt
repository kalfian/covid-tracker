package com.ppb2.kalfian.covidtracker.modules.setting

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ppb2.kalfian.covidtracker.databinding.ActivityNotificationBinding
import com.ppb2.kalfian.covidtracker.utils.Constant
import com.ppb2.kalfian.covidtracker.utils.fallservice.FallObject
import com.ppb2.kalfian.covidtracker.utils.fallservice.FallService
import com.ppb2.kalfian.covidtracker.utils.fallservice.OnSensorChanged
import com.ppb2.kalfian.covidtracker.utils.isAuthorize
import com.ppb2.kalfian.covidtracker.utils.nearbyService.NearbyService
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class NotificationActivity : AppCompatActivity(), OnSensorChanged {

    private lateinit var b: ActivityNotificationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    private lateinit var sharedPref : SharedPreferences

    private var userUID = ""
    private var isActivateAlarm = false
    private var isActivateNearby = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityNotificationBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        sharedPref = this.getSharedPreferences(Constant.PREF_CONF_NAME, Constant.PREF_CONF_MODE)
        isActivateAlarm = sharedPref.getBoolean(Constant.PREF_ALERT_ACTIVATE, false)
        isActivateNearby = sharedPref.getBoolean(Constant.NEARBY_IS_ACTIVE, false)

        if(isActivateNearby) {
            b.switchNearby.isChecked = true
        }

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

        b.switchNearby.setOnClickListener {

            if(
                EasyPermissions.hasPermissions(this,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            ) {
                isActivateNearby = !isActivateNearby
                val editor = sharedPref.edit()
                editor.putBoolean(Constant.NEARBY_IS_ACTIVE, isActivateNearby)
                editor.apply()

            } else {
                EasyPermissions.requestPermissions(this, "Need Permission to get nearby people", Constant.NEARBY_PEOPLE_PERMISSION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }

        b.switchEmergency.setOnClickListener {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                isActivateAlarm = !isActivateAlarm
                val editor = sharedPref.edit()
                editor.putBoolean(Constant.PREF_ALERT_ACTIVATE, isActivateAlarm)
                editor.apply()

                if (isActivateAlarm) {
                    FallService.startService(applicationContext, this)
                } else {
                    FallService.stopService(applicationContext)
                }
            } else {
                EasyPermissions.requestPermissions(
                    this, "Need Permission to access location this file", Constant.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)

    override fun onFall(fallObject: FallObject) {
        val intent = Intent(this, EmergencyActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}