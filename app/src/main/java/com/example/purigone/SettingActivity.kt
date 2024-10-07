package com.example.purigone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.purigone.databinding.ActivitySettingBinding
import com.example.purigone.model.UserModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化 Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 初始化 DrawerLayout 和 NavigationView
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        // 獲取並顯示用戶資訊
        fetchUserInfo()

        // 設置點擊事件
        setupClickListeners()
    }

    private fun fetchUserInfo() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w("SettingActivity", "No user is currently logged in")
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(UserModel::class.java)
                    user?.let {
                        // 確保設置文檔 ID
                        it.id = document.id
                        // 更新 UI
                        binding.tvUserName.text = "Name: ${it.name}"
                        binding.tvUserPermission.text = "Access: ${it.access}"
                    }
                } else {
                    Log.d("SettingActivity", "No such document")
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("SettingActivity", "Error getting user document", exception)
                Toast.makeText(
                    this,
                    "Error fetching user data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setupClickListeners() {
        // 登出按鈕點擊事件
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LogInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Menu 按鈕點擊事件
        binding.menuButton.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            } else {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        // Navigation View 項目點擊事件
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_stain -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                R.id.nav_cleaning -> {
                    startActivity(Intent(this, CleanActivity::class.java))
                    finish()
                }
                R.id.nav_access -> {
                    startActivity(Intent(this, AccessActivity::class.java))
                    finish()
                }
                R.id.nav_settings -> {
                    // 已在設定頁面，不需處理
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}