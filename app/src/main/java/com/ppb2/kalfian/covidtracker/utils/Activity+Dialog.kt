package com.ppb2.kalfian.covidtracker.utils

import android.app.Activity
import androidx.core.content.ContextCompat
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import com.ppb2.kalfian.covidtracker.R

fun Activity.fireDialog(title: String, body: String) {
    AwesomeDialog.build(this)
        .title(title)
        .body(body)
        .onPositive(
            "Ok",
            R.drawable.rounded_blue,
            ContextCompat.getColor(this, android.R.color.white)
        )
}