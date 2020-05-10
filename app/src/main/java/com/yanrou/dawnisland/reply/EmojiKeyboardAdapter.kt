package com.yanrou.dawnisland.reply

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.constant.Emoji

class EmojiKeyboardAdapter(val clickListener: (position: Int) -> Unit) : RecyclerView.Adapter<EmojiKeyboardAdapter.ViewHolder>() {
    class ViewHolder(itemView: View, clickListener: (position: Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        var index = -1
        val emojiTextView = (itemView as TextView).apply {
            setOnClickListener { clickListener(index) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.itew_emoji, parent, false), clickListener)
    }

    override fun getItemCount(): Int {
        return Emoji.COUNT
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.emojiTextView.text = Emoji.EMOJI_NAME[position]
        holder.index = position
    }
}