package com.yanrou.dawnisland.trend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.content.SeriesContentFragment

class TrendFragment : Fragment() {
    var trendList: RecyclerView? = null
    val viewModel by viewModels<TrendViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = LinearLayoutManager(trendList!!.context)
        trendList!!.layoutManager = layoutManager
        val trendAdapter = TrendAdapter(emptyList()) { context, seriesId, forumName ->
            val fragment = SeriesContentFragment.newInstance(seriesId, forumName)
            requireParentFragment().parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, "series_content")
                    .addToBackStack("series_content")
                    .commit()
        }
        trendList!!.adapter = trendAdapter
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer {
            trendAdapter.trendItems = it
            trendAdapter.notifyDataSetChanged()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trand, container, false)
        trendList = view.findViewById(R.id.trend_recycleview)
        return view
    }
}