package com.yanrou.dawnisland.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanrou.dawnisland.R;

public class FeedFragment extends Fragment {

    private FeedViewModel mViewModel;
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.feed_fragment, container, false);
        recyclerView = v.findViewById(R.id.feed_recycler_view);
        refreshLayout = v.findViewById(R.id.smart_refresh);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        mViewModel.getDataChangeMutableLiveData().observe(getViewLifecycleOwner(), FeedViewModel.DataChange::notifyDataSetChanged);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(mViewModel.getMultiTypeAdapter());
        mViewModel.getFeed();

    }
}
