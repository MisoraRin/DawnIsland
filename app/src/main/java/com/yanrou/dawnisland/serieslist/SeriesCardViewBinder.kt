package com.yanrou.dawnisland.serieslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drakeet.multitype.ItemViewBinder
import com.yanrou.dawnisland.R
import timber.log.Timber

class SeriesCardViewBinder(val loadMore: () -> Unit, private val jumpToContent: (seriesId: String, forumName: String, view: View) -> Unit) : ItemViewBinder<SeriesCardView, SeriesCardViewBinder.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, item: SeriesCardView) {
        holder.id = item.id
        holder.forumName = item.forum
        holder.cookie.text = item.cookie
        holder.time.text = item.time
        holder.forumTextView.setText(item.forumAndReply, TextView.BufferType.SPANNABLE)
        holder.content.text = item.content
        holder.itemView.transitionName = item.id
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
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(parent.context)
        val letterSpace = defaultSharedPreferences.getInt(CardViewFactory.LETTER_SPACE, 0) * 1.0f / 50
        val mainTextSize = defaultSharedPreferences.getInt(CardViewFactory.MAIN_TEXT_SIZE, 15)
        val view = inflater.inflate(R.layout.series_card, parent, false)
        return ViewHolder(view, jumpToContent).apply {
            content.letterSpacing = letterSpace
            content.textSize = mainTextSize.toFloat()
        }
    }

    class ViewHolder(itemView: View, jumpToContent: (seriesId: String, forumName: String, view: View) -> Unit) : RecyclerView.ViewHolder(itemView) {
        var id: String = ""
        var forumName: String = ""
        var cookie: TextView = itemView.findViewById(R.id.SeriesListCookie)
        var content: TextView = itemView.findViewById(R.id.SeriesListContent)
        var time: TextView = itemView.findViewById(R.id.SeriesListTime)
        var forumTextView: TextView = itemView.findViewById(R.id.SeriesListForum)
        var sage: TextView = itemView.findViewById(R.id.sage)
        var image: ImageView = itemView.findViewById(R.id.SeriesListImageView2)

        init {
            itemView.setOnClickListener { jumpToContent(id, forumName, itemView) }
        }
    }
}