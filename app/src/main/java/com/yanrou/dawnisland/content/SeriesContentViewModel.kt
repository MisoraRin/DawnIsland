package com.yanrou.dawnisland.content


import android.app.Application
import android.graphics.Color
import android.graphics.Typeface
import android.text.*
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yanrou.dawnisland.json2class.ReplysBean
import com.yanrou.dawnisland.span.SegmentSpacingSpan
import com.yanrou.dawnisland.util.ReadableTime
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeriesContentViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "SeriesContentViewModel"
    private val NEXT_PAGE = true
    private val FRONT_PAGE = false
    private lateinit var model: SeriesContentModel
    val listLiveData = MutableLiveData<List<ContentItem>>()
    private val contentList = ArrayList<ContentItem>()
    var onlyPoList = emptyList<ContentItem>()
    var nowIndex = 0
    lateinit var seriesId: String
    var loading = false;
    /**
     * activity onCreate完成以后调用这个方法
     */
    fun firstStart() {
        model = SeriesContentModel(seriesId)
        getContent(1, NEXT_PAGE)
    }

    /**
     * 加载更多
     */
    fun loadMore(index: Int) {
        Log.d(TAG, "loadMore" + getNowPage())
        getNowPage()?.let { getContent(it, NEXT_PAGE) }
    }

    /**
     * 下拉逻辑
     */
    fun refresh(index: Int) {
        getNowPage()?.let { getContent(it, FRONT_PAGE) }
    }

    /**
     * 处理跳页逻辑
     *
     * @param page 将要跳到的页数
     */
    fun jumpPage(page: Int) {}

    /**
     * 在这里返回当前看到的页数,实现方式是通过获取当前可见的最后一个item的pos，然后找到对应的串号，再问model这个串在第几页
     * @return 当前看到的页数
     */
    fun getNowPage(): Int? {
        return model.getPageBySeries(contentList[nowIndex].seriesId)
    }

    /**
     * 在这里返回总页数
     *
     * @return 总页数
     */
    val totalPage: Int
        get() = 0

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
                        contentList.addAll(formatContent(result as List<ReplysBean>))
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
                listLiveData.value = contentList
            }
            Log.d(TAG, "在此，表示lodaing完成")
            loading = false
        }

    }

    /**
     * 获取一个只有po的列表
     */
    private fun getOnlyPoList() {
        onlyPoList = contentList.filter { model.isPo(it.seriesId) }
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
            contentItem.time = ReadableTime.getDisplayTime(temp.now)
            /*
              处理饼干
              PO需要加粗
              普通饼干是灰色，po是黑色，红名是红色
             */
            val cookie = SpannableStringBuilder(temp.userid)
            if (temp.admin == 1) {
                val adminColor = ForegroundColorSpan(Color.parseColor("#FF0F0F"))
                cookie.setSpan(adminColor, 0, cookie.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            } else if (model.isPo(temp.userid)) {
                val poColor = ForegroundColorSpan(Color.parseColor("#000000"))
                cookie.setSpan(poColor, 0, cookie.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            if (model.isPo(temp.userid)) {
                val styleSpanBold = StyleSpan(Typeface.BOLD)
                cookie.setSpan(styleSpanBold, 0, cookie.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            contentItem.cookie = cookie
            /*
              处理内容
              主要是处理引用串号
             */
            val contentSpan = SpannableStringBuilder(Html.fromHtml(temp.content))
            /*
              这一句是添加段间距
              if用来判断作者有没有自己加空行，加了的话就不加段间距
              TODO 但是依然要添加行间距和字间距
             */
            if (!contentSpan.toString().contains("\n\n")) {
                contentSpan.setSpan(SegmentSpacingSpan(0, 20), 0, contentSpan.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            var index = -1
            var hideStart: Int
            var hideEnd: Int
            hideStart = contentSpan.toString().indexOf("[h]")
            hideEnd = contentSpan.toString().indexOf("[/h]")
            while (hideStart != -1 && hideEnd != -1 && hideStart < hideEnd) {
                contentSpan.delete(hideStart, hideStart + 3)
                Log.d(TAG, "onResponse: " + contentSpan.toString().substring(hideEnd))
                contentSpan.delete(hideEnd - 3, hideEnd + 1)
                val backgroundColorSpan = BackgroundColorSpan(Color.parseColor("#555555"))
                val foregroundColorSpan = ForegroundColorSpan(Color.TRANSPARENT)
                contentSpan.setSpan(backgroundColorSpan, hideStart, hideEnd - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                contentSpan.setSpan(foregroundColorSpan, hideStart, hideEnd - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                val clickableSpan: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        if (widget is TextView) {
                            val charSequence = widget.text
                            if (charSequence is Spannable) {
                                charSequence.removeSpan(backgroundColorSpan)
                                charSequence.removeSpan(foregroundColorSpan)
                                widget.highlightColor = Color.TRANSPARENT
                            }
                        }
                    }
                }
                contentSpan.setSpan(clickableSpan, hideStart, hideEnd - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                index = hideEnd - 3
                hideStart = contentSpan.toString().indexOf("[h]", index)
                hideEnd = contentSpan.toString().indexOf("[/h]", index)
            }
            /*****************************************************************8
             * TODO temporary solution for quotation, needs rework after restructuring
             * 支持点击展开
             * 暂时先取消掉这一段
             * TODO 读取设置选择这里是打开对话框还是直接展开
             * TODO **目前仅支持串内引用**
             */
            val quoteColor = ForegroundColorSpan(Color.parseColor("#19A8A8")) // primary color
            val foregroundColorSpans = contentSpan.getSpans(0, contentSpan.length, ForegroundColorSpan::class.java)
            Log.d(TAG, "onResponse: " + foregroundColorSpans.size)
            if (foregroundColorSpans.size != 0) {
                Log.d(TAG, "onResponse: 进入选字阶段！")
                for (j in foregroundColorSpans.indices) {
                    val start = contentSpan.getSpanStart(foregroundColorSpans[j])
                    val end = contentSpan.getSpanEnd(foregroundColorSpans[j])
                    val charSequence = contentSpan.subSequence(start, end)
                    if (charSequence.toString().contains(">>No.")) {
                        Log.d(TAG, "onResponse: 起" + start + " 末" + end + contentSpan.subSequence(start, end).toString().substring(5))
                        val seriesId = contentSpan.subSequence(start, end).toString().substring(5)
                        val originalText = contentSpan.subSequence(end, contentSpan.length)
                        Log.d(TAG, "onResponse: seriesID$seriesId ")
                        var quote: ReplysBean? = null
                        for (d in replysBeans) {
                            if (seriesId == d.seriesId) {
                                quote = d
                                break
                            }
                        }
                        if (quote != null) {
                            val finalQuote: ReplysBean = quote
                            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    if (widget is TextView) {
                                        Log.d(TAG, "Clicked on quote " + finalQuote.seriesId)
                                        var charSequence = widget.text
                                        if (charSequence is Spannable) {
                                            charSequence = (charSequence.subSequence(start, end).toString() + "\n"
                                                    + finalQuote.userid + " " + finalQuote.now + "\n"
                                                    + finalQuote.content)
                                            val divider = charSequence.length
                                            val s: Spannable = SpannableString(charSequence.toString() + "\n" + Html.fromHtml(originalText.toString()))
                                            s.setSpan(quoteColor, start, divider, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                                            widget.text = s
                                        }
                                    }
                                }

                                override fun updateDrawState(ds: TextPaint) {
                                    super.updateDrawState(ds)
                                }
                            }
                            contentSpan.setSpan(clickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                        }
                    }
                }
            }

            contentItem.content = contentSpan

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

            val nametitleBulider = StringBuilder()
            if (temp.title != null && temp.title != "无标题") {
                nametitleBulider.append("标题：").append(temp.title)
                contentItem.hasTitleOrName = true
            }
            if (temp.name != null && temp.name != "无名氏") {
                if (nametitleBulider.length != 0) {
                    nametitleBulider.append("\n")
                }
                nametitleBulider.append("作者：").append(temp.name)
                contentItem.hasTitleOrName = true
            }
            contentItem.titleAndName = nametitleBulider.toString()

            contentItems.add(contentItem)
        }
        return contentItems
    }
}