package com.oktydeniz.k_foursquare.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.oktydeniz.k_foursquare.databinding.ActivityMainBinding
import com.parse.ParseUser

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actions()
    }

    private fun actions() {
        binding.singIn.setOnClickListener {
            singIn()
        }
        binding.singUp.setOnClickListener {
            singUp()
        }
    }

    private fun singUp() {
        val user = ParseUser()
        user.username = binding.userNameEditText.text.toString()
        user.setPassword(binding.userPasswordText.text.toString())
        user.signUpInBackground {
            if (it != null) {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "User Created !", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@AuthActivity, StartingActivity::class.java))
                finish()

            }
        }
    }

    private fun singIn() {
        ParseUser.logInInBackground(
            binding.userNameEditText.text.toString(),
            binding.userPasswordText.text.toString()
        ) { user, e ->
            if (e != null) {
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Welcome ${user.username}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@AuthActivity, StartingActivity::class.java))
                finish()
            }
        }
    }
}