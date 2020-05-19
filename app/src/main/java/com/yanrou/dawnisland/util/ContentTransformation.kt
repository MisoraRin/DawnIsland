package com.yanrou.dawnisland.util

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.yanrou.dawnisland.Reference
import com.yanrou.dawnisland.span.SegmentSpacingSpan
import org.jsoup.Jsoup
import org.jsoup.nodes.Element


fun transformForumName(forumName: String): Spanned {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Html.fromHtml(forumName)
    } else {
        Html.fromHtml(forumName, Html.FROM_HTML_MODE_COMPACT)
    }
}

fun transformCookie(userId: String, admin: Int, isPo: ((po: String) -> Boolean)): SpannableStringBuilder {
    /*
      处理饼干
      PO需要加粗
      普通饼干是灰色，po是黑色，红名是红色
     */
    val cookie = SpannableStringBuilder(userId)
    if (admin == 1) {
        val adminColor = ForegroundColorSpan(Color.parseColor("#FF0F0F"))
        cookie.setSpan(adminColor, 0, cookie.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        // TODO: support multiple po
    } else if (isPo(userId)) {
        val poColor = ForegroundColorSpan(Color.parseColor("#000000"))
        cookie.setSpan(poColor, 0, cookie.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
    if (isPo(userId)) {
        val styleSpanBold = StyleSpan(Typeface.BOLD)
        cookie.setSpan(styleSpanBold, 0, cookie.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
    return cookie
}

fun transformTime(now: String, style: String = "default"): String {
    // TODO: transform time based on style, which could be in preference
    return ReadableTime.getDisplayTime(now)
}

fun transformTitleAndName(title: String? = "", name: String? = ""): String {
    var titleAndName = ""
    if (title != null && title != "" && title != "无标题") {
        titleAndName += "标题：$title"
    }
    if (name != null && name != "" && name != "无名氏") {
        if (titleAndName.isNotEmpty()) {
            titleAndName += "\n"
        }
        titleAndName += "作者：$name"
    }
    return titleAndName
}

fun extractQuote(content: String): List<String> {
    /** api response
    <font color=\"#789922\">&gt;&gt;No.23527403</font>
     */
    val regex = """&gt;&gt;No.\d+""".toRegex()

    return regex.findAll(content).toList().map {
        it.value.substring(11)
    }

}

fun SpannableStringBuilder.addLineHeightAndSegGap(lineHeight: Int, segGap: Int) {
    if (this.toString().contains("\n\n")) {
        this.setSpan(SegmentSpacingSpan(lineHeight, lineHeight), 0, this.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    } else {
        this.setSpan(SegmentSpacingSpan(lineHeight, segGap), 0, this.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}


fun transformContent(content: String): SpannableStringBuilder {

    val nonHide = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SpannableStringBuilder(Html.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY))
    } else {
        SpannableStringBuilder(Html.fromHtml(content))
    }
    return transformHideContent(nonHide)
}

fun transformHideContent(content: SpannableStringBuilder): SpannableStringBuilder {
    var index = -1
    var hideStart: Int
    var hideEnd: Int
    hideStart = content.indexOf("[h]")
    hideEnd = content.indexOf("[/h]")
    while (hideStart != -1 && hideEnd != -1 && hideStart < hideEnd) {
        content.delete(hideStart, hideStart + 3)
        content.delete(hideEnd - 3, hideEnd + 1)
        val foregroundColorSpan = ForegroundColorSpan(Color.TRANSPARENT)
        val backgroundColorSpan = BackgroundColorSpan(Color.parseColor("#555555"))
        content.setSpan(backgroundColorSpan, hideStart, hideEnd - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        content.setSpan(foregroundColorSpan, hideStart, hideEnd - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

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

            // overrides, DO NOT CREATE PAINT
            override fun updateDrawState(ds: TextPaint) {
            }
        }
        content.setSpan(clickableSpan, hideStart, hideEnd - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        index = hideEnd - 3
        hideStart = content.indexOf("[h]", index)
        hideEnd = content.indexOf("[/h]", index)
    }
    return content
}

/**
 * 用来获取引用内容
 * 引用内容分为串内引用和串外引用，这个是用来获取串外引用的跳转原帖链接的
 *
 * @param html
 * @return
 */
private fun decodeReference(html: String): Reference {
    val reference = Reference()
    val doc = Jsoup.parse(html)
    val elements: List<Element> = doc.allElements
    for (element in elements) {
        val className = element.className()
        if ("h-threads-item-reply h-threads-item-ref" == className) {
            reference.id = element.attr("data-threads-id")
        } else if ("h-threads-img-a" == className) {
            reference.image = element.attr("href")
        } else if ("h-threads-img" == className) {
            reference.thumb = element.attr("src")
        } else if ("h-threads-info-title" == className) {
            reference.title = element.text()
        } else if ("h-threads-info-email" == className) { // TODO email or user ?
            reference.user = element.text()
        } else if ("h-threads-info-createdat" == className) {
            reference.time = element.text()
        } else if ("h-threads-info-uid" == className) {
            val user = element.text()
            if (user.startsWith("ID:")) {
                reference.userId = user.substring(3)
            } else {
                reference.userId = user
            }
            reference.admin = element.childNodeSize() > 1
        } else if ("h-threads-info-id" == className) {
            val href = element.attr("href")
            if (href.startsWith("/t/")) {
                val index = href.indexOf('?')
                if (index >= 0) {
                    reference.postId = href.substring(3, index)
                } else {
                    reference.postId = href.substring(3)
                }
            }
        } else if ("h-threads-content" == className) {
            reference.content = element.html()
        }
    }
    return reference
}