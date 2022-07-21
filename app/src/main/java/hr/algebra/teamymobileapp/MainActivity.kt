package hr.algebra.teamymobileapp

import android.app.DatePickerDialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.android.volley.RequestQueue
import hr.algebra.teamymobileapp.databinding.ActivityMainBinding
import hr.algebra.teamymobileapp.framework.*
import java.util.*

private const val CHOSEN_DATE_KEY = "hr.algebra.MainActivity.chosendatekey"

class MainActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    var volleyRequestQueue: RequestQueue? = null
    val TAG = "TeamListActivity"
    private lateinit var _binding: ActivityMainBinding
    lateinit var dataHelper: DataHelper
    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)

        // login for test
        preferences.edit().apply {
            this.putString(LOGIN_KEY_UID, "andro@mail.com")
            this.putInt(LOGIN_KEY_ID, 13)
            apply()
        }

        //timer
        dataHelper = DataHelper(applicationContext)
        _binding.startStopButton.setOnClickListener { startStopAction() }
        _binding.saveButton.setOnClickListener { saveAction() }
        _binding.saveButton.setOnClickListener { submitAction() }

        if (dataHelper.timerCounting()) {
            startTimer()
        } else {
            stopTimer()
            if (dataHelper.startTime() != null && dataHelper.stopTime() != null) {
                val time = Date().time - calcRestartTime().time
                _binding.tvTime.text = timeStringFromLong(time)
            }
        }
        timer.scheduleAtFixedRate(TimeTask(), 0, 500)
    }



    private fun submitAction() {
        // TODO: send data to server, api for that not yet implemented
    }

    private fun saveAction() {
        // TODO: save in table, if we have still some time
    }

    private inner class TimeTask : TimerTask() {
        override fun run() {
            if (dataHelper.timerCounting()) {
                val time = Date().time - dataHelper.startTime()!!.time
                _binding.tvTime.text = timeStringFromLong(time)
            }
        }
    }


    private fun stopTimer() {
        dataHelper.setTimerCounting(false)
        _binding.startStopButton.text = getString(R.string.start_time)
    }

    private fun startTimer() {
        dataHelper.setTimerCounting(true)
        _binding.startStopButton.text = getString(R.string.stop)
    }

    private fun startStopAction() {
        if (dataHelper.timerCounting()) {
            dataHelper.setStopTime(Date())
            stopTimer()
        } else {
            if (dataHelper.stopTime() != null) {
                dataHelper.setStartTime(calcRestartTime())
                dataHelper.setStopTime(null)
            } else {
                dataHelper.setStartTime(Date())
            }
            startTimer()
        }
    }

    private fun calcRestartTime(): Date {
        val diff = dataHelper.startTime()!!.time - dataHelper.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }

    private fun timeStringFromLong(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60) % 60)
        val hours = (ms / (1000 * 60 * 60) % 24)
        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    override fun onStart() {
        super.onStart()
        // go to login if not logged in
        if (!preferences.contains(LOGIN_KEY_UID)) {
            showToast("Redirecting to login...")
            goToLogin()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
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
            R.id.miTeams->{
                goToTeamsActivity()
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
                val dateFormat = android.text.format.DateFormat.getDateFormat(this@MainActivity)
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
