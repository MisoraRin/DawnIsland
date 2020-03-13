package com.yanrou.dawnisland;

public class SubscriberItem {
    String forum;
    String seriesid;
    String cookie;
    String content;
    //更新详情
    String newInfo;

    public SubscriberItem(String forum, String seriesid, String cookie, String content, String newInfo) {
        this.forum = forum;
        this.seriesid = seriesid;
        this.cookie = cookie;
        this.content = content;
        this.newInfo = newInfo;
    }


}
