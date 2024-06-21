package com.example.musicappplayer

import UserDatabaseHelper
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicappplayer.databinding.ActivityLoginBinding
import com.example.musicappplayer.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val username = binding.inputUsername.text.toString().trim()
            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()

            // Kiểm tra tính hợp lệ của dữ liệu
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra định dạng email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra độ dài mật khẩu
            if (password.length < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                if(it.isSuccessful){
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                }
            }
            binding.alreadyHaveAccount.setOnClickListener{
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)

            }



        }
    }

}