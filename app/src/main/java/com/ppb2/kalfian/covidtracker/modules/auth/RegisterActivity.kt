package com.ppb2.kalfian.covidtracker.modules.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ActivityRegisterBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

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
            showLoader()
            if (!isValidated()) {
                hideLoader()
                return@setOnClickListener
            }



        }
    }

    private fun isValidated(): Boolean {
        resetValidation()
        var isValidated = true

        if (b.nikEdit.text.toString() == "") {
            b.nikValidateText.text = "Silahkan isi NIK terlebih dahulu"
            isValidated = false
        }

        if (b.nameEdit.text.toString() == "") {
            b.nameValidateText.text = "Silahkan isi Nama terlebih dahulu"
            isValidated = false
        }

        if (b.phoneEdit.text.toString() == "") {
            b.phoneValidateText.text = "Silahkan isi Nomor Telepon terlebih dahulu"
            isValidated = false
        }

        if (b.addressEdit.text.toString() == "") {
            b.addressValidateText.text = "Silahkan isi Alamat terlebih dahulu"
            isValidated = false
        }

        if (b.emailEdit.text.toString() == "") {
            b.emailValidateText.text = "Silahkan isi Email terlebih dahulu"
            isValidated = false
        }

        if (b.passwordEdit.text.toString() == "") {
            b.passwordValidateText.text = "Silahkan isi Password terlebih dahulu"
            isValidated = false
        }

        if (b.passwordConfirmationEdit.text.toString() == "") {
            b.passwordConfirmationValidateText.text = "Silahkan isi Konfirmasi Password terlebih dahulu"
            isValidated = false
        }

        return isValidated



    }

    private fun resetValidation() {
        hideLoader()
        b.nikValidateText.text = ""
        b.nameValidateText.text = ""
        b.phoneValidateText.text = ""
        b.addressValidateText.text = ""
        b.emailValidateText.text = ""
        b.passwordValidateText.text = ""
        b.passwordConfirmationValidateText.text = ""
    }

    private fun hideLoader() {
        b.progressBar.visibility = View.INVISIBLE
    }

    private fun showLoader() {
        b.progressBar.visibility = View.VISIBLE
    }
}