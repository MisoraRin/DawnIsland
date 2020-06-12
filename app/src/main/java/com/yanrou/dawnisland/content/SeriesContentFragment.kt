package com.yanrou.dawnisland.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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

const val SERIES_ID = "series_id"
const val FORUM_NAME = "forum_name"

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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_series_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
        }

        (requireActivity() as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        refresher.transitionName = seriesId
//        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.title = "A岛 · $forumName"
        toolbar.subtitle = ">>No.$seriesId · adnmb.com"
        viewModel.seriesId = seriesId!!
        viewModel.referenceHandler = {
            QuotePopup(requireActivity()).showQuote(it, "")
        }
        val multiTypeAdapter = MultiTypeAdapter()
        multiTypeAdapter.register(ContentItem::class.java, ContentViewBinder())
        val layoutManager = LinearLayoutManager(series_content_recycleview.context)
        series_content_recycleview.layoutManager = layoutManager
        //用于刷新的监听器
        series_content_recycleview.addOnScrollListener(object : SeriesRecyclerOnScrollListener() {
            override fun onLoadMore() {
                viewModel.loadMore()
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
        refresher.setEnableAutoLoadMore(false)
        refresher.setOnRefreshListener {
            viewModel.loadPreviousPage()
        }

        refresher.setOnLoadMoreListener { viewModel.loadMore() }
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer { contentItems ->
            val oldList = multiTypeAdapter.items
            //创建一个新的表
            val newList: List<Any> = ArrayList<Any>(contentItems!!)
            val diffResult = DiffUtil.calculateDiff(DiffContentList(oldList, newList))
            multiTypeAdapter.items = newList

            diffResult.dispatchUpdatesTo(multiTypeAdapter)
            refresher.finishRefresh()
            refresher.finishLoadMore()


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
