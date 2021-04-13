package com.example.firebaseauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseauth.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        /** First, your login status will be logged out!! */
        auth .signOut()

        binding.btnRegister.setOnClickListener{
            register()
        }
        binding.btnLogin.setOnClickListener{
            login()
        }
    }

    // TextView shows your login status when lifecycle begin!!
    override fun onStart() {
        super.onStart()
        checkLoggedInstance()
    }

    private fun register(){

        val email = binding.etEmailRegister.text.toString()
        val password = binding.etPasswordRegister.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch{
                try {
                    auth.createUserWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInstance()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.toString(),Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun login(){

        val email = binding.etEmailLogin.text.toString()
        val password = binding.etPasswordLogin.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch{
                try {
                    auth.signInWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInstance()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.toString(),Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInstance() {
        if (auth.currentUser == null){
            binding.tvLoggedIn.text = "Not Logged in!"
        } else{
            binding.tvLoggedIn.text = "Now, you are Logged in!!"
        }
    }
}