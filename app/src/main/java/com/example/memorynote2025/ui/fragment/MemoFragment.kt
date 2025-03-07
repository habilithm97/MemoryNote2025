package com.example.memorynote2025.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import com.example.memorynote2025.databinding.FragmentMemoBinding
import com.example.memorynote2025.room.memo.Memo
import com.example.memorynote2025.ui.activity.MainActivity
import com.example.memorynote2025.viewmodel.MemoViewModel

class MemoFragment : Fragment() {
    private var _binding: FragmentMemoBinding? = null
    private val binding get() = _binding!!
    private val memoViewModel: MemoViewModel by viewModels()

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
    }

    override fun onPause() {
        super.onPause()

        val memoStr = binding.edtMemo.text.toString()
        if (memoStr.isNotBlank()) {
            saveMemo(memoStr)
        }
    }

    private fun saveMemo(memoStr: String) {
        val date = System.currentTimeMillis()
        val memo = Memo(content = memoStr, date  = date)
        memoViewModel.insertMemo(memo)
    }

    override fun onResume() {
        super.onResume()

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