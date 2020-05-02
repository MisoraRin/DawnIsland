package com.yanrou.dawnisland.trend;

import android.text.Html;
import android.text.Spanned;

public class TrendItem {
    private String rank;
    private String trend;
    private Spanned content;
    private String forum;
    private String id;


    public TrendItem(String rank, String trend, String content, String forum, String id) {
        this.rank = rank;
        this.trend = trend;
        this.content = Html.fromHtml(content);
        this.forum = forum;
        this.id = id;
    }

    public String getRank() {
        return rank;
    }

    public String getTrend() {
        return trend;
    }

    public Spanned getContent() {
        return content;
    }

    public String getForum() {
        return forum;
    }

    public String getId() {
        return id;
    }
}
