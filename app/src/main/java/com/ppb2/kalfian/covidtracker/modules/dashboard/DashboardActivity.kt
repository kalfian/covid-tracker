package com.ppb2.kalfian.covidtracker.modules.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.ppb2.kalfian.covidtracker.R

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        Log.d("DEBUG_AUTH_LOGIN","Start Here")
        if (auth.currentUser != null) {
            Log.d("DEBUG_AUTH_LOGIN", auth.currentUser!!.uid)
        }
    }
}