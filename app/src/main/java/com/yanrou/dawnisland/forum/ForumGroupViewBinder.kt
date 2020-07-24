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
import com.yanrou.dawnisland.util.startRotationAnim

class ForumGroupViewBinder(val clickHandler: (String) -> Unit) : ItemViewBinder<ForumJson, ForumGroupViewBinder.ViewHolder>() {
    class ViewHolder(itemView: View, val clickHandler: (String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        var fname: String? = null
        lateinit var forumJson: ForumJson
        var linearLayout: LinearLayout = itemView.findViewById(R.id.forum_layout)
        var forum: TextView = itemView.findViewById(R.id.group_name)
        var arrow: ImageView = itemView.findViewById(R.id.arrow)
        var pos = 0

        init {
            itemView.setOnClickListener { v ->
                clickHandler(forumJson.id)
                if (forumJson.isExpand) {
                    arrow.startRotationAnim(180f, 0f)
                } else {
                    arrow.startRotationAnim(0f, 180f)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ForumJson) {
        holder.forum.text = item.name
        holder.forumJson = item
        holder.arrow.apply {
            rotation = if (item.isExpand) {
                0f
            } else {
                180f
            }
        }
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forum_group_item, parent, false)
        return ViewHolder(view, clickHandler)
    }


}