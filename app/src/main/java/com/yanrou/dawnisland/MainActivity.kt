package com.yanrou.dawnisland

import android.app.Activity
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.yanrou.dawnisland.feed.FeedFragment
import com.yanrou.dawnisland.forum.ForumViewModel
import com.yanrou.dawnisland.serieslist.SeriesFragment
import com.yanrou.dawnisland.trend.TrendFragment
import com.yanrou.dawnisland.util.ReadableTime
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {
    val forumViewModel by viewModels<ForumViewModel>()
    private var trendFragment: TrendFragment? = null
    private var feedFragment: FeedFragment? = null
    var forumName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(forumViewModel.toString())

        setContentView(R.layout.activity_main)

        val ts = supportFragmentManager.beginTransaction()
        val seriesFragment = supportFragmentManager.findFragmentByTag("series")
        if (seriesFragment == null) {
            ts.add(R.id.fragmentContainer, SeriesFragment.newInstance(), "series").commit()
        }


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

        resourceInitialization()
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