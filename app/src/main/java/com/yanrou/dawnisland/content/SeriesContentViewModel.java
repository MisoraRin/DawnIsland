package com.yanrou.dawnisland.content;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class SeriesContentViewModel extends AndroidViewModel {
    public SeriesContentViewModel(@NonNull Application application) {
        super(application);
    }

    void init(String seriesId) {

    }

    /**
     * 处理加载更多逻辑
     */
    public void loadMore() {

    }

    /**
     * 处理下拉逻辑
     */
    public void refresh() {

    }

    /**
     * 处理跳页逻辑
     *
     * @param page 将要跳到的页数
     */
    public void jumpPage(int page) {

    }

    /**
     * 在这里返回当前看到的页数
     *
     * @return 当前看到的页数
     */
    public int getNowPage() {
        return 0;
    }

    /**
     * 在这里返回总页数
     *
     * @return 总页数
     */
    public int getTotalPage() {
        return 0;
    }

}
