package com.yanrou.dawnisland.feed;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

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

public class FeedViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel


    private MutableLiveData<DataChange> dataChangeMutableLiveData = new MutableLiveData<>();
    private MultiTypeAdapter multiTypeAdapter;
    private List<FeedJson> feedJsons;
    private String subscriberId;
    private int page = 1;

    public FeedViewModel(Application application) {
        super(application);
        feedJsons = new ArrayList<>();
        multiTypeAdapter = new MultiTypeAdapter(feedJsons);
        multiTypeAdapter.register(FeedJson.class, new FeedItemViewBinder());
        subscriberId = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("subscriber_id", "666");
    }


    MutableLiveData<DataChange> getDataChangeMutableLiveData() {
        return dataChangeMutableLiveData;
    }

    public MultiTypeAdapter getMultiTypeAdapter() {
        return multiTypeAdapter;
    }

    private void getFeed() {
        HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/feed?uuid=" + subscriberId + "&page=" + page, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                List<FeedJson> list = new Gson().fromJson(response.body().string(), new TypeToken<List<FeedJson>>() {
                }.getType());
                feedJsons.addAll(list);
                dataChangeMutableLiveData.postValue(FeedViewModel.this::notifyDataSetChanged);
                page++;
            }
        });
    }

    private void notifyDataSetChanged() {
        multiTypeAdapter.notifyDataSetChanged();
    }

    void getFirstPage() {
        if (page == 1) {
            getFeed();
        }
    }

    interface DataChange {
        /**
         * 用来更新recyclerview数据
         */

        void notifyDataSetChanged();
    }


}
