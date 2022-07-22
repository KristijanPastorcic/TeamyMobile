package hr.algebra.teamymobileapp

import android.app.DatePickerDialog
import android.content.Intent
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
import hr.algebra.teamymobileapp.databinding.ActivityTeamsBinding
import hr.algebra.teamymobileapp.framework.*
import hr.algebra.teamymobileapp.models.TeamInfo
import hr.algebra.teamymobileapp.models.TeamInfoItem
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


private const val CHOSEN_DATE_KEY = "hr.algebra.TeamsActivity.chosendatekey"

class TeamsActivity : AppCompatActivity() {
    var volleyRequestQueue: RequestQueue? = null
    val TAG = "TeamsActivity"
    private lateinit var preferences: SharedPreferences
    private lateinit var _binding: ActivityTeamsBinding
    var teams = arrayOf<TeamInfoItem>()
    val teamInfo = TeamInfo()
    private var id: Int = 0
    private var uid: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTeamsBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
/*        // login for test
        preferences.edit().apply {
            this.putString(LOGIN_KEY_UID, "andro@mail.com")
            this.putInt(LOGIN_KEY_ID, 13)
            apply()
        }*/

        id = preferences.getInt(LOGIN_KEY_ID, 0)

        // get users id
        id = androidx.preference.PreferenceManager
            .getDefaultSharedPreferences(this).getInt(LOGIN_KEY_ID, -1)

        uid = androidx.preference.PreferenceManager
            .getDefaultSharedPreferences(this).getString(LOGIN_KEY_UID, "")

        // get users teams
        getTeams(id)
        setupListeners()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_teams, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miPreferences -> {
                showSettings()
                return true
            }
            R.id.miCalendar -> {
                showCalendar()
                return true
            }
            R.id.miExit -> {
                exitAppAndLogout()
                return true
            }
            R.id.miJoinTeam -> {
                /*goToJoinTeamActivity()*/
                val i = Intent(this, JoinTeamActivity::class.java)
                i.putExtra("teams", teams)
                startActivity(i)
                return true
            }
            R.id.miInvites -> {
                goToInvitesActivity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCalendar() {
        val initialDate = Calendar.getInstance()
        if (preferences.contains(CHOSEN_DATE_KEY)) {
            initialDate.timeInMillis = preferences.getLong(CHOSEN_DATE_KEY, -1)
        }
        val initialYear = initialDate.get(Calendar.YEAR)
        val initialMonth = initialDate.get(Calendar.MONTH)
        val initialDayOfMonth = initialDate.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                    preferences
                        .edit()
                        .putLong(CHOSEN_DATE_KEY, timeInMillis)
                        .apply()
                    setDate()
                }
            }, initialYear,
            initialMonth,
            initialDayOfMonth
        ).show()
    }

    //set todays date on launch
    private fun setDate() {
        if (preferences.contains(CHOSEN_DATE_KEY)) {
            val timeInMillis = preferences.getLong(CHOSEN_DATE_KEY, -1)
            with(Date(timeInMillis)) {
                val dateFormat = android.text.format.DateFormat.getDateFormat(this@TeamsActivity)
                _binding.tvDate.text = dateFormat.format(this)
            }
        }
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