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
import com.example.memorynote2025.constants.PopupAction
import com.example.memorynote2025.databinding.FragmentListBinding
import com.example.memorynote2025.room.memo.Memo
import com.example.memorynote2025.ui.activity.MainActivity
import com.example.memorynote2025.utils.ToastUtil
import com.example.memorynote2025.viewmodel.MemoViewModel
import com.example.memorynote2025.viewmodel.PasswordViewModel

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null // nullable
    private val binding get() = _binding!! // non-null, 항상 null-safe한 접근 가능
    // 프래그먼트 생명주기에 맞춰 ViewModel 생성
    private val memoViewModel: MemoViewModel by viewModels()
    private val passwordViewModel: PasswordViewModel by viewModels()
    private lateinit var memoAdapter: MemoAdapter

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
            // 어댑터 생성 및 아이템 클릭 동작 정의
            memoAdapter = MemoAdapter(
                onItemClick = { memo ->
                    searchView.setQuery("", false) // 검색어 초기화

                    if (memo.isLocked) { // 메모가 잠겨 있으면 -> PasswordFragment로 이동
                        val passwordFragment = PasswordFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable(Constants.MEMO, memo)
                            }
                        }
                        replaceIfNew(passwordFragment)
                    } else { // 메모가 잠겨 있지 않으면 -> 바로 MemoFragment로 이동
                        val memoFragment = MemoFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable(Constants.MEMO, memo)
                            }
                        }
                        replaceIfNew(memoFragment)
                    }
                },
                onItemLongClick = { memo, action ->
                    when (action) {
                        PopupAction.DELETE ->
                            showDeleteDialog(listOf(memo), isMultiDelete = false)
                        PopupAction.LOCK -> {
                            passwordViewModel.getPassword { savedPassword ->
                                if (savedPassword == null) {
                                    ToastUtil.showToast(requireContext(), getString(R.string.password_required))
                                } else {
                                    val passwordFragment = PasswordFragment().apply {
                                        arguments = Bundle().apply {
                                            putParcelable(Constants.MEMO, memo)
                                            putBoolean(Constants.LOCK_MODE, memo.isLocked)
                                        }
                                    }
                                    replaceIfNew(passwordFragment)
                                }
                            }
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
                replaceIfNew(MemoFragment())
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
                            if (newText.isNullOrEmpty()) { // 검색어가 없으면 (검색어 유무 체크)
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

    private fun showDeleteDialog(memos: List<Memo>, isMultiDelete: Boolean) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.delete_dialog))
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                memos.forEach { memo ->
                    if (memo.isLocked) {
                        val passwordFragment = PasswordFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable(Constants.MEMO, memo)
                                putBoolean(Constants.LOCK_DELETE_MODE, true)
                            }
                        }
                        replaceIfNew(passwordFragment)
                    } else {
                        memoViewModel.deleteMemo(memo)
                    }
                }
                if (isMultiDelete) {
                    memoAdapter.isMultiSelect = false
                    (requireActivity() as? MainActivity)?.toggleMenuVisibility(R.id.cancel)
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel),null)
            .show()
    }

    fun setMultiSelect(isMultiSelect: Boolean) {
        memoAdapter.isMultiSelect = isMultiSelect
    }

    fun toggleSelectAll() {
        (binding.recyclerView.adapter as? MemoAdapter)?.toggleSelectAll()
    }

    fun deleteSelectedMemos() {
        val selectedMemos = memoAdapter.getSelectedMemos()

        if (selectedMemos.isEmpty()) {
            ToastUtil.showToast(requireContext(), getString(R.string.selected_delete))
        } else {
            showDeleteDialog(selectedMemos, isMultiDelete = true)
        }
    }

    // 프래그먼트 중복 실행 방지
    private fun replaceIfNew(newFragment: Fragment) {
        val currentFragment = parentFragmentManager.findFragmentById(R.id.container)
        if (currentFragment?.javaClass != newFragment.javaClass) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().invalidateOptionsMenu() // 옵션 메뉴 업데이트
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null // 메모리 누수 방지
    }
}