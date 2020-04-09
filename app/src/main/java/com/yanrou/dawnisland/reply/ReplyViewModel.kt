package com.yanrou.dawnisland.reply

import android.app.Application
import android.text.Html
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yanrou.dawnisland.entities.Cookie
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class ReplyViewModel(application: Application) : AndroidViewModel(application) {
    private val model = ReplyModel()
    private val _cookies = MutableLiveData<List<Cookie>>()
    val cookies: LiveData<List<Cookie>> get() = _cookies
    fun sendReply(requestBody: RequestBody) {
        viewModelScope.launch {
            //TODO 需要在这里把“cookie”替换为用户的饼干，不需要加userhash，model里面已经加了
            val result = model.sendReply(requestBody, "cookie")
            if (result.contains("成功")) {
                Toast.makeText(getApplication(), "回复成功", Toast.LENGTH_SHORT).show()
            } else {
                //TODO 回传过来的信息是html，需要把html标签去除
                //TODO 或者使用dialog展示错误信息，但是这里弹dialog有点麻烦
                Toast.makeText(getApplication(), Html.fromHtml(result), Toast.LENGTH_SHORT).show()
                //AlertDialog.Builder(getApplication()).setTitle("出了点问题").setMessage(Html.fromHtml(result)).create().show()
            }
        }
    }

    fun getCookies() {
        viewModelScope.launch {
            _cookies.postValue(model.getCookies())
        }
    }

    init {
        getCookies()
    }
}