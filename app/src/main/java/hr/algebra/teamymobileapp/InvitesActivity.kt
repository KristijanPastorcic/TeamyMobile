package hr.algebra.teamymobileapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import hr.algebra.teamymobileapp.databinding.ActivityInvitesBinding
import hr.algebra.teamymobileapp.databinding.ActivityJoinTeamBinding
import hr.algebra.teamymobileapp.framework.Adapter
import hr.algebra.teamymobileapp.framework.goToTeamsActivity
import hr.algebra.teamymobileapp.framework.showToast
import hr.algebra.teamymobileapp.models.TeamInfoItem
import hr.algebra.teamymobileapp.models.UserInfo
import hr.algebra.teamymobileapp.models.UserInfoItem
import org.json.JSONObject

class InvitesActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    var teams = arrayOf<UserInfoItem>()
    var users = arrayOf<UserInfoItem>()
    private lateinit var _binding: ActivityInvitesBinding
    var volleyRequestQueue: RequestQueue? = null
    val TAG = "ActivityInvitesBinding"
    var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInvitesBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        id = preferences.getInt(LOGIN_KEY_ID, 0)
        setupListeners()
    }


    private fun getAllUsers() {
        volleyRequestQueue = Volley.newRequestQueue(this)
        val uri = "https://oicarapi.azurewebsites.net/api/User"
        val strReq: StringRequest = object : StringRequest(
            Method.GET, uri,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")
                try {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    teams = gson.fromJson(response, Array<UserInfoItem>::class.java)
                } catch (e: Exception) { // caught while parsing the response
                    Log.e(TAG, "problem occurred")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
                Log.e(TAG, "problem occurred, volley error: " + volleyError.message)
                volleyError.printStackTrace()
            }) {}
        val volleyQueue = Volley.newRequestQueue(this)
        // Adding request to request queue
        volleyRequestQueue?.add(strReq)
    }

    private fun setupListeners() {
        _binding.btnInvite.setOnClickListener {
            volleyRequestQueue = Volley.newRequestQueue(this)

            val postData = JSONObject()
            postData.put("UserId", id)
            postData.put("TeamName", _binding.etEmail.text)
            val uri = "https://oicarapi.azurewebsites.net/api/Teams/CreateInvite"

            Log.e(TAG, postData.toString())

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, uri, postData,
                { _ ->
                    showToast(getString(R.string.request_sent))
                }
            ) { error ->
                error.printStackTrace()
                showToast(getString(R.string.failed_to_send_request))
            }
            // Adding request to request queue
            volleyRequestQueue?.add(jsonObjectRequest)
        }
    }

    private fun createInvitationRequest() {
        if (!validateInput()) {
            showToast(getString(R.string.please_enter_team_name))
        }
        volleyRequestQueue = Volley.newRequestQueue(this)

        val postData = JSONObject()
        postData.put("UserId", id)
        postData.put("TeamName", _binding.etEmail.text)
        val uri = "https://oicarapi.azurewebsites.net/api/Teams/JoinTeamThroughInvite"

        Log.e(TAG, postData.toString())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, uri, postData,
            { _ ->
                showToast(getString(R.string.request_sent))
            }
        ) { error ->
            error.printStackTrace()
            showToast(getString(R.string.failed_to_send_request))
        }
        // Adding request to request queue
        volleyRequestQueue?.add(jsonObjectRequest)
    }

    override fun onBackPressed() {
        goToTeamsActivity()
    }

    private fun validateInput() = _binding.etEmail.text.isNotBlank()

}