package com.yanrou.dawnisland

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentPagerAdapter
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.susion.rabbit.Rabbit.open
import com.tencent.bugly.crashreport.CrashReport
import com.yanrou.dawnisland.feed.FeedFragment
import com.yanrou.dawnisland.json2class.ForumJson
import com.yanrou.dawnisland.json2class.ForumJson.ForumsBean
import com.yanrou.dawnisland.serieslist.SeriesFragment
import com.yanrou.dawnisland.settings.SettingsActivity
import com.yanrou.dawnisland.trend.TrandFragment
import com.yanrou.dawnisland.util.HttpUtil
import com.yanrou.dawnisland.util.ReadableTime
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var forumList: RecyclerView? = null
    private var viewPager: ViewPager? = null
    var appBarLayout: AppBarLayout? = null
    private var forumsList: MutableList<ForumsBean> = ArrayList()
    private var forumAdapter: ForumAdapter? = null

    /**
     * 记录当前的页数
     */
    var sharedPreferences: SharedPreferences? = null
    var drawerLayout: DrawerLayout? = null
    var seriesFragment: SeriesFragment? = null
    var trandFragment: TrandFragment? = null
    var feedFragment: FeedFragment? = null
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
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("DawnIsland", Context.MODE_PRIVATE)
        open(true, this)
        forumAdapter = ForumAdapter(forumsList, applicationContext)

        /**
         * 初始化
         */
        ReadableTime.initialize(this)
        view
        /**
         * 新的状态栏透明方案
         */
        val window = window
        //这一步最好要做，因为如果这两个flag没有清除的话下面没有生效
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //设置状态栏(StatusBar)颜色透明
        window.statusBarColor = Color.TRANSPARENT
        //设置导航栏(NavigationBar)颜色透明
        //window.setNavigationBarColor(Color.TRANSPARENT);

//        LitePal.getDatabase();
        Timber.d("onCreate: %s", resources.getStringArray(R.array.face).contentToString())
        /**
         * 设置抽屉滑动响应宽度
         */
        setDrawerLeftEdgeSize(this, drawerLayout, 1f)
        /**
         * 标题栏组件初始化
         */
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationOnClickListener { drawerLayout!!.openDrawer(GravityCompat.START) }
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.toolbar_home_as_up)
        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.coolapsing_toolbar)
        Timber.d("onCreate: 重新调用了onCreat方法%s", supportFragmentManager.fragments)
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
        }
        toolbar!!.setOnClickListener { seriesFragment!!.refresh() }
        viewPager!!.adapter = myViewPagerAdapter
        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                Timber.d("onPageSelected: $position")
                if (position == 0) {
                    collapsingToolbarLayout.title = forumName
                }
                if (position == 1) {
                    appBarLayout!!.setExpanded(false)
                    forumName = collapsingToolbarLayout.title.toString()
                    collapsingToolbarLayout.title = "A岛热榜"
                }
                if (position == 2) {
                    //appBarLayout.setExpanded(false);
                    //subtitleCollapsingToolbarLayout.setTitle("订阅");
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        forumAdapter!!.setChangeForum { id: Int, name: String? ->
            collapsingToolbarLayout.title = name
            drawerLayout!!.closeDrawers()
            seriesFragment!!.changeForum(id)
            Timber.d("changeForum: $seriesFragment")
        }
        val layoutManager2 = LinearLayoutManager(this)
        forumList!!.layoutManager = layoutManager2
        forumList!!.adapter = forumAdapter
        getForumList()

        // resource initialization
        resourceInitialization()
    }

    val view: Unit
        get() {
            toolbar = findViewById(R.id.toolbar)
            drawerLayout = findViewById(R.id.drawer_layout)
            forumList = findViewById(R.id.forum_list)
            viewPager = findViewById(R.id.main_page_viewer)
            appBarLayout = findViewById(R.id.appbar_layout)
        }

    fun getForumList() {
        //本地已有，直接读取
        Timber.d("getForumList: 开始读取")
        if (sharedPreferences!!.contains("ForumJson")) {
            Timber.d("getForumList: 本地已有")
            val ForumJson = sharedPreferences!!.getString("ForumJson", "")
            val forumJsonList = Gson().fromJson<List<ForumJson>>(ForumJson, object : TypeToken<List<ForumJson?>?>() {}.type)
            val allForum: MutableList<ForumsBean> = ArrayList()
            for (i in forumJsonList.indices) {
                allForum.addAll(forumJsonList[i].forums)
            }
            Log.d(TAG, "onResponse: " + allForum.size)
            Fid2Name.setDB(allForum)
            forumsList.addAll(allForum)
            runOnUiThread { forumAdapter!!.notifyDataSetChanged() }
            Log.d(TAG, "getForumList: 调用碎片方法")
            //seriesFragment.getNewPage();
            //trandFragment.startGetTrend();
        } else { //否则从网络加载
            Log.d(TAG, "getForumList: 网络加载开始")
            HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/getForumList", object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "onFailure: 获取板块列表失败")
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

    private fun setDrawerLeftEdgeSize(activity: Activity?, drawerLayout: DrawerLayout?, displayWidthPercentage: Float) {
        if (activity == null || drawerLayout == null) {
            return
        }
        try {
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
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (displaySize.x *
                    displayWidthPercentage).toInt()))
            val leftCallbackField = drawerLayout.javaClass.getDeclaredField("mLeftCallback")
            leftCallbackField.isAccessible = true
            val leftCallback = leftCallbackField[drawerLayout] as ViewDragHelper.Callback
            val peekRunnableField = leftCallback.javaClass.getDeclaredField("mPeekRunnable")
            peekRunnableField.isAccessible = true
            val nullRunnalbe = Runnable {}
            peekRunnableField[leftCallback] = nullRunnalbe
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalAccessException) {
        }
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