package com.yanrou.dawnisland.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.SeriesRecyclerOnScrollListener
import kotlinx.android.synthetic.main.activity_series_content.*
import java.util.*

private const val SERIES_ID = "series_id"
private const val FORUM_NAME = "forum_name"

class SeriesContentFragment : Fragment() {
    private var seriesId: String? = null
    private var forumName: String? = null
    private val viewModel by viewModels<SeriesContentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            seriesId = it.getString(SERIES_ID)
            forumName = it.getString(FORUM_NAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_series_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        smart_refresh.transitionName = seriesId
        requireActivity().window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏(StatusBar)颜色透明
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        }
//        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.title = "A岛 · $forumName"
        toolbar.subtitle = ">>No.$seriesId · adnmb.com"
        viewModel.seriesId = seriesId!!
        val multiTypeAdapter = MultiTypeAdapter()
        multiTypeAdapter.register(ContentItem::class.java, ContentViewBinder(requireActivity()))
        val layoutManager = LinearLayoutManager(series_content_recycleview.context)
        series_content_recycleview.layoutManager = layoutManager
        //用于刷新的监听器
        series_content_recycleview.addOnScrollListener(object : SeriesRecyclerOnScrollListener() {
            override fun onLoadMore() {
                viewModel.loadMore(layoutManager.findLastVisibleItemPosition())
            }
        })
        //用于报告页数的监听器
        series_content_recycleview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.nowIndex = layoutManager.findLastVisibleItemPosition()
            }
        })
        series_content_recycleview.adapter = multiTypeAdapter
        smart_refresh.setEnableAutoLoadMore(false)
        smart_refresh.setOnRefreshListener {
            viewModel.loadPreviousPage(layoutManager.findLastVisibleItemPosition())
        }

        smart_refresh.setOnLoadMoreListener { viewModel.loadMore(layoutManager.findLastVisibleItemPosition()) }
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer { contentItems ->
            val oldList = multiTypeAdapter.items
            //创建一个新的表
            val newList: List<Any> = ArrayList<Any>(contentItems!!)
            val diffResult = DiffUtil.calculateDiff(DiffContentList(oldList, newList))
            multiTypeAdapter.items = newList

            diffResult.dispatchUpdatesTo(multiTypeAdapter)
            smart_refresh.finishRefresh()
            smart_refresh.finishLoadMore()


        })
        //告诉view model可以开始读取数据了
        viewModel.firstStart()
    }

    companion object {
        @JvmStatic
        fun newInstance(seriesId: String, forumName: String) =
                SeriesContentFragment().apply {
                    arguments = Bundle().apply {
                        putString(SERIES_ID, seriesId)
                        putString(FORUM_NAME, forumName)
                    }
                }
    }
}
