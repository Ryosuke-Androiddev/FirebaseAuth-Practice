package com.example.firebaseauth

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseauth.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
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
        //auth .signOut()

        binding.btnRegister.setOnClickListener{
            register()
        }
        binding.btnLogin.setOnClickListener{
            login()
        }
        binding.btnUpdateProfile.setOnClickListener{
            updateProfile()
        }
    }

    // TextView shows your login status when lifecycle begin!!
    override fun onStart() {
        super.onStart()
        checkLoggedInstance()
    }

    private fun updateProfile(){

        auth.currentUser?.let { user->

            val username = binding.etUsername.text.toString()
            val photoURI = Uri.parse("android.resource://$packageName/${R.drawable.logo_black_square}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoURI)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.IO){
                        checkLoggedInstance()
                        Toast.makeText(this@MainActivity,"Successfully update your profile!!",Toast.LENGTH_LONG).show()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.IO){
                        Toast.makeText(this@MainActivity,e.toString(),Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
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
        val user = auth.currentUser
        if (user == null){
            binding.tvLoggedIn.text = "Not Logged in!"
        } else{
            binding.tvLoggedIn.text = "Now, you are Logged in!!"
            binding.etUsername.setText(user.displayName)
            binding.ivProfilePicture.setImageURI(user.photoUrl)
        }
    }
}