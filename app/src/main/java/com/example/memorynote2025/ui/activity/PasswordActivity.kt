package com.example.memorynote2025.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.memorynote2025.constants.PasswordString
import com.example.memorynote2025.databinding.ActivityPasswordBinding
import com.example.memorynote2025.room.password.Password
import com.example.memorynote2025.utils.ToastUtil
import com.example.memorynote2025.utils.VibrateUtil
import com.example.memorynote2025.viewmodel.PasswordViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PasswordActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPasswordBinding.inflate(layoutInflater) }
    private val passwordViewModel: PasswordViewModel by viewModels()

    private lateinit var dots: List<View>

    private var password = StringBuilder()
    private var confirmingPassword = StringBuilder() // 임시 저장 비밀번호
    private var storedPassword: String? = null // 저장된 비밀번호

    private var isLocked = false // 비밀번호 입력 잠금 상태
    private var isConfirming = false // 비밀번호 확인 상태
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
            val buttons = listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9)
            for (btn in buttons) {
                btn.setOnClickListener {
                    onPwKeyPressed(btn.text.toString())
                }
            }
            dots = listOf(dot1, dot2, dot3, dot4)

            btnCancel.setOnClickListener {
                if (password.isNotEmpty()) {
                    password.deleteAt(password.length - 1)
                    updateDots()
                } else {
                    finish()
                }
            }
            passwordViewModel.getPassword { savedPassword ->
                storedPassword = savedPassword?.password

                tvPwTitle.text = if (storedPassword == null) {
                    getString(PasswordString.NEW.resId)
                } else {
                    getString(PasswordString.ENTER.resId)
                }
            }
        }
    }

    private fun onPwKeyPressed(number: String) {
        // 입력 처리 중이거나 4자리 이상이면 추가 입력 제한
        if (isLocked || password.length >= 4) return

        password.append(number)
        updateDots()

        // 4자리 입력 완료
        if (password.length == 4) {
            isLocked = true // 입력 잠금

            // lifecycleScope : 생명주기 자동 관리 -> Handler보다 안전/간결
            lifecycleScope.launch {
                delay(500)

                when {
                    // 저장된 비밀번호가 없으면 -> 새 비밀번호 설정
                    storedPassword == null -> {
                        if (!isConfirming) { // 첫 번째 입력
                            confirmingPassword.clear().append(password)
                            isConfirming = true
                            binding.tvPwTitle.text = getString(PasswordString.CONFIRM.resId)
                            password.clear()
                        } else { // 두 번째 입력
                            confirmPassword()
                        }
                    }
                    // 저장된 비밀번호 o, 변경 모드 x
                    !isChanging -> {
                        confirmExistPassword()
                    }
                    // 저장된 비밀번호 o, 변경 모드 o
                    else -> {
                        if (!isConfirming) {
                            confirmingPassword.clear().append(password)
                            isConfirming = true
                            binding.tvPwTitle.text = getString(PasswordString.CONFIRM.resId)
                            password.clear()
                        } else {
                            confirmPassword()
                        }
                    }
                }
                updateDots()
                isLocked = false
            }
        }
    }

    private fun updateDots() {
        for (i in dots.indices) {
            // 현재 입력된 비밀번호 개수보다 작은 인덱스만 활성화
            dots[i].isSelected = i < password.length
        }
    }

    private fun confirmPassword() {
        if (password.toString() == confirmingPassword.toString()) {
            savePassword(password.toString())
        } else {
            VibrateUtil.vibrate(this@PasswordActivity)
            binding.tvPwTitle.text = getString(PasswordString.REENTER.resId)
            clearPassword()
        }
    }

    private fun confirmExistPassword() {
        if (password.toString() == storedPassword) {
            isChanging = true
            binding.tvPwTitle.text = getString(PasswordString.CHANGE.resId)
            password.clear()
        } else {
            VibrateUtil.vibrate(this@PasswordActivity)
            binding.tvPwTitle.text = getString(PasswordString.REENTER.resId)
            clearPassword()
        }
    }

    private fun savePassword(pw: String) {
        passwordViewModel.apply {
            getPassword { savedPassword ->
                if (savedPassword == null) { // 비밀번호 없으면 생성
                    val password = Password(password = pw)
                    insertPassword(password)
                } else { // 비밀번호 있으면 수정
                    val updatedPassword = savedPassword.copy(password = pw)
                    updatePassword(updatedPassword)
                }
                lifecycleScope.launch {
                    delay(500)
                    ToastUtil.showToast(this@PasswordActivity, getString(PasswordString.SETUP_COMPLETE.resId))
                    finish()
                }
            }
        }
    }

    private fun clearPassword() {
        isLocked = false // 비밀번호 입력 잠금 해제 (재입력 가능 상태로 전환)
        isConfirming = false // 비밀번호 확인 상태 초기화 (새로운 흐름 시작 가능)
        password.clear()
        confirmingPassword.clear()
        updateDots()
    }
}