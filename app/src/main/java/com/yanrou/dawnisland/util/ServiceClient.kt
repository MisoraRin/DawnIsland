package com.yanrou.dawnisland.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yanrou.dawnisland.json2class.FeedJson
import com.yanrou.dawnisland.json2class.ForumJson
import com.yanrou.dawnisland.json2class.SeriesContentJson
import com.yanrou.dawnisland.json2class.TimeLineJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

object ServiceClient {
    private val service: SeriesContentService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        Retrofit.Builder()
                .baseUrl("https://nmb.fastmirror.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SeriesContentService::class.java)
    }

    private val gson = Gson()

    suspend fun getSeriesContentFromNet(seriesId: String, page: Int): SeriesContentJson {
        return service.getSeriesContent(seriesId, page)
    }


    fun preFormatJson(s: String): SeriesContentJson {
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
        return gson.fromJson(response, object : TypeToken<List<TimeLineJson>>() {}.type)
    }

    fun sendReply(requestBody: RequestBody, cookie: String): String {
        return service.sendReply(requestBody, "userhash=$cookie")!!.execute().body()!!.string()
    }

    fun getQuote(id: String): String {
        return service.getQuote(id)!!.execute().body()!!.string()
    }

    fun getForumList(): String {
        return service.getForumList().execute().body()!!.string()
    }

    // TODO: handle case where thread is deleted
    suspend fun getFeeds(uuid: String, page: Int): List<FeedJson> {
        try {
            val rawResponse =
                    withContext(Dispatchers.IO) {
                        Timber.i("Downloading Feeds on page $page...")
                        service.getNMBFeeds(uuid, page).execute().body()!!.string()
                    }
            return withContext(Dispatchers.Default) {
                Timber.i("Parsing Feeds...")
                Gson().fromJson<List<FeedJson>>(rawResponse, object : TypeToken<List<FeedJson>>() {}.type)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get Feeds")
            throw e
        }
    }

    suspend fun addFeed(uuid: String, tid: String): String {
        try {
            return withContext(Dispatchers.IO) {
                Timber.i("Adding Feed $tid...")
                service.addNMBFeed(uuid, tid).execute().body()!!.string()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to add feed")
            throw e
        }
    }

    suspend fun delFeed(uuid: String, tid: String): String {
        try {
            return withContext(Dispatchers.IO) {
                Timber.i("Deleting Feed $tid...")
                service.delNMBFeed(uuid, tid).execute().body()!!.string()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete feed")
            throw e
        }
    }


    fun convertForumListFromJson(forumJson: String): List<ForumJson> =
            gson.fromJson(forumJson, object : TypeToken<List<ForumJson>>() {}.type)
}