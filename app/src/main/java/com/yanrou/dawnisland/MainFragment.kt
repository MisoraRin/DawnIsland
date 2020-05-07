package com.yanrou.dawnisland

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.yanrou.dawnisland.feed.FeedFragment
import com.yanrou.dawnisland.forum.ForumDiffCallback
import com.yanrou.dawnisland.forum.ForumGroupViewBinder
import com.yanrou.dawnisland.forum.ForumItemViewBinder
import com.yanrou.dawnisland.forum.ForumViewModel
import com.yanrou.dawnisland.json2class.ForumJson
import com.yanrou.dawnisland.json2class.ForumsBean
import com.yanrou.dawnisland.serieslist.SeriesFragment
import com.yanrou.dawnisland.trend.TrendFragment
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber

class MainFragment : Fragment() {
    private val FEED_FRAGMENT_TAG = "feed"
    private val SERIES_FRAGMENT_TAG = "series"
    private val TREND_FRAGMENT_TAG = "trend"

    var forumAdapter: MultiTypeAdapter? = null
    private val forumViewModel by activityViewModels<ForumViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
            setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        }

        (requireActivity() as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.toolbar_home_as_up)
        }

        forumAdapter = MultiTypeAdapter().apply {
            register(ForumsBean::class.java, ForumItemViewBinder(requireContext()) { id: Int, name: String? ->
                drawerLayout.closeDrawers()
                toolbar.title = name
//                changeForum(id)
            })
            register(ForumJson::class.java, ForumGroupViewBinder {
                forumViewModel.refreshForumGroupExpandState(it)
            })
        }

        forum_list.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = forumAdapter
        }
        forumViewModel.forumOnView.observe(requireActivity(), androidx.lifecycle.Observer {
            val oldList = forumAdapter!!.items
            //创建一个新的表
            val newList: List<Any> = ArrayList(it)
            val diffResult = DiffUtil.calculateDiff(ForumDiffCallback(oldList, newList), false)
            forumAdapter!!.items = newList
            diffResult.dispatchUpdatesTo(forumAdapter!!)
        })

        val ts = childFragmentManager.beginTransaction()
        val seriesFragment = childFragmentManager.findFragmentByTag("series")
        if (seriesFragment == null) {
            SeriesFragment.newInstance().apply {
                ts.add(R.id.listContainer, this, "series").commit()
            }
        }
        dslTabLayout.configTabLayoutConfig {
            onSelectItemView =
                    { _, index, selected, _ ->
                        Timber.d("$index is selected")
                        if (selected) {
                            when (index) {
                                0 -> switchFragment(SERIES_FRAGMENT_TAG)
                                1 -> switchFragment(TREND_FRAGMENT_TAG)
                                2 -> switchFragment(FEED_FRAGMENT_TAG)
                            }
                        }
                        false
                    }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        forumAdapter = null
    }

    private fun switchFragment(fragmentTag: String) {
        val transaction = childFragmentManager.beginTransaction()
        //先判断有没有添加进去
        val currentFragment = childFragmentManager.fragments.last { !it.isHidden }
        if (childFragmentManager.findFragmentByTag(fragmentTag) == null) {
            Timber.d("未添加")
            if (currentFragment != null) {
                transaction.hide(currentFragment)
            }
            val targetFragment =
                    when (fragmentTag) {
                        SERIES_FRAGMENT_TAG -> SeriesFragment.newInstance()
                        FEED_FRAGMENT_TAG -> FeedFragment()
                        TREND_FRAGMENT_TAG -> TrendFragment()
                        else -> throw Exception("没有这个标签")
                    }
            transaction.add(R.id.listContainer, targetFragment, fragmentTag)
        } else {
            val targetFragment = childFragmentManager.findFragmentByTag(fragmentTag)!!
            currentFragment?.let { transaction.hide(currentFragment) }
            transaction.show(targetFragment)
        }
        transaction.commit()
        childFragmentManager.executePendingTransactions()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
