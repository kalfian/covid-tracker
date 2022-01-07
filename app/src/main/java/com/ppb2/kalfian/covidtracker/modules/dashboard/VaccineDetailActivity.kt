package com.ppb2.kalfian.covidtracker.modules.dashboard

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ppb2.kalfian.covidtracker.databinding.ActivityVaccineDetailBinding
import com.ppb2.kalfian.covidtracker.utils.Constant
import com.ppb2.kalfian.covidtracker.utils.ImageDownloader
import com.squareup.picasso.Picasso

class VaccineDetailActivity : AppCompatActivity() {

    private lateinit var b: ActivityVaccineDetailBinding
    private var vaccineImage: String = ""
    private var vaccineText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityVaccineDetailBinding.inflate(layoutInflater)

        val v = b.root
        setContentView(v)

        vaccineImage = intent.getStringExtra(Constant.VACCINE_LINK).toString()
        vaccineText = intent.getStringExtra(Constant.VACCINE_TITLE).toString()
        if (vaccineImage.isEmpty() or vaccineText.isEmpty()) {
            finish()
        }

        b.titleNav.text = vaccineText

        b.backButton.setOnClickListener {
            finish()
        }

        b.btnDownload.setOnClickListener {
            val imageDownloader = ImageDownloader(this, vaccineImage)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                imageDownloader.askPermissions()
            } else {
                imageDownloader.download()
            }
        }

        Picasso.get()
            .load(vaccineImage)
            .into(b.vaccineImage)

    }
}