package com.yanrou.dawnisland.forum

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tencent.mmkv.MMKV
import com.yanrou.dawnisland.Fid2Name
import com.yanrou.dawnisland.json2class.ForumJson
import com.yanrou.dawnisland.util.ServiceClient
import com.yanrou.dawnisland.util.getForumList
import com.yanrou.dawnisland.util.putForumList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForumViewModel(application: Application) : AndroidViewModel(application) {
    private var forumList: List<ForumJson> = emptyList()
    private lateinit var groupState: List<GroupInfo>
    val forumOnView = MutableLiveData<List<Any>>(emptyList())
    private val switchedForum = MutableLiveData<Int>()


    init {
        getForumList()
    }

    private fun getForumList() {
        viewModelScope.launch(Dispatchers.Default) {
            forumList = getForumListRaw()
            groupState = List(forumList.size) { GroupInfo(-1, true) }
            //下面两个任务异步进行，一个是用来生成fid到forum的映射，另一个用来显示分组
            launch(Dispatchers.Main) {
                forumOnView.value = generateForumListWithGroup()
            }
            val initFidMap = launch(Dispatchers.Default) {
                //这个地方本来可以用kotlin自带的方法，但是那个方法会产生一堆Pair对象，触发GC，所以还是手写算了
                val forumMap = HashMap<Int, String>()
                for (forumGroup in forumList) {
                    for (forum in forumGroup.forums) {
                        forumMap[forum.id] = forum.name
                    }
                }
                withContext(Dispatchers.Main) { Fid2Name.db.value = forumMap }
            }
            //等待map初始化完成
            initFidMap.join()
            //可以开始取数据了,更改这个livedata的值就好，默认是时间线
            switchedForum.value = -1
        }

    }

    private fun generateForumListWithGroup(): List<Any> {
        val tempList = ArrayList<Any>(80)
        forumList.withIndex().forEach {
            groupState[it.index].adapterPosition = tempList.size
            tempList.add(it.value)
            if (groupState[it.index].isExpand) {
                tempList.addAll(it.value.forums)
            }
        }
        return tempList
    }

    fun refreshForumGroupExpandState(adapterPosition: Int) {
        groupState.map {
            if (it.adapterPosition == adapterPosition) {
                it.isExpand = it.isExpand.not()
            }
        }
        forumOnView.value = generateForumListWithGroup()
    }

    private suspend fun getForumListRaw(): List<ForumJson> {
        var forumJson = MMKV.defaultMMKV().getForumList()
        if (forumJson.isEmpty()) {
            val json = withContext(Dispatchers.IO) { ServiceClient.getForumList() }
            MMKV.defaultMMKV().putForumList(json)
            forumJson = json
        }
        return ServiceClient.convertForumListFromJson(forumJson)
    }

    private class GroupInfo(var adapterPosition: Int, var isExpand: Boolean)
}