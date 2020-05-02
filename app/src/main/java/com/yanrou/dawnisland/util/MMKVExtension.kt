package com.yanrou.dawnisland.util

import com.tencent.mmkv.MMKV

const val FORUM_JSON_KEY = "ForumJson"

fun MMKV.putForumList(json: String) {
    this.encode(FORUM_JSON_KEY, json)
}

fun MMKV.getForumList(): String {
    return this.getString(FORUM_JSON_KEY, "")!!
}