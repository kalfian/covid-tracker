package com.ppb2.kalfian.covidtracker.utils

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import com.google.firebase.auth.FirebaseAuth
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.modules.auth.LoginActivity

fun Activity.isAuthorize(auth: FirebaseAuth): String {
    if (auth.currentUser == null) {
        AwesomeDialog.build(this)
        .title("Sesi habis")
        .body("Silahkan login kembali untuk memulai sesi baru")
        .onPositive(
            "Ok",
            R.drawable.rounded_blue,
            ContextCompat.getColor(this, android.R.color.white)
        ) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        return ""
    }

    return auth.currentUser!!.uid
}