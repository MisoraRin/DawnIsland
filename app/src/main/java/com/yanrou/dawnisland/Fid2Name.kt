package com.yanrou.dawnisland

/**
 * @author suche
 * 用于将fid与板块名称对应
 * 需要先初始化后再执行其他任务
 */
object Fid2Name {
    var db: Map<Int, String> = emptyMap()
        set(db) {
            if (field.isEmpty()) {
                field = db
            }
        }

    fun getForumByFid(fid: Int): String? {
        return db[fid]
    }
}