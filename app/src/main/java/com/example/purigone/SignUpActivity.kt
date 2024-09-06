package com.example.purigone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.purigone.databinding.ActivityLogInBinding
import com.example.purigone.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.goToLoginBtn.setOnClickListener{
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }
}