package com.ppb2.kalfian.covidtracker.modules.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.databinding.ActivityLoginBinding
import com.ppb2.kalfian.covidtracker.models.User
import com.ppb2.kalfian.covidtracker.modules.dashboard.DashboardActivity
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class LoginActivity : AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    private var email = "";
    private var password = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        b.progressBar.visibility = View.INVISIBLE
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        val v = b.root


        setContentView(v)

        b.loginBtn.setOnClickListener {
            b.progressBar.visibility = View.VISIBLE
            email = b.emailEdit.text.toString()
            password = b.passwordEdit.text.toString()

            signIn(email, password) {
                if (it == null) {
                    MotionToast.createColorToast(this,"Login Gagal!",
                        "Kredensial Salah !",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    )
                } else {
                    val intent = Intent(this, DashboardActivity::class.java)
                    MotionToast.createColorToast(this,"Login Berhasil!",
                        "Selamat Datang ${it?.email} !",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    )
                    startActivity(intent)
                    finish()
                }

            }
        }

        b.registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String, callback: (user: User?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FIREBASE", "signInWithEmail:success")
                    val user = auth.currentUser
                    val uid = if (user?.uid != null) user.uid else ""


                    // TODO: Add logic to get user profile
                    db.child("Users").child(uid).get().addOnSuccessListener {
                        val user = it.getValue(User::class.java)
                        callback(user)
                    }

                } else {
                    callback(null)
                }
            }
    }
}