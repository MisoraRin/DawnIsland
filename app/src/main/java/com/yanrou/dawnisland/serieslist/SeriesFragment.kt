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


class SeriesFragment : Fragment() {
    private val TAG = "SeriesFragment"

    private lateinit var seriesList: RecyclerView
    private lateinit var activity: Activity

    private lateinit var viewModel: SeriesViewModel
    private lateinit var seriesListAdapter: MultiTypeAdapter

    private lateinit var smartRefreshLayout: SmartRefreshLayout
    private var adapterItems = listOf<SeriesCardView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()!!
        seriesListAdapter = MultiTypeAdapter()
        viewModel = ViewModelProvider(getActivity()!!).get(SeriesViewModel::class.java)
        seriesListAdapter.register(SeriesCardView::class.java, SeriesCardViewBinder(activity))
        seriesListAdapter.register(FooterView::class.java, FooterViewBinder())

        seriesListAdapter.items = emptyList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        smartRefreshLayout.setEnableAutoLoadMore(false)
        smartRefreshLayout.setDragRate(0.3f)
        smartRefreshLayout.setFooterTriggerRate(2f)

        smartRefreshLayout.setOnLoadMoreListener {
            Log.d(TAG, "onLoadMore: 触发了onLoadMore")
            smartRefreshLayout.finishLoadMore(0)
        }
        smartRefreshLayout.setOnRefreshListener {
            Log.d(TAG, "onActivityCreated: 触发了刷新")
            reFresh()
        }
        /**
         * 这里是设定滑动监听，以便实现加载下一页的效果
         */
        seriesList.addOnScrollListener(object : SeriesRecyclerOnScrollListener() {
            override fun onLoadMore() {
                viewModel.getNextPage()
            }
        })
        /**
         * 这里是添加layoutManager
         */
        val layoutManager = LinearLayoutManager(getActivity())
        seriesList.layoutManager = layoutManager
        /**
         * 添加adapter
         */
        seriesList.adapter = seriesListAdapter
        Log.d(TAG, "onActivityCreated: 到这里")
        viewModel.seriesCards.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "new Data found, updating adapter...")
            updateAdapter(it)
            smartRefreshLayout.finishLoadMore()
            smartRefreshLayout.closeHeaderOrFooter()
        })
//      TODO
//        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
//            when (it){
//                viewModel.COMPLETE
//            }
//        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_series, container, false)
        seriesList = view.findViewById(R.id.series_list_fragment)
        smartRefreshLayout = view.findViewById(R.id.smartrefresh)
        return view
    }

    fun changeForum(fid: Int) {
        seriesList.scrollToPosition(0)
        //Log.d(TAG, "changeForum: 调用refresh前finish一遍");
        smartRefreshLayout.finishRefresh()
        Log.d(TAG, "changeForum: $fid 启动刷新动画")
        val d = smartRefreshLayout.autoRefreshAnimationOnly()
        Log.d(TAG, "changeForum: 启用动画的返回值 $d")
        updateAdapter(emptyList())
        viewModel.changeForum(fid)
    }

    fun reFresh() {
        updateAdapter(emptyList())
        seriesList.scrollToPosition(0)
        smartRefreshLayout.autoRefreshAnimationOnly()
        viewModel.refresh()
    }

    fun updateAdapter(newList: List<SeriesCardView>) {
        adapterItems = newList
        val diffResult = DiffUtil.calculateDiff(DiffCallback(seriesListAdapter.items, adapterItems))
        seriesListAdapter.items = adapterItems
        diffResult.dispatchUpdatesTo(seriesListAdapter)
        seriesListAdapter.notifyDataSetChanged()
    }


//    when (state) {
//        FirstPage -> {
//            items.addAll(seriesCardViews)
//            footerView = FooterView()
//            footerView.text = "加载大成功"
//            items.add(footerView)
//            presenter.getFirstPageSuccess(items)
//        }
//        NextPage -> {
//            items.addAll(items.size - 1, seriesCardViews)
//            presenter.getNextPageSuccess()
//        }
//        Refresh -> {
//            items.clear()
//            items.addAll(seriesCardViews)
//            footerView = FooterView()
//            footerView.text = "加载大成功"
//            items.add(footerView)
//            presenter.refreshSuccess()
//        }
//                else -> {
//        }
//    }
    /**因为服务器禁止，取消获取新提醒相关功能
    private void getNewReply() {
    final List<SeriesData> seriesDatas = LitePal.where("substate = ?", String.valueOf(SeriesData.NEW_REPLY)).find(SeriesData.class);
    Log.d(TAG, "getNewReply: 订阅了" + seriesDatas.size());
    for (int i = 0; i < seriesDatas.size(); i++) {
    final int finalI = i;
    Runnable runnable = new Runnable() {
    @Override public void run() {
    SeriesData temp = seriesDatas.get(finalI);
    int newReplyCount = HttpUtil.getReplyCount(temp.seriesid);
    int newcount = newReplyCount - temp.lastReplyCount;
    //说明有更新
    if (newcount > 0) {
    Log.d(TAG, "run: " + temp.content);
    subscriberItems.add(new SubscriberItem(temp.forumName, temp.seriesid, temp.po.get(0), temp.content, String.valueOf(newcount)));
    contentAdapter.setSubscriberVisiable(true);
    activity.runOnUiThread(new Runnable() {
    @Override public void run() {
    subscriberAdapter.notifyItemInserted(0);
    contentAdapter.notifyDataSetChanged();
    }
    });
    activity.runOnUiThread(new Runnable() {
    @Override public void run() {
    contentAdapter.notifyDataSetChanged();
    }
    });

    }
    }
    };
    new Thread(runnable).start();
    }
    }

    public void changeForum(int fid) {
    page = 1;
    this.fid = fid;
    timeLineJsons.clear();
    contentAdapter.notifyDataSetChanged();
    getNewPage();
    }

     */
}
