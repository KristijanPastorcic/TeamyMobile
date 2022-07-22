package hr.algebra.teamymobileapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import hr.algebra.teamymobileapp.databinding.ActivityLoginBinding
import hr.algebra.teamymobileapp.framework.*

var volleyRequestQueue: RequestQueue? = null
//https://oicarapi.azurewebsites.net/api/User/uname=q@q.q&pwd=123
val TAG = "LoginActivity"

const val LOGIN_KEY_UID = "hr.algebra.timey.LoginActivity.uidkey"
const val LOGIN_KEY_ID = "hr.algebra.timey.LoginActivity.idkey"
const val MIN_PASSWORD_LENGTH = 4
class LoginActivity : AppCompatActivity() {



    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            authUser(binding.uid.text.toString(), binding.pwd.text.toString())
        }
        binding.tvRegister.setOnClickListener {
            goToRegister()
        }
        // TODO: on focus lost validate and show error messages, not high priority!!!!!!!!!
    }





    // validate input beforehand
    private fun authUser(uid: String, pwd: String) {
        if (!validateInput())
            return
        volleyRequestQueue = Volley.newRequestQueue(this)
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap

        val uri = "https://oicarapi.azurewebsites.net/api/User/uname=$uid&pwd=$pwd"



        val strReq: StringRequest = object : StringRequest(
            Method.GET,uri,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")

                var id = response.toInt()
                // save successful login
                if(id != -1){
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
                        this.putString(LOGIN_KEY_UID, uid)
                        this.putInt(LOGIN_KEY_ID, id)
                        apply()
                    }
                    goToTeamsActivity()
                } else{
                    showToast(getString(R.string.wrong_uid_or_pwd))
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
                Log.e(TAG, "problem occurred, volley error: " + volleyError.message)
                volleyError.printStackTrace()
            }) {}
        // Adding request to request queue
        volleyRequestQueue?.add(strReq)
    }

    // Checking if the input in form is valid
    private fun validateInput(): Boolean {
        var valid = true

        // checking the proper email format
        if (binding.uid.text.isBlank() ||
            !isEmailValid(binding.uid.text.toString())
        ) {
            binding.uid.error = getString(R.string.pls_enter_valid_email)
            valid = false
        }

        // password validation
        if (binding.pwd.text.isBlank() ||
            binding.pwd.text.length < MIN_PASSWORD_LENGTH
        ) {
            binding.pwd.error = getString(R.string.pls_enter_valid_pwd)
            valid = false
        }
        return valid
    }
}