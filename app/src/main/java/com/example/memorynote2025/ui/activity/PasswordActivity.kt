package com.example.memorynote2025.ui.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.memorynote2025.databinding.ActivityPasswordBinding

class PasswordActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPasswordBinding.inflate(layoutInflater) }

    private lateinit var dots: List<View>
    private var password = ""
    private var isLocked = false // 입력 잠금 상태

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
            listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9).forEach { btn ->
                btn.setOnClickListener { onNumberClick(btn.text.toString()) }
            }
            dots = listOf(dot1, dot2, dot3, dot4)

            btnCancel.setOnClickListener {
                finish()
            }
        }
    }

    private fun onNumberClick(number: String) {
        if (password.length >= 4) return

        password += number
        updateDots()

        if (password.length == 4) {
            isLocked = true // 입력 잠금
            Handler(Looper.getMainLooper()).postDelayed({
                clearPassword()
            }, 500)
        }
    }

    private fun updateDots() {
        for (i in dots.indices) {
            dots[i].isSelected = i < password.length
        }
    }

    private fun clearPassword() {
        isLocked = false // 입력 잠금 해제
        password = ""
        updateDots()
    }
}