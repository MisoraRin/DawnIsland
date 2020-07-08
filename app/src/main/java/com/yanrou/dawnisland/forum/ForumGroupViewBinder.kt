package com.yanrou.dawnisland.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.json2class.ForumJson

class ForumGroupViewBinder(val clickHandler: (position: Int) -> Unit) : ItemViewBinder<ForumJson, ForumGroupViewBinder.ViewHolder>() {
    class ViewHolder(itemView: View, val clickHandler: (position: Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        var fname: String? = null
        var fid = 0
        var linearLayout: LinearLayout
        var forum: TextView
        var arrow: ImageView = itemView.findViewById(R.id.arrow)
        var pos = 0

        init {
            forum = itemView.findViewById(R.id.group_name)
            linearLayout = itemView.findViewById(R.id.forum_layout)
            itemView.setOnClickListener { v ->
                clickHandler(layoutPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ForumJson) {
        holder.forum.text = item.name
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forum_group_item, parent, false)
        return ViewHolder(view, clickHandler)
    }


}