package com.ppb2.kalfian.covidtracker.modules.dashboard

import android.Manifest
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.ACTION_FOUND
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ppb2.kalfian.covidtracker.R
import com.ppb2.kalfian.covidtracker.adapters.CheckInHistoryAdapter
import com.ppb2.kalfian.covidtracker.adapters.TestCovidAdapter
import com.ppb2.kalfian.covidtracker.adapters.VaccineCertAdapter
import com.ppb2.kalfian.covidtracker.databinding.ActivityDashboardBinding
import com.ppb2.kalfian.covidtracker.models.CheckInHistory
import com.ppb2.kalfian.covidtracker.models.TestCovid
import com.ppb2.kalfian.covidtracker.models.User
import com.ppb2.kalfian.covidtracker.models.VaccineCert
import com.ppb2.kalfian.covidtracker.modules.history.CheckInHistoryActivity
import com.ppb2.kalfian.covidtracker.modules.setting.SettingActivity
import com.ppb2.kalfian.covidtracker.utils.*
import com.ppb2.kalfian.covidtracker.utils.trackService.TrackService
import pub.devrel.easypermissions.EasyPermissions


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
    private var nearby = 0

    override fun onResume() {
        super.onResume()
        checkNearby()
    }

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
            checkNearby()
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

                    var isCovid = false
                    listTestCovid.forEach {
                        if (it.positive) {
                            isCovid = true
                        }
                    }

                    if (isCovid) {
                        Log.d("DEBUGG", "Service Started")
                        TrackService.startService(this@DashboardActivity)
                    } else {
                        Log.d("DEBUGG", "Service Stopped")
                        TrackService.stopService(this@DashboardActivity)
                    }

                    b.listTestCovid.visibility = View.VISIBLE
                    b.emptyTestCovid.visibility = View.GONE
                } else {
                    Log.d("DEBUGG", "Service Stopped")
                    TrackService.stopService(this@DashboardActivity)
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

    private val mBroadcastReceiver3: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d("LOG_NEARBY", "onReceive: " + action)
            if (action == ACTION_FOUND) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                nearby += 1
                Log.d("LOG_NEARBY","${device}")
                setSubstitleBluetooth(nearby)

            }
        }
    }

    private fun setSubstitleBluetooth(total: Int) {
        b.nearbyScanSubtitle.text = "Ada sekitar ${total} orang di sekitarmu"
    }

    private fun checkNearby() {
        this.nearby = 0;
        val sharedPref = this.getSharedPreferences(Constant.PREF_CONF_NAME, Constant.PREF_CONF_MODE)
        val isActivateNearby = sharedPref.getBoolean(Constant.NEARBY_IS_ACTIVE, false)
        if(isActivateNearby) {
            b.nearbyScan.visibility = View.VISIBLE

            val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter
            if (bluetoothAdapter.isEnabled) {

                checkBtPermission()

                var filter = IntentFilter()
                filter.addAction(ACTION_FOUND)
                this.registerReceiver(mBroadcastReceiver3, filter)

                if (bluetoothAdapter.isDiscovering) {
                    bluetoothAdapter.cancelDiscovery()
                }

                Log.d("LOG_NEARBY", bluetoothAdapter.startDiscovery().toString())

                b.nearbyScan.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                b.nearbyScanTitle.text = "Info terbaru nih"
                setSubstitleBluetooth(this.nearby)
                return
            }


            b.nearbyScan.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            b.nearbyScanTitle.text = "Maaf Fitur Nearby Terhenti"
            b.nearbyScanSubtitle.text = "Aktifkan bluetooth untuk menggunakan layanan ini"
            b.nearbyScan.setOnClickListener {
                startActivity(Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
            }

            return
        }

        b.nearbyScan.visibility = View.GONE



    }

    private fun checkBtPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if (!EasyPermissions.hasPermissions(
                    this,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            ) {
                EasyPermissions.requestPermissions(
                    this, "Need Permission to access bluetooth", Constant.NEARBY_PEOPLE_PERMISSION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
                return
            }
        } else {
            if (!EasyPermissions.hasPermissions(
                    this,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            ) {
                EasyPermissions.requestPermissions(
                    this, "Need Permission to access bluetooth", Constant.NEARBY_PEOPLE_PERMISSION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
                return
            }
        }
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
            DB.getPlaceById(db, result.contents.toString()) {
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
        val intent = Intent(this, VaccineDetailActivity::class.java)
        intent.putExtra(Constant.VACCINE_LINK, data.image)
        intent.putExtra(Constant.VACCINE_TITLE, data.title)
        startActivity(intent)
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