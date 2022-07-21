package hr.algebra.teamymobileapp

import android.R
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import hr.algebra.teamymobileapp.databinding.ActivityJoinTeamBinding
import hr.algebra.teamymobileapp.framework.goToTeamsActivity
import hr.algebra.teamymobileapp.framework.showToast
import hr.algebra.teamymobileapp.models.TeamInfoItem
import hr.algebra.teamymobileapp.models.UserInfoItem
import org.json.JSONObject


class JoinTeamActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    var teams = arrayOf<TeamInfoItem>()
    private lateinit var _binding: ActivityJoinTeamBinding
    var volleyRequestQueue: RequestQueue? = null
    val TAG = "JoinTeamActivity"
    var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityJoinTeamBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        id = preferences.getInt(LOGIN_KEY_ID, 0)
        getAllTeams()
        setupListeners()
    }

    private fun setupListeners() {
        _binding.btnJoinTeam.setOnClickListener {
            createJoinTeamRequest()
        }


    }

    private fun createJoinTeamRequest() {
        if (!validateInput()) {
            // TODO: invalid input message
            return
        }
        volleyRequestQueue = Volley.newRequestQueue(this)

        val postData = JSONObject()
        postData.put("UserId", id)
        postData.put("TeamName", _binding.etTeamName.text)
        val uri = "https://oicarapi.azurewebsites.net/api/Teams/JoinTeamThroughInvite"

        Log.e(TAG, postData.toString())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, uri, postData,
            { _ ->
                showToast("Request sent")
            }
        ) { error ->
            error.printStackTrace()
            showToast("Request not sent")
        }
        // Adding request to request queue
        volleyRequestQueue?.add(jsonObjectRequest)
    }

    private fun getAllTeams() {
        volleyRequestQueue = Volley.newRequestQueue(this)
        val uri = "https://oicarapi.azurewebsites.net/api/Teams"
        val strReq: StringRequest = object : StringRequest(
            Method.GET, uri,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")
                try {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    teams = gson.fromJson(response, Array<TeamInfoItem>::class.java)
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



    override fun onBackPressed() {
        goToTeamsActivity()
    }

    private fun validateInput() = _binding.etTeamName.text.isNotBlank()
}