package com.ppb2.kalfian.covidtracker.modules.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ppb2.kalfian.covidtracker.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        b.progressBar.visibility = View.INVISIBLE

        val v = b.root
        setContentView(v)

        b.backButton.setOnClickListener {
            finish()
        }
    }
}