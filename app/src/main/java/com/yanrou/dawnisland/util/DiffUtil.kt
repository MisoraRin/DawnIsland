package com.yanrou.dawnisland.util

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.yanrou.dawnisland.serieslist.SeriesCardView

class DiffCallback(val oldList: List<Any>, val newList: List<Any>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            (oldItem is SeriesCardView && newItem is SeriesCardView) -> oldItem.id === newItem.id
            else -> {
                Log.e("DiffCallback", "Unhandled type comparison")
                throw Exception("Unhandled type comparison")
            }
        }
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            (oldItem is SeriesCardView && newItem is SeriesCardView) -> oldItem.id == newItem.id
            else -> {
                Log.e("DiffCallback", "Unhandled type comparison")
                throw Exception("Unhandled type comparison")
            }
        }
    }
}