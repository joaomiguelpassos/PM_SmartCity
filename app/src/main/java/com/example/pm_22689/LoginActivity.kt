package com.example.pm_22689

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

/**
 * Activity that prompts the login menu
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private val secretKey: String = "662ede816988e58fb6d057d9d85605e0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        findViewById<Button>(R.id.buttonSignUp).setOnClickListener{
            userLogin()
        }
    }

    private fun userLogin(){
        val email = editTextEmail.text.toString().trim { it <= ' ' }
        val password = editTextPassword.text.toString().trim { it <= ' ' }
        val encryptedPassword: String? = encrypt(password,secretKey)
        val intentmap = Intent(this, MapsActivity::class.java)
        val sharedPref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        if (email.isEmpty()) {
            editTextEmail.error = getString(R.string.email_empty)
            editTextEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = getString(R.string.email_valid)
            editTextEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            editTextPassword.error = getString(R.string.password_empty)
            editTextPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            editTextPassword.error = getString(R.string.password_lenght)
            editTextPassword.requestFocus()
            return
        }
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.login(email, encryptedPassword)
        call.enqueue(object : Callback<OutputPost> {
            override fun onResponse(call: Call<OutputPost>, response: Response<OutputPost>) {
                if (response.isSuccessful) {
                    val resp: OutputPost = response.body()!!
                    Toast.makeText(this@LoginActivity, resp.MSG, Toast.LENGTH_SHORT).show()
                    if(resp.status){
                        with (sharedPref.edit()) {
                            putBoolean(getString(R.string.loggedin), true)
                            putInt("id",resp.data.id)
                            commit()
                        }
                        Log.d("****SHAREDPREF", "Logged in changed to true")
                        Log.d("****SHAREDPREF", "ID saved: ${resp.data.id}")
                        startActivity(intentmap)
                    }
                }
            }

            override fun onFailure(call: Call<OutputPost>, t: Throwable) {
                Toast.makeText(this@LoginActivity, R.string.response_failure, Toast.LENGTH_SHORT).show()
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

    private fun encrypt(strToEncrypt: String, secret_key: String): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = secret_key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = strToEncrypt.toByteArray(charset("UTF8"))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, skey)

                val cipherText = ByteArray(cipher.getOutputSize(input.size))
                var ctLength = cipher.update(
                    input, 0, input.size,
                    cipherText, 0
                )
                ctLength += cipher.doFinal(cipherText, ctLength)
                return String(
                    Base64.encode(cipherText)
                )
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return null
    }
}