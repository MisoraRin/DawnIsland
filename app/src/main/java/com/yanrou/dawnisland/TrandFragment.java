package com.yanrou.dawnisland;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class TrandFragment extends Fragment {
    Activity activity;
    RecyclerView trendList;
    List<TrendItem> trendItems = new ArrayList<>();
    TrendAdapter trendAdapter = new TrendAdapter(trendItems);


    public TrandFragment() {
        // Required empty public constructor
    }


    public static TrandFragment newInstance(Activity activity) {
        TrandFragment fragment = new TrandFragment();
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        trendAdapter.setOnItemClick(new OnItemClick() {
            @Override
            public void onItemClick(String id, String forum) {
                Intent intent = new Intent(activity, SeriesContentActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("forumTextView", forum);
                startActivity(intent);
            }
        });
        trendList.setLayoutManager(layoutManager);
        trendList.setAdapter(trendAdapter);

        startGetTrend();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        //activity.findViewById(R.id.trend_recycleview);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the trend_item for this fragment
        View view = inflater.inflate(R.layout.fragment_trand, container, false);
        trendList = view.findViewById(R.id.trend_recycleview);
        return view;
    }

    public void startGetTrend() {
        TrendList trendList = new TrendList(new OnFinish() {
            @Override
            public void tellDataGetFinish() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        trendAdapter.notifyDataSetChanged();
                        Log.d(TAG, "run: 提示更新列表");
                    }
                });
            }
        });
        trendList.getTodayTrend(trendItems);
    }
}
