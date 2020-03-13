package com.yanrou.dawnisland;

import com.yanrou.dawnisland.json2class.ForumJson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB {
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
