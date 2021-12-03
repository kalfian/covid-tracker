package com.ppb2.kalfian.covidtracker.modules.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ppb2.kalfian.covidtracker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        b.progressBar.visibility = View.INVISIBLE
        val v = b.root
        setContentView(v)
    }
}