package com.yanrou.dawnisland.content

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.SimpleCallback
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.SeriesRecyclerOnScrollListener
import com.yanrou.dawnisland.reply.ReplyDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*


/**
 * @author suche
 */
class SeriesContentActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var actionBar: ActionBar
    var id: String? = null
    private var forumName: String? = null
    private lateinit var smartRefreshLayout: SmartRefreshLayout
    private var multiTypeAdapter: MultiTypeAdapter? = null
    private lateinit var viewModel: SeriesContentViewModel
    private val jumpPopup: JumpPopup by lazy { JumpPopup(this) }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.series_content_menu, menu)
        val onlyPoSwitch = menu.findItem(R.id.only_po_switch)
        viewModel.OnlyPoLiveData.observe(this, Observer { t ->
            if (t) {
                onlyPoSwitch.title = "查看所有"
            } else {
                onlyPoSwitch.title = "只看po"
            }
        })
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series_content)
        //获取控件
        initView()
        actionBar.setDisplayHomeAsUpEnabled(true)
        val intent = intent
        id = intent.getStringExtra("id")
        //TODO 这里应该传入fid，然后通过DB类获取到板块名称，而不是直接传入板块名称
        forumName = intent.getStringExtra("forumTextView")
        Log.d(TAG, "onCreate: $id")
        toolbar.title = "A岛 · $forumName"
        toolbar.subtitle = ">>No.$id · adnmb.com"
        viewModel = ViewModelProvider(this).get(SeriesContentViewModel::class.java)
        viewModel.seriesId = id.toString()
        multiTypeAdapter = MultiTypeAdapter()
        multiTypeAdapter!!.register(ContentItem::class.java, ContentViewBinder(this@SeriesContentActivity))
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        //用于刷新的监听器
        recyclerView.addOnScrollListener(object : SeriesRecyclerOnScrollListener() {
            override fun onLoadMore() {
                viewModel.loadMore(layoutManager.findLastVisibleItemPosition())
            }
        })
        //用于报告页数的监听器
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.nowIndex = layoutManager.findLastVisibleItemPosition()
            }
        })
        recyclerView.adapter = multiTypeAdapter
        smartRefreshLayout.setEnableAutoLoadMore(false)
        smartRefreshLayout.setOnRefreshListener {
            viewModel.loadPreviousPage(layoutManager.findLastVisibleItemPosition())
        }

        smartRefreshLayout.setOnLoadMoreListener { viewModel.loadMore(layoutManager.findLastVisibleItemPosition()) }
        viewModel.listLiveData.observe(this, Observer { contentItems ->
            lifecycleScope.launch(Dispatchers.Default) {
                val oldList = multiTypeAdapter!!.items
                //创建一个新的表
                val newList: List<Any> = ArrayList<Any>(contentItems!!)
                val diffResult = DiffUtil.calculateDiff(DiffContentList(oldList, newList))
                multiTypeAdapter!!.items = newList
                withContext(Dispatchers.Main) {
                    diffResult.dispatchUpdatesTo(multiTypeAdapter!!)
                    smartRefreshLayout.finishRefresh()
                    smartRefreshLayout.finishLoadMore()
                }
            }
        })
        //告诉view model可以开始读取数据了
        viewModel.firstStart()
    }

    @Suppress("unused")
    private fun makeStatusBarTran(window: Window) { //这一步要做，因为如果这两个flag没有清除的话下面没有生效
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //设置状态栏(StatusBar)颜色透明
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        //设置导航栏(NavigationBar)颜色透明
//window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
    }

    private fun initView() {
        recyclerView = findViewById(R.id.series_content_recycleview)
        toolbar = findViewById(R.id.cotent_toolbar)
        setSupportActionBar(toolbar)
        actionBar = this.supportActionBar!!
        smartRefreshLayout = findViewById(R.id.smart_refresh)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.copy_series_id -> {
                copyToClipboard(this, ">>No." + viewModel.seriesId)
            }
            R.id.jump_page -> {
                val page = viewModel.getNowPage((recyclerView.layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()) ?: 1
                XPopup.Builder(baseContext)
                        .setPopupCallback(object : SimpleCallback() {
                            override fun beforeShow() {
                                super.beforeShow()
                                jumpPopup.updatePages(page, viewModel.maxPage)
                            }
                        })
                        .asCustom(jumpPopup)
                        .show()
                        .dismissWith {
                            if (jumpPopup.submit) {
                                Timber.i("jumping to ${jumpPopup.targetPage}")
                                viewModel.jumpPage(jumpPopup.targetPage)
                            }
                        }
            }
            R.id.reply -> {
                val replyDialog = ReplyDialog()
                val bundle = Bundle()
                bundle.putString("seriesId", id)
                replyDialog.arguments = bundle
                val ft = supportFragmentManager.beginTransaction()
                if (supportFragmentManager.findFragmentByTag("reply") == null) {
                    ft.add(replyDialog, "reply")
                    ft.commit()
                }
            }
            R.id.only_po_switch -> {
                viewModel.switchToOnlyPo()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun copyToClipboard(context: Context, content: CharSequence?) {
        val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(null, content)) //参数一：标签，可为空，参数二：要复制到剪贴板的文本
    }

    companion object {
        private const val TAG = "SeriesContentActivity"
    }

}