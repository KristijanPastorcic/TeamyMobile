package hr.algebra.teamymobileapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import hr.algebra.teamymobileapp.databinding.ActivityRegisterBinding
import hr.algebra.teamymobileapp.framework.goToLogin
import hr.algebra.teamymobileapp.framework.isEmailValid
import hr.algebra.teamymobileapp.framework.isValidPassword
import hr.algebra.teamymobileapp.framework.showToast
import org.json.JSONObject


class RegisterActivity : AppCompatActivity() {

    var volleyRequestQueue: RequestQueue? = null
    val TAG = "LoginActivity"

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToLogin()
    }


    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            registerUser(
                binding.uid.text.toString(),
                binding.pwd.text.toString(),
                binding.pwd2.text.toString()
            )
        }
    }


    private fun registerUser(
        uid: String,
        pwd: String,
        pwd2: String
    ) {
/*        if (!validateInput())
            return*/
        volleyRequestQueue = Volley.newRequestQueue(this)

        val postData = JSONObject()
        postData.put("Id", 0)
        postData.put("Name", uid)
        postData.put("Roll", null)
        postData.put("Pwd", "1234")
        postData.put("DateCreated", null)
        val uri = "https://oicarapi.azurewebsites.net/api/User"
        Log.e(TAG, postData.toString())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, uri, postData,
            { response ->
                Log.e(TAG, "response: $response")
                // TODO: goToLogin(uid, pwd) -> fill uid and pwd fields on success register
                //  not high priority!!!!!!!!!
                showToast(getString(R.string.succesful_register_message))
                goToLogin()
            }
        ) { error ->
            showToast(getString(R.string.register_fail_message))
            error.printStackTrace()
        }
        // Adding request to request queue
        volleyRequestQueue?.add(jsonObjectRequest)
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
        if (!isValidPassword(binding.pwd.text.toString())
        ) {
            binding.pwd.error = getString(R.string.password_validation_massage)
            valid = false
        }

        if (binding.pwd2.text.toString() != binding.pwd.text.toString()) {
            binding.pwd2.error = getString(R.string.pass_dont_match)
            valid = false
        }
        return valid
    }
}