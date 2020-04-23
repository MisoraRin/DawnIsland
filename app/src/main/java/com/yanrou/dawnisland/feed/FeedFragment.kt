package com.yanrou.dawnisland.feed

import android.os.Bundle
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
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.json2class.FeedJson
import com.yanrou.dawnisland.util.DiffCallback

class FeedFragment : Fragment() {
    private var mViewModel: FeedViewModel? = null
    private var recyclerView: RecyclerView? = null
    private var refreshLayout: SmartRefreshLayout? = null
    private var multiTypeAdapter: MultiTypeAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.feed_fragment, container, false)
        recyclerView = rootView.findViewById(R.id.feed_recycler_view)
        refreshLayout = rootView.findViewById(R.id.smart_refresh)

        mViewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        multiTypeAdapter = MultiTypeAdapter()
        val clickListener = View.OnClickListener {

        }
        multiTypeAdapter!!.register(FeedJson::class.java, FeedItemViewBinder(requireContext(), mViewModel!!))
        mViewModel!!.feeds.observe(viewLifecycleOwner, Observer { newList ->
            val diffResult = DiffUtil.calculateDiff(DiffCallback(multiTypeAdapter!!.items, newList))
            multiTypeAdapter!!.items = newList.toList()
            diffResult.dispatchUpdatesTo(multiTypeAdapter!!)
        })
        recyclerView!!.layoutManager = LinearLayoutManager(recyclerView!!.context)
        recyclerView!!.adapter = multiTypeAdapter
        refreshLayout!!.setEnableAutoLoadMore(false)
        refreshLayout!!.setOnLoadMoreListener { refreshLayout: RefreshLayout ->
            mViewModel!!.getFeeds()
            refreshLayout.finishLoadMore()
        }

//        recyclerView!!.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener{
//            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
//                    val intent = Intent(context, SeriesContentActivity::class.java)
//                    intent.putExtra("id", (rv as CardViewFactory.MyCardView).id)
//                    intent.putExtra("forumTextView", (rv as CardViewFactory.MyCardView).forum)
//                    requireContext().startActivity(intent)
//
//            }
//
//            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                TODO("Not yet implemented")
//            }
//
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//                TODO("Not yet implemented")
//            }
//
//        } )

        refreshLayout!!.setOnRefreshListener { refreshLayout1: RefreshLayout -> refreshLayout1.finishRefresh(0) }

        return rootView
    }

    override fun onStop() {
        super.onStop()
        refreshLayout = null
        multiTypeAdapter = null
        recyclerView = null
    }

    companion object {
        fun newInstance(): FeedFragment {
            return FeedFragment()
        }
    }
}