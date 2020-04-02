package com.yanrou.dawnisland.serieslist

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.yanrou.dawnisland.FooterView
import com.yanrou.dawnisland.FooterViewBinder
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.SeriesRecyclerOnScrollListener
import com.yanrou.dawnisland.util.DiffCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SeriesFragment : Fragment() {
    private val TAG = "SeriesFragment"

    private var seriesList: RecyclerView? = null
    private lateinit var activity: Activity

    private lateinit var viewModel: SeriesViewModel
    private var seriesListAdapter: MultiTypeAdapter? = null

    private var smartRefreshLayout: SmartRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()!!

        viewModel = ViewModelProvider(getActivity()!!).get(SeriesViewModel::class.java)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        smartRefreshLayout!!.setEnableAutoLoadMore(false)
        smartRefreshLayout!!.setDragRate(0.3f)

        smartRefreshLayout!!.setOnLoadMoreListener {
            Log.d(TAG, "onLoadMore: 触发了onLoadMore")
            smartRefreshLayout!!.finishLoadMore(0)
        }
        smartRefreshLayout!!.setOnRefreshListener {
            Log.d(TAG, "onActivityCreated: 触发了刷新")
            refresh()
        }
        /**
         * 这里是设定滑动监听，以便实现加载下一页的效果
         */
        seriesList!!.addOnScrollListener(object : SeriesRecyclerOnScrollListener() {
            override fun onLoadMore() {
                viewModel.getNextPage()
            }
        })
        /**
         * 这里是添加layoutManager
         */
        val layoutManager = LinearLayoutManager(getActivity())
        seriesList!!.layoutManager = layoutManager
        /**
         * 添加adapter
         */
        seriesList!!.adapter = seriesListAdapter
        Log.d(TAG, "onActivityCreated: 到这里")
        viewModel.seriesCards.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "new Data found, updating adapter...")
            updateAdapter(it)
            smartRefreshLayout!!.closeHeaderOrFooter()
        })
//      TODO 依据加载状态给与用户相应反馈
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_series, container, false)
        seriesList = view.findViewById(R.id.series_list_fragment)
        smartRefreshLayout = view.findViewById(R.id.smartrefresh)
        seriesListAdapter = MultiTypeAdapter()
        seriesListAdapter!!.register(SeriesCardView::class.java, SeriesCardViewBinder(activity))
        seriesListAdapter!!.register(FooterView::class.java, FooterViewBinder())
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        smartRefreshLayout = null
        seriesList = null
        seriesListAdapter = null
    }


    fun changeForum(fid: Int) {
        seriesList!!.scrollToPosition(0)
        smartRefreshLayout!!.finishRefresh()
        smartRefreshLayout!!.autoRefreshAnimationOnly()
        viewModel.changeForum(fid)
    }

    fun refresh() {
        seriesList!!.scrollToPosition(0)
        smartRefreshLayout!!.autoRefreshAnimationOnly()
        viewModel.refresh()
    }

    private fun updateAdapter(newList: List<SeriesCardView>) {
        lifecycleScope.launch {
            val diffResult = withContext(Dispatchers.Default) { DiffUtil.calculateDiff(DiffCallback(seriesListAdapter!!.items, newList)) }
            seriesListAdapter!!.items = newList.toList()
            diffResult.dispatchUpdatesTo(seriesListAdapter!!)
        }
    }
}
