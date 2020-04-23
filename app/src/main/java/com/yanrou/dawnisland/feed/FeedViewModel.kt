package com.yanrou.dawnisland.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.yanrou.dawnisland.json2class.FeedJson
import com.yanrou.dawnisland.util.ServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.text.StringEscapeUtils
import timber.log.Timber

class FeedViewModel(application: Application?) : AndroidViewModel(application!!) {
    private val _feeds = MutableLiveData<List<FeedJson>>()
    val feeds: LiveData<List<FeedJson>> get() = _feeds
    private val feedsList = mutableListOf<FeedJson>()
    private val feedsIds = mutableSetOf<String>()
    private var subscriberId: String? = null
    private var pageCount = 1

    fun getFeeds() {
        Timber.i("Requesting subscriptions for page $pageCount with uuid:$subscriberId")
        viewModelScope.launch {
            try {
                val list = ServiceClient.getFeeds(subscriberId!!, pageCount)
                val noDuplicates = list.filterNot { feedsIds.contains(it.id) }
                if (noDuplicates.isNotEmpty()) {
                    feedsIds.addAll(noDuplicates.map { it.id })
                    feedsList.addAll(noDuplicates)
                    Timber.i(
                            "feedsList now have ${feedsList.size} feeds"
                    )
                    _feeds.postValue(feedsList)
                    if (feedsList.size % 10 == 0) pageCount += 1
                } else {
                    Timber.i("feedsList has no new feeds.")
                }
            } catch (e: Exception) {
                Timber.e(e, "failed to get feeds")
            }
        }
    }

    fun deleteFeed(id: String) {
        Timber.i("Deleting Feed $id")
        viewModelScope.launch(Dispatchers.IO) {
            ServiceClient.delFeed(subscriberId!!, id).run {
                // TODO: check failure response, and use response msg
                /** res:
                 *  "\u53d6\u6d88\u8ba2\u9605\u6210\u529f!"
                 */
                val msg = StringEscapeUtils.unescapeJava(this.replace("\"", ""))
                Timber.i("$msg")
                for (i in 0 until feedsList.size) {
                    if (feedsList[i].id == id) {
                        feedsList.removeAt(i)
                        feedsIds.remove(id)
                        _feeds.postValue(feedsList)
                        break
                    }
                }
            }
        }
    }

    fun refresh() {
        feedsList.clear()
        feedsIds.clear()
        pageCount = 1
        getFeeds()
    }

    init {
        subscriberId = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("subscriber_id", "666")
        getFeeds()
    }
}