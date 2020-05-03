package com.yanrou.dawnisland.serieslist

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanrou.dawnisland.Fid2Name
import com.yanrou.dawnisland.json2class.TimeLineJson
import com.yanrou.dawnisland.span.RoundBackgroundColorSpan
import com.yanrou.dawnisland.util.extractQuote
import com.yanrou.dawnisland.util.removeQuote
import com.yanrou.dawnisland.util.transformContent
import com.yanrou.dawnisland.util.transformTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeriesViewModel : ViewModel() {

    private var model: SeriesModel = SeriesModel()
    private val roundBackgroundColorSpan = RoundBackgroundColorSpan(Color.parseColor("#12DBD1"), Color.parseColor("#FFFFFF"))

    var fid = -1
    var page = 1

    enum class LoadingState {
        LOADING,
        COMPLETE,
        FAIL
    }
    private val _loadingState = MutableLiveData(LoadingState.COMPLETE)
    val loadingState get() = _loadingState

    private val _seriesCards = MutableLiveData<List<SeriesCardView>>()
    val seriesCards get() = _seriesCards

    init {
//            getNextPage()
    }
    /**
     * 当前所有串的view
     */
    var seriesCardList = mutableListOf<SeriesCardView>()

    var first = true
    fun getFirstPage() {
        if (first) {
            getNextPage()
            first = false
        }
    }

    fun getNextPage() {
        if (model.fid2Name == null) {
            model.fid2Name = Fid2Name.db.value
        }
        if (loadingState.value == LoadingState.LOADING) {
            return
        }
        loadingState.value = LoadingState.LOADING
        viewModelScope.launch {
            val list: List<TimeLineJson>
            try {
                list = model.getSeriesList(fid, page)
            } catch (e: Exception) {

                _loadingState.postValue(LoadingState.FAIL)
                return@launch
            }
            // no new data
            if (list.isEmpty()) {

                _loadingState.postValue(LoadingState.COMPLETE)
                return@launch
            }

            withContext(Dispatchers.Default) {
                list.map {

//                    seriesIds.add(it.id)

                    val seriesCardView = SeriesCardView()
                    /**
                     * 处理数据
                     */
                    seriesCardView.id = it.id
                    seriesCardView.forum = model.getForumName(it.fid)

                    //格式化时间
                    // TODO: 这里要检测用户设置，目前在model里不能获取，重构后再增加此功能，暂时先使用默认时间
                    seriesCardView.time = transformTime(it.now)

                    //处理内容
                    // TODO: add quotes
                    val quotes = extractQuote(it.content)

                    val noQuotesContent = removeQuote(it.content)

                    // will also hide [h]
                    seriesCardView.content = transformContent(noQuotesContent)

                    val cookie = SpannableString(it.userid)
                    if (it.admin == 1) {
                        cookie.setSpan(ForegroundColorSpan(Color.parseColor("#FF0F0F")), 0, cookie.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    }
                    seriesCardView.cookie = cookie

                    if (fid == -1) {
                        val spannableString = SpannableString(seriesCardView.forum + " · " + it.replyCount)

                        spannableString.setSpan(roundBackgroundColorSpan, 0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        spannableString.setSpan(RelativeSizeSpan(1.0f), 0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        seriesCardView.forumAndReply = spannableString
                    } else {
                        seriesCardView.forumAndReply = SpannableString(it.replyCount.toString())
                    }

                    if (it.sage == 1) {
                        seriesCardView.sage = View.VISIBLE
                    } else {
                        seriesCardView.sage = View.GONE
                    }

                    if (it.ext != null && "" != it.ext) {
                        seriesCardView.haveImage = true
                        seriesCardView.imageUri = it.img + it.ext
                    } else {
                        seriesCardView.haveImage = false
                    }

                    seriesCardList.add(seriesCardView)

                }
                page++
                _seriesCards.postValue(seriesCardList)
                _loadingState.postValue(LoadingState.COMPLETE)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            seriesCardList.clear()
            page = 1
            model.refresh()
            getNextPage()
        }
    }

    fun changeForum(fid: Int) {
        this.fid = fid
        viewModelScope.launch {
            seriesCardList.clear()
            page = 1
            model.changeForum(fid)
            getNextPage()
        }
    }
}