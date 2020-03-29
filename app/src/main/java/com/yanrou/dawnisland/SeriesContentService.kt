package com.yanrou.dawnisland

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface SeriesContentService {
    @GET("Api/thread")
    fun getSeriesContent(@Query("id") id: String?, @Query("page") page: Int): Call<ResponseBody>?
}