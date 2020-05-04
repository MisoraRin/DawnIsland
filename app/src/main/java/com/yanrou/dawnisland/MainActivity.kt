package com.yanrou.dawnisland

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.yanrou.dawnisland.feed.FeedFragment
import com.yanrou.dawnisland.forum.ForumViewModel
import com.yanrou.dawnisland.serieslist.SeriesFragment
import com.yanrou.dawnisland.trend.TrendFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {
    val forumViewModel by viewModels<ForumViewModel>()
    private var currentFragment: Fragment? = null
    private val FEED_FRAGMENT_TAG = "feed"
    private val SERIES_FRAGMENT_TAG = "series"
    private val TREND_FRAGMENT_TAG = "trend"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ts = supportFragmentManager.beginTransaction()
        val seriesFragment = supportFragmentManager.findFragmentByTag("series")
        if (seriesFragment == null) {
            currentFragment = SeriesFragment.newInstance().apply {
                ts.add(R.id.fragmentContainer, this, "series").commit()
            }
        }

        dslTabLayout.configTabLayoutConfig {
            onSelectIndexChange = { fromIndex, selectList, reselect, fromUser ->
                Timber.d("$fromIndex is changed,reselect is $reselect")

            }
            onSelectItemView =
                    { _, index, selected, fromUser ->
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

        resourceInitialization()
    }

    private fun switchFragment(fragmentTag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        //先判断有没有添加进去
        if (supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
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
            transaction.add(R.id.fragmentContainer, targetFragment, fragmentTag)
        } else {
            val targetFragment = supportFragmentManager.findFragmentByTag(fragmentTag)!!
            currentFragment?.let { transaction.hide(currentFragment!!) }
            transaction.show(targetFragment)
        }
        transaction.commit()
        supportFragmentManager.executePendingTransactions()
        val targetFragment = supportFragmentManager.findFragmentByTag(fragmentTag)!!
        currentFragment = targetFragment

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