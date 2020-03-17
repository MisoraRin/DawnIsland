package com.yanrou.dawnisland.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于存储串的数据，方便下次打开串的时候恢复上次的数据
 */
public class SeriesData extends LitePalSupport {
    //未启用回复
    public final static int NONE = 0;
    //订阅po更新数量
    public final static int NEW_PO = 1;
    //订阅po更新字数
    public final static int NEW_PO_WORD_COUNT = 2;
    //订阅有新回复
    public final static int NEW_REPLY = 3;
    /**
     * 支持重设po的饼干，用来追更、只看po
     * 第一个位置是串首的饼干
     */
    @Column(nullable = false)
    public List<String> po = new ArrayList<>();
    /**
     * 上次阅读位置
     */
    @Column(defaultValue = "1")
    public int lastPage;
    /**
     * 串号
     */
    @Column(unique = true, nullable = false)
    public String seriesid;
    /**
     * 上次的回复数量，用来简单的判断有无新回复
     */
    public int lastReplyCount;
    /**
     * 指明订阅类型
     */
    public int substate = NONE;
    /**
     * 板块名称
     */
    public String forumName;
    /**
     * 已经加载的所有信息
     */
    @Column(nullable = false)
    public List<String> seriesContentJsons = new ArrayList<>();

    long time;

    /**
     * 重载方法自动更新保存时间
     *
     * @return
     */
    @Override
    public boolean save() {
        time = System.currentTimeMillis();
        return super.save();
    }
}
