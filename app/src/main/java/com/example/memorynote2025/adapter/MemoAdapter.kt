package com.example.memorynote2025.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memorynote2025.R
import com.example.memorynote2025.constants.PopupAction
import com.example.memorynote2025.databinding.ItemMemoBinding
import com.example.memorynote2025.room.memo.Memo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 아이템 클릭 동작을 외부에서 전달 받음
class MemoAdapter(private val onItemClick: (Memo) -> Unit,
                  private val onItemLongClick: (Memo, PopupAction) -> Unit) :
    ListAdapter<Memo, MemoAdapter.MemoViewHolder>(DIFF_CALLBACK) {

    private var memoList: List<Memo> = emptyList() // 원본 리스트
    private var selectedMemos = mutableSetOf<Int>() // 선택된 메모

    var isMultiSelect: Boolean = false
        // isMultiSelect에 값이 할당될 때마다 자동으로 호출
        set(value) {
            field = value // 내부 백킹 필드에 저장
            if (!value) selectedMemos.clear() // 다중 선택 모드 해제 시 선택 상태 초기화
            notifyDataSetChanged() // 전체 아이템 갱신
        }

    fun toggleSelectAll() {
        // 현재 선택된 메모 수 = 전체 메모 수 -> 전체 선택 해제
        if (selectedMemos.size == currentList.size) {
            selectedMemos.clear()
        } else { // 전체 선택
            selectedMemos.apply {
                clear()
                addAll(currentList.indices)
            }
        }
        notifyDataSetChanged()
    }

    // 선택된 인덱스에서 유효한 Memo 객체만 추출하여 리스트로 반환
    fun getSelectedMemos(): List<Memo> {
        return selectedMemos.mapNotNull { index ->
            currentList.getOrNull(index)
        }
    }

    inner class MemoViewHolder(private val binding: ItemMemoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(memo: Memo) {
            binding.apply {
                // Memo 데이터를 각 뷰에 할당
                tvContent.text = memo.content
                tvDate.text = SimpleDateFormat(itemView.context.getString(R.string.date_format),
                    Locale.getDefault()).format(Date(memo.date))
                ivLock.visibility = if (memo.isLocked) View.VISIBLE else View.INVISIBLE

                // adapterPosition이 -1일 수 있기 때문에 예외 처리 (바인딩 중이거나 뷰 재활용 시)
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    checkBox.apply {
                        visibility = if (isMultiSelect) View.VISIBLE else View.GONE
                        isChecked = adapterPosition in selectedMemos // 선택된 항목이면 체크

                        // 체크 상태 변경 시 selectedMemos에 추가 또는 제거
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                selectedMemos.add(adapterPosition)
                            } else {
                                selectedMemos.remove(adapterPosition)
                            }
                        }
                    }
                }
                root.apply {
                    setOnClickListener {
                        onItemClick(memo)
                    }
                    setOnLongClickListener {
                        showPopupMenu(it, memo)
                        true
                    }
                }
            }
        }
        private fun showPopupMenu(view: View, memo: Memo) {
            PopupMenu(view.context, view).apply {
                menuInflater.inflate(R.menu.item_popup_menu, menu)

                // 메모 잠금 상태에 따른 잠금 메뉴 텍스트 동적 변경
                val lockMenuItem = menu.findItem(R.id.lock)
                lockMenuItem.title = if (memo.isLocked) {
                    view.context.getString(R.string.unlock)
                } else {
                    view.context.getString(R.string.lock)
                }
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete -> {
                            onItemLongClick(memo, PopupAction.DELETE)
                            true
                        }
                        R.id.lock -> {
                            onItemLongClick(memo, PopupAction.LOCK)
                            true
                        }
                        else -> false
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setForceShowIcon(true) // 아이콘 강제 표시
                }
                show()
            }
        }
    }

    companion object { // 클래스 내부에서 객체 없이 접근 가능한 정적 멤버
        // RecyclerView 성능 최적화를 위해 변경 사항만 업데이트
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Memo>() {
            override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
                return oldItem == newItem
            }
        }
    }

    // ViewHolder 생성 (레이아웃을 뷰 객체로 변환 -> MemoViewHolder에 전달하여 반환)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = ItemMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoViewHolder(binding)
    }

    // ViewHolder에 데이터를 바인딩하여 UI 업데이트
    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        // 현재 위치의 Memo 객체를 가져와서 ViewHolder에 전달
        holder.bind(getItem(position))
    }

    // 원본 리스트 초기화 후 어댑터에 제출
    fun submitMemoList(list: List<Memo>) {
        memoList = list
        submitList(list)
    }

    fun filterList(searchText: String, onFilterComplete: () -> Unit) {
        val filteredList = if (searchText.isEmpty()) { // 비어 있으면
            memoList // 원본 리스트
        } else { // 비어있지 않으면
            memoList.filter { // 필터링
                it.content.contains(searchText, ignoreCase = true) // 대소문자 구분 없이 검색
            }
        }
        submitList(filteredList) {
            onFilterComplete() // 필터링 후속 작업
        }
    }
}