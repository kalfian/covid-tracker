package com.ppb2.kalfian.covidtracker.modules.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ppb2.kalfian.covidtracker.adapters.CheckInHistoryAdapter
import com.ppb2.kalfian.covidtracker.databinding.ActivityCheckInHistoryBinding
import com.ppb2.kalfian.covidtracker.databinding.ActivityDashboardBinding
import com.ppb2.kalfian.covidtracker.models.CheckInHistory
import com.ppb2.kalfian.covidtracker.utils.DB
import com.ppb2.kalfian.covidtracker.utils.fireDialog
import com.ppb2.kalfian.covidtracker.utils.isAuthorize

class CheckInHistoryActivity : AppCompatActivity(), CheckInHistoryAdapter.AdapterCheckInHistoryOnClickListener {

    private lateinit var b: ActivityCheckInHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    private lateinit var checkinHistoryAdapter: CheckInHistoryAdapter

    private var userUID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCheckInHistoryBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        setupCheckin()

        val v = b.root
        setContentView(v)

        this.userUID = isAuthorize(auth)

        listenCheckIn()

        b.nav.titleNav.text = "Riwayat Lengkap"
        b.nav.backButton.setOnClickListener {
            finish()
        }

        b.swipeRefresh.setOnRefreshListener {
            listenCheckIn()

            b.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupCheckin() {
        val lm = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        b.listCheckinHistory.layoutManager = lm
        checkinHistoryAdapter = CheckInHistoryAdapter(this)
        b.listCheckinHistory.adapter = checkinHistoryAdapter
    }

    private fun listenCheckIn() {
        val proc = db.child("UserCheckins").child(this.userUID).orderByChild("checkin_timestamp")

        proc.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listCheckIn = arrayListOf<CheckInHistory>()
                snapshot.children.forEach {
                    val tc = it.getValue(CheckInHistory::class.java)
                    if (tc != null) {
                        listCheckIn.add(tc)
                    }
                }

                checkinHistoryAdapter.clear()
                checkinHistoryAdapter.addList(listCheckIn.reversed())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error when fetch Masuk History", Toast.LENGTH_LONG).show()
            }

        })

    }

    override fun onBtnClickListener(data: CheckInHistory) {
        DB.checkOut(db, this.userUID, data) { error, message ->
            if(error) {
                this.fireDialog("Gagal Keluar", "Gagal keluar, coba beberapa saat lagi")
                Log.d("ERROR_LOG", message)
                return@checkOut
            }

            this.fireDialog("Berhasil Keluar", "Berhasil keluar dari ${data.place_name}")
        }
    }

    override fun onItemClickListener(data: CheckInHistory) {

    }
}