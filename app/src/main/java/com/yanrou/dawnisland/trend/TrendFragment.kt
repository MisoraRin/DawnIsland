package com.yanrou.dawnisland.trend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            requireActivity().window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                //设置状态栏(StatusBar)颜色透明
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                //设置导航栏(NavigationBar)颜色透明
                //window.setNavigationBarColor(Color.TRANSPARENT);
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏(StatusBar)颜色透明
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            //设置导航栏(NavigationBar)颜色透明
            //window.setNavigationBarColor(Color.TRANSPARENT);
        }
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