package com.yanrou.dawnisland;

import com.yanrou.dawnisland.json2class.ForumJson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suche
 * 用于将fid与板块名称对应
 * 需要先初始化后再执行其他任务
 */
public class Fid2Name {
    static public Map<Integer, String> db = new HashMap<>();

    static void setDB(List<ForumJson.ForumsBean> forums) {
        for (int i = 0; i < forums.size(); i++) {
            db.put(forums.get(i).getId(), forums.get(i).getName());
        }
    }

    public static Map<Integer, String> getDb() {
        return db;
    }
}
