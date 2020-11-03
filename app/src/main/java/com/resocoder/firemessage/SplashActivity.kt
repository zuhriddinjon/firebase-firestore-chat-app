package com.resocoder.firemessage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser == null)
            startActivity(Intent(this, SignInActivity::class.java))
        else
            startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
