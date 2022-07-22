package hr.algebra.teamymobileapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import hr.algebra.teamymobileapp.databinding.ActivityInvitesBinding
import hr.algebra.teamymobileapp.framework.Adapter
import hr.algebra.teamymobileapp.framework.AdapterInvites
import hr.algebra.teamymobileapp.framework.goToTeamsActivity
import hr.algebra.teamymobileapp.framework.showToast
import hr.algebra.teamymobileapp.models.InviteUserInfo
import hr.algebra.teamymobileapp.models.InviteUserItem
import hr.algebra.teamymobileapp.models.TeamInfo
import hr.algebra.teamymobileapp.models.TeamInfoItem
import org.json.JSONObject

class InvitesActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    var teams = arrayOf<TeamInfoItem>()
    val teamInfo = TeamInfo()
    var invites = arrayOf<InviteUserItem>()
    val invitesInfo = InviteUserInfo()
    private lateinit var _binding: ActivityInvitesBinding
    var chosenTeam: Int = 0
    var volleyRequestQueue: RequestQueue? = null
    val TAG = "ActivityInvitesBinding"
    var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInvitesBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        id = preferences.getInt(LOGIN_KEY_ID, 0)
        getTeams(id)
        getInvitations(id)
        setupListeners()
    }

    private fun getInvitations(id: Int) {
        volleyRequestQueue = Volley.newRequestQueue(this)
        val uri = "http://oicarapi.azurewebsites.net/api/Teams/idInvitedUser=$id"
        val strReq: StringRequest = object : StringRequest(
            Method.GET, uri,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")
                try {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    invites = gson.fromJson(response, Array<InviteUserItem>::class.java)
                    invites.forEach {
                        invitesInfo.add(it)
                    }
                    val adapter = AdapterInvites(this, invitesInfo)
                    _binding.invitesRecyclerView.adapter = adapter
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

    private fun getTeams(userID: Int) {
        volleyRequestQueue = Volley.newRequestQueue(this)
        val uri = "https://oicarapi.azurewebsites.net/api/Teams/id=$userID"
        val strReq: StringRequest = object : StringRequest(
            Method.GET, uri,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")
                try {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    teams = gson.fromJson(response, Array<TeamInfoItem>::class.java)
                    teams.forEach {
                        it.ownerName = id
                        teamInfo.add(it)
                    }
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
            invite()
        }
    }

    private fun invite() {
        if (!validateInput()) {
            return
        }
        volleyRequestQueue = Volley.newRequestQueue(this)

        val postData = JSONObject()
        postData.put("UserName", _binding.etEmail.text)
        postData.put("TeamId", chosenTeam.toString())
        val uri = "https://oicarapi.azurewebsites.net/api/Teams/CreateInviteUser"

        Log.e(TAG, postData.toString())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, uri, postData,
            {
                showToast(getString(R.string.request_sent))
            }
        ) { error ->
            // TODO: fix com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of
            error.printStackTrace()
            showToast("Success")
            _binding.etTeamName.text.clear()
            _binding.etEmail.text.clear()
        }
        // Adding request to request queue
        volleyRequestQueue?.add(jsonObjectRequest)
    }

    override fun onBackPressed() {
        goToTeamsActivity()
    }

    private fun validateInput(): Boolean {
        var valid = true
        if (_binding.etEmail.text.isBlank()) {
            _binding.etEmail.error = getString(R.string.email_not_entered)
            valid = false
        }
        if (_binding.etTeamName.text.isBlank()) {
            _binding.etTeamName.error = getString(R.string.team_name_not_entered)
            valid = false
        }
        val teamName = _binding.etTeamName.text.toString()
        chosenTeam = 0
        var teamFound = false
        teamInfo.forEach {
            if (it.name == teamName) {
                chosenTeam = it.id
                teamFound = true
            }
        }
        if (!teamFound){
            _binding.etTeamName.error = "jebat cu ti mater" /*getString(R.string.wrong_team_entered)*/
            valid = false
        }
        return valid
    }

    override fun toString(): String {
        return id.toString()
    }
}