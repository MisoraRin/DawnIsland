package com.yanrou.dawnisland.trend

import com.yanrou.dawnisland.json2class.ReplysBean
import com.yanrou.dawnisland.util.ServiceClient

fun getTrend(): ArrayList<TrendItem> {
    var seriesContentJson = ServiceClient.preFormatJson(ServiceClient.getSeriesContentFromNet("15347469", 1))
    val replyCount = seriesContentJson.replyCount
    var page = replyCount / 19
    if ((replyCount % 19) > 0) {
        page++
    }
    var lastTrend: ReplysBean?
    do {
        seriesContentJson = ServiceClient.preFormatJson(ServiceClient.getSeriesContentFromNet("15347469", page))
        lastTrend = seriesContentJson.replys.lastOrNull { it.userid == "m9R9kaD" && it.content.contains("Trend") }
        page--
    } while (lastTrend == null)
    val pattern = """(\d{1,2})\.\sTrend\s(\d+)\s\[(.+?)].+?No\.(\d+).+?(:?<br\s/>\n)+(.+?)(:?<br\s/>\n)+—""".toRegex(RegexOption.DOT_MATCHES_ALL)
    val list = ArrayList<TrendItem>(32)
    pattern.findAll(lastTrend.content).iterator().forEach {
        list.add(TrendItem(it.groupValues[1], it.groupValues[2], it.groupValues[6], it.groupValues[3], it.groupValues[4]))
    }
    return list
}
