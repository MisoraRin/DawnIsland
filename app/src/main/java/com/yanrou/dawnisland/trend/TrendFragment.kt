package com.yanrou.dawnisland.trend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.content.SeriesContentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrendFragment : Fragment() {
    var trendList: RecyclerView? = null
    var trendItems: List<TrendItem> = emptyList()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(trendList!!.context)
        trendList!!.layoutManager = layoutManager
        val trendAdapter = TrendAdapter(trendItems) { context, id, forum ->
            val intent = Intent(context, SeriesContentActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("forumTextView", forum)
            context.startActivity(intent)
        }
        trendList!!.adapter = trendAdapter
        lifecycleScope.launch {
            trendAdapter.trendItems = withContext(Dispatchers.IO) { getTrend() }
            trendAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trand, container, false)
        trendList = view.findViewById(R.id.trend_recycleview)
        return view
    }
}