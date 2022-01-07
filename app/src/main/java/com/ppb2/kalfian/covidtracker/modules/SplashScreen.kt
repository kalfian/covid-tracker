package com.ppb2.kalfian.covidtracker.modules

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.ppb2.kalfian.covidtracker.databinding.ActivitySplashScreenBinding
import com.ppb2.kalfian.covidtracker.modules.auth.LoginActivity
import com.ppb2.kalfian.covidtracker.modules.dashboard.DashboardActivity
import com.ppb2.kalfian.covidtracker.utils.Constant
import pub.devrel.easypermissions.EasyPermissions

class SplashScreen : AppCompatActivity() {
    private val SPLASH_TIME: Long = 1000
    private lateinit var b: ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySplashScreenBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        val v = b.root
        setContentView(v)

        b.rotateloading.start()

        Handler(Looper.getMainLooper()).postDelayed({
            if(auth.currentUser != null) {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                return@postDelayed
            }

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }, SPLASH_TIME)
    }
}