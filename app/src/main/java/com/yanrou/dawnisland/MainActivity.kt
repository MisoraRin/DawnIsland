package com.yanrou.dawnisland

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.tencent.bugly.crashreport.CrashReport
import com.yanrou.dawnisland.feed.FeedFragment
import com.yanrou.dawnisland.forum.ForumViewModel
import com.yanrou.dawnisland.json2class.ForumsBean
import com.yanrou.dawnisland.serieslist.SeriesFragment
import com.yanrou.dawnisland.settings.SettingsActivity
import com.yanrou.dawnisland.trend.TrandFragment
import com.yanrou.dawnisland.util.ReadableTime
import com.yanrou.dawnisland.util.getForumList
import timber.log.Timber
import java.util.*
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.susion.rabbit.Rabbit.open
import com.yanrou.dawnisland.json2class.ForumJson
import com.yanrou.dawnisland.json2class.ForumJson.ForumsBean
import com.yanrou.dawnisland.util.HttpUtil
import com.yanrou.dawnisland.util.ReadableTime
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import com.yanrou.dawnisland.util.ReadableTime
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var seriesFragment: SeriesFragment? = null
    private var trandFragment: TrandFragment? = null
    private var feedFragment: FeedFragment? = null
    var forumName: String? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.cookie_button -> {
                val intent1 = Intent(this@MainActivity, CookiesManageActivity::class.java)
                startActivity(intent1)
            }
            R.id.crash -> CrashReport.testJavaCrash()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val forumViewModel = ViewModelProvider(this).get(ForumViewModel::class.java)
        setContentView(R.layout.activity_main)
//        open(true, this)

        /**
         * 初始化
         */
        ReadableTime.initialize(this)
        /**
         * 新的状态栏透明方案
         */
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏(StatusBar)颜色透明
            statusBarColor = Color.TRANSPARENT
            //设置导航栏(NavigationBar)颜色透明
            //window.setNavigationBarColor(Color.TRANSPARENT);
        }
        /**
         * 设置抽屉滑动响应宽度
         */
        setDrawerLeftEdgeSize(this, drawer_layout, 1f)
        /**
         * 标题栏组件初始化
         */
        toolbar.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener { drawer_layout!!.openDrawer(GravityCompat.START) }
            setOnClickListener { seriesFragment!!.refresh() }
        }
        supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.toolbar_home_as_up)
        }

        val myViewPagerAdapter = MyViewPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        if (supportFragmentManager.fragments.size == 0) {
            seriesFragment = SeriesFragment()
            myViewPagerAdapter.addFragment(seriesFragment)
            trandFragment = TrandFragment.newInstance(this)
            myViewPagerAdapter.addFragment(trandFragment)
            feedFragment = FeedFragment()
            myViewPagerAdapter.addFragment(feedFragment)
        } else {
            myViewPagerAdapter.addFragments(supportFragmentManager.fragments)
            supportFragmentManager.fragments.apply {
                this[0]?.let { seriesFragment = it as SeriesFragment }
                this[1]?.let { trandFragment = it as TrandFragment }
                //this[2]?.let { feedFragment = it as FeedFragment}
            }

        }
        main_page_viewer.apply {
            adapter = myViewPagerAdapter
            addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                override fun onPageSelected(position: Int) {
                    Timber.d("onPageSelected: $position")
                    if (position == 0) {
                        coolapsing_toolbar.title = forumName
                    }
                    if (position == 1) {
                        appbar_layout.setExpanded(false)
                        forumName = coolapsing_toolbar.title.toString()
                        coolapsing_toolbar.title = "A岛热榜"
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }

        val forumAdapter = PinnedHeaderAdapter().apply {
            register(ForumsBean::class.java, ForumItemViewBinder(this@MainActivity) { id: Int, name: String? ->
                drawer_layout!!.closeDrawers()
                coolapsing_toolbar.title = name
                seriesFragment!!.changeForum(id)
            })
            register(ForumJson::class.java, ForumGroupViewBinder {
                Timber.d("$it was clicked")
                forumViewModel.refreshForumGroupExpandState(it)
            })
        }

        forum_list.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = forumAdapter
            //addItemDecoration(PinnedHeaderItemDecoration())
        }
        forumViewModel.forumOnView.observe(this, Observer {
            val oldList = forumAdapter.items
            //创建一个新的表
            val newList: List<Any> = ArrayList(it!!)
            val diffResult = DiffUtil.calculateDiff(ForumDiffCallback(oldList, newList), false)
            forumAdapter.items = newList
            diffResult.dispatchUpdatesTo(forumAdapter)
        })

    }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val json = response.body!!.string()
                    sharedPreferences!!.edit().putString("ForumJson", json).apply()
                    getForumList()
                }
            })
        }
    }

    private fun setDrawerLeftEdgeSize(activity: Activity, drawerLayout: DrawerLayout, displayWidthPercentage: Float) {
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
            edgeSizeField.setInt(leftDragger, edgeSize.coerceAtLeast((displaySize.x *
                    displayWidthPercentage).toInt()))
            val leftCallbackField = drawerLayout.javaClass.getDeclaredField("mLeftCallback")
            leftCallbackField.isAccessible = true
            val leftCallback = leftCallbackField[drawerLayout] as ViewDragHelper.Callback
            val peekRunnableField = leftCallback.javaClass.getDeclaredField("mPeekRunnable")
            peekRunnableField.isAccessible = true
        peekRunnableField[leftCallback] = Runnable {}
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    private fun resourceInitialization() {
        // default subscriptionId
        PreferenceManager.getDefaultSharedPreferences(baseContext).run {
            val mFeedsId = getString("subscriber_id", "666")
            if (mFeedsId == "666") {
                edit().putString("subscriber_id", UUID.randomUUID().toString()).apply()
            }
        }
    }
}