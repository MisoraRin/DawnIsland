package com.yanrou.dawnisland

import com.yanrou.dawnisland.util.ForumMapLiveData

/**
 * @author suche
 * 用于将fid与板块名称对应
 * 需要先初始化后再执行其他任务
 */
object Fid2Name {
    var db = ForumMapLiveData<Map<Int, String>>()
}
