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

class MemoAdapter : ListAdapter<Memo, MemoAdapter.MemoViewHolder>(DIFF_CALLBACK) {

    inner class MemoViewHolder(private val binding: ItemMemoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(memo: Memo) {
            binding.apply {
                // Memo 데이터를 각 뷰에 할당
                tvContent.text = memo.content
                tvDate.text = SimpleDateFormat(
                    itemView.context.getString(R.string.date_format),
                    Locale.getDefault()
                ).format(Date(memo.date))
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = ItemMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}