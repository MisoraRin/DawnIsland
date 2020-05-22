package com.yanrou.dawnisland.content

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.imageviewer.ImageViewerView
import com.yanrou.dawnisland.json2class.ReplysBean
import com.yanrou.dawnisland.serieslist.CardViewFactory
import com.yanrou.dawnisland.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class QuotePopup(private val caller: Activity) : CenterPopupView(caller) {

    override fun getImplLayoutId(): Int {
        return R.layout.quote_popup
    }

    fun convertContentItem(contentItem: ContentItem, po: String) {
        findViewById<TextView>(R.id.SeriesListCookie).setText(contentItem.cookie, TextView.BufferType.SPANNABLE)

        findViewById<TextView>(R.id.SeriesListTime).text = contentItem.time

        // TODO: handle ads
        findViewById<TextView>(R.id.number).text = contentItem.seriesId

        // TODO: add sage transformation
        findViewById<TextView>(R.id.sega).let {
            if (contentItem.sega == 1) {
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }

        findViewById<TextView>(R.id.titleAndName).let {
            if (contentItem.hasTitleOrName) {
                it.text = contentItem.titleAndName
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }

        // load image
        findViewById<ImageView>(R.id.seriesContentImageView).let { it ->
            if (contentItem.hasImage) {
                Glide.with(caller)
                        .load("https://nmbimg.fastmirror.org/thumb/" + contentItem.imgurl)
                        .override(250, 250)
                        .fitCenter()
                        .into(it)
                it.visibility = View.VISIBLE
                it.setOnClickListener { imageView ->
                    Timber.i("clicked on image in quote ${contentItem.seriesId}")
                    val url = contentItem.imgurl
                    val fullScreenImageViewer = Intent(caller, ImageViewerView::class.java)
                    fullScreenImageViewer.putExtra("imgurl", "https://nmbimg.fastmirror.org/image/" + url)
                    caller.startActivity(fullScreenImageViewer)
                }
            } else {
                it.visibility = View.GONE
            }

        }

//        findViewById<LinearLayout>(R.id.seriesQuotes).let { linearLayout ->
//            val quotes = contentItem.quotes
//            if (quotes.isNotEmpty()) {
//                linearLayout.removeAllViews()
//                val quotePopup = QuotePopup(caller)
//                quotes.map { id ->
//                    val q = LayoutInflater.from(caller)
//                            .inflate(R.layout.quote_list_item, linearLayout as ViewGroup, false)
//                    q.findViewById<TextView>(R.id.quoteId).text = "No. $id"
//                    q.setOnClickListener {
//                        // TODO: get Po based on Thread
//                        quotePopup.showQuote(id, po)
//                    }
//                    linearLayout.addView(q)
//                }
//                linearLayout.visibility = View.VISIBLE
//            } else {
//                linearLayout.visibility = View.GONE
//            }
//        }

        findViewById<TextView>(R.id.SeriesListContent).setText(contentItem.content, TextView.BufferType.SPANNABLE)


    }

    fun showQuote(
            id: String,
            po: String
    ) {

        MainScope().launch {
            try {
                val reply = getQuote(id)
                XPopup.Builder(caller)
                        .setPopupCallback(object : SimpleCallback() {
                            override fun beforeShow() {
                                super.beforeShow()
                                convertContentItem(reply, po)
                            }
                        })
                        .asCustom(this@QuotePopup)
                        .show()
            } catch (e: Exception) {
                Timber.e(e, "Failed to get quote..")
                Toast.makeText(caller.baseContext, "无法读取引用...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getQuote(id: String): ContentItem {
        try {
            val rawResponse = withContext(Dispatchers.IO) {
                Timber.i("Downloading quote...")
                ServiceClient.getQuote(id)
            }
            return withContext(Dispatchers.Default) {
                Timber.i("Parsing quote")
                parseQuote(rawResponse)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get quote")
            throw e
        }
    }


    // TODO: below is duplicate with actions in model/view model, needs consolidation
    private fun parseQuote(response: String): ContentItem {
        val replyBean = Gson().fromJson(response, ReplysBean::class.java)

        val contentItem = ContentItem()
        /*
          处理时间
         */
        contentItem.time = transformTime(replyBean.now)
        /*
          处理饼干
          PO需要加粗
          普通饼干是灰色，po是黑色，红名是红色
         */

        // TODO: format po, currently omit po
        contentItem.cookie = transformCookie(replyBean.userid, replyBean.admin) { false }

        /*
          处理内容
          主要是处理引用串号
         */
        val quotes = extractQuote(replyBean.content)

        contentItem.quotes = quotes


//        val noQuotesContent = removeQuote(replyBean.content)

        // will also hide [h]
        contentItem.content = transformContent(replyBean.content)

        // 添加段间距和行间距，由于需要读取设置所以先放到这里
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(caller.application)
        val lineHeight = sharedPreferences.getInt(CardViewFactory.LINE_HEIGHT, 0)
        val segGap = sharedPreferences.getInt(CardViewFactory.SEG_GAP, 0)
//        addLineHeightAndSegGap(contentItem, lineHeight, segGap)

        if (replyBean.sage == 1) {
            contentItem.sega = View.VISIBLE
        } else {
            contentItem.sega = View.GONE
        }

        contentItem.seriesId = replyBean.seriesId

        if (replyBean.ext != null && "" != replyBean.ext) {
            contentItem.hasImage = true
            contentItem.imgurl = replyBean.img + replyBean.ext
        } else {
            contentItem.hasImage = false
        }

        val titleAndName = transformTitleAndName(replyBean.title, replyBean.name)
        if (titleAndName != "") {
            contentItem.hasTitleOrName = true
        }

        contentItem.titleAndName = titleAndName

        return contentItem
    }
}





