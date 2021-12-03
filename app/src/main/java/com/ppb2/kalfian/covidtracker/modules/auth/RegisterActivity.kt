package com.ppb2.kalfian.covidtracker.modules.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ppb2.kalfian.covidtracker.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        val v = b.root
        setContentView(v)
    }
}