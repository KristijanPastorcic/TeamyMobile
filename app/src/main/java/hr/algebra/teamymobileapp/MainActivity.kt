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

    override fun onBackPressed() {
        goToTeamsActivity()
    }


}
