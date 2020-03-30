package com.yanrou.dawnisland.serieslist

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yanrou.dawnisland.Fid2Name
import com.yanrou.dawnisland.json2class.TimeLineJson
import com.yanrou.dawnisland.util.SeriesListService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
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
            getSeriesListFromNet(fid, page)
        }
        if ("\"\\u8be5\\u677f\\u5757\\u4e0d\\u5b58\\u5728\"" == res || "" == res) {
            throw IOException("Forum does not exist")
        }
        return withContext(Dispatchers.Default) { formatSeriesList(res) }
    }

    private fun getSeriesListFromNet(fid: Int, page: Int): String {
        // TODO move to interface
        val retrofit = Retrofit.Builder()
                .baseUrl("https://nmb.fastmirror.org/")
                .build()
                .create(SeriesListService::class.java)
        val result = when (fid) {
            -1 -> retrofit.getTimelineList(page)
            else -> retrofit.getSeriesList(fid, page)
        }
        return result!!.execute().body()!!.string()
    }

    private fun formatSeriesList(response: String): List<TimeLineJson> {

        val list: List<TimeLineJson> = Gson().fromJson(response, object : TypeToken<List<TimeLineJson>>() {}.type)
        val noDuplicates = list.filterNot { seriesId.contains(it.id) }
        noDuplicates.map {
            seriesId.add(it.id)
            series.add(it)
        }
        if (noDuplicates.isNotEmpty()) page += 1
        return noDuplicates
    }

    fun getForumName(fid: String): String {
        return fid2Name?.get(fid.toInt()) ?: ""
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