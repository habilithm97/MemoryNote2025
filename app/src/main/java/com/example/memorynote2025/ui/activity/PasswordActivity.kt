package com.example.memorynote2025.ui.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.memorynote2025.R
import com.example.memorynote2025.databinding.ActivityPasswordBinding
import com.example.memorynote2025.room.password.Password
import com.example.memorynote2025.utils.ToastUtil
import com.example.memorynote2025.viewmodel.PasswordViewModel

class PasswordActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPasswordBinding.inflate(layoutInflater) }
    private val passwordViewModel: PasswordViewModel by viewModels()

    private lateinit var dots: List<View>

    private var password = ""
    private var confirmPassword = "" // 확인용 비밀번호
    private var storedPassword: String? = null // 저장된 비밀번호

    private var isLocked = false // 비밀번호 입력 잠금 여부
    private var isConfirming = false // 비밀번호 확인 진행 여부
    private var isChanging = false // 비밀번호 변경 모드 여부

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
            passwordViewModel.getPassword { savedPw ->
                storedPassword = savedPw?.password

                tvPwTitle.text = if (storedPassword == null) {
                    getString(R.string.password_new)
                } else {
                    getString(R.string.password_enter)
                }
            }
        }
    }

    private fun onNumberClick(number: String) {
        // 4자리 이상이면 추가 입력 제한
        if (password.length >= 4) return

        password += number
        updateDots()

        // 4자리 입력 완료 시 처리
        if (password.length == 4) {
            isLocked = true // 입력 잠금

            Handler(Looper.getMainLooper()).postDelayed({
                if (storedPassword == null) { // 저장된 비밀번호가 없는 경우 -> 새 비밀번호 등록 진행
                    if (!isConfirming) { // 첫 번째 입력일 경우
                        confirmPassword = password // 저장
                        isConfirming = true // 확인중으로 변경
                        binding.tvPwTitle.text = getString(R.string.password_confirm)
                        password = ""
                    } else { // 두 번째 입력일 경우
                        checkPassword()
                    }
                } else if (!isChanging) { // 저장된 비밀번호가 있는 경우
                    checkExistPassword() // 기존 비밀번호 확인
                } else { // 비밀번호 변경 모드
                    if (!isConfirming) {
                        confirmPassword = password
                        isConfirming = true
                        binding.tvPwTitle.text = getString(R.string.password_confirm)
                        password = ""
                    } else {
                        checkPassword()
                    }
                }
                updateDots()
                isLocked = false // 입력 잠금 해제
            }, 500)
        }
    }

    private fun updateDots() {
        for (i in dots.indices) { // dots의 모든 인덱스를 순회
            // 현재 입력된 비밀번호 개수보다 작은 인덱스만 활성화
            dots[i].isSelected = i < password.length
        }
    }

    private fun checkPassword() {
        if (password == confirmPassword) {
            savePassword(password)
        } else {
            vibrate()
            binding.tvPwTitle.text = getString(R.string.password_reenter)
            clearPassword()
        }
    }

    private fun checkExistPassword() {
        if (password == storedPassword) {
            binding.tvPwTitle.text = getString(R.string.password_change)
            password = ""
            isChanging = true
        } else {
            vibrate()
            binding.tvPwTitle.text = getString(R.string.password_reenter)
            clearPassword()
        }
    }

    private fun savePassword(pw: String) {
        val password = Password(password = pw)
        passwordViewModel.insertPassword(password)
        Handler(Looper.getMainLooper()).postDelayed({
            ToastUtil.showToast(this@PasswordActivity, getString(R.string.password_setup_complete))
            finish()
        }, 500)
    }

    private fun clearPassword() {
        isLocked = false // 입력 잠금 해제
        isConfirming = false
        password = ""
        confirmPassword = ""
        updateDots()
    }

    private fun vibrate() {
        // Vibrator 객체 가져오기
        // API 31 이상 (VibratorManager 사용)
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION") // 경고 메시지 무시
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        // 진동 실행
        // API 26 이상 (VibrationEffect 사용)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }
}