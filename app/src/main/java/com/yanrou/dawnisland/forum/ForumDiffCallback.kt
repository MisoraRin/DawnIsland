package com.yanrou.dawnisland.forum

import androidx.recyclerview.widget.DiffUtil
import com.yanrou.dawnisland.json2class.ForumsBean

public class ForumDiffCallback(private val oldList: List<Any>, private val newList: List<Any>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition] is ForumsBean
        val newItem = newList[newItemPosition] is ForumsBean
        return if (oldItem && newItem) {
            (oldList[oldItemPosition] as ForumsBean).id == (newList[newItemPosition] as ForumsBean).id
        } else !(oldItem || newItem)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }
}