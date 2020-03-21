package com.yanrou.dawnisland.feed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.drakeet.multitype.MultiTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanrou.dawnisland.json2class.FeedJson;
import com.yanrou.dawnisland.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FeedViewModel extends ViewModel {
    // TODO: Implement the ViewModel


    private MutableLiveData<DataChange> dataChangeMutableLiveData = new MutableLiveData<>();
    private MultiTypeAdapter multiTypeAdapter;
    private List<FeedJson> feedJsons;

    public FeedViewModel() {
        feedJsons = new ArrayList<>();
        multiTypeAdapter = new MultiTypeAdapter(feedJsons);
        multiTypeAdapter.register(FeedJson.class, new FeedItemViewBinder());
    }


    MutableLiveData<DataChange> getDataChangeMutableLiveData() {
        return dataChangeMutableLiveData;
    }

    public MultiTypeAdapter getMultiTypeAdapter() {
        return multiTypeAdapter;
    }

    public void getFeed() {
        HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/feed?uuid=666&page=1", new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                List<FeedJson> list = new Gson().fromJson(response.body().string(), new TypeToken<List<FeedJson>>() {
                }.getType());
                feedJsons.addAll(list);
                dataChangeMutableLiveData.postValue(FeedViewModel.this::notifyDataSetChanged);
            }
        });
    }

    private void notifyDataSetChanged() {
        multiTypeAdapter.notifyDataSetChanged();
    }

    interface DataChange {
        /**
         * 用来更新recyclerview数据
         */

        void notifyDataSetChanged();
    }


}
