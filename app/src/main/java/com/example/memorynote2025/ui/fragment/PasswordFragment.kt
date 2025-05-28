package com.example.memorynote2025.ui.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.memorynote2025.R
import com.example.memorynote2025.constants.Constants
import com.example.memorynote2025.databinding.FragmentPasswordBinding
import com.example.memorynote2025.room.memo.Memo
import com.example.memorynote2025.utils.VibrateUtil
import com.example.memorynote2025.viewmodel.MemoViewModel
import com.example.memorynote2025.viewmodel.PasswordViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PasswordFragment : Fragment() {
    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!
    private val passwordViewModel: PasswordViewModel by viewModels()
    private val memoViewModel: MemoViewModel by viewModels()

    private lateinit var dots: List<View>

    private var password = StringBuilder()
    private var storedPassword: String? = null

    private var isLocked = false

    private val memo: Memo by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(Constants.MEMO, Memo::class.java)!!
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
    }
    // 비밀번호 확인 후 삭제 = true, 아니면 false (기본값)
    private val isLockDelete: Boolean by lazy {
        arguments?.getBoolean(Constants.LOCK_DELETE_MODE, false) ?: false
    }

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
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun onPwKeyPressed(number: String) {
        if (isLocked || password.length >= 4) return

        password.append(number)
        updateDots()

        if (password.length == 4) {
            isLocked = true

            lifecycleScope.launch {
                delay(500)
                confirmPassword()
            }
        }
    }

    private fun confirmPassword() {
        passwordViewModel.getPassword { savedPassword ->
            storedPassword = savedPassword?.password
            val isCorrect = password.toString() == storedPassword // 비밀번호 일치 여부

            if (isCorrect) { // 비밀번호 일치
                handleCorrectPw()
            } else { // 비밀번호 불일치
                handleIncorrectPw()
            }
            password.clear()
            updateDots()
            isLocked = false
        }
    }

    private fun handleCorrectPw() {
        val unlockMode = arguments?.getBoolean(Constants.LOCK_MODE, false) ?: false

        when {
            isLockDelete -> {
                memoViewModel.deleteMemo(memo)
                requireActivity().supportFragmentManager.popBackStack()
            }
            unlockMode -> { // 잠긴 상태 -> 잠금 해제 클릭 -> 잠금 해제
                memoViewModel.updateMemo(memo.copy(isLocked = false))
                requireActivity().supportFragmentManager.popBackStack()
            }
            memo.isLocked -> { // 잠긴 메모 보기 -> MemoFragment로 이동
                val memoFragment = MemoFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(Constants.MEMO, memo)
                    }
                }
                parentFragmentManager.apply {
                    popBackStack() // PasswordFragment를 백 스택에서 제거
                    beginTransaction()
                        .replace(R.id.container, memoFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
            else -> { // 잠기지 않은 상태 -> 잠금 클릭 -> 잠금
                memoViewModel.updateMemo(memo.copy(isLocked = true))
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun handleIncorrectPw() {
        VibrateUtil.vibrate(requireContext())
        binding.tvPwTitle.text = getString(R.string.password_reenter)
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
}