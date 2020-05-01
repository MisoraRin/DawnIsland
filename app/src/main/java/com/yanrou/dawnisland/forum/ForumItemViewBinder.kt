package com.yanrou.dawnisland.forum

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder

import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.json2class.ForumsBean

public class ForumItemViewBinder(val context: Context, val click: (fid: Int, fname: String) -> Unit) : ItemViewBinder<ForumsBean, ForumItemViewBinder.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, forum: ForumsBean) {
        holder.fid = forum.id
        holder.fname = forum.name

        if (forum.getShowName() != null && !forum.getShowName().equals("")) {
            val displayName: Spanned
            displayName = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Html.fromHtml(forum.getShowName())
            } else {
                Html.fromHtml(forum.getShowName(), Html.FROM_HTML_MODE_COMPACT)
            }
            holder.forum.setText(displayName, TextView.BufferType.SPANNABLE)
        } else {
            holder.forum.setText(forum.getName(), TextView.BufferType.SPANNABLE)
        }

        // special handling for drawable resource ID, which cannot have -
        val biId = if (forum.id > 0) forum.getId() else 1
        val resourceId: Int = context.getResources().getIdentifier("bi_$biId", "drawable",
                context.getPackageName())
        if (resourceId != 0) {
            holder.imageView.visibility = View.VISIBLE
            holder.imageView.setImageResource(resourceId)
        } else {
            holder.imageView.visibility = View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forum_list_item, parent, false)
        return ViewHolder(view, click)
    }

    public class ViewHolder(itemView: View, click: (fid: Int, fname: String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        var fname: String? = null
        var fid = 0
        var linearLayout: LinearLayout
        var forum: TextView
        var imageView: ImageView

        init {
            forum = itemView.findViewById(R.id.forum_name)
            imageView = itemView.findViewById(R.id.icon)
            linearLayout = itemView.findViewById(R.id.forum_layout)
            itemView.setOnClickListener { click(fid, fname!!) }
        }
    }
}