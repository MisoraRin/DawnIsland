package com.yanrou.dawnisland.content


import android.app.Application
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
    private val nextPage = true
    private val frontPage = false
    private lateinit var model: SeriesContentModel
    val listLiveData = MutableLiveData<List<ContentItem>>()
    private val contentList = ArrayList<ContentItem>()
    private var onlyPoList = ArrayList<ContentItem>()
    var nowIndex = 0
    lateinit var seriesId: String
    private var loading = false
    var onlyPoLiveData = MutableLiveData(false)
    lateinit var referenceHandler: ReferenceHandler

    /**
     * 在这里返回总页数
     *
     * @return 总页数
     */
    val maxPage
        get() = model.maxPage

    /**
     * activity onCreate完成以后调用这个方法
     */
    fun firstStart() {
        model = SeriesContentModel(seriesId)
        getContent(1, nextPage)
    }

    /**
     * 加载更多
     */
    fun loadMore() {
        getNowPage(contentList.lastIndex)?.let { getContent(it, nextPage) }
    }

    /**
     * 下拉逻辑
     */
    fun loadPreviousPage(index: Int) {
        getNowPage(index)?.let { if (it - 1 > 0) getContent(it - 1, frontPage) }
    }

    /**
     * 只看po
     */
    fun switchToOnlyPo() {
        if (onlyPoLiveData.value!!) {
            listLiveData.value = contentList
            onlyPoLiveData.value = false
        } else {
            listLiveData.value = onlyPoList
            onlyPoLiveData.value = true
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
        val handle = CoroutineExceptionHandler { _, throwable -> Timber.d(throwable) }
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
                    Timber.d("已到最后一页")
                }
            } else if (result is String) {
                //串已被删除
                Timber.d("串已被删除")
            }

            withContext(Dispatchers.Main) {
                if (onlyPoLiveData.value!!) {
                    listLiveData.value = onlyPoList
                } else {
                    listLiveData.value = contentList
                }
            }
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

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val lineHeight = sharedPreferences.getInt(CardViewFactory.LINE_HEIGHT, 0)
        val segGap = sharedPreferences.getInt(CardViewFactory.SEG_GAP, 0)

        return replysBeans.map(convertToContentItem(lineHeight, segGap))
    }

    private fun convertToContentItem(lineHeight: Int, segGap: Int): (ReplysBean) -> ContentItem {
        return {
            ContentItem().apply {

                time = transformTime(it.now)

                cookie = transformCookie(it.userid, it.admin, model::isPo)

                content = transformContent(it.content).apply {
                    addLineHeightAndSegGap(lineHeight, segGap)
                    addQuoteSpan(referenceHandler)
                }

                sega = if (it.sage == 1) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                seriesId = it.seriesId

                if (it.ext != null && "" != it.ext) {
                    hasImage = true
                    imgurl = it.img + it.ext
                } else {
                    hasImage = false
                }

                val titleAndName = transformTitleAndName(it.title, it.name)
                hasTitleOrName = titleAndName != ""
                this.titleAndName = titleAndName
            }
        }
    }


    fun addFeed(subscriptionId: String, tid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ServiceClient.addFeed(subscriptionId, tid).run {
                // TODO: check failure response, and use msg
                /** res:
                 *  "\u53d6\u6d88\u8ba2\u9605\u6210\u529f!"
                 */
                val msg = StringEscapeUtils.unescapeJava(this.replace("\"", ""))
                Timber.i(msg)
            }
        }
    }

}