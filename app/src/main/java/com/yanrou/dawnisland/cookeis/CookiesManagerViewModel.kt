package com.yanrou.dawnisland.cookeis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanrou.dawnisland.MyApplication
import com.yanrou.dawnisland.entities.Cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CookiesManagerViewModel : ViewModel() {

    private var _cookies = MutableLiveData<List<Cookie>>()
    val cookie: LiveData<List<Cookie>> get() = _cookies

    fun saveCookie(cookie: Cookie) {
        viewModelScope.launch(Dispatchers.IO) { MyApplication.getDaoSession().cookieDao().insert(cookie) }
    }

    fun readCookie() {
        viewModelScope.launch(Dispatchers.IO) { _cookies.postValue(MyApplication.getDaoSession().cookieDao().getAll()) }
    }

    init {
        readCookie()
    }
}