package com.ppb2.kalfian.covidtracker.modules.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.awesomedialog.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ActivitySettingBinding
import com.ppb2.kalfian.covidtracker.modules.auth.LoginActivity
import com.ppb2.kalfian.covidtracker.modules.dashboard.DashboardActivity
import com.ppb2.kalfian.covidtracker.utils.fallservice.FallObject
import com.ppb2.kalfian.covidtracker.utils.fallservice.FallService
import com.ppb2.kalfian.covidtracker.utils.fallservice.OnSensorChanged
import com.ppb2.kalfian.covidtracker.utils.isAuthorize

class SettingActivity : AppCompatActivity(), OnSensorChanged {

    private lateinit var b: ActivitySettingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    private var userUID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivitySettingBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        val v = b.root
        setContentView(v)

        this.userUID = isAuthorize(auth)


        b.nav.titleNav.text = "Pengaturan"
        b.nav.backButton.setOnClickListener {
            finish()
        }

        b.btnEditProfile.setOnClickListener {

            FallService.startService(applicationContext, this)


        }

        b.btnLogout.setOnClickListener {
            AwesomeDialog.build(this)
                .title("Sesi habis")
                .body("Silahkan login kembali untuk memulai sesi baru")
                .onPositive(
                    "Ok",
                    R.drawable.rounded_blue,
                    ContextCompat.getColor(this, android.R.color.white)
                ) {
                    auth.signOut()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }.onNegative("Batal", R.drawable.rounded_border_blue, ContextCompat.getColor(this, R.color.blue)) {

                }
        }
    }

    override fun onFall(fallObject: FallObject) {
        val intent = Intent(this, EmergencyActivity::class.java)
        startActivity(intent)
    }
}