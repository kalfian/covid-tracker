package com.ppb2.kalfian.covidtracker.modules.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ppb2.kalfian.covidtracker.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding

    private var email = "";
    private var password = "";
    private var password_confirmation = "";
    private var nik = "";
    private var phone_number = "";
    private var jk = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        b.progressBar.visibility = View.INVISIBLE

        val v = b.root
        setContentView(v)

        b.backButton.setOnClickListener {
            finish()
        }

        b.submitBtn.setOnClickListener {
            b.progressBar.visibility = View.VISIBLE
            b.nameEdit.setError("Error lur")
        }
    }

    private fun validateInput(): Boolean {
        return false
    }
}