package com.yanrou.dawnisland.serieslist

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.tencent.bugly.crashreport.CrashReport
import com.yanrou.dawnisland.*
import com.yanrou.dawnisland.forum.ForumDiffCallback
import com.yanrou.dawnisland.forum.ForumGroupViewBinder
import com.yanrou.dawnisland.forum.ForumItemViewBinder
import com.yanrou.dawnisland.forum.ForumViewModel
import com.yanrou.dawnisland.json2class.ForumJson
import com.yanrou.dawnisland.json2class.ForumsBean
import com.yanrou.dawnisland.settings.SettingsActivity
import com.yanrou.dawnisland.util.DiffCallback
import kotlinx.android.synthetic.main.fragment_series.*
import timber.log.Timber


class SeriesFragment : Fragment() {
    private var seriesListAdapter: MultiTypeAdapter? = null
    private val forumViewModel by activityViewModels<ForumViewModel>()
    private val viewModel by viewModels<SeriesViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Timber.d(forumViewModel.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d(viewModel.toString())
        /**
         * 设置抽屉滑动响应宽度
         */
        setDrawerLeftEdgeSize(requireActivity(), drawer_layout)
        /**
         * 标题栏组件初始化
         */
        toolbar.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(this)
            setNavigationOnClickListener { drawer_layout!!.openDrawer(GravityCompat.START) }
            setOnClickListener { refresh() }
        }
        (requireActivity() as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.toolbar_home_as_up)
        }

        //TODO 暂时的解决方案，最终应该把第一次获取放到一个globa里面
        Fid2Name.db.observe(viewLifecycleOwner, Observer {
            Timber.d(it.toString())
            viewModel.getFirstPage()
        })
        val forumAdapter = MultiTypeAdapter().apply {
            register(ForumsBean::class.java, ForumItemViewBinder(requireContext()) { id: Int, name: String? ->
                drawer_layout!!.closeDrawers()
                coolapsing_toolbar.title = name
                changeForum(id)
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

        smartrefresh!!.setEnableAutoLoadMore(false)

        smartrefresh!!.setOnLoadMoreListener {
            smartrefresh!!.finishLoadMore(0)
        }
        smartrefresh!!.setOnRefreshListener {
            refresh()
        }
        /**
         * 这里是添加layoutManager
         */
        val layoutManager = LinearLayoutManager(activity)
        series_list_fragment!!.layoutManager = layoutManager
        /**
         * 添加adapter
         */
        seriesListAdapter = MultiTypeAdapter().apply {
            register(SeriesCardView::class.java, SeriesCardViewBinder {
                viewModel.getNextPage()
                Timber.d("这里也有好好执行")
            })
            register(FooterView::class.java, FooterViewBinder())
        }

        series_list_fragment!!.adapter = seriesListAdapter
        viewModel.seriesCards.observe(viewLifecycleOwner, Observer {
            updateAdapter(it)
            smartrefresh!!.closeHeaderOrFooter()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> {
                val intent = Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.cookie_button -> {
                val intent1 = Intent(requireContext(), CookiesManageActivity::class.java)
                startActivity(intent1)
            }
            R.id.crash -> CrashReport.testJavaCrash()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun changeForum(fid: Int) {
        viewModel.page = 1
        series_list_fragment!!.scrollToPosition(0)
        updateAdapter(emptyList())
        smartrefresh!!.finishRefresh()
        smartrefresh!!.autoRefreshAnimationOnly()
        viewModel.changeForum(fid)
    }

    private fun refresh() {
        viewModel.page = 1
        series_list_fragment!!.scrollToPosition(0)
        smartrefresh!!.autoRefreshAnimationOnly()
        viewModel.refresh()
    }

    private fun updateAdapter(newList: List<SeriesCardView>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(seriesListAdapter!!.items, newList))
            seriesListAdapter!!.items = newList.toList()
            diffResult.dispatchUpdatesTo(seriesListAdapter!!)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SeriesFragment()
    }


}
