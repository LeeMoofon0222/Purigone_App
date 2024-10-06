package com.example.purigone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.purigone.databinding.ActivitySettingBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class SettingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 初始化 DrawerLayout 和 NavigationView
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        binding.btnLogout.setOnClickListener {
            logout()
        }

        // 點擊 menuButton 時打開抽屜
        binding.menuButton.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START) // 打開抽屜
            } else {
                drawerLayout.closeDrawer(GravityCompat.START) // 關閉抽屜（如果已經打開）
            }
        }

        // 設置 NavigationView 的選擇項目點擊事件
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_stain -> {
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                R.id.nav_cleaning -> {
                    startActivity(Intent(this,CleanActivity::class.java))
                    finish()
                }
                R.id.nav_access -> {
                    startActivity(Intent(this,AccessActivity::class.java))
                    finish()
                }
                R.id.nav_settings -> {
                }
            }
            drawerLayout.closeDrawers() // 點擊項目後關閉抽屜
            true
        }
    }

    private fun logout() {
        val auth = FirebaseAuth.getInstance()

        val currentUserId = auth.currentUser?.uid

        // 檢查是否有當前用戶
        if (currentUserId != null) {
            // 執行登出操作
            auth.signOut()

            // 跳轉到登錄頁面或其他操作
            val intent = Intent(this, LogInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // 關閉當前頁面，防止用戶回退到此頁面
        } else {
            // 如果用戶尚未登入，顯示錯誤或處理相應邏輯
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
        }
    }
}