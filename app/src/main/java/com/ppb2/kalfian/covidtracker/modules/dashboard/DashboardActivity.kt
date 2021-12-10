package com.ppb2.kalfian.covidtracker.modules.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ppb2.kalfian.covidtracker.adapters.VaccineCertAdapter
import com.ppb2.kalfian.covidtracker.databinding.ActivityDashboardBinding
import com.ppb2.kalfian.covidtracker.models.VaccineCert
import com.ppb2.kalfian.covidtracker.modules.auth.LoginActivity
import com.ppb2.kalfian.covidtracker.utils.customModal

class DashboardActivity : AppCompatActivity(), VaccineCertAdapter.AdapterVaccineCertOnClickListener {

    private lateinit var b: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference


    private lateinit var adapter: VaccineCertAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var userUID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDashboardBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        setupVaccineCert()

        val v = b.root
        setContentView(v)

        if (auth.currentUser == null) {
            customModal(applicationContext,
                "Apakah anda yakin ingin keluar?",
                "tekan tombol keluar untuk kembali ke halaman login", "Keluar", "Batal", true
            ) {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            return
        }

        this.userUID = auth.currentUser!!.uid

        listenVaccineCert()
        dummyList()

        b.swipeRefreshHome.setOnRefreshListener {
//            if (this.isShowed) {
//                b.test.visibility = View.GONE
//            } else {
//                b.test.visibility = View.VISIBLE
//            }
//
//            this.isShowed = !this.isShowed
//
            b.swipeRefreshHome.isRefreshing = false
        }
    }

    private fun setupVaccineCert() {
        b.listVaccineCert.layoutManager = layoutManager
        adapter = VaccineCertAdapter(this)
        b.listVaccineCert.adapter = adapter
    }

    private fun listenVaccineCert() {
        b.listVaccineCert.visibility = View.GONE
//        val proc = db.child("ListedEmail").child()

    }

    private fun dummyList() {
        b.listVaccineCert.visibility = View.VISIBLE
        b.emptyVaccineCert.visibility = View.GONE

        adapter.add(VaccineCert("1", "Vaksin Pertama", "https://asset.kompas.com/crops/DUy-Sev8-DjV1byu_JzKBKct16Y=/177x72:1066x665/780x390/data/photo/2021/07/05/60e250f122139.jpg", "1", 1))
        adapter.add(VaccineCert("1", "Vaksin Kedua", "https://asset.kompas.com/crops/DUy-Sev8-DjV1byu_JzKBKct16Y=/177x72:1066x665/780x390/data/photo/2021/07/05/60e250f122139.jpg", "1", 1))
    }

    override fun onItemClickListener(data: VaccineCert) {

    }
}