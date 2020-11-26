package com.yanrou.dawnisland.trend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanrou.dawnisland.json2class.ReplysBean
import com.yanrou.dawnisland.util.ServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrendViewModel : ViewModel() {
    val listLiveData = MutableLiveData<ArrayList<TrendItem>>()

    init {
        getTrend()
    }

    private fun getTrend() {
        viewModelScope.launch {
            var seriesContentJson = withContext(Dispatchers.Default) { ServiceClient.getSeriesContentFromNet("15347469", 1)  }
            val replyCount = seriesContentJson.replyCount
            var page = replyCount / 19
            if ((replyCount % 19) > 0) {
                page++
            }
            var lastTrend: ReplysBean?
            do {
                seriesContentJson = ServiceClient.getSeriesContentFromNet("15347469", page)
                lastTrend = seriesContentJson.replys.lastOrNull { it.userid == "m9R9kaD" && it.content.contains("Trend") }
                page--
            } while (lastTrend == null)
            val list = ArrayList<TrendItem>(32)
            withContext(Dispatchers.Default) {
                val pattern = """(\d{1,2})\.\sTrend\s(\d+)\s\[(.+?)].+?No\.(\d+).+?(:?<br\s/>\n)+(.+?)(:?<br\s/>\n)+—""".toRegex(RegexOption.DOT_MATCHES_ALL)
                pattern.findAll(lastTrend.content).iterator().forEach {
                    list.add(TrendItem(it.groupValues[1], it.groupValues[2], it.groupValues[6], it.groupValues[3], it.groupValues[4]))
                }
            }
            withContext(Dispatchers.Main) { listLiveData.value = list }
        }
    }
}