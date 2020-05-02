package com.yanrou.dawnisland.trend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yanrou.dawnisland.R

class TrendAdapter(var trendItems: List<TrendItem>, private val onItemClick: (context: Context, id: String, forum: String) -> Unit) : RecyclerView.Adapter<TrendAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trend_item, parent, false)
        return ViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trendItem = trendItems[position]
        holder.forum.text = trendItem.forum
        holder.content.text = trendItem.content
        holder.trend.text = trendItem.trend
        holder.id.text = trendItem.id
        holder.rank.text = trendItem.rank
    }

    override fun getItemCount(): Int {
        return trendItems.size
    }

    class ViewHolder(itemView: View, onItemClick: (context: Context, id: String, forum: String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        var rank: TextView = itemView.findViewById(R.id.rank)
        var trend: TextView = itemView.findViewById(R.id.trend)
        var content: TextView = itemView.findViewById(R.id.SeriesListContent)
        var forum: TextView = itemView.findViewById(R.id.SeriesListForum)
        var id: TextView = itemView.findViewById(R.id.id)

        init {
            itemView.setOnClickListener { view: View -> onItemClick(view.context, id.text.toString(), forum.text.toString()) }
        }
    }
}