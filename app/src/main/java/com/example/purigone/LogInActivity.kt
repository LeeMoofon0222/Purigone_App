package com.example.purigone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.example.purigone.Util.UI
import com.example.purigone.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseAuth.getInstance().currentUser?.let {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        binding.goToSignupBtn.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener{
            login()
        }
    }

    fun login()
    {
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.emailInput.error = "Email not valid"
            return
        }

        if(password.length<6)
        {
            binding.passwordInput.error = "At least 6 character"
            return
        }


        loginWithFirebase(email,password)
    }

    fun loginWithFirebase(email : String, password: String)
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            email,
            password
        ).addOnSuccessListener {
            UI.showToast(this,"Login successfully")
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }.addOnFailureListener{
            UI.showToast(applicationContext,it.localizedMessage?:"Something went wrong")
        }
    }
}