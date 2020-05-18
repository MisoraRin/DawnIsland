package com.yanrou.dawnisland.trend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.content.SeriesContentActivity
import kotlinx.android.synthetic.main.fragment_series.*

class TrendFragment : Fragment() {
    private val viewModel by viewModels<TrendViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(recyclerview!!.context)
        recyclerview!!.layoutManager = layoutManager
        val trendAdapter = TrendAdapter(emptyList()) { context, seriesId, forumName ->
            val intent = Intent(requireContext(), SeriesContentActivity::class.java)
            intent.putExtra("id", seriesId)
            intent.putExtra("forumTextView", forumName)
            startActivity(intent)
        }
        recyclerview!!.adapter = trendAdapter
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer {
            trendAdapter.trendItems = it
            trendAdapter.notifyDataSetChanged()
        })
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "昨日争吵"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }
}