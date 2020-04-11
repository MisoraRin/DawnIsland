package com.yanrou.dawnisland.reply

import android.app.Application
import android.text.Html
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yanrou.dawnisland.entities.Cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

class ReplyViewModel(application: Application) : AndroidViewModel(application) {
    private val model = ReplyModel()
    private var cookies: List<Cookie> = ArrayList()
    private var switchedCookieIndex = -1
    private val _switchedCookieName = MutableLiveData("没有饼干")
    val switchedCookie get() = _switchedCookieName

    fun sendReply(requestBody: RequestBody) {
        viewModelScope.launch {
            val result = model.sendReply(requestBody, getSwitchedCookieHash())
            if (result.contains("成功")) {
                Toast.makeText(getApplication(), "回复成功", Toast.LENGTH_SHORT).show()
            } else {
                //TODO 回传过来的信息是 html，需要把 html 标签去除
                Toast.makeText(getApplication(), Html.fromHtml(result), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCookies() {
        viewModelScope.launch {
            cookies = withContext(Dispatchers.IO) { model.getCookies() }
            if (!cookies.isEmpty()) {
                switchCookie(0)
            }
        }
    }

    fun switchCookie(position: Int) {
        switchedCookieIndex = position
        _switchedCookieName.value = if (switchedCookieIndex == -1) {
            "没有饼干"
        } else (cookies[switchedCookieIndex].cookieName)
    }

    private fun getSwitchedCookieHash(): String {
        return if (switchedCookieIndex != -1) {
            (cookies[switchedCookieIndex].userHash)
        } else {
            ""
        }
    }

    fun getCookieNameList(): ArrayList<String> {
        return this.cookies.mapTo(arrayListOf(), { cookie -> cookie.cookieName })
    }

    init {
        getCookies()
    }
}