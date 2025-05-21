package com.example.memorynote2025.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.memorynote2025.R
import com.example.memorynote2025.databinding.ActivitySettingsBinding
import com.example.memorynote2025.ui.fragment.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun initView() {
        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = getString(R.string.settings)
            }
            toolbar.setNavigationOnClickListener {
                finish()
            }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, SettingsFragment())
            .commit()
    }
}