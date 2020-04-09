package com.yanrou.dawnisland.database;

//import org.litepal.annotation.Column;
//import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于存储串的数据，方便下次打开串的时候恢复上次的数据
 */
//public class SeriesData extends LitePalSupport {
public class SeriesData {
    //未启用回复
    private final static int NONE = 0;
    //订阅po更新数量
    private final static int NEW_PO = 1;
    //订阅po更新字数
    private final static int NEW_PO_WORD_COUNT = 2;
    //订阅有新回复
    private final static int NEW_REPLY = 3;
    /**
     * 支持重设po的饼干，用来追更、只看po
     * 第一个位置是串首的饼干
     */
//    @Column(nullable = false)
    private List<String> po = new ArrayList<>();
    /**
     * 上次阅读位置
     */
//    @Column(defaultValue = "1")
    private int lastPage;
    /**
     * 串号
     */
//    @Column(unique = true, nullable = false)
    private String seriesid;
    /**
     * 上次的回复数量，用来简单的判断有无新回复
     */
    private int lastReplyCount;
    /**
     * 指明订阅类型
     */
    private int substate = NONE;
    /**
     * 板块名称
     */
    private String forumName;

    public List<String> getPo() {
        return po;
    }

    public void setPo(List<String> po) {
        this.po = po;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public String getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(String seriesid) {
        this.seriesid = seriesid;
    }

    public int getLastReplyCount() {
        return lastReplyCount;
    }

    public void setLastReplyCount(int lastReplyCount) {
        this.lastReplyCount = lastReplyCount;
    }

    public int getSubstate() {
        return substate;
    }

    public void setSubstate(int substate) {
        this.substate = substate;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public List<String> getSeriesContentJsons() {
        return seriesContentJsons;
    }

    public void setSeriesContentJsons(List<String> seriesContentJsons) {
        this.seriesContentJsons = seriesContentJsons;
    }

    /**
     * 已经加载的所有信息
     */
//    @Column(nullable = false)
    private List<String> seriesContentJsons = new ArrayList<>();

    long time;

    /**
     * 重载方法自动更新保存时间
     *
     * @return
     */
//    @Override
//    public boolean save() {
//        time = System.currentTimeMillis();
//        return super.save();
//    }
}
