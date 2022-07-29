package com.katic.rssfeedapp.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.katic.rssfeedapp.R
import com.katic.rssfeedapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewBinder: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinder = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBinder.root)

        supportActionBar?.title = getString(R.string.menu_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentFrame, SettingsFragment())
            .commit()
    }

}