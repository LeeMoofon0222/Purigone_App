package com.example.purigone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.purigone.databinding.ActivityCleanBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class CleanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCleanBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var deviceSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCleanBinding.inflate(layoutInflater)
        setContentView(binding.root)




        // 初始化 DrawerLayout 和 NavigationView
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        // 初始化 Spinner
        deviceSpinner = binding.deviceSpinner

        // 設置 Spinner
        setupSpinner()

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
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                R.id.nav_cleaning -> {
                }
                R.id.nav_access -> {
                    startActivity(Intent(this, AccessActivity::class.java))
                    finish()
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers() // 點擊項目後關閉抽屜
            true
        }
    }



    private fun setupSpinner() {
        // 創建一個 ArrayAdapter 使用字符串數組和默認的 spinner 布局
        ArrayAdapter.createFromResource(
            this,
            R.array.device_array,  // 確保你在 res/values/strings.xml 中定義了這個數組
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 指定下拉列表的布局樣式
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 將適配器應用到 spinner
            deviceSpinner.adapter = adapter
        }

        // 設置選擇監聽器
        deviceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDevice = parent.getItemAtPosition(position).toString()
                // 在這裡處理設備選擇邏輯
                // 例如：updateUIForSelectedDevice(selectedDevice)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 可選：處理沒有選擇任何項目的情況
            }
        }
    }

    // 可選：根據選擇的設備更新 UI
    private fun updateUIForSelectedDevice(device: String) {
        // 根據選擇的設備更新 UI 或執行其他操作
    }
}