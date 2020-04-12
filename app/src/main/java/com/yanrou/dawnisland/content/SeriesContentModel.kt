package com.yanrou.dawnisland.content

import com.yanrou.dawnisland.Reference
import com.yanrou.dawnisland.json2class.ReplysBean
import com.yanrou.dawnisland.util.ServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import timber.log.Timber

class SeriesContentModel(private val id: String) {
    private var po = ArrayList<String>()
    //持有一个Map,用来查询数据、去重
    private var replyMap = HashMap<String, ReplysBean>()
    private var seriesId: String = id
    /**
     * 标记是否有缓存
     */
    private var hasCache = false
    /**
     * 标记最后一页是否完整
     * 如果为false,则应该用获取到的页面替换当前页
     */
    private var wholePage = false
    /**
     * 标记最后一页是否有广告
     */
    private var hasAd = false
    /**
     * 控制翻页，如果有缓存就会从缓存中获取上次看到的位置,没有的话从第一页开始
     */
    private val GET_FIRST_PAGE = 1001
    private val GET_FRONT_PAGE = 1002
    private val GET_NEXT_PAGE = 1003
    private val GET_JUMP_PAGE = 1004
    /**
     * 只有state为READY时可以请求新数据
     */
    private val READY = 1000
    private var state = 1000


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
            ServiceClient.getSeriesContentFromNet(seriesId, page)
        }
        //先判断串是否存在，如果为真表示串已经被删
        if ("\"\\u8be5\\u4e3b\\u9898\\u4e0d\\u5b58\\u5728\"" == s || "" == s) {
            return s
        }

        val seriesContentJson = withContext(Dispatchers.Default) { ServiceClient.preFormatJson(page, s) }


        wholePage = (seriesContentJson.replys.size == 20 || (seriesContentJson.replys.size == 19 && !"9999999".equals(seriesContentJson.replys.get(0).seriesId)))


        //为真则表示空页
        if (page != 1 && (seriesContentJson.replys.size == 1 && "9999999" == seriesContentJson.replys[0].seriesId || seriesContentJson.replys.size == 0)) {
            return seriesContentJson.replys
        }
        Timber.d("$page")
        //为真则表示是第一页
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
        //一页就是20条，直接初始化,避免无意义的扩容
        val resultList = ArrayList<ReplysBean>(20)
        //赋值方便以后处理
        for (i in seriesContentJson.replys.indices) {
            val temp = seriesContentJson.replys[i]
            //判断是否已存在，为了避免加载最后一页的新增内容时内存溢出
            if (!replyMap.containsKey(temp.seriesId)) {
                temp.page = page
                temp.parentId = seriesId
                temp.posInPage = i

                replyMap.put(temp.seriesId, temp)
                resultList.add(temp)
            }
        }
        /**
         * 保存页面数据用于下次加载
         * TODO 先确认网络请求没问题，稍后补上缓存
         */
        /**
        val replysBeanDao = MyApplication.getDaoSession().replysBeanDao
        replysBeanDao.insertInTx(seriesContentJson.replys)
         */

        return resultList
    }

    /**
     * 判断是否为po
     */
    fun isPo(userId: String): Boolean {
        return po.contains(userId)
    }

    fun getPageBySeries(seriesId: String): Int? {
        Timber.d("getPage$seriesId")
        return replyMap[seriesId]?.page
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

    companion object {
        private const val TAG = "SeriesContentModel"
    }

}