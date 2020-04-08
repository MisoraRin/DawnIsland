package com.yanrou.dawnisland.reply

import com.yanrou.dawnisland.util.Server
import okhttp3.RequestBody

class ReplyModel {
    fun sendReply(requestBody: RequestBody, cookie: String) {
        val call = Server.getService.sendReply(requestBody, "userhash=$cookie")
        val result = call!!.execute().body()!!.string()
    }
}