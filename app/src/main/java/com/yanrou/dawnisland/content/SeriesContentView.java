package com.yanrou.dawnisland.content;

import java.util.List;

public interface SeriesContentView {


    void setFirstPage(List<Object> items);

    void loadMoreSuccess();

    void refreshSuccess(int itemCount);

    void jumpSuccess();
}
