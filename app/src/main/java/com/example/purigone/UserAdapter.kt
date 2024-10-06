package com.example.purigone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.purigone.model.UserModel

class UserAdapter(
    private val userList: MutableList<UserModel>,
    private val onAccessChange: (UserModel, String) -> Unit,
    private val onDeleteClick: (UserModel) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarTextView: TextView = itemView.findViewById(R.id.avatarTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val accessSpinner: Spinner = itemView.findViewById(R.id.accessSpinner)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(
            user: UserModel,
            onAccessChange: (UserModel, String) -> Unit,
            onDeleteClick: (UserModel) -> Unit
        ) {
            // 設置字母頭像
            val firstLetter = user.name.firstOrNull()?.uppercase() ?: "?"
            avatarTextView.text = firstLetter

            // 設置用戶名
            nameTextView.text = user.name

            // 設置Spinner
            val accessOptions = arrayOf("User", "Admin")
            val adapter = ArrayAdapter(itemView.context,
                android.R.layout.simple_spinner_item,
                accessOptions
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            accessSpinner.adapter = adapter

            // 設置當前選中的權限
            val currentPosition = if (user.access == "User") 0 else 1
            accessSpinner.setSelection(currentPosition)

            // 設置Spinner選擇監聽器
            accessSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val newAccess = accessOptions[position]
                    // 確保無論選擇什麼選項，都更新 user 的 access 屬性
                    if (newAccess != user.access) {
                        user.access = newAccess // 更新 user.access
                        onAccessChange(user, newAccess) // 回調函數通知變更
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // 設置刪除按鈕點擊事件
            deleteButton.setOnClickListener {
                onDeleteClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user, onAccessChange, onDeleteClick)
    }

    override fun getItemCount(): Int = userList.size

    fun updateList(newList: List<UserModel>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }
}