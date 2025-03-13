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
import com.example.memorynote2025.databinding.ItemMemoBinding
import com.example.memorynote2025.room.memo.Memo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 아이템 실행 동작을 외부에서 전달 받음
class MemoAdapter(private val onItemClick: (Memo) -> Unit,
                  private val onItemLongClick: (Memo) -> Unit) :
    ListAdapter<Memo, MemoAdapter.MemoViewHolder>(DIFF_CALLBACK) {

    private var memoList: List<Memo> = emptyList() // 전체 데이터 저장

    fun submitMemoList(list: List<Memo>) {
        memoList = list
        submitList(list)
    }

    fun filterList(searchText: String, onFilterComplete: () -> Unit) {
        val filteredList = if (searchText.isEmpty()) {
            memoList
        } else {
            memoList.filter {
                it.content.contains(searchText, ignoreCase = true) // 대소문자 구분 없이 검색
            }
        }
        submitList(filteredList) {
            onFilterComplete() // 필터링 후속 작업
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

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete -> {
                            onItemLongClick(memo)
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

    companion object {
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
        // 현재 위치에 해당하는 Memo 객체를 가져와서 ViewHolder에 전달
        holder.bind(getItem(position))
    }
}