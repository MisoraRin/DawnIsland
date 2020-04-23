package com.yanrou.dawnisland.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.drakeet.multitype.MultiTypeAdapter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.content.SeriesContentActivity
import com.yanrou.dawnisland.json2class.FeedJson
import com.yanrou.dawnisland.serieslist.CardViewFactory
import com.yanrou.dawnisland.util.DiffCallback
import timber.log.Timber

class FeedFragment : Fragment() {
    private var mViewModel: FeedViewModel? = null
    private var multiTypeAdapter: MultiTypeAdapter? = null
    private var recyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.feed_fragment, container, false)
        recyclerView = rootView.findViewById(R.id.feed_recycler_view)
        val refreshLayout:SmartRefreshLayout = rootView.findViewById(R.id.smart_refresh)

        mViewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        multiTypeAdapter = MultiTypeAdapter()
        val clickListener = View.OnClickListener {
            val intent = Intent(context, SeriesContentActivity::class.java)
            intent.putExtra("id", (it as CardViewFactory.MyCardView).id)
            intent.putExtra("forumTextView", it.forum)
            requireContext().startActivity(intent)
        }

        val longClickListener = View.OnLongClickListener {
            val id = (it as CardViewFactory.MyCardView).id
            MaterialDialog(requireContext()).show {
                title(text = "删除订阅 $id?")
                positiveButton(text = "删除") {
                    mViewModel!!.deleteFeed(id)
                    Toast.makeText(context, "取消订阅", Toast.LENGTH_SHORT).show()
                }
                negativeButton(text = "取消")
            }
            true
        }

        multiTypeAdapter!!.register(FeedJson::class.java, FeedItemViewBinder(clickListener, longClickListener))

        recyclerView!!.layoutManager = LinearLayoutManager(recyclerView!!.context)
        recyclerView!!.adapter = multiTypeAdapter
        refreshLayout.setEnableAutoLoadMore(false)
        refreshLayout.setOnLoadMoreListener {
            mViewModel!!.getFeeds()
            refreshLayout.finishLoadMore()
        }

        refreshLayout.setOnRefreshListener {
            mViewModel!!.refresh()
            refreshLayout.finishRefresh()
        }

        mViewModel!!.feeds.observe(viewLifecycleOwner, Observer { newList ->
            val diffResult = DiffUtil.calculateDiff(DiffCallback(multiTypeAdapter!!.items, newList))
            multiTypeAdapter!!.items = newList.toList()
            diffResult.dispatchUpdatesTo(multiTypeAdapter!!)
        })

        return rootView
    }

    override fun onStop() {
        super.onStop()
        recyclerView = null
        multiTypeAdapter = null
    }

    companion object {
        fun newInstance(): FeedFragment {
            return FeedFragment()
        }
    }
}