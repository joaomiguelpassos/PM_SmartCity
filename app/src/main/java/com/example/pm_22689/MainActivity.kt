package com.example.pm_22689

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pm_22689.api.EndPoints
import com.example.pm_22689.api.OutputPost
import com.example.pm_22689.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Activity that prompts the login menu to user if never logged in or last session logged out
 */
class MainActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextAddress: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextName = findViewById(R.id.editTextName)
        editTextAddress = findViewById(R.id.editTextAddress)

        findViewById<Button>(R.id.buttonSignUp).setOnClickListener{
            userSignup()
        }
    }

    fun userSignup(){
        val email = editTextEmail.text.toString().trim { it <= ' ' }
        val password = editTextPassword.text.toString().trim { it <= ' ' }
        val name = editTextName.text.toString().trim { it <= ' ' }
        val address: String = editTextAddress.getText().toString().trim { it <= ' ' }

        if (email.isEmpty()) {
            editTextEmail.error = "Email is required"
            editTextEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Enter a valid email"
            editTextEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            editTextPassword.error = "Password required"
            editTextPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            editTextPassword.error = R.string.password_lenght.toString()
            editTextPassword.requestFocus()
            return
        }

        if (name.isEmpty()) {
            editTextName.error = "Name required"
            editTextName.requestFocus()
            return
        }

        if (address.isEmpty()) {
            editTextAddress.error = "Address required"
            editTextAddress.requestFocus()
            return
        }
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.addUser(null ,name, email, password, address)

        call.enqueue(object : Callback<OutputPost> {
            override fun onResponse(call: Call<OutputPost>, response: Response<OutputPost>) {
                if (response.isSuccessful) {
                    val c: OutputPost = response.body()!!       // variavel que contem o user em gson
                    Toast.makeText(this@MainActivity,"Sucesso!",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<OutputPost>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    // Options menu to navigate to personal notes
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.view_notes -> {
                val intent = Intent(this, NotesActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)               //returns false
        }
    }
}