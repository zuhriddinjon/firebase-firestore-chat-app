package com.resocoder.firemessage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myfiremessage.common.setProgressDialog
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.resocoder.firemessage.service.MyFirebaseInstanceIDService
import com.resocoder.firemessage.util.FirestoreUtil
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1

    private val signInProviders =
            listOf(AuthUI.IdpConfig.EmailBuilder()
                    .setAllowNewAccounts(true)
                    .setRequireName(true)
                    .build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        account_sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(signInProviders)
                    .setLogo(R.drawable.ic_fire_emoji)
                    .build()
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val progressDialog = setProgressDialog(this, "Setting up your account")
                FirestoreUtil.initCurrentUserIfFirstTime {
                    startActivity(Intent(this, MainActivity::class.java))

                    val registrationToken = FirebaseMessaging.getInstance().token
                    MyFirebaseInstanceIDService.addTokenToFirestore(registrationToken.result)

                    progressDialog.dismiss()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return

                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK ->
                        Snackbar.make(constraint_layout, "No network", Snackbar.LENGTH_LONG).show()
                    ErrorCodes.UNKNOWN_ERROR ->
                        Snackbar.make(constraint_layout, "Unknown error", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
