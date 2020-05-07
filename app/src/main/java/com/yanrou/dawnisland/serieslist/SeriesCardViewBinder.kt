package com.yanrou.dawnisland.serieslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drakeet.multitype.ItemViewBinder
import com.yanrou.dawnisland.R
import timber.log.Timber

class SeriesCardViewBinder(val loadMore: () -> Unit, val jumpToContent: (seriesId: String, forumName: String) -> Unit) : ItemViewBinder<SeriesCardView, SeriesCardViewBinder.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, item: SeriesCardView) {
        holder.id = item.id
        holder.forumName = item.forum
        holder.cookie.text = item.cookie
        holder.time.text = item.time
        holder.forumTextView.setText(item.forumAndReply, TextView.BufferType.SPANNABLE)
        holder.content.text = item.content
        if (item.haveImage) {
            holder.image.visibility = View.VISIBLE
            Glide.with(holder.image.context)
                    .load("https://nmbimg.fastmirror.org/thumb/" + item.imageUri)
                    .override(250, 250)
                    .into(holder.image)
        } else {
            holder.image.visibility = View.GONE
        }
        holder.sage.visibility = item.sage
        if (getPosition(holder) > adapterItems.size - 10) {
            loadMore()
            Timber.d("执行了")
        }
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = inflater.inflate(R.layout.series_card, parent, false)
        return ViewHolder(view, jumpToContent)
    }

    class ViewHolder(itemView: View, jumpToContent: (seriesId: String, forumName: String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        var id: String = ""
        var forumName: String = ""
        var cookie: TextView
        var content: TextView
        var time: TextView
        var forumTextView: TextView
        var sage: TextView
        var replycount: TextView
        var image: ImageView

        init {
            cookie = itemView.findViewById(R.id.SeriesListCookie)
            content = itemView.findViewById(R.id.SeriesListContent)
            time = itemView.findViewById(R.id.SeriesListTime)
            forumTextView = itemView.findViewById(R.id.SeriesListForum)
            image = itemView.findViewById(R.id.SeriesListImageView2)
            sage = itemView.findViewById(R.id.sage)
            replycount = itemView.findViewById(R.id.reply_count)
            itemView.setOnClickListener {
                jumpToContent(id, forumName)
//                val intent = Intent(it.context, SeriesContentActivity::class.java)
//                intent.putExtra("id", id)
//                intent.putExtra("forumTextView", forumName)
//                it.context.startActivity(intent)
            }
        }
    }
}