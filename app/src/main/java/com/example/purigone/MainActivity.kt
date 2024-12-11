package com.example.purigone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.os.Build
import java.net.NetworkInterface
import java.util.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.purigone.Util.UI
import com.example.purigone.databinding.ActivityMainBinding
import com.example.purigone.model.UserModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var wifiManager: WifiManager

    private var userAccess: String = ""

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        database = FirebaseDatabase.getInstance("https://purigone-f5ba5-default-rtdb.asia-southeast1.firebasedatabase.app")

        // Initialize DrawerLayout and NavigationView
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        // Initialize WifiManager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Fetch and display user info
        fetchUserInfo()

        // Set up click listener for menu button
        binding.menuButton.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            } else {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        // Set up NavigationView item selection
        setupNavigationView()

        // Set up click listener for report button
        binding.reportBtn.setOnClickListener {
            if (checkAndRequestPermissions()) {
                displayWifiInfo()
            }
        }
    }

    private fun fetchUserInfo() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w("MainActivity", "No user is currently logged in")
            // You might want to redirect to login page here
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(UserModel::class.java)
                    user?.let {
                        userAccess = it.access
                    }
                } else {
                    Log.d("MainActivity", "No such document")
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting user document", exception)
                Toast.makeText(
                    this,
                    "Error fetching user data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_stain -> {
                    // Already on main page, no action needed
                }
                R.id.nav_cleaning -> {
                    if (userAccess == "Admin") {
                        startActivity(Intent(this, CleanActivity::class.java))
                        finish()
                    } else {
                        UI.showToast(this, "You have no access to view this page")
                    }
                }
                R.id.nav_access -> {
                    if (userAccess == "Admin") {
                        startActivity(Intent(this, AccessActivity::class.java))
                        finish()
                    } else {
                        UI.showToast(this, "You have no access to view this page")
                    }
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
            return false
        }

        return true
    }

    private fun displayWifiInfo() {
//        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_CODE)
//            return
//        }
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ssid = wifiInfo.ssid.removeSurrounding("\"")
        val signalLevel = wifiInfo.rssi

        //val macAddress = getMacAddress(wifiManager)

//        val wifiInfoText = """
//            MAC Address：$macAddress
//            ESSID(SSID)：$ssid
//            Signal level：$signalLevel/4
//        """.trimIndent()


        sendToFirebase(ssid,signalLevel)
        //Toast.makeText(this, "Report Success!", Toast.LENGTH_LONG).show()

    }

    private fun sendToFirebase(ssid: String, signalLevel: Int) {
        try {
            Log.d("Firebase", "Attempting to send data to Firebase")
            val wifiInfoRef = database.getReference("wifi_info")

            val currentTimestamp = System.currentTimeMillis()
            val formattedTimestamp = getFormattedTimestamp(currentTimestamp)

            // 直接使用已存在的 SSID 節點
            val ssidRef = wifiInfoRef.child(ssid)

            // 更新資訊
            ssidRef.child("signal_level").setValue(signalLevel.toString())
                .addOnSuccessListener {
                    ssidRef.child("timestamp").setValue(formattedTimestamp)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Data sent successfully")
                            Toast.makeText(this, "WiFi info sent successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error sending timestamp", e)
                            //Toast.makeText(this, "Failed to send timestamp: ${e.message}", Toast.LENGTH_SHORT).show()
                            Toast.makeText(this, "WiFi info sent successfully", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error sending data", e)
                    //Toast.makeText(this, "Failed to send WiFi info: ${e.message}", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "WiFi info sent successfully", Toast.LENGTH_SHORT).show()
                }

            Log.d("Firebase", "Data push initiated")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in sendToFirebase: ${e.message}", e)
            Toast.makeText(this, "Error sending data to Firebase: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    private fun getFormattedTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

//    private fun getMacAddress(wifiManager: WifiManager): String {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                val networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
//                for (networkInterface in networkInterfaces) {
//                    if (!networkInterface.name.equals("wlan0", ignoreCase = true)) continue
//                    val macBytes = networkInterface.hardwareAddress ?: return "02:00:00:00:00:00"
//                    val macStringBuilder = StringBuilder()
//                    for (byte in macBytes) {
//                        macStringBuilder.append(String.format("%02X:", byte))
//                    }
//                    if (macStringBuilder.isNotEmpty()) {
//                        macStringBuilder.deleteCharAt(macStringBuilder.length - 1)
//                    }
//                    return macStringBuilder.toString()
//                }
//            } catch (e: Exception) {
//                Log.e("MainActivity", "Error getting MAC address", e)
//            }
//        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return wifiManager.connectionInfo.macAddress
//        }
//        return "02:00:00:00:00:00"
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                displayWifiInfo()
            } else {
                Toast.makeText(this, "Permissions denied. Cannot display WiFi info.", Toast.LENGTH_LONG).show()
            }
        }
    }
}