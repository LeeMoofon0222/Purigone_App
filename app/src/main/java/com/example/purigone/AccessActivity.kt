package com.example.purigone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.purigone.databinding.ActivityAccessBinding
import com.google.android.material.navigation.NavigationView

class AccessActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAccessBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 初始化 DrawerLayout 和 NavigationView
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

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
                }
                R.id.nav_cleaning -> {
                    startActivity(Intent(this,CleanActivity::class.java))
                    finish()
                }
                R.id.nav_access -> {
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this,SettingActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers() // 點擊項目後關閉抽屜
            true
        }
    }
}