package com.yanrou.dawnisland.util

import com.yanrou.dawnisland.json2class.SeriesContentJson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface SeriesContentService {
    @GET("Api/thread")
    suspend fun getSeriesContent(@Query("id") id: String, @Query("page") page: Int): SeriesContentJson

    @GET("Api/showf")
    fun getSeriesList(@Query("id") id: Int, @Query("page") page: Int): Call<ResponseBody>

    @GET("Api/timeline")
    fun getTimelineList(@Query("page") page: Int): Call<ResponseBody>?

    @POST("Home/Forum/doReplyThread.html")
    fun sendReply(@Body body: RequestBody, @Header("Cookie") cookie: String): Call<ResponseBody>

    @GET("Api/ref")
    fun getQuote(@Query("id") id: String): Call<ResponseBody>

    @GET("Api/feed")
    fun getNMBFeeds(@Query("uuid") fid: String, @Query("page") page: Int): Call<ResponseBody>

    @GET("Api/addFeed")
    fun addNMBFeed(@Query("uuid") fid: String, @Query("tid") id: String): Call<ResponseBody>

    @GET("Api/delFeed")
    fun delNMBFeed(@Query("uuid") fid: String, @Query("tid") id: String): Call<ResponseBody>

    @GET("Api/getForumList")
    fun getForumList(): Call<ResponseBody>
}
