package com.yanrou.dawnisland.mrecyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter

abstract class PinnedHeaderAdapter : MultiTypeAdapter() {
    /**
     * 判断该position对应的位置是要固定
     *
     * @param position adapter position
     * @return true or false
     */
    abstract fun isPinnedPosition(position: Int): Boolean
    fun onCreatePinnedViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return onCreateViewHolder(parent!!, viewType)
    }

    fun onBindPinnedViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder, position)
    }
}