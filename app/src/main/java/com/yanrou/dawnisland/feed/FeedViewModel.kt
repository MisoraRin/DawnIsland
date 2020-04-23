package com.yanrou.dawnisland.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.yanrou.dawnisland.json2class.FeedJson
import com.yanrou.dawnisland.util.ServiceClient
import kotlinx.coroutines.launch
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
        TODO("delete feed")
    }

    init {
        subscriberId = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("subscriber_id", "666")
        getFeeds()
    }
}