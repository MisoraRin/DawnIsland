package com.yanrou.dawnisland.trend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.content.SeriesContentFragment
import kotlinx.android.synthetic.main.fragment_series.*

class TrendFragment : Fragment() {
    private val viewModel by viewModels<TrendViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(recyclerview!!.context)
        recyclerview!!.layoutManager = layoutManager
        val trendAdapter = TrendAdapter(emptyList()) { context, seriesId, forumName ->
            val fragment = SeriesContentFragment.newInstance(seriesId, forumName)
            requireParentFragment().parentFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainer, fragment, "series_content")
                    .addToBackStack("series_content")
                    .commit()
        }
        recyclerview!!.adapter = trendAdapter
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer {
            trendAdapter.trendItems = it
            trendAdapter.notifyDataSetChanged()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }
}