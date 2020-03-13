package com.yanrou.dawnisland;

import java.util.List;

public interface SeriesListView {
    void setStartGetFirstPage();

    void setFirstPage(List<Object> items);

    void setGetNextPage();

    void setNextPage();

    void setRefreshSuccess();
}
