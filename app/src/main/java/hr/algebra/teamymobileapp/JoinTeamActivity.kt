package hr.algebra.teamymobileapp

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import hr.algebra.teamymobileapp.databinding.ActivityJoinTeamBinding
import hr.algebra.teamymobileapp.framework.*
import hr.algebra.teamymobileapp.models.*
import org.json.JSONObject


class JoinTeamActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    var teams = arrayOf<TeamInfoItem>()
    var teamInfo = TeamInfo()
    var teamRequests = arrayOf<TeamRequestItem>()
    val teamRequestsInfo = TeamRequestInfo()
    val teamRequestsMap = java.util.HashMap<TeamInfoItem, TeamRequestInfo>()
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

/*        getTeams(id)*/
        val extras = this.intent.extras
        teams = extras?.get("teams") as Array<TeamInfoItem>
        getRequests()

        setupListeners()
    }


    private fun getRequests() {
        teams.forEach {
            getRequestsForTeam(it)
        }
        val adapter = AdapterTeamRequests(this, teamRequestsMap, teamRequestsInfo)
        _binding.invitesRecyclerView.adapter = adapter
    }

    private fun getRequestsForTeam(teamItem: TeamInfoItem) {
        volleyRequestQueue = Volley.newRequestQueue(this)
        val uri = "http://oicarapi.azurewebsites.net/api/Teams/idOfTeam=${teamItem.id}"
        val strReq: StringRequest = object : StringRequest(
            Method.GET, uri,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")
                if(!response.equals("[]")){
                    try {
                        val gsonBuilder = GsonBuilder()
                        val gson = gsonBuilder.create()
                        teamRequests = gson.fromJson(response, Array<TeamRequestItem>::class.java)
                        val teamRequestsInfoTeam = TeamRequestInfo()
                        teamRequests.forEach {
                            teamRequestsInfo.add(it)
                            teamRequestsInfoTeam.add(it)
                        }
                        teamRequestsMap[teamItem] = teamRequestsInfoTeam
                    } catch (e: Exception) { // caught while parsing the response
                        Log.e(TAG, "problem occurred")
                        e.printStackTrace()
                    }
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
        _binding.btnJoinTeam.setOnClickListener {
            createJoinTeamRequest()
        }

/*        _binding.etTeamName.doOnTextChanged { text, _, _, _ ->
            // TODO: switch btn on of on filled tvs
        }*/


    }

    private fun createJoinTeamRequest() {
        if (!validateInput()) {
            // TODO: invalid input message
            return
        }
        volleyRequestQueue = Volley.newRequestQueue(this)

        val postData = JSONObject()
        postData.put("UserId", id.toString())
        postData.put("TeamName", _binding.etTeamName.text)
        val uri = "https://oicarapi.azurewebsites.net/api/Teams/CreateInvite"

        Log.e(TAG, postData.toString())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, uri, postData,
            {
                // TODO: fix com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of
                showToast(getString(hr.algebra.teamymobileapp.R.string.send_request))
                _binding.etTeamName.text.clear()
            }
        ) { error ->
            error.printStackTrace()
            showToast(getString(hr.algebra.teamymobileapp.R.string.request_not_sent))
            _binding.etTeamName.text.clear()
        }
        // Adding request to request queue
        volleyRequestQueue?.add(jsonObjectRequest)
    }

    override fun onBackPressed() {
        goToTeamsActivity()
    }

    private fun validateInput(): Boolean {
        if (_binding.etTeamName.text.isNotBlank()) {
            _binding.etTeamName.error =
                getString(hr.algebra.teamymobileapp.R.string.team_name_not_entered)
            return false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_teams, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miPreferences -> {
                goToTeamsActivity()
                return true
            }
            R.id.miPreferences -> {
                showSettings()
                return true
            }
            R.id.miExit -> {
                exitAppAndLogout()
                return true
            }
            R.id.miJoinTeam -> {
                goToJoinTeamActivity()
                return true
            }
            R.id.miInvites -> {
                goToInvitesActivity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exitAppAndLogout() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.logout)
            setMessage(getString(R.string.leaving))
            setIcon(R.drawable.exit)
            setCancelable(true)
            setPositiveButton(getString(R.string.ok)) { _, _ ->
                preferences.edit().remove(LOGIN_KEY_UID).apply()
                // TODO: exit the damon app on back or logout btn, don't go to login activity
                finish()
            }
            setNegativeButton(getString(R.string.cancel), null)
            show()
        }
    }
}