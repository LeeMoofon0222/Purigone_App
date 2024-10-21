package com.example.purigone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.purigone.Util.UI
import com.example.purigone.databinding.ActivityLogInBinding
import com.example.purigone.databinding.ActivitySignUpBinding
import com.example.purigone.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpBtn.setOnClickListener{
            signup()
        }


        binding.goToLoginBtn.setOnClickListener{
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }

    //fun setInProgress(inProgress : Boolean){}

    private fun signup()
    {

        val name = binding.nameInput.text.toString() + binding.lastNameInput.text.toString()
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

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

        if(password != confirmPassword)
        {
            binding.confirmPasswordInput.error = "Password not matched"
            return
        }

        signUpWithFirebase(email,password,name)
    }

    private fun signUpWithFirebase(email : String, password : String, name : String){
        //setInProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            email,password
        ).addOnSuccessListener {
            it.user?.let{user ->
                val userModel = UserModel(user.uid,email, password, name, "User")
                Firebase.firestore.collection("users")
                    .document(user.uid)
                    .set(userModel).addOnSuccessListener {
                        UI.showToast(applicationContext,"Account created successfully")
                    }
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        }.addOnFailureListener{
            UI.showToast(applicationContext,it.localizedMessage?:"Something went wrong")
        }
    }
}