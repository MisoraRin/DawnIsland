package com.yanrou.dawnisland.database;

//import org.greenrobot.greendao.annotation.Convert;
//import org.greenrobot.greendao.annotation.Entity;
//import org.greenrobot.greendao.annotation.Generated;
//import org.greenrobot.greendao.annotation.NotNull;
//

import org.jetbrains.annotations.NotNull;

import java.util.List;

//@Entity
public class SeriesDAO {
    /**
     * 未启用回复
     */
    private final static int NONE = 0;
    /**
     * 订阅po更新数量
     */
    private final static int NEW_PO = 1;
    /**
     * 订阅po更新字数
     */
    private final static int NEW_PO_WORD_COUNT = 2;
    /**
     * 订阅有新回复
     */
    private final static int NEW_REPLY = 3;
    /**
     * 支持重设po的饼干，用来追更、只看po
     * 第一个位置是串首的饼干
     */
//    @NotNull
//    @Convert(converter = List2String.class, columnType = String.class)
    private List<String> po;
    /**
     * 上次阅读位置
     */
    private int lastPage;

    /**
     * 上次的回复数量，用来简单的判断有无新回复
     */
    private int lastReplyCount;
    /**
     * 指明订阅类型
     */
    private int substate = NONE;
    /**
     * 板块id
     */
    private int forumId;


    private String seriesId;

    private String fid;

    private String img;

    private String ext;

    private String now;

    private String userId;

    private String name;

    private String email;

    private String title;

    private String content;

    private boolean sage;

    private boolean admin;

    private int replyCount;

  //    @Generated(hash = 2103980481)
    public SeriesDAO(@NotNull List<String> po, int lastPage, int lastReplyCount,
                     int substate, int forumId, String seriesId, String fid, String img,
                     String ext, String now, String userId, String name, String email,
                     String title, String content, boolean sage, boolean admin,
                     int replyCount) {
        this.po = po;
        this.lastPage = lastPage;
        this.lastReplyCount = lastReplyCount;
        this.substate = substate;
        this.forumId = forumId;
        this.seriesId = seriesId;
        this.fid = fid;
        this.img = img;
        this.ext = ext;
        this.now = now;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.title = title;
        this.content = content;
        this.sage = sage;
        this.admin = admin;
        this.replyCount = replyCount;
    }

  //    @Generated(hash = 1048576983)
    public SeriesDAO() {
    }

    public List<String> getPo() {
        return this.po;
    }

    public void setPo(List<String> po) {
        this.po = po;
    }

    public int getLastPage() {
        return this.lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getLastReplyCount() {
        return this.lastReplyCount;
    }

    public void setLastReplyCount(int lastReplyCount) {
        this.lastReplyCount = lastReplyCount;
    }

    public int getSubstate() {
        return this.substate;
    }

    public void setSubstate(int substate) {
        this.substate = substate;
    }

    public int getForumId() {
        return this.forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    public String getSeriesId() {
        return this.seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getFid() {
        return this.fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getExt() {
        return this.ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getNow() {
        return this.now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getSage() {
        return this.sage;
    }

    public void setSage(boolean sage) {
        this.sage = sage;
    }

    public boolean getAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getReplyCount() {
        return this.replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }
}
