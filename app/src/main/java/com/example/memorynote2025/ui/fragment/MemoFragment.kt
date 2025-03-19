package com.example.memorynote2025.ui.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import com.example.memorynote2025.constants.Constants
import com.example.memorynote2025.databinding.FragmentMemoBinding
import com.example.memorynote2025.room.memo.Memo
import com.example.memorynote2025.ui.activity.MainActivity
import com.example.memorynote2025.viewmodel.MemoViewModel

class MemoFragment : Fragment() {
    private var _binding: FragmentMemoBinding? = null
    private val binding get() = _binding!!
    private val memoViewModel: MemoViewModel by viewModels()
    private var previousMemo: Memo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // MainActivity 툴바에 업 버튼 활성화
        (activity as? MainActivity)?.showUpButton(true)

        previousMemo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(Constants.MEMO, Memo::class.java)
        } else {
            @Suppress("DEPRECATION") // API 33 미만 호환성 처리
            arguments?.getParcelable(Constants.MEMO)
        }
        if (previousMemo != null) {
            binding.edtMemo.setText(previousMemo!!.content)
        }
    }

    override fun onPause() {
        super.onPause()

        val currentMemo = binding.edtMemo.text.toString()

        // 메모가 비어있지 않고, 이전 메모와 다를 경우에만 처리
        if (currentMemo.isNotBlank() && currentMemo != previousMemo?.content) {
            if (previousMemo != null) { // 수정 모드
                updateMemo(currentMemo)
            } else { // 추가 모드
                insertMemo(currentMemo)
            }
        }
    }

    private fun insertMemo(currentMemo: String) {
        val date = System.currentTimeMillis()
        val memo = Memo(content = currentMemo, date  = date)
        memoViewModel.insertMemo(memo)
    }

    private fun updateMemo(currentMemo: String) {
        val date = System.currentTimeMillis()
        // 기존 memo 객체의 content만 수정하여 새로운 객체 생성
        val updatedMemo = previousMemo?.copy(content = currentMemo, date = date)

        if (updatedMemo != null) {
            memoViewModel.updateMemo(updatedMemo)
        }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().invalidateOptionsMenu() // 옵션 메뉴 업데이트

        binding.apply {
            edtMemo.requestFocus()
            // requireContext : null을 반환하지 않는 Context를 보장
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edtMemo, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as? MainActivity)?.showUpButton(false)
        _binding = null
    }
}