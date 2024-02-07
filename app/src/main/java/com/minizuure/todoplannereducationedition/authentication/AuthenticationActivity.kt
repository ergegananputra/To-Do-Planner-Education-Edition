package com.minizuure.todoplannereducationedition.authentication

import android.app.Activity
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.minizuure.todoplannereducationedition.CustomSystemTweak
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.ActivityAuthenticationBinding
import com.minizuure.todoplannereducationedition.model.Users
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.preferences.UserPreferences
import java.time.Instant
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class AuthenticationActivity : AppCompatActivity() {


    private val binding by lazy {
        ActivityAuthenticationBinding.inflate(layoutInflater)
    }

    private val signInResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private val fireAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CustomSystemTweak(this)
            .statusBarTweak()

        val currentUser = fireAuth.currentUser

        if (currentUser != null) {
            // User is signed in
            showLogOut()

            val lastUpdate = currentUser.metadata?.lastSignInTimestamp?.let{
                Timestamp(Date(it))
            } ?: Timestamp.now()
            setStatus(Users(username = currentUser.displayName!!, email = currentUser.email!!, last_updated = lastUpdate))
        } else {
            // No user is signed in
            showSignIn()
            setStatus(null)
        }

        setupButtonClose()
        setupLogOutButton()
        setupSignInButton()
    }

    private fun setupSignInButton() {
        binding.authenticationButtonLogInWithGoogle.setOnClickListener {
            signIn()
        }
    }


    private fun signIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                @Suppress("Unresolved: default_web_client_id")
                getString(R.string.default_web_client_id)
            ) // NOTES: Abaikan saja jika IDE menganggap ini Error
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        signInResultLauncher.launch(signInIntent)

    }

    private fun setupButtonClose() {
        binding.authenticationButtonClose.setOnClickListener {
            setResult(RESULT_CANCELED,)
            finish()
        }
    }

    private fun showSignIn() {
        binding.authenticationButtonLogInWithGoogle.visibility = View.VISIBLE
        binding.authenticationTextViewSideNote.visibility = View.VISIBLE
        binding.authenticationButtonLogOut.visibility = View.GONE
    }

    private fun setupLogOutButton() {
        binding.authenticationButtonLogOut.setOnClickListener {
            UserPreferences(this).clearUser()
            fireAuth.signOut()
            showSignIn()
        }
    }

    private fun showLogOut() {
        binding.authenticationButtonLogOut.visibility = View.VISIBLE
        binding.authenticationTextViewSideNote.visibility = View.GONE
        binding.authenticationButtonLogInWithGoogle.visibility = View.GONE
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        fireAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = fireAuth.currentUser!!.uid
                    val user = Users(
                        email = fireAuth.currentUser!!.email!!,
                        username = fireAuth.currentUser!!.displayName!!
                    )

                    firestore.collection(user.table)
                        .document(uid)
                        .set(user.convertToMapFirebase())
                        .addOnSuccessListener {
                            UserPreferences(this).apply {
                                userId = uid
                                userName = user.username
                            }
                        }
                        .addOnFailureListener {
                            Log.e("AuthenticationActivity", "firebaseAuthWithGoogle: ${it.message}", it)
                        }

                    setStatus(user)
                    showLogOut()

                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setStatus(user: Users?) {
        if (user != null) {
            val date = user.last_updated.toDate()
            val sdf = SimpleDateFormat("hh:mm, d MMMM yyyy", Locale.ENGLISH)
            val formattedDate = sdf.format(date)

            val statusText = "Congratulations, now you are currently Signed in as ${user.username} on $formattedDate"
            binding.authenticationTextViewStatus.text = statusText
        } else {
            binding.authenticationTextViewStatus.text = getString(R.string.you_are_not_currently_logged_in_please_sign_in_with_google_to_continue)
        }
    }


}