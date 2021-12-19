package com.ppb2.kalfian.covidtracker.modules.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ppb2.kalfian.covidtracker.adapters.CheckInHistoryAdapter
import com.ppb2.kalfian.covidtracker.adapters.TestCovidAdapter
import com.ppb2.kalfian.covidtracker.adapters.VaccineCertAdapter
import com.ppb2.kalfian.covidtracker.databinding.ActivityDashboardBinding
import com.ppb2.kalfian.covidtracker.models.*
import com.ppb2.kalfian.covidtracker.modules.history.CheckInHistoryActivity
import com.ppb2.kalfian.covidtracker.modules.setting.SettingActivity
import com.ppb2.kalfian.covidtracker.utils.DB
import com.ppb2.kalfian.covidtracker.utils.Validate
import com.ppb2.kalfian.covidtracker.utils.fireDialog
import com.ppb2.kalfian.covidtracker.utils.isAuthorize


class DashboardActivity : AppCompatActivity(), VaccineCertAdapter.AdapterVaccineCertOnClickListener, TestCovidAdapter.AdapterTestCovidOnClickListener, CheckInHistoryAdapter.AdapterCheckInHistoryOnClickListener {

    private lateinit var b: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference


    private lateinit var vaccineAdapter: VaccineCertAdapter
    private lateinit var testCovidAdapter: TestCovidAdapter
    private lateinit var checkinHistoryAdapter: CheckInHistoryAdapter

    private var listVaccineCert = arrayListOf<VaccineCert>()
    private var listTestCovid = arrayListOf<TestCovid>()

    private var userUID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDashboardBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        setupVaccineCert()
        setupTestCovid()
        setupCheckin()

        val v = b.root
        setContentView(v)

        this.userUID = isAuthorize(auth)

        listenVaccineCert()
        listenTestCovid()
        listenUser()
        listenCheckIn()

        b.swipeRefreshHome.setOnRefreshListener {
            listenVaccineCert()
            listenTestCovid()
            listenUser()
            listenCheckIn()
            b.swipeRefreshHome.isRefreshing = false
        }

        listenScanButton()

        b.btnRiwayatLengkap.setOnClickListener {
            val intent = Intent(this, CheckInHistoryActivity::class.java)
            startActivity(intent)
        }

        b.btnOption.setOnClickListener{
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupVaccineCert() {
        val lm = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        b.listVaccineCert.layoutManager = lm
        vaccineAdapter = VaccineCertAdapter(this)
        b.listVaccineCert.adapter = vaccineAdapter
    }

    private fun setupTestCovid() {
        val lm = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        b.listTestCovid.layoutManager = lm
        testCovidAdapter = TestCovidAdapter(this)
        b.listTestCovid.adapter = testCovidAdapter
    }

    private fun setupCheckin() {
        val lm = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        b.listCheckinHistory.layoutManager = lm
        checkinHistoryAdapter = CheckInHistoryAdapter(this)
        b.listCheckinHistory.adapter = checkinHistoryAdapter
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
                listVaccineCert.removeAll{ true }
                snapshot.children.forEach {
                    val vaccine = it.getValue(VaccineCert::class.java)
                    if (vaccine != null) {
                        listVaccineCert.add(vaccine)
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

        val proc = db.child("TestCovid").child(this.userUID).orderByChild("valid_date").startAt(currentTimestamp.toDouble())

        proc.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                listTestCovid.removeAll{ true }

                snapshot.children.forEach {
                    val tc = it.getValue(TestCovid::class.java)
                    if (tc != null) {
                        listTestCovid.add(tc)
                    }
                }

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

    private fun listenCheckIn() {
        val proc = db.child("UserCheckins").child(this.userUID).orderByChild("checkin_timestamp").limitToLast(10)

        proc.addValueEventListener(object: ValueEventListener{
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

    private fun listenScanButton() {
        b.scanBtn.setOnClickListener{
            val options = ScanOptions()
            options.setOrientationLocked(false)
            options.setBeepEnabled(true)

            barcodeLauncher.launch(options)
        }
    }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            DB.getPlaceById(db, result.contents.toString()) { it ->
                if(it == null) {
                    this.fireDialog("Gagal Masuk", "QR Code tidak terdaftar pada sistem kami, silahkan mencoba QR Code yang lain")
                    return@getPlaceById
                }

                val place = it

                if (place.need_vaccine == 1 && listVaccineCert.size < 1) {
                    this.fireDialog("Gagal Masuk", "Gagal Masuk di ${place.name}, karena belum vaksin minimal 1 kali")
                    return@getPlaceById
                }

                if(place.need_test != "") {
                    if (!Validate.hasTestCovid(listTestCovid, place.need_test)) {
                        this.fireDialog("Gagal Masuk", "Gagal Masuk di ${place.name}, karena belum memiliki test covid ${place.need_test}")
                        return@getPlaceById
                    }
                }

                DB.getPlaceTotal(db, place) { error, userCheckIn ->
                    if (error) {
                        this.fireDialog("Gagal Masuk", "Gagal Masuk di ${place.name}, silahkan coba beberapa saat lagi")
                        return@getPlaceTotal
                    }

                    Log.d("TESTER", "$userCheckIn ${this.userUID} ${Validate.isAlreadyCheckIn(userCheckIn, this.userUID)}")
                    if(Validate.isAlreadyCheckIn(userCheckIn, this.userUID)) {
                        this.fireDialog("Gagal Masuk", "Gagal Masuk di ${place.name} karena masih terdapat sesi")
                        return@getPlaceTotal
                    }

                    if (userCheckIn.size >= place.quota) {
                        this.fireDialog("Gagal Masuk", "Gagal Masuk di ${place.name} karena quota penuh")
                        return@getPlaceTotal
                    }

                    DB.checkIn(db, place, this.userUID ) { error, msg ->
                        if (error) {
                            Log.d("ERROR_LOG", msg)
                            this.fireDialog("Gagal Masuk", "Terjadi kesalah saat melakukan Masuk")
                            return@checkIn
                        }

                        this.fireDialog("Berhasil Masuk", "Berhasil Masuk di ${place.name}")
                    }
                }
            }
        }
    }


    override fun onItemClickListener(data: VaccineCert) {

    }

    override fun onItemClickListener(data: TestCovid) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data.link))
        startActivity(browserIntent)
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