package com.yanrou.dawnisland.serieslist

import com.yanrou.dawnisland.Fid2Name
import com.yanrou.dawnisland.json2class.TimeLineJson
import com.yanrou.dawnisland.util.ServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SeriesModel {

    private var fid2Name: Map<Int, String>? = null

    // MIGHT NOT BE NEEDED
    private var fid: Int = -1

    init {
        fid2Name = Fid2Name.getDb()
    }

    /**
     * 当前所有串
     */
    var series = mutableListOf<TimeLineJson>()

    /**
     * 保存了当前所有串号，用于快速排查重复串
     */
    var seriesId = mutableSetOf<String>()

    var page = 1


    suspend fun getSeriesList(fid: Int, page: Int): List<TimeLineJson> {

        val res = withContext(Dispatchers.IO) {
            ServiceClient.getSeriesListFromNet(fid, page)
        }
        if ("\"\\u8be5\\u677f\\u5757\\u4e0d\\u5b58\\u5728\"" == res || "" == res) {
            throw IOException("Forum does not exist")
        }
        return withContext(Dispatchers.Default) { filterDuplicates(ServiceClient.formatSeriesList(res)) }
    }

    private fun filterDuplicates(list: List<TimeLineJson>): List<TimeLineJson> {
        val noDuplicates = list.filterNot { seriesId.contains(it.id) }
        noDuplicates.map {
            seriesId.add(it.id)
            series.add(it)
        }
        if (noDuplicates.isNotEmpty()) page += 1
        return noDuplicates
    }


    fun getForumName(fid1: Int): String {
        return when {
            fid == -1 -> fid2Name?.get(fid1) ?: ""
            else -> fid2Name?.get(fid) ?: ""
        }
    }

    fun clearData() {
        page = 1
        seriesId.clear()
        series.clear()
    }

    fun refresh() {
        clearData()
    }

    fun changeForum(fid: Int) {
        clearData()
        this.fid = fid
    }
}