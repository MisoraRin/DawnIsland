package com.yanrou.dawnisland.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yanrou.dawnisland.json2class.SeriesContentJson
import com.yanrou.dawnisland.json2class.TimeLineJson
import okhttp3.RequestBody
import retrofit2.Retrofit

object ServiceClient {
    private val service: SeriesContentService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        Retrofit.Builder()
                .baseUrl("https://nmb.fastmirror.org/")
                .build()
                .create(SeriesContentService::class.java)
    }

    private val gson = Gson()

    fun getSeriesContentFromNet(seriesId: String, page: Int): String {
        return service.getSeriesContent(seriesId, page)!!.execute().body()!!.string()
    }


    fun preFormatJson(page: Int, s: String): SeriesContentJson {
        return gson.fromJson(s, SeriesContentJson::class.java)
    }

    fun getSeriesListFromNet(fid: Int, page: Int): String {
        val result = when (fid) {
            -1 -> service.getTimelineList(page)
            else -> service.getSeriesList(fid, page)
        }
        return result!!.execute().body()!!.string()
    }

    fun formatSeriesList(response: String): List<TimeLineJson> {
        return Gson().fromJson(response, object : TypeToken<List<TimeLineJson>>() {}.type)
    }

    fun sendReply(requestBody: RequestBody, cookie: String): String {
        return service.sendReply(requestBody, "userhash=$cookie")!!.execute().body()!!.string()
    }

    fun getQuote(id: String): String {
        return service.getQuote(id)!!.execute().body()!!.string()
    }


}