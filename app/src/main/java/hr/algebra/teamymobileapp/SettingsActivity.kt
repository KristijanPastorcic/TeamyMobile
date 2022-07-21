package hr.algebra.teamymobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hr.algebra.teamymobileapp.databinding.ActivitySettinggsBinding
import hr.algebra.teamymobileapp.framework.goToMain


class SettingsActivity : AppCompatActivity() {

    lateinit var b: ActivitySettinggsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettinggsBinding.inflate(layoutInflater)
        setContentView(b.root)
    }

    override fun onBackPressed() {
        goToMain()
    }
}