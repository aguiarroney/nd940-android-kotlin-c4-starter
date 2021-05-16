package com.udacity.project4.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.firebase.ui.auth.AuthUI

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)

        binding.btnLogin.setOnClickListener { launchSignInFlow() }

        setContentView(binding.root)

//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google

//          TODO: If the user was authenticated, send him to RemindersActivity

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build(), 1001
        )
    }
}
