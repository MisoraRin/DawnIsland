package com.yanrou.dawnisland.content


import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.yanrou.dawnisland.json2class.ReplysBean
import com.yanrou.dawnisland.serieslist.CardViewFactory
import com.yanrou.dawnisland.util.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.text.StringEscapeUtils
import timber.log.Timber

class SeriesContentViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "SeriesContentViewModel"
    private val NEXT_PAGE = true
    private val FRONT_PAGE = false
    private lateinit var model: SeriesContentModel
    val listLiveData = MutableLiveData<List<ContentItem>>()
    private val contentList = ArrayList<ContentItem>()
    private var onlyPoList = ArrayList<ContentItem>()
    var nowIndex = 0
    lateinit var seriesId: String
    var loading = false
    var OnlyPoLiveData = MutableLiveData<Boolean>()

    /**
     * 在这里返回总页数
     *
     * @return 总页数
     */
    val maxPage get() = model.maxPage

    init {
        OnlyPoLiveData.value = false
    }
    /**
     * activity onCreate完成以后调用这个方法
     */
    fun firstStart() {
        model = SeriesContentModel(seriesId)
        getContent(1, NEXT_PAGE)
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(getApplication())

    }

    /**
     * 加载更多
     */
    fun loadMore(index: Int) {
        getNowPage(contentList.lastIndex)?.let { getContent(it, NEXT_PAGE) }
    }

    /**
     * 下拉逻辑
     */
    fun loadPreviousPage(index: Int) {
        getNowPage(index)?.let { if (it - 1 > 0) getContent(it - 1, FRONT_PAGE) }
    }

    /**
     * 只看po
     */
    fun switchToOnlyPo() {
        if (OnlyPoLiveData.value!!) {
            listLiveData.value = contentList
            OnlyPoLiveData.value = false
        } else {
            listLiveData.value = onlyPoList
            OnlyPoLiveData.value = true
        }

    }
    /**
     * 处理跳页逻辑
     *
     * @param page 将要跳到的页数
     */
    fun jumpPage(page: Int) {
        contentList.clear()
        onlyPoList.clear()
        model.clearIds()
        getContent(page, false)
    }

    /**
     * 在这里返回当前看到的页数,实现方式是通过获取当前可见的最后一个item的pos，然后找到对应的串号，再问model这个串在第几页
     * @return 当前看到的页数
     */
    fun getNowPage(index: Int): Int? {
        return model.getPageBySeries(contentList[index].seriesId)
    }



    /**
     * 不需要考虑具体能得到什么数据，只需要告诉model需要新的数据就可以
     * 传入的参数是获取前一页还是获取后一页,true是获取后一页
     */
    @Suppress("UNCHECKED_CAST")
    private fun getContent(page: Int, isNext: Boolean) {
        val handle = CoroutineExceptionHandler { _, throwable -> Log.e(TAG, "Caught $throwable") }
        if (loading) {
            return
        }
        loading = true
        viewModelScope.launch(handle) {
            val result = model.getSeriesContent(page, isNext)
            if (result is List<*>) {
                if (result.size > 0) {
                    // 表示有数据
                    withContext(Dispatchers.Default) {
                        val rawlist = formatContent(result as List<ReplysBean>)
                        if (isNext) {
                            contentList.addAll(rawlist)
                            onlyPoList.addAll(getOnlyPoList(rawlist))
                        } else {
                            contentList.addAll(0, rawlist)
                            onlyPoList.addAll(0, getOnlyPoList(rawlist))
                        }
                    }
                } else {
                    //没数据，到最后一页了
                    Log.d(TAG, "已到最后一页")
                }
            } else if (result is String) {
                //串已被删除
                Log.d(TAG, "串已被删除")
            }
            //在主线程中执行，用于刷新view,由于已经在Mian中了所以使用setValue就好
            withContext(Dispatchers.Main) {
                if (OnlyPoLiveData.value!!) {
                    listLiveData.value = onlyPoList
                } else {
                    listLiveData.value = contentList
                }
            }
            Log.d(TAG, "在此，表示lodaing完成")
            loading = false
        }

    }

    /**
     * 获取一个只有po的列表
     */
    private fun getOnlyPoList(rawlist: List<ContentItem>): List<ContentItem> {
        return rawlist.filter { model.isPo(it.cookie.toString()) }
    }

    /**
     * 用于处理Reply数据以显示到view层
     */
    private fun formatContent(replysBeans: List<ReplysBean>): List<ContentItem> {

        val contentItems = ArrayList<ContentItem>()

        var temp: ReplysBean
        //在这里预处理内容，保证显示时可以直接显示
        for (i in replysBeans.indices) {
            temp = replysBeans[i]
            val contentItem = ContentItem()
            /*
              处理时间
             */
            contentItem.time = transformTime(temp.now)
            /*
              处理饼干
              PO需要加粗
              普通饼干是灰色，po是黑色，红名是红色
             */

            contentItem.cookie = transformCookie(temp.userid, temp.admin, model::isPo)

            /*
              处理内容
              主要是处理引用串号
             */
            val quotes = extractQuote(temp.content)

            contentItem.quotes = quotes


            val noQuotesContent = removeQuote(temp.content)

            // will also hide [h]
            contentItem.content = transformContent(noQuotesContent)

            //添加段间距和行间距，由于需要读取设置所以先放到这里
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
            val lineHeight = sharedPreferences.getInt(CardViewFactory.LINE_HEIGHT, 0)
            val segGap = sharedPreferences.getInt(CardViewFactory.SEG_GAP, 0)
            addLineHeightAndSegGap(contentItem, lineHeight, segGap)


            if (temp.sage == 1) {
                contentItem.sega = View.VISIBLE
            } else {
                contentItem.sega = View.GONE
            }

            contentItem.seriesId = temp.seriesId

            if (temp.ext != null && "" != temp.ext) {
                contentItem.hasImage = true
                contentItem.imgurl = temp.img + temp.ext
            } else {
                contentItem.hasImage = false
            }

            val titleAndName = transformTitleAndName(temp.title, temp.name)
            if (titleAndName != "") {
                contentItem.hasTitleOrName = true
            }

            contentItem.titleAndName = titleAndName
            contentItems.add(contentItem)
        }
        return contentItems
    }

    fun addFeed(subscriptionId: String, tid: String) {
        Timber.i("Adding Feed $tid")
        viewModelScope.launch(Dispatchers.IO) {
            ServiceClient.addFeed(subscriptionId, tid).run {
                // TODO: check failure response, and use msg
                /** res:
                 *  "\u53d6\u6d88\u8ba2\u9605\u6210\u529f!"
                 */
                val msg = StringEscapeUtils.unescapeJava(this.replace("\"", ""))
                Timber.i("$msg")
            }
        }
    }

}