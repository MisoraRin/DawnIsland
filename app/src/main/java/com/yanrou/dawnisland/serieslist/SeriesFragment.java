package com.yanrou.dawnisland.serieslist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.multitype.MultiTypeAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanrou.dawnisland.FooterView;
import com.yanrou.dawnisland.FooterViewBinder;
import com.yanrou.dawnisland.R;
import com.yanrou.dawnisland.SeriesRecyclerOnScrollListener;

import java.util.List;


public class SeriesFragment extends Fragment implements SeriesListView {
    private static final String TAG = "SeriesFragment";

    private RecyclerView seriesList;
    private Activity activity;

    private MultiTypeAdapter seriesListAdapter;
    private SeriesListGetterPresenter presenter;

    private SmartRefreshLayout smartRefreshLayout;

    public SeriesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        seriesListAdapter = new MultiTypeAdapter();
        presenter = new SeriesListGetterPresenter(this);
        seriesListAdapter.register(SeriesCardView.class, new SeriesCardViewBinder(activity));
        seriesListAdapter.register(FooterView.class, new FooterViewBinder());


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        smartRefreshLayout.setEnableAutoLoadMore(false);
        smartRefreshLayout.setDragRate(0.3f);

        smartRefreshLayout.setFooterTriggerRate(2f);
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            Log.d(TAG, "onLoadMore: 触发了onLoadMore");
            smartRefreshLayout.finishLoadMore(0);
        });
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            Log.d(TAG, "onActivityCreated: 触发了刷新");
            reFresh();
        });


        /**
         * 这里是设定滑动监听，以便实现加载下一页的效果
         */
        seriesList.addOnScrollListener(new SeriesRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                presenter.getNextPage();
            }
        });

        /**
         * 这里是添加layoutManager
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        seriesList.setLayoutManager(layoutManager);


        /**
         * 添加adapter
         */
        seriesList.setAdapter(seriesListAdapter);

        Log.d(TAG, "onActivityCreated: 到这里");
        presenter.getFirstPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_series, container, false);
        seriesList = view.findViewById(R.id.series_list_fragment);
        smartRefreshLayout = view.findViewById(R.id.smartrefresh);
        Log.d(TAG, "onCreateView: " + view.findViewById(R.id.series_list_fragment));
        return view;
    }

    @Override
    public void setStartGetFirstPage() {
        //swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void setFirstPage(List<Object> items) {
        seriesListAdapter.setItems(items);
        activity.runOnUiThread(() -> {
            smartRefreshLayout.finishRefresh();
            seriesListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void setGetNextPage() {

    }

    @Override
    public void setNextPage() {
        activity.runOnUiThread(() -> {
            seriesListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void setRefreshSuccess() {
        activity.runOnUiThread(() -> {
            Log.d(TAG, "setRefreshSuccess: 回调中的finishRefresh被调用");
            smartRefreshLayout.finishRefresh();
            seriesListAdapter.notifyDataSetChanged();
        });
    }

    public void changeForum(int fid) {
        seriesList.scrollToPosition(0);
        //Log.d(TAG, "changeForum: 调用refresh前finish一遍");
        //smartRefreshLayout.finishRefresh();
        Log.d(TAG, "changeForum: 启动刷新动画");
        boolean d = smartRefreshLayout.autoRefreshAnimationOnly();
        Log.d(TAG, "changeForum: 启用动画的返回值" + d);
        presenter.changeForum(fid);
    }

    public void reFresh() {
        seriesList.scrollToPosition(0);

        smartRefreshLayout.autoRefreshAnimationOnly();
        presenter.startRefresh();
    }
    /**因为服务器禁止，取消获取新提醒相关功能
     private void getNewReply() {
     final List<SeriesData> seriesDatas = LitePal.where("substate = ?", String.valueOf(SeriesData.NEW_REPLY)).find(SeriesData.class);
     Log.d(TAG, "getNewReply: 订阅了" + seriesDatas.size());
     for (int i = 0; i < seriesDatas.size(); i++) {
     final int finalI = i;
     Runnable runnable = new Runnable() {
    @Override public void run() {
    SeriesData temp = seriesDatas.get(finalI);
    int newReplyCount = HttpUtil.getReplyCount(temp.seriesid);
    int newcount = newReplyCount - temp.lastReplyCount;
    //说明有更新
    if (newcount > 0) {
    Log.d(TAG, "run: " + temp.content);
    subscriberItems.add(new SubscriberItem(temp.forumName, temp.seriesid, temp.po.get(0), temp.content, String.valueOf(newcount)));
    contentAdapter.setSubscriberVisiable(true);
    activity.runOnUiThread(new Runnable() {
    @Override public void run() {
    subscriberAdapter.notifyItemInserted(0);
    contentAdapter.notifyDataSetChanged();
    }
    });
    activity.runOnUiThread(new Runnable() {
    @Override public void run() {
    contentAdapter.notifyDataSetChanged();
    }
    });

    }
    }
    };
     new Thread(runnable).start();
     }
     }

     public void changeForum(int fid) {
     page = 1;
     this.fid = fid;
     timeLineJsons.clear();
     contentAdapter.notifyDataSetChanged();
     getNewPage();
     }

     */
}
