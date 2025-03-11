package com.example.memorynote2025.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorynote2025.R
import com.example.memorynote2025.adapter.MemoAdapter
import com.example.memorynote2025.constants.Constants
import com.example.memorynote2025.databinding.FragmentListBinding
import com.example.memorynote2025.room.memo.Memo
import com.example.memorynote2025.viewmodel.MemoViewModel

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null // nullable
    private val binding get() = _binding!! // non-null, 항상 null-safe한 접근 가능
    private val memoViewModel: MemoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val memoAdapter = MemoAdapter(
            onItemClick = { memo ->
            val memoFragment = MemoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.MEMO, memo)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, memoFragment)
                .addToBackStack(null)
                .commit()
        },
            onItemLongClick = { memo ->
                showDeleteDialog(memo)
            })
        binding.apply {
            recyclerView.apply {
                adapter = memoAdapter
                layoutManager = LinearLayoutManager(requireContext()).apply {
                    reverseLayout = true
                    stackFromEnd = true
                }
                setHasFixedSize(true) // 아이템 크기 고정 -> 성능 최적화
            }
            fab.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, MemoFragment())
                    .addToBackStack(null) // 백 스택에 추가
                    .commit()
            }
            memoViewModel.getAll.observe(viewLifecycleOwner) {
                // 어댑터에 새로운 리스트 제출
                memoAdapter.apply {
                    submitList(it) {
                        if (itemCount > 0) {
                            recyclerView.smoothScrollToPosition(itemCount - 1)
                        }
                    }
                }
            }
        }
    }

    private fun showDeleteDialog(memo: Memo) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.delete_dialog))
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                memoViewModel.deleteMemo(memo)
                dialog.dismiss()
            }
            .setNegativeButton("취소",null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null // 메모리 릭 방지
    }
}