package de.graw.android.grawapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.EditText
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import de.graw.android.grawapp.dataBase.DbHelper
import de.graw.android.grawapp.dataBase.TableHelper


class MainActivity : AppCompatActivity() {

    val PREFS_FILENAME = "de.graw.android.grawapp.prefs"
    val PREF_USERNAME = "de.graw.android.grawapp.prefs.username"
    val PREF_PASSWORD = "de.graw.android.grawapp.prefs.password"

    var prefs:SharedPreferences? = null
    var loginButton:Button? = null
    var signInButton:Button? = null
    var textEmail:EditText? = null
    var textPassword:EditText? = null

    var googleSingInButton:SignInButton? = null

    private var mAuth: FirebaseAuth? = null

    var googleApiClient: GoogleApiClient? = null
    val TAG = "CreateAccount"

    val GOOGLE_LOG_IN_RC = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = this.getSharedPreferences(PREFS_FILENAME,0)
        mAuth = FirebaseAuth.getInstance()
        textEmail = findViewById<EditText>(R.id.editTextEmail)
        textPassword = findViewById<EditText>(R.id.editTextPassword)
        loginButton = findViewById<Button>(R.id.buttonLogin)
        googleSingInButton = findViewById(R.id.google_sign_in_button)

        loginButton!!.setOnClickListener { setLoginClick() }
        val username = prefs!!.getString(PREF_USERNAME,"")
        val password = prefs!!.getString(PREF_PASSWORD,"")
        if(!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            textEmail!!.setText(username)
            textPassword!!.setText( password)
        }

        signInButton = findViewById<Button>(R.id.buttonSignIn)
        signInButton!!.setOnClickListener {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)


        }

        googleSingInButton!!.setOnClickListener {
            googleLogin()
        }
        // Configure Google Sign In
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.request_client_id))
                .requestEmail()
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this){}
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build()

    }

    private fun googleLogin() {
        Log.i(TAG, "Starting Google LogIn Flow.")
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, GOOGLE_LOG_IN_RC)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("test", "Got Result code ${requestCode}.")
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_LOG_IN_RC) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.i(TAG, "With Google LogIn, is result a success? ${result.isSuccess}.")
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(result.signInAccount!!)
            } else {
                Toast.makeText(this, "Some error occurred.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.i(TAG, "Authenticating user with firebase.")
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            Log.i(TAG, "Firebase Authentication, is result a success? ${task.isSuccessful}.")
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.i("test","google user email: ${mAuth!!.currentUser!!.email} id: ${mAuth!!.currentUser!!.uid}")
                saveUser(mAuth!!.currentUser!!.email!!,mAuth!!.currentUser!!.uid)
                gotoNextPage(mAuth!!.currentUser!!.email!!,mAuth!!.currentUser!!.uid)
            } else {
                // If sign in fails, display a message to the user.
                Log.e(TAG, "Authenticating with Google credentials in firebase FAILED !!")
            }
        }
    }

    // Creating and Configuring Google Api Client.
   /* googleApiClient = GoogleApiClient.Builder(this@CreateAccount)
    .enableAutoManage(this@CreateAccount  /* OnConnectionFailedListener */) { }
    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
    .build()*/

    fun setLoginClick() {
        mAuth!!.signInWithEmailAndPassword(textEmail!!.text.toString(),
                textPassword!!.text.toString())
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        val edit = prefs!!.edit()
                        edit.putString(PREF_USERNAME,textEmail!!.text.toString())
                        edit.putString(PREF_PASSWORD,textPassword!!.text.toString())
                        edit.apply()
                        // Sign in success, update UI with the signed-in user's information
                        Log.i("test", "signInWithEmail:success")
                        val user = mAuth!!.getCurrentUser()
                        Log.i("test","user succesfully logged in: ${user!!.email}")
                        /*val userItem = UserItem(0,user!!.email.toString(),user.uid)
                        Log.i("test",user.uid)
                        val tableHelper = TableHelper(this)
                        val id = tableHelper.saveUser(userItem)*/

                        saveUser(mAuth!!.currentUser!!.email!!,mAuth!!.currentUser!!.uid)
                        gotoNextPage(mAuth!!.currentUser!!.email!!,mAuth!!.currentUser!!.uid)

                        //updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.i("test", "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_LONG).show()
                        //updateUI(null)
                    }

                    // ...
                })
    }

    private fun gotoNextPage(email:String, userId:String) {
        val intent = Intent(this,MainApplicationActivity::class.java)
        intent.putExtra("email",email)
        intent.putExtra("userid",userId)
        startActivity(intent)
    }

    private fun saveUser(email:String,userId:String) {
        val userItem = UserItem(0,email,userId)
        //Log.i("test",user.uid)
        val tableHelper = TableHelper(this)
        val id = tableHelper.saveUser(userItem)
        if(id != null) {
            Log.i("test", "User added successfully")
        }
    }
}
