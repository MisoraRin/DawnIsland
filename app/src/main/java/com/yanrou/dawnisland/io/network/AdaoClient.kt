package com.yanrou.dawnisland.io.network

import com.yanrou.dawnisland.content.ContentItem
import rxhttp.toOkResponse
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxAdaoHttp

sealed class HttpResult {
    object Failed : HttpResult()
    class Succeed(contentItems: List<ContentItem>) : HttpResult()
}

suspend fun getSeriesContent(id: String, page: Int) =
        RxAdaoHttp.get("Api/thread")
                .add("id", id)
                .add("page", page)
                .setCacheMode(CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK)
                .toOkResponse()
                .await()
