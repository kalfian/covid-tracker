package com.ppb2.kalfian.covidtracker.modules.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ActivityRegisterBinding
import com.ppb2.kalfian.covidtracker.models.User
import com.ppb2.kalfian.covidtracker.modules.dashboard.DashboardActivity
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var mails: MutableList<String> = ArrayList()
    private var phone: MutableList<String> = ArrayList()
    private var nik: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        b.progressBar.visibility = View.INVISIBLE

        db = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        listenEmail()
        listenPhone()
        listenNik()

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

            val user = getUserData()
            insertUser(user) {
                if (!it) {
                    MotionToast.createColorToast(this,"Register Gagal!",
                        "Terjadi kesalahan saat melakukan registrasi",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.inter_regular)
                    )
                    return@insertUser
                }

                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        }
    }

    private fun insertUser(user: User, callback: (isSuccess: Boolean) -> Unit) {

        auth.createUserWithEmailAndPassword(user.email, user.password).addOnSuccessListener {

            user.uid = it.user?.uid ?: "KOSONG"
            if (user.uid == "") {
                callback(false)
                return@addOnSuccessListener
            }

            // Insert User
            db.child("Users").child(user.uid).setValue(user)

            // Index Email
            db.child("ListedEmail").push().setValue(user.email)

            // Index Phone Number
            db.child("ListedPhoneNumber").push().setValue(user.phoneNumber)

            // Index NIK
            db.child("ListedNik").push().setValue(user.nik)



            callback(true)
            return@addOnSuccessListener

        }.addOnFailureListener {
            callback(false)
            return@addOnFailureListener
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

        if (b.passwordEdit.text.toString().length < 6) {
            b.passwordValidateText.text = "password minimal 6 digit"
            isValidated = false
        }

        if (b.passwordConfirmationEdit.text.toString() == "") {
            b.passwordConfirmationValidateText.text = "Silahkan isi Konfirmasi Password terlebih dahulu"
            isValidated = false
        }

        if (b.passwordEdit.text.toString() != b.passwordConfirmationEdit.text.toString()) {
            b.passwordConfirmationValidateText.text = "Password tidak sama"
            isValidated = false
        }

        if (!uniqueEmail(b.emailEdit.text.toString())) {
            b.emailValidateText.text = "Email Telah digunakan, coba gunakan email lain"
            isValidated = false
        }

        if (!uniquePhone(b.phoneEdit.text.toString())) {
            b.phoneValidateText.text = "Nomor telepon Telah digunakan, coba gunakan nomor telepon lain"
            isValidated = false
        }

        if (!uniqueNik(b.nikEdit.text.toString())) {
            b.nikValidateText.text = "NIK Telah digunakan, coba gunakan NIK lain"
            isValidated = false
        }

        return isValidated
    }

    private fun getUserData(): User {
        var user = User()

        user.nik = b.nikEdit.text.toString()
        user.name = b.nameEdit.text.toString()
        user.phoneNumber = b.phoneEdit.text.toString()

        var gender = 0
        if (b.femaleRadio.isSelected) {
            gender = 1
        }

        user.gender = gender
        user.email = b.emailEdit.text.toString()
        user.password = b.passwordEdit.text.toString()

        return user
    }

    private fun uniqueEmail(email: String): Boolean {
        if (mails.contains(email)) {
            return false
        }
        return true
    }

    private fun uniquePhone(phoneInput: String): Boolean {
        if (phone.contains(phoneInput)) {
            return false
        }
        return true
    }

    private fun uniqueNik(nikInput: String): Boolean {
        if (nik.contains(nikInput)) {
            return false
        }
        return true
    }

    private fun listenEmail() {
        val proc = db.child("ListedEmail")

        proc.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("DB_CHANGED", "DATABASE CHANGED! VALUE LISTENER")
                mails.clear()
                snapshot.children.forEach {
                    mails.add(it.value.toString())
                }

                Log.d("DB_CHANGED", mails.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun listenPhone() {
        val proc = db.child("ListedPhoneNumber")

        proc.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                phone.clear()
                snapshot.children.forEach {
                    phone.add(it.value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun listenNik() {
        val proc = db.child("ListedNik")

        proc.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                nik.clear()
                snapshot.children.forEach {
                    nik.add(it.value.toString())
                }

                Log.d("DEBUG_DB", mails.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
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