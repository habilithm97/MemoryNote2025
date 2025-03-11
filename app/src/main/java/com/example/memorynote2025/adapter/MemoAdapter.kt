package com.example.memorynote2025.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memorynote2025.R
import com.example.memorynote2025.databinding.ItemMemoBinding
import com.example.memorynote2025.room.memo.Memo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 아이템 클릭 시 실행할 동작을 외부에서 전달 받음
class MemoAdapter(private val onItemClick: (Memo) -> Unit) :
    ListAdapter<Memo, MemoAdapter.MemoViewHolder>(DIFF_CALLBACK) {

    inner class MemoViewHolder(private val binding: ItemMemoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(memo: Memo) {
            binding.apply {
                // Memo 데이터를 각 뷰에 할당
                tvContent.text = memo.content
                tvDate.text = SimpleDateFormat(itemView.context.getString(R.string.date_format),
                    Locale.getDefault()).format(Date(memo.date))

                root.setOnClickListener {
                    // 외부에서 전달 받은 onItemClick 호출, 현재 memo 객체 전달
                    onItemClick(memo)
                }
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