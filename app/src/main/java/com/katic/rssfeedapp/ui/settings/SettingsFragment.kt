package com.katic.rssfeedapp.ui.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.*
import com.katic.rssfeedapp.R
import com.katic.rssfeedapp.appComponent
import com.katic.rssfeedapp.data.AppPreferences
import com.katic.rssfeedapp.data.RefreshFeedWorker

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var preferences: AppPreferences
    private lateinit var enableFeedRefreshPref: CheckBoxPreference
    private lateinit var feedRefreshPeriodPref: EditTextPreference

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setPreferencesFromResource(R.xml.settings, s)

        preferences = appComponent.appPreferences
        enableFeedRefreshPref = findPreference(getString(R.string.pref_key_enable_feed_refresh))!!
        feedRefreshPeriodPref = findPreference(getString(R.string.pref_key_feed_refresh_period))!!

        enableFeedRefreshPref.setOnPreferenceChangeListener { preference, newValue ->
            val enable = newValue as Boolean
            if (enable) {
                RefreshFeedWorker.initialize(
                    preference.context,
                    preferences.feedRefreshPeriod
                )
            } else {
                RefreshFeedWorker.cancel(preference.context)
            }
            true
        }

        feedRefreshPeriodPref.apply {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            setOnPreferenceChangeListener { preference, newValue ->
                val value = (newValue as String).toLong()
                preference.summary = newValue
                RefreshFeedWorker.initialize(requireContext(), value)
                true
            }
            summary = preferences.feedRefreshPeriod.toString()
        }
    }
}