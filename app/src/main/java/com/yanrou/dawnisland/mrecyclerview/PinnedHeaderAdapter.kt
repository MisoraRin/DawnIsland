package com.yanrou.dawnisland.mrecyclerview

import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.drakeet.multitype.MultiTypeAdapter
import com.yanrou.dawnisland.json2class.ForumJson

class PinnedHeaderAdapter : MultiTypeAdapter() {
    /**
     * 判断该position对应的位置是要固定
     *
     * @param position adapter position
     * @return true or false
     */
    fun isPinnedPosition(position: Int): Boolean {
        return items[position] is ForumJson
    }

    fun onBindViewHolderZ(holder: RecyclerView.ViewHolder, position: Int, viewType: Int) {
        val item = items[position]
        getOutDelegateByType(viewType).onBindViewHolder(holder, item, emptyList())
    }

    private fun getOutDelegateByType(viewType: Int): ItemViewDelegate<Any, RecyclerView.ViewHolder> {
        @Suppress("UNCHECKED_CAST")
        return types.getType<Any>(viewType).delegate as ItemViewDelegate<Any, RecyclerView.ViewHolder>
    }

}