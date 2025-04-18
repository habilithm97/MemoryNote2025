package com.example.memorynote2025.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
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

        binding.apply {
            // 어댑터 생성 및 아이템 동작 정의
            val memoAdapter = MemoAdapter(
                onItemClick = { memo ->
                    searchView.setQuery("", false) // 검색어 초기화

                    if (memo.isLocked) { // 메모가 잠겨있는 경우 -> PasswordFragment로 이동
                        val passwordFragment = PasswordFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable(Constants.MEMO, memo)
                            }
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.container, passwordFragment)
                            .addToBackStack(null)
                            .commit()
                    } else { // 메모가 잠겨있지 않은 경우 -> 바로 MemoFragment로 이동
                        val memoFragment = MemoFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable(Constants.MEMO, memo)
                            }
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.container, memoFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                },
                onItemLongClick = { memo, action ->
                    when (action) {
                        MemoAdapter.Action.DELETE ->
                            showDeleteDialog(memo)
                        MemoAdapter.Action.LOCK -> {
                            val passwordFragment = PasswordFragment().apply {
                                arguments = Bundle().apply {
                                    putParcelable(Constants.MEMO, memo)
                                    putBoolean(Constants.UNLOCK_MODE, memo.isLocked)
                                }
                            }
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.container, passwordFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }
            )
            recyclerView.apply {
                adapter = memoAdapter
                layoutManager = LinearLayoutManager(requireContext()).apply {
                    reverseLayout = true
                    stackFromEnd = true
                }
                setHasFixedSize(true) // 아이템 크기 고정 -> 성능 최적화
            }
            fab.setOnClickListener {
                searchView.setQuery("", false) // 검색어 초기화

                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, MemoFragment())
                    .addToBackStack(null) // 백 스택에 추가
                    .commit()
            }
            memoViewModel.getAll.observe(viewLifecycleOwner) {
                memoAdapter.apply {
                    submitMemoList(it)
                    if (itemCount > 0) {
                        recyclerView.smoothScrollToPosition(itemCount - 1)
                    }
                }
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                // 검색어 입력 시 호출
                override fun onQueryTextChange(newText: String?): Boolean {
                    memoAdapter.apply {
                        filterList(newText.orEmpty()) { // null이면 "" 사용 (null 방지)
                            if (newText.isNullOrEmpty()) { // 검색어 없을 시 (검색어 유무 체크)
                                recyclerView.apply {
                                    post {
                                        if (itemCount > 0) {
                                            smoothScrollToPosition(itemCount - 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return true
                }
                // 키보드 검색 버튼 클릭 시 호출
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
            })
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
            .setNegativeButton(getString(R.string.cancel),null)
            .show()
    }

    override fun onResume() {
        super.onResume()

        requireActivity().invalidateOptionsMenu() // 옵션 메뉴 업데이트
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null // 메모리 릭 방지
    }
}