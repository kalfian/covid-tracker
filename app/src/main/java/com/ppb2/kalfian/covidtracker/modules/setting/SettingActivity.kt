package com.ppb2.kalfian.covidtracker.modules.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ppb2.kalfian.covidtracker.databinding.ActivityCheckInHistoryBinding
import com.ppb2.kalfian.covidtracker.databinding.ActivitySettingBinding
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

        isAuthorize(auth)

        this.userUID = auth.currentUser!!.uid
    }
}