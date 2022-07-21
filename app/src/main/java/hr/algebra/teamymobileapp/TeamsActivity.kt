package hr.algebra.teamymobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import hr.algebra.teamymobileapp.databinding.ActivityTeamsBinding
import hr.algebra.teamymobileapp.framework.*
import hr.algebra.teamymobileapp.models.TeamInfo
import hr.algebra.teamymobileapp.models.TeamInfoItem
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TeamsActivity : AppCompatActivity() {
    var volleyRequestQueue: RequestQueue? = null
    val TAG = "TeamsActivity"
    private lateinit var _binding: ActivityTeamsBinding
    var teams = arrayOf<TeamInfoItem>()
    val teamInfo = TeamInfo()
    private var id: Int = 0
    private var uid: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTeamsBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        // get users id
        id = androidx.preference.PreferenceManager
            .getDefaultSharedPreferences(this).getInt(LOGIN_KEY_ID, -1)

        uid = androidx.preference.PreferenceManager
            .getDefaultSharedPreferences(this).getString(LOGIN_KEY_UID, "")

        // get users teams
        getTeams(id)
        setupListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_teams, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miJoinTeam -> {
                goToJoinTeamActivity()
                return true
            }
            R.id.miInvites->{
                goToInvitesActivity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupListeners() {
        _binding.fapBtnTeams.setOnClickListener {
            createTeam(id)
            _binding.etTeamName.text.clear()
            teamInfo.clear()
            _binding.teamsRecyclerView.adapter?.notifyDataSetChanged()
            getTeams(id)
        }

    }

    private fun createTeam(id: Int) {
        if (!validateInput()) {
            showToast(getString(R.string.please_enter_team_name))
            return
        }
        volleyRequestQueue = Volley.newRequestQueue(this)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("mm/dd/yyyy")
        val formattedDate = current.format(formatter)


        val postData = JSONObject()
        postData.put("id", 0)
        postData.put("name", _binding.etTeamName.text.toString())
        postData.put("teacherID", null)
        postData.put("ownerID", id)
        postData.put("dateCreated", formattedDate)
        postData.put("ownerName", null)
        postData.put("teacherName", null)

        val uri = "https://oicarapi.azurewebsites.net/api/Teams/CreateTeam"
        Log.e(TAG, postData.toString())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, uri, postData,
            { response ->
                Log.e(TAG, "response: $response")
                // TODO: goToLogin(uid, pwd) -> fill uid and pwd fields on success register
                //  not high priority!!!!!!!!!
                showToast(getString(R.string.teamCreatedSucessfuly))

            }
        ) { error ->
            showToast(getString(R.string.register_fail_message))
            error.printStackTrace()
        }
        // Adding request to request queue
        volleyRequestQueue?.add(jsonObjectRequest)
    }

    private fun validateInput() = _binding.etTeamName.text.isNotBlank()


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
                        it.ownerName = uid
                        teamInfo.add(it)
                    }
                    val adapter = Adapter(this, teamInfo)
                    _binding.teamsRecyclerView.adapter = adapter
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
        goToMain()
    }
}