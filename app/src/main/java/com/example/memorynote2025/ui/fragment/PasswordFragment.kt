package com.example.memorynote2025.ui.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.memorynote2025.R
import com.example.memorynote2025.constants.Constants
import com.example.memorynote2025.databinding.FragmentPasswordBinding
import com.example.memorynote2025.room.memo.Memo
import com.example.memorynote2025.utils.VibrateUtil
import com.example.memorynote2025.viewmodel.MemoViewModel
import com.example.memorynote2025.viewmodel.PasswordViewModel

class PasswordFragment : Fragment() {
    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!
    private val passwordViewModel: PasswordViewModel by viewModels()
    private val memoViewModel: MemoViewModel by viewModels()

    private lateinit var dots: List<View>

    private var password = ""
    private var storedPassword: String? = null // 저장된 비밀번호

    private var isLocked = false // 비밀번호 입력 잠금 여부

    private val memo: Memo by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(Constants.MEMO, Memo::class.java)!!
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
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
            listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9).forEach { btn ->
                btn.setOnClickListener { onNumberClick(btn.text.toString()) }
            }
            dots = listOf(dot1, dot2, dot3, dot4)

            btnCancel.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
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
                checkPassword()
            }, 500)
        }
    }

    private fun checkPassword() {
        passwordViewModel.getPassword { savedPw ->
            storedPassword = savedPw?.password
            if (storedPassword == password) { // 비밀번호 일치
                // PasswordFragment로 넘어올 때 잠금 해제 모드인지 확인 (기본값은 false)
                val unlockMode = arguments?.getBoolean(Constants.UNLOCK_MODE, false) ?: false
                if (unlockMode) { // 잠금 해제 모드일 경우 false로 바꾸고 다시 ListFragment로 이동
                    memoViewModel.updateMemo(memo.copy(isLocked = false))
                    requireActivity().supportFragmentManager.popBackStack()
                } else { // 잠금 모드일 경우
                    if (memo.isLocked) { // 메모가 잠겨있는 경우 -> MemoFragment로 이동
                        val memoFragment = MemoFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable(Constants.MEMO, memo)
                            }
                        }
                        parentFragmentManager.apply {
                            popBackStack() // PasswordFragment 제거
                            beginTransaction()
                                .replace(R.id.container, memoFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    } else { // 메모가 잠겨있지 않은 경우 -> 이전 화면인 ListFragment로 이동
                        memoViewModel.updateMemo(memo.copy(isLocked = true))
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            } else { // 불일치
                VibrateUtil.vibrate(requireContext())
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
}