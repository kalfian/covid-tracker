package com.ppb2.kalfian.covidtracker

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.ppb2.kalfian.covidtracker.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    private val SPLASH_TIME: Long = 1000
    private lateinit var b: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySplashScreenBinding.inflate(layoutInflater)
        val v = b.root
        setContentView(v)
        
        b.rotateloading.start()

        Handler(Looper.getMainLooper()).postDelayed({
        // TODO: Create logic to handle auto login
        }, SPLASH_TIME)
    }
}