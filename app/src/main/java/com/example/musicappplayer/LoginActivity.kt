package com.example.musicappplayer

import UserDatabaseHelper
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicappplayer.databinding.ActivityLoginBinding
import com.example.musicappplayer.databinding.ActivitySelectionBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewSignUp.setOnClickListener{
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        }
        auth = FirebaseAuth.getInstance()
        binding.btnlogin.setOnClickListener{
            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                if(it.isSuccessful){
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                }
            }

        }


    }
}