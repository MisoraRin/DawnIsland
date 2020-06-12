package com.yanrou.dawnisland.content

import com.yanrou.dawnisland.json2class.ReplysBean
import com.yanrou.dawnisland.util.ServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SeriesContentModel(id: String) {
    private var po = ArrayList<String>()

    //持有一个Map,用来查询数据、去重
    private var replyMap = HashMap<String, ReplysBean>()
    private var seriesId: String = id

    /**
     * 标记最后一页是否完整
     * 如果为false,则应该用获取到的页面替换当前页
     */
    private var wholePage = false

    private var _replyCount = 0
    val maxPage
        get() = 1.coerceAtLeast(kotlin.math.ceil(_replyCount.toDouble() / 19).toInt())

    /**
     * 用于给ViewModel调用
     * next用于控制是上一页还是下一页，true则下一页，false则上一页
     * 由model处理最后一页是翻页还是刷新当前页
     * 返回一个ReplysBean的List或者一个String
     * 由于A岛的APi设计问题，不得不很不优雅的写一个Any在这里
     * 当串被删了，就返回一个String对象，如果没有更多数据，就返回一个空list，如果还有数据则返回一个正常的list
     */
    suspend fun getSeriesContent(page: Int, next: Boolean): Any {
        var mpage = page
        if (next && wholePage) {
            Timber.d("页数+1")
            mpage++
        }

        val s = withContext(Dispatchers.IO) {
            com.yanrou.dawnisland.io.network.getSeriesContent(seriesId, mpage).body!!.string()
//            ServiceClient.getSeriesContentFromNet(seriesId, mpage)
        }
        //先判断串是否存在，如果为真表示串已经被删
        if ("\"\\u8be5\\u4e3b\\u9898\\u4e0d\\u5b58\\u5728\"" == s || "" == s) {
            return s
        }

        val seriesContentJson = withContext(Dispatchers.Default) { ServiceClient.preFormatJson(s) }
        wholePage = (seriesContentJson.replys.size == 20 || (seriesContentJson.replys.size == 19 && "9999999" != seriesContentJson.replys[0].seriesId))

        _replyCount = seriesContentJson.replyCount

        //为真则表示空页
        if (page != 1 && (seriesContentJson.replys.size == 1 && "9999999" == seriesContentJson.replys[0].seriesId || seriesContentJson.replys.size == 0)) {
            return seriesContentJson.replys
        }
        Timber.d("$page")
        if (page == 1) {
            po.add(seriesContentJson.userid)
            seriesContentJson.replys.add(0, ReplysBean(
                    seriesContentJson.seriesId,
                    seriesContentJson.userid,
                    seriesContentJson.admin,
                    seriesContentJson.title,
                    seriesContentJson.email,
                    seriesContentJson.now,
                    seriesContentJson.content,
                    seriesContentJson.img,
                    seriesContentJson.ext,
                    seriesContentJson.name,
                    seriesContentJson.sage
            ))
        }

        val resultList = ArrayList<ReplysBean>(20)

        for (i in seriesContentJson.replys.indices) {
            val temp = seriesContentJson.replys[i]
            //判断是否已存在，为了避免加载最后一页的新增内容时内存溢出
            if (!replyMap.containsKey(temp.seriesId)) {
                temp.page = mpage
                temp.parentId = seriesId
                temp.posInPage = i

                replyMap[temp.seriesId] = temp
                resultList.add(temp)
            }
        }
        return resultList
    }

    /**
     * 判断是否为po
     */
    fun isPo(userId: String): Boolean {
        return po.contains(userId)
    }

    fun getPageBySeries(seriesId: String): Int? {
        return replyMap[seriesId]?.page
    }

    /**
     * 清空id，为跳页/刷新准备
     */
    fun clearIds() {
        replyMap.clear()
    }
}