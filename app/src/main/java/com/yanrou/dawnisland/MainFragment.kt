package com.yanrou.dawnisland

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.angcyo.tablayout.delegate.ViewPager1Delegate
import com.drakeet.multitype.MultiTypeAdapter
import com.yanrou.dawnisland.forum.ForumDiffCallback
import com.yanrou.dawnisland.forum.ForumGroupViewBinder
import com.yanrou.dawnisland.forum.ForumItemViewBinder
import com.yanrou.dawnisland.forum.ForumViewModel
import com.yanrou.dawnisland.json2class.ForumJson
import com.yanrou.dawnisland.json2class.ForumsBean
import com.yanrou.dawnisland.serieslist.SeriesFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private var forumAdapter: MultiTypeAdapter? = null
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
                (childFragmentManager.findFragmentByTag(makeFragmentName(R.id.viewPager, 0)) as SeriesFragment).changeForum(id)
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

        viewPager.adapter = MyViewPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.drawerListener = { drawerLayout.openDrawer(GravityCompat.START) }

        dslTabLayout.configTabLayoutConfig {
            onGetIcoStyleView = { itemView, _ ->
                itemView
            }
        }
        ViewPager1Delegate.install(viewPager, dslTabLayout)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        forumAdapter = null
    }


    private fun makeFragmentName(viewId: Int, id: Long) = "android:switcher:$viewId:$id"

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
