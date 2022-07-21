package hr.algebra.teamymobileapp

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import hr.algebra.teamymobileapp.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }
}