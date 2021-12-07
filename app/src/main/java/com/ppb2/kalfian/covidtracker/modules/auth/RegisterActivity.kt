package com.ppb2.kalfian.covidtracker.modules.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ActivityRegisterBinding
import com.ppb2.kalfian.covidtracker.models.User
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var mails: MutableList<String> = ArrayList()
    private var phone: MutableList<String> = ArrayList()
    private var nik: MutableList<String> = ArrayList()

    var isSuccess = true

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

            var user = getUserData()
            if (!insertUser(user)) {
                MotionToast.createColorToast(this,"Register Gagal!",
                    "Terjadi kesalahan saat melakukan registrasi",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                )
                return@setOnClickListener
            }

        }
    }

    private fun insertUser(user: User): Boolean {

        auth.createUserWithEmailAndPassword(user.email, user.password).addOnSuccessListener {
            this.isSuccess = true

            Log.d("DEBUG_DB", it.toString())

            user.uid = it.user?.uid ?: "KOSONG"
            if (user.uid == "") {
                this.isSuccess = false
                Log.d("DEBUG_DB", "UID KOSONG")
                return@addOnSuccessListener
            }


            // Index Email
            db.child("ListedEmail").push().setValue(user.email)

            // Index Phone Number
            db.child("ListedPhoneNumber").push().setValue(user.phoneNumber)

            // Index NIK
            db.child("ListedNik").push().setValue(user.nik)

            // Insert User
            db.child("Users").child(user.uid).setValue(user)

        }.addOnFailureListener {
            Log.d("DEBUG_DB", it.message.toString())
            this.isSuccess = false
            Log.d("DEBUG_DB_STATUS", isSuccess.toString())
        }

        if (!this.isSuccess) {
            Log.d("DEBUG_DB", "RETURN FALSE")
            return false
        }

        return this.isSuccess
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

        if (!uniqueEmail(b.phoneEdit.text.toString())) {
            b.emailValidateText.text = "Nomor telepon Telah digunakan, coba gunakan nomor telepon lain"
            isValidated = false
        }

        if (!uniqueEmail(b.nikEdit.text.toString())) {
            b.emailValidateText.text = "NIK Telah digunakan, coba gunakan NIK lain"
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

        proc.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                mails.add(snapshot.value.toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        proc.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mails.clear()
                snapshot.children.forEach {
                    mails.add(it.value.toString())
                }

                Log.d("DEBUG_DB", mails.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun listenPhone() {
        val proc = db.child("ListedPhoneNumber")

        proc.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                phone.add(snapshot.value.toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

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

        proc.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                nik.add(snapshot.value.toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

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