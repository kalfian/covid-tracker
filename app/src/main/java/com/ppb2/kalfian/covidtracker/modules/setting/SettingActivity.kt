package com.ppb2.kalfian.covidtracker.modules.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.awesomedialog.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ActivitySettingBinding
import com.ppb2.kalfian.covidtracker.models.User
import com.ppb2.kalfian.covidtracker.modules.auth.LoginActivity
import com.ppb2.kalfian.covidtracker.modules.dashboard.DashboardActivity
import com.ppb2.kalfian.covidtracker.utils.fallservice.FallObject
import com.ppb2.kalfian.covidtracker.utils.fallservice.FallService
import com.ppb2.kalfian.covidtracker.utils.fallservice.OnSensorChanged
import com.ppb2.kalfian.covidtracker.utils.isAuthorize

class SettingActivity : AppCompatActivity() {

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

        listenUser()

        b.nav.titleNav.text = "Pengaturan"
        b.nav.backButton.setOnClickListener {
            finish()
        }

        b.btnEditProfile.setOnClickListener {

        }

        b.btnNotif.setOnClickListener{
            val intent = Intent(applicationContext, NotificationActivity::class.java)
            startActivity(intent)
        }

        b.btnLogout.setOnClickListener {
            AwesomeDialog.build(this)
                .title("Keluar")
                .body("Apakah anda yakin ingin keluar?")
                .onPositive(
                    "Ok",
                    R.drawable.rounded_blue,
                    ContextCompat.getColor(this, android.R.color.white)
                ) {
                    auth.signOut()
                    FallService.stopService(applicationContext)
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }.onNegative("Batal", R.drawable.rounded_border_blue, ContextCompat.getColor(this, R.color.blue)) {

                }
        }
    }

    private fun listenUser() {
        val proc = db.child("Users").child(this.userUID)
        proc.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                b.name.text = "${user?.name}"
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}