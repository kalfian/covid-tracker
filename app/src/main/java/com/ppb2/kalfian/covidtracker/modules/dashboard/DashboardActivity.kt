package com.ppb2.kalfian.covidtracker.modules.dashboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ppb2.kalfian.covidtracker.adapters.VaccineCertAdapter
import com.ppb2.kalfian.covidtracker.databinding.ActivityDashboardBinding
import com.ppb2.kalfian.covidtracker.models.User
import com.ppb2.kalfian.covidtracker.models.VaccineCert
import com.ppb2.kalfian.covidtracker.utils.isAuthorize


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

        isAuthorize(auth)

        this.userUID = auth.currentUser!!.uid

        listenVaccineCert()
        listenUser()

        b.swipeRefreshHome.setOnRefreshListener {
            listenUser()
            b.swipeRefreshHome.isRefreshing = false
        }
    }

    private fun setupVaccineCert() {
        b.listVaccineCert.layoutManager = layoutManager
        adapter = VaccineCertAdapter(this)
        b.listVaccineCert.adapter = adapter
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
                    adapter.clear()
                    adapter.addList(listVaccineCert)
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

    override fun onItemClickListener(data: VaccineCert) {
        auth.signOut()
    }
}