package com.yanrou.dawnisland.content

import android.annotation.SuppressLint
import android.content.Intent
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drakeet.multitype.ItemViewBinder
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.imageviewer.ImageViewerView
import com.yanrou.dawnisland.serieslist.CardViewFactory

class ContentViewBinder : ItemViewBinder<ContentItem, ContentViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(parent.context)
        val letterSpace = defaultSharedPreferences.getInt(CardViewFactory.LETTER_SPACE, 0) * 1.0f / 50
        val mainTextSize = defaultSharedPreferences.getInt(CardViewFactory.MAIN_TEXT_SIZE, 15)
        val root = inflater.inflate(R.layout.series_content_card, parent, false)
        val viewHolder = ViewHolder(root)
        viewHolder.content.letterSpacing = letterSpace
        viewHolder.content.textSize = mainTextSize.toFloat()
        return viewHolder
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, item: ContentItem) {
        holder.cookie.setText(item.cookie, TextView.BufferType.SPANNABLE)
        holder.time.text = item.time
        holder.number.text = item.seriesId
        holder.content.setText(item.content, TextView.BufferType.SPANNABLE)
        holder.sega.visibility = item.sega
        if (item.hasTitleOrName) {
            holder.titleAndName.visibility = View.VISIBLE
            holder.titleAndName.text = item.titleAndName
        } else {
            holder.titleAndName.visibility = View.GONE
        }
        if (item.hasImage) {
            holder.imageView.visibility = View.VISIBLE
            Glide.with(holder.imageView.context)
                    .load("https://nmbimg.fastmirror.org/thumb/" + item.imgurl)
                    .override(250, 250)
                    .into(holder.imageView)
            holder.imageView.setOnClickListener { view ->
                val fullScreenImageViewer = Intent(view.context, ImageViewerView::class.java)
                fullScreenImageViewer.putExtra("imgurl", "https://nmbimg.fastmirror.org/image/" + item.imgurl)
                view.context.startActivity(fullScreenImageViewer)
            }
        } else {
            holder.imageView.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sega: TextView = itemView.findViewById(R.id.sega)
        var number: TextView = itemView.findViewById(R.id.number)
        var cookie: TextView = itemView.findViewById(R.id.SeriesListCookie)
        var content: TextView = itemView.findViewById(R.id.SeriesListContent)
        var time: TextView = itemView.findViewById(R.id.SeriesListTime)
        var titleAndName: TextView = itemView.findViewById(R.id.titleAndName)
        var imageView: ImageView = itemView.findViewById(R.id.seriesContentImageView)

        init {
            content.setOnTouchListener { v: View, event: MotionEvent ->
                var ret = false
                val text = (v as TextView).text as SpannableString
                val action = event.action
                if (action == MotionEvent.ACTION_UP ||
                        action == MotionEvent.ACTION_DOWN) {
                    var x = event.x.toInt()
                    var y = event.y.toInt()
                    x -= v.totalPaddingLeft
                    y -= v.totalPaddingTop
                    x += v.scrollX
                    y += v.scrollY
                    val layout = v.layout
                    val line = layout.getLineForVertical(y)
                    val off = layout.getOffsetForHorizontal(line, x.toFloat())
                    val link = text.getSpans(off, off, ClickableSpan::class.java)
                    if (link.isNotEmpty()) {
                        if (action == MotionEvent.ACTION_UP) {
                            link[0].onClick(v)
                        }
                        ret = true
                    }
                }
                ret
            }
        }
    }
}