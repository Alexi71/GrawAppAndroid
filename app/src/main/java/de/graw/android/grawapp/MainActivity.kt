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
import com.google.firebase.auth.FirebaseAuth
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

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = this.getSharedPreferences(PREFS_FILENAME,0)
        mAuth = FirebaseAuth.getInstance()
        textEmail = findViewById<EditText>(R.id.editTextEmail)
        textPassword = findViewById<EditText>(R.id.editTextPassword)
        loginButton = findViewById<Button>(R.id.buttonLogin)
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



    }

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
                        val userItem = UserItem(0,user!!.email.toString(),"12345")
                        val tableHelper = TableHelper(this)
                        val id = tableHelper.saveUser(userItem)
                        if(id != null) {
                            Log.i("test", "User added successfully")
                        }
                        val intent = Intent(this,MainApplicationActivity::class.java)
                        startActivity(intent)
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
}
