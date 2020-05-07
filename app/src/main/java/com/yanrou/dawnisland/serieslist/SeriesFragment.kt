package com.yanrou.dawnisland.serieslist

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.tencent.bugly.crashreport.CrashReport
import com.yanrou.dawnisland.*
import com.yanrou.dawnisland.content.SeriesContentFragment
import com.yanrou.dawnisland.settings.SettingsActivity
import com.yanrou.dawnisland.util.DiffCallback
import kotlinx.android.synthetic.main.fragment_series.*
import timber.log.Timber


class SeriesFragment : Fragment() {
    private var seriesListAdapter: MultiTypeAdapter? = null
    private val viewModel by viewModels<SeriesViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO 暂时的解决方案，最终应该把第一次获取放到一个globa里面
        Fid2Name.db.observe(viewLifecycleOwner, Observer {
            Timber.d(it.toString())
            viewModel.getFirstPage()
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
            register(SeriesCardView::class.java, SeriesCardViewBinder(
                    {
                        viewModel.getNextPage()
                    },
                    { seriesId, forumName ->
                        val fragment = SeriesContentFragment.newInstance(seriesId, forumName)
                        requireParentFragment().parentFragmentManager
                                .beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragmentContainer, fragment, "series_content")
                                .addToBackStack("series_content")
                                .commit()
                    }
            ))
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
        menu.clear()
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
