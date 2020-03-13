package com.yanrou.dawnisland;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SeriesRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "SeriesRecyclerOnScrollL";
    private boolean isSlidingUpward = false;

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastItemPosition = manager.findLastVisibleItemPosition();

        int itemCount = manager.getItemCount();
        // 判断是否滑动到了最后一个item，并且是向上滑动
        Log.d(TAG, "onScrollStateChanged: " + itemCount + " " + lastItemPosition);
        if (lastItemPosition >= (itemCount - 5) && isSlidingUpward) {
            //加载更多
            onLoadMore();
        }

    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        isSlidingUpward = (dy > 0);
    }

    /**
     * 在滑动到倒数第四条时执行该方法
     */
    public abstract void onLoadMore();
}
