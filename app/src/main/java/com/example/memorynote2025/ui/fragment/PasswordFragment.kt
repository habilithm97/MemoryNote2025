package com.example.memorynote2025.ui.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.memorynote2025.R
import com.example.memorynote2025.constants.Constants
import com.example.memorynote2025.databinding.FragmentPasswordBinding
import com.example.memorynote2025.room.memo.Memo
import com.example.memorynote2025.viewmodel.PasswordViewModel

class PasswordFragment : Fragment() {
    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!
    private val passwordViewModel: PasswordViewModel by viewModels()

    private lateinit var dots: List<View>

    private var password = ""
    private var storedPassword: String? = null // 저장된 비밀번호

    private var isLocked = false // 비밀번호 입력 잠금 여부

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9).forEach { btn ->
                btn.setOnClickListener { onNumberClick(btn.text.toString()) }
            }
            dots = listOf(dot1, dot2, dot3, dot4)
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
                checkPassword()
            }, 500)
        }
    }

    private fun checkPassword() {
        passwordViewModel.getPassword { savedPw ->
            storedPassword = savedPw?.password
            if (storedPassword == password) { // 일치
                requireActivity().supportFragmentManager.popBackStack()
            } else { // 불일치
                vibrate()
                binding.tvPwTitle.text = getString(R.string.password_reenter)
            }
            password = ""
            updateDots()
            isLocked = false
        }
    }

    private fun updateDots() {
        for (i in dots.indices) { // dots의 모든 인덱스를 순회
            // 현재 입력된 비밀번호 개수보다 작은 인덱스만 활성화
            dots[i].isSelected = i < password.length
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun vibrate() {
        // Vibrator 객체 가져오기
        // API 31 이상 (VibratorManager 사용)
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION") // 경고 메시지 무시
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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