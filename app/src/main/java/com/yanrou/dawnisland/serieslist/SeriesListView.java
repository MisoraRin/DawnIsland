package com.yanrou.dawnisland.serieslist;

import java.util.List;

public interface SeriesListView {
    void setStartGetFirstPage();

    void setFirstPage(List<Object> items);

    void setGetNextPage();

    void setNextPage();

    void setRefreshSuccess();
}
