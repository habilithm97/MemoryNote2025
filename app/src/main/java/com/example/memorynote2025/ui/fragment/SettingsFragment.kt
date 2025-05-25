package com.example.memorynote2025.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.memorynote2025.R
import com.example.memorynote2025.constants.Constants
import com.example.memorynote2025.ui.activity.PasswordActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val syncPref = findPreference<SwitchPreferenceCompat>(Constants.SYNC)
        //val isLogin = checkLoginStatus() // 로그인 상태 확인
        val isLogin = false
        syncPref?.isEnabled = isLogin

        val pwSettings = findPreference<Preference>(Constants.PW_SETTINGS)
        pwSettings?.setOnPreferenceClickListener {
            context?.let {
                val intent = Intent(it, PasswordActivity::class.java)
                startActivity(intent)
            }
            true
        }
    }
}