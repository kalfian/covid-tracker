package com.ppb2.kalfian.covidtracker.modules.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.ppb2.kalfian.covidtracker.adapters.TestCovidAdapter
import com.ppb2.kalfian.covidtracker.adapters.VaccineCertAdapter
import com.ppb2.kalfian.covidtracker.databinding.ActivityDashboardBinding
import com.ppb2.kalfian.covidtracker.models.TestCovid
import com.ppb2.kalfian.covidtracker.models.User
import com.ppb2.kalfian.covidtracker.models.VaccineCert
import com.ppb2.kalfian.covidtracker.utils.isAuthorize
import java.security.Timestamp
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class DashboardActivity : AppCompatActivity(), VaccineCertAdapter.AdapterVaccineCertOnClickListener, TestCovidAdapter.AdapterTestCovidOnClickListener {

    private lateinit var b: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference


    private lateinit var vaccineAdapter: VaccineCertAdapter
    private lateinit var vaccinelayoutManager: LinearLayoutManager

    private lateinit var testCovidAdapter: TestCovidAdapter
    private lateinit var testCovidlayoutManager: LinearLayoutManager

    private var userUID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDashboardBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        setupVaccineCert()
        setupTestCovid()

        val v = b.root
        setContentView(v)

        isAuthorize(auth)

        this.userUID = auth.currentUser!!.uid

        listenVaccineCert()
        listenTestCovid()
        listenUser()

        b.swipeRefreshHome.setOnRefreshListener {
            listenUser()
            b.swipeRefreshHome.isRefreshing = false
        }
    }

    private fun setupVaccineCert() {
        vaccinelayoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        b.listVaccineCert.layoutManager = vaccinelayoutManager
        vaccineAdapter = VaccineCertAdapter(this)
        b.listVaccineCert.adapter = vaccineAdapter
    }

    private fun setupTestCovid() {
        testCovidlayoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        b.listTestCovid.layoutManager = testCovidlayoutManager
        testCovidAdapter = TestCovidAdapter(this)
        b.listTestCovid.adapter = testCovidAdapter
    }

    private fun listenUser() {
        val proc = db.child("Users").child(this.userUID)
        proc.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                b.displayName.text = "Hi, ${user?.name}"
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun listenVaccineCert() {
        b.listVaccineCert.visibility = View.GONE

        val proc = db.child("VaccineCert").child(this.userUID).orderByChild("status").equalTo(true)
        proc.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val listVaccineCert = arrayListOf<VaccineCert>()
                snapshot.children.forEach {
                    val vaccine = it.getValue(VaccineCert::class.java)
                    if (vaccine != null) {
                        listVaccineCert.add(vaccine!!)
                    }
                }
                if(listVaccineCert.size > 0 ){
                    vaccineAdapter.clear()
                    vaccineAdapter.addList(listVaccineCert)
                    b.listVaccineCert.visibility = View.VISIBLE
                    b.emptyVaccineCert.visibility = View.GONE
                } else {
                    b.listVaccineCert.visibility = View.GONE
                    b.emptyVaccineCert.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                b.listVaccineCert.visibility = View.GONE
                b.emptyVaccineCert.visibility = View.VISIBLE
                Toast.makeText(applicationContext, "Error when fetch Vaccine Certificate", Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun listenTestCovid() {
        b.listTestCovid.visibility = View.GONE

        val currentTimestamp =  System.currentTimeMillis() / 1000L
        Log.d("TIMESTAMP", currentTimestamp.toString())

        val proc = db.child("TestCovid").child(this.userUID).orderByChild("valid_date").startAt(currentTimestamp.toDouble())

        proc.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val listTestCovid = arrayListOf<TestCovid>()
                snapshot.children.forEach {
                    val tc = it.getValue(TestCovid::class.java)
                    if (tc != null) {
                        listTestCovid.add(tc!!)
                    }
                }

                Log.d("TIMESTAMP", listTestCovid.toString())

                if(listTestCovid.size > 0 ){
                    testCovidAdapter.clear()
                    testCovidAdapter.addList(listTestCovid)
                    b.listTestCovid.visibility = View.VISIBLE
                    b.emptyTestCovid.visibility = View.GONE
                } else {
                    b.listTestCovid.visibility = View.GONE
                    b.emptyTestCovid.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                b.listTestCovid.visibility = View.GONE
                b.emptyTestCovid.visibility = View.VISIBLE
                Toast.makeText(applicationContext, "Error when fetch Test Covid", Toast.LENGTH_LONG).show()
            }

        })

    }

    override fun onItemClickListener(data: VaccineCert) {
        
    }

    override fun onItemClickListener(data: TestCovid) {

    }
}