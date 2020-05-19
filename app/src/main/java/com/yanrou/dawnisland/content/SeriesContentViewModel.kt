package com.yanrou.dawnisland.content


import android.annotation.SuppressLint
import android.app.Application
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.yanrou.dawnisland.json2class.ReplysBean
import com.yanrou.dawnisland.serieslist.CardViewFactory
import com.yanrou.dawnisland.span.ReferenceClickableSpan
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
    lateinit var referenceHandler: (id: String) -> Unit

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
    @SuppressLint("BinaryOperationInTimber")
    private fun formatContent(replysBeans: List<ReplysBean>): List<ContentItem> {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val lineHeight = sharedPreferences.getInt(CardViewFactory.LINE_HEIGHT, 0)
        val segGap = sharedPreferences.getInt(CardViewFactory.SEG_GAP, 0)

        val contentItems = ArrayList<ContentItem>(20)

        for (temp in replysBeans) {
            val contentItem = ContentItem()

            contentItem.time = transformTime(temp.now)

            contentItem.cookie = transformCookie(temp.userid, temp.admin, model::isPo)

            contentItem.content = transformContent(temp.content).apply {
                addLineHeightAndSegGap(lineHeight, segGap)
                val spans = getSpans(0, length, ForegroundColorSpan::class.java)
                for (span in spans) {
                    Timber.d("引用颜色" + span.foregroundColor + "内容" + this.subSequence(this.getSpanStart(span), this.getSpanEnd(span)))
                    if (this.subSequence(this.getSpanStart(span), this.getSpanEnd(span)).contains(">>No.")) {
                        setSpan(ReferenceClickableSpan(subSequence(this.getSpanStart(span), this.getSpanEnd(span)).substring(5), referenceHandler), getSpanStart(span), getSpanEnd(span), Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                }
            }

            contentItem.sega = if (temp.sage == 1) {
                View.VISIBLE
            } else {
                View.GONE
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