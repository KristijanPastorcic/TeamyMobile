package hr.algebra.teamymobileapp.framework


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import hr.algebra.teamymobileapp.*

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showToast(text: String) {
    with(Toast.makeText(this, text, Toast.LENGTH_SHORT)) {
        show()
    }
}

fun Activity.isEmailValid(email: String?): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()
}

fun isValidPassword(password: String?): Boolean {
    password?.let {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return passwordMatcher.find(password) != null
    } ?: return false
}

fun isExternalStorageWritable(): Boolean {
    return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
}

fun isExternalStorageReadable(): Boolean {
    return isExternalStorageWritable() ||
            Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()
}


fun View.applyAnimation(resourceId: Int) =
    startAnimation(AnimationUtils.loadAnimation(context, resourceId))

inline fun <reified T : Activity> Context.startActivity() =
    startActivity(Intent(this, T::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })

inline fun <reified T : Activity> Context.startActivity(key: String, value: Int) =
    startActivity(Intent(this, T::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(key, value)
    })

inline fun <reified T : BroadcastReceiver> Context.sendBroadcast() =
    sendBroadcast(Intent(this, T::class.java))

fun Context.setBooleanPreference(key: String, value: Boolean) {
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit()
        .putBoolean(key, value)
        .apply()
}

fun Context.getBooleanPreference(key: String): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean(key, false)
}

fun Context.isOnline(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    if (network != null) {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        if (networkCapabilities != null) {
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
    }
    return false
}

fun Activity.goToLogin() {
    startActivity<LoginActivity>()
}

fun Activity.goToRegister() {
    startActivity<RegisterActivity>()
}

fun Activity.goToMain() {
    startActivity<MainActivity>()
}

fun Activity.goToTeamsActivity() {
    startActivity<TeamsActivity>()
}

fun Activity.goToJoinTeamActivity() {
    startActivity<JoinTeamActivity>()
}

fun Activity.goToInvitesActivity() {
    startActivity<InvitesActivity>()
}

fun Activity.showSettings() {
    startActivity<SettingsActivity>()
}

