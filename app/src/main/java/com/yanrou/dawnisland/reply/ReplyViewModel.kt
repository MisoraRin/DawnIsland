package com.yanrou.dawnisland.reply

import android.app.Application
import android.text.Html
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class ReplyViewModel(application: Application) : AndroidViewModel(application) {
    private val model = ReplyModel()
    fun sendReply(requestBody: RequestBody) {
        viewModelScope.launch {
            //TODO 添加饼干
            val result = model.sendReply(requestBody, "cookie")
            if (result.contains("成功")) {
                Toast.makeText(getApplication(), "回复成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(getApplication(), Html.fromHtml(result), Toast.LENGTH_SHORT).show()
                //AlertDialog.Builder(getApplication()).setTitle("出了点问题").setMessage(Html.fromHtml(result)).create().show()
            }
        }
    }
}