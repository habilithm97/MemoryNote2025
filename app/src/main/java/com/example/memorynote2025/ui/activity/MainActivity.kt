package com.example.memorynote2025.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.memorynote2025.R
import com.example.memorynote2025.databinding.ActivityMainBinding
import com.example.memorynote2025.ui.fragment.ListFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

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

        if (savedInstanceState == null) {
            replaceFragment(ListFragment())
        }
    }

    private fun initView() {
        binding.apply {
            setSupportActionBar(toolbar)
            setOnBackPressedCallback() // 백 버튼 동작
        }
    }

    private fun setOnBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 백 스택에 프래그먼트가 있으면 최상위 프래그먼트 제거
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    isEnabled = false // 현재 콜백 비활성화
                    onBackPressedDispatcher.onBackPressed() // 기본 동작 (현재 화면 제거)
                }
            }
        })
    }

    // 업 버튼 활성화 (MemoFragment에서 사용)
    fun showUpButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    // 업 버튼 동작 (MemoFragment에서 사용)
    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        return if (currentFragment is ListFragment) {
            menuInflater.inflate(R.menu.main_menu, menu)
            true
        } else {
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is ListFragment) {
            return when (item.itemId) {
                R.id.select -> {
                    toggleMenuVisibility(item.itemId)
                    fragment.setMultiSelect(true)
                    true
                }
                R.id.setting -> {
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    true
                }
                R.id.selectAll -> {
                    fragment.selectAll()
                    true
                }
                R.id.cancel -> {
                    toggleMenuVisibility(item.itemId)
                    fragment.setMultiSelect(false)
                    true
                }
                else -> super.onOptionsItemSelected(item) // 그 외 항목은 상위 클래스에게 위임
            }
        }
        return super.onOptionsItemSelected(item) // ListFragment가 아니면 기본 처리 위임
    }

    private fun toggleMenuVisibility(itemId: Int) {
        with(binding.toolbar.menu) {
            val isSelect = itemId == R.id.select

            findItem(R.id.select).isVisible = !isSelect
            findItem(R.id.setting).isVisible = !isSelect
            findItem(R.id.cancel).isVisible = isSelect
            findItem(R.id.selectAll).isVisible = isSelect
        }
    }

    override fun onResume() {
        super.onResume()

        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is ListFragment) {
            fragment.setMultiSelect(false) // 다중 선택 모드 해제
        }
    }
}