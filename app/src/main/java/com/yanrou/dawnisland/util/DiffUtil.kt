package com.yanrou.dawnisland.util

import androidx.recyclerview.widget.DiffUtil
import com.yanrou.dawnisland.serieslist.SeriesCardView

class DiffCallback(private val oldList: List<Any>, private val newList: List<Any>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem is SeriesCardView && newItem is SeriesCardView) {
            return oldItem.id == newItem.id
        }
        return false
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }
}