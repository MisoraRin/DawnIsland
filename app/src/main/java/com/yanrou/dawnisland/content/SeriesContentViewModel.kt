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
import androidx.lifecycle.viewModelScope
import com.yanrou.dawnisland.json2class.ReplysBean
import com.yanrou.dawnisland.json2class.SeriesContentJson
import com.yanrou.dawnisland.span.SegmentSpacingSpan
import com.yanrou.dawnisland.util.ReadableTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SeriesContentViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "SeriesContentViewModel"
    lateinit var seriesData: SeriesContentJson
    lateinit var po: MutableList<String>
    private val model = SeriesContentModel("")

    fun init(seriesId: String?) {}

    /**
     * 处理加载更多逻辑
     */
    fun loadMore() {}

    /**
     * 处理下拉逻辑
     */
    fun refresh() {}

    /**
     * 处理跳页逻辑
     *
     * @param page 将要跳到的页数
     */
    fun jumpPage(page: Int) {}

    /**
     * 在这里返回当前看到的页数
     *
     * @return 当前看到的页数
     */
    val nowPage: Int
        get() = 0

    /**
     * 在这里返回总页数
     *
     * @return 总页数
     */
    val totalPage: Int
        get() = 0

    private fun getContent() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { model.getSeriesContent(1) }
            if (result is List<*>) {
                //表示有数据
            } else if (result is String) {
                //串已被删除
            }
            //用于执行CPU密集型任务
            withContext(Dispatchers.Default) {

            }
            //在主线程中执行
            withContext(Dispatchers.Main) {

            }
        }

    }

    /**
     * 用于处理获取到的json数据
     *
     * @param seriesContentJson gson格式化json后产生的类
     */
    private fun formatContent(replysBeans: List<ReplysBean>, page: Int) {

        val contentItems: MutableList<ContentItem> = ArrayList()

        var temp: ReplysBean
        /*
          在这里预处理内容，保证显示时可以直接显示
         */for (i in replysBeans.indices) {
            temp = replysBeans[i]
            val contentItem = ContentItem()
            /*
              处理时间
             */contentItem.time = ReadableTime.getDisplayTime(temp.now)
            /*
              处理饼干
              PO需要加粗
              普通饼干是灰色，po是黑色，红名是红色
             */
            val cookie = SpannableStringBuilder(temp.userid)
            val normalColor = ForegroundColorSpan(Color.parseColor("#B0B0B0"))
            val quoteColor = ForegroundColorSpan(Color.parseColor("#19A8A8")) // primary color
            val adminColor = ForegroundColorSpan(Color.parseColor("#FF0F0F"))
            val poColor = ForegroundColorSpan(Color.parseColor("#000000"))
            val styleSpanBold = StyleSpan(Typeface.BOLD)
            if (temp.admin == 1) {
                cookie.setSpan(adminColor, 0, cookie.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            } else if (po.contains(temp.userid)) {
                cookie.setSpan(poColor, 0, cookie.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            if (po.contains(temp.userid)) {
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
             */if (!contentSpan.toString().contains("\n\n")) {
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
             * temporary solution for quotation, needs rework after restructuring
             * 支持点击展开
             * 暂时先取消掉这一段
             * TODO 读取设置选择这里是打开对话框还是直接展开
             * TODO **目前仅支持串内引用**
             */
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
        seriesData.lastPage = page
        seriesData.save()
    }
}