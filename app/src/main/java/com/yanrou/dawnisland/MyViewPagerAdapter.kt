package com.yanrou.dawnisland

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.yanrou.dawnisland.feed.FeedFragment
import com.yanrou.dawnisland.serieslist.SeriesFragment
import com.yanrou.dawnisland.trend.TrendFragment

class MyViewPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SeriesFragment()
            1 -> TrendFragment()
            2 -> FeedFragment()
            else -> throw IllegalStateException("索引越界")
        }
    }

    override fun getCount() = 3


}