package com.yanrou.dawnisland

import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.skydoves.transformationlayout.onTransformationStartContainer
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
    private val forumViewModel by activityViewModels<ForumViewModel>()
    private var currentFragment: Fragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        onTransformationStartContainer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDrawerLeftEdgeSize(requireActivity(), drawerLayout)

        toolbar.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
            setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        }

        (requireActivity() as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.toolbar_home_as_up)
        }

        val forumAdapter = MultiTypeAdapter().apply {
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
            val oldList = forumAdapter.items
            //创建一个新的表
            val newList: List<Any> = ArrayList(it)
            val diffResult = DiffUtil.calculateDiff(ForumDiffCallback(oldList, newList), false)
            forumAdapter.items = newList
            diffResult.dispatchUpdatesTo(forumAdapter)
        })

        val ts = childFragmentManager.beginTransaction()
        val seriesFragment = childFragmentManager.findFragmentByTag("series")
        if (seriesFragment == null) {
            currentFragment = SeriesFragment.newInstance().apply {
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

    private fun switchFragment(fragmentTag: String) {
        val transaction = childFragmentManager.beginTransaction()
        //先判断有没有添加进去
        if (childFragmentManager.findFragmentByTag(fragmentTag) == null) {
            if (currentFragment != null) {
                transaction.hide(currentFragment!!)
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
            currentFragment?.let { transaction.hide(currentFragment!!) }
            transaction.show(targetFragment)
        }
        transaction.commit()
        childFragmentManager.executePendingTransactions()
        val targetFragment = childFragmentManager.findFragmentByTag(fragmentTag)!!
        currentFragment = targetFragment

    }

    private fun setDrawerLeftEdgeSize(activity: Activity, drawerLayout: DrawerLayout) {
        // 找到 ViewDragHelper 并设置 Accessible 为true
        //Right
        val leftDraggerField = drawerLayout.javaClass.getDeclaredField("mLeftDragger")
        leftDraggerField.isAccessible = true
        val leftDragger = leftDraggerField[drawerLayout] as ViewDragHelper

        // 找到 edgeSizeField 并设置 Accessible 为true
        val edgeSizeField = leftDragger.javaClass.getDeclaredField("mEdgeSize")
        edgeSizeField.isAccessible = true
        val edgeSize = edgeSizeField.getInt(leftDragger)

        // 设置新的边缘大小
        val displaySize = Point()
        activity.windowManager.defaultDisplay.getSize(displaySize)
        edgeSizeField.setInt(leftDragger, edgeSize.coerceAtLeast((displaySize.x)))
        val leftCallbackField = drawerLayout.javaClass.getDeclaredField("mLeftCallback")
        leftCallbackField.isAccessible = true
        val leftCallback = leftCallbackField[drawerLayout] as ViewDragHelper.Callback
        val peekRunnableField = leftCallback.javaClass.getDeclaredField("mPeekRunnable")
        peekRunnableField.isAccessible = true
        peekRunnableField[leftCallback] = Runnable {}
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
