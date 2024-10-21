package com.example.purigone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purigone.databinding.ActivityAccessBinding
import com.example.purigone.model.UserModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class AccessActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAccessBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<UserModel>()  // 初始化空的 user list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 初始化 DrawerLayout 和 NavigationView
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 初始化 UserAdapter
        userAdapter = UserAdapter(
            userList = userList,
            onAccessChange = { user, newAccess ->
                changeAccess(user, newAccess)
            },
            onDeleteClick = { user ->
                deleteUser(user)
            }
        )

        // 設置 adapter 到 RecyclerView
        recyclerView.adapter = userAdapter

        // 從 Firestore 獲取數據並更新 RecyclerView
        fetchUserData()


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
    private fun fetchUserData() {
        val db = FirebaseFirestore.getInstance()
        Log.d("AccessActivity", "Starting to fetch user data")

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                Log.d("AccessActivity", "Successfully fetched ${result.size()} documents")
                for (document in result) {
                    val user = document.toObject(UserModel::class.java)
                    user.id = document.id
                    userList.add(user)
                    Log.d("AccessActivity", "Added user: ${user.id}")
                }
                userAdapter.notifyDataSetChanged()
                Log.d("AccessActivity", "Notified adapter of data change")
            }
            .addOnFailureListener { exception ->
                Log.e("AccessActivity", "Error getting documents: ", exception)
            }
    }

    private fun changeAccess(user: UserModel, newAccess: String) {
        Log.w("TAG", newAccess)
        val db = FirebaseFirestore.getInstance()
        // 檢查 user.id 是否不為空，避免傳入空的 ID
        if (user.id.isNotEmpty()) {
            db.collection("users").document(user.id)  // 使用 user.id 來更新 Firestore 文件
                .update("access", newAccess)
                .addOnSuccessListener {
                    Log.d("TAG", "DocumentSnapshot successfully updated!")  // 成功更新日誌
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error updating document", e)  // 更新失敗日誌
                }
        } else {
            Log.w("TAG", "User ID is empty or null")  // 錯誤處理，當 user.id 为空或 null
        }
    }

    private fun deleteUser(user: UserModel) {
        val db = FirebaseFirestore.getInstance()

        // 檢查用戶的 ID 是否不為空
        if (user.id.isNotEmpty()) {
            db.collection("users").document(user.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("TAG", "User successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error deleting user", e)
                }
        }
        else {
            Log.w("TAG", "User ID is empty or null")  // ID 為空的錯誤處理
        }
    }


}