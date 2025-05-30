package com.example.memorynote2025.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.memorynote2025.R
import com.example.memorynote2025.constants.Constants
import com.example.memorynote2025.ui.activity.LoginActivity
import com.example.memorynote2025.ui.activity.PasswordActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val loginPref = findPreference<Preference>(Constants.LOGIN_INFO)
        loginPref?.setOnPreferenceClickListener {
            context?.let {
                val intent = Intent(it, LoginActivity::class.java)
                startActivity(intent)
            }
            true
        }
        val syncPref = findPreference<SwitchPreferenceCompat>(Constants.SYNC)
        //val isLogin = checkLoginStatus() // 로그인 상태 확인
        val isLogin = false
        syncPref?.isEnabled = isLogin

        val pwPref = findPreference<Preference>(Constants.PW_SETTINGS)
        pwPref?.setOnPreferenceClickListener {
            context?.let {
                val intent = Intent(it, PasswordActivity::class.java)
                startActivity(intent)
            }
            true
        }
    }
}