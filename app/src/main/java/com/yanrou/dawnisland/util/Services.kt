package com.yanrou.dawnisland.util

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query


interface SeriesContentService {
    @GET("Api/thread")
    fun getSeriesContent(@Query("id") id: String?, @Query("page") page: Int): Call<ResponseBody>?

    @GET("Api/showf")
    fun getSeriesList(@Query("id") id: Int?, @Query("page") page: Int): Call<ResponseBody>?

    @GET("Api/timeline")
    fun getTimelineList(@Query("page") page: Int): Call<ResponseBody>?
}

object Server {
    val getService: SeriesContentService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        Retrofit.Builder()
                .baseUrl("https://nmb.fastmirror.org/")
                .build()
                .create(SeriesContentService::class.java)
    }
}