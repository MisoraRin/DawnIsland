package com.yanrou.dawnisland.content;

import android.text.SpannableStringBuilder;

import java.util.List;

public class ContentItem {
    int sega;
    String time;
    SpannableStringBuilder cookie;


    public SpannableStringBuilder content;
    String seriesId;
    boolean hasImage = false;
    String imgurl;
    boolean hasTitleOrName = false;
    String titleAndName;

    List<String> quotes;
}