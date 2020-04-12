package com.yanrou.dawnisland.reply

import com.yanrou.dawnisland.MyApplication
import com.yanrou.dawnisland.entities.Cookie
import com.yanrou.dawnisland.util.ServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

class ReplyModel {
    suspend fun sendReply(requestBody: RequestBody, cookie: String): String =
            withContext(Dispatchers.IO) {
                ServiceClient.sendReply(requestBody, cookie)
            }

    suspend fun getCookies(): List<Cookie> =
            withContext(Dispatchers.IO) {
                MyApplication.getDaoSession().cookieDao().getAll()
            }
}