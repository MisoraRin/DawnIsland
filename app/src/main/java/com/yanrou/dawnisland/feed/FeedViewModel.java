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

    private List<FeedJson> feedJsons;
    private String subscriberId;
    private int page = 1;

    public FeedViewModel(Application application) {
        super(application);
        feedJsons = new ArrayList<>();

        subscriberId = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("subscriber_id", "666");
    }


    MutableLiveData<DataChange> getDataChangeMutableLiveData() {
        return dataChangeMutableLiveData;
    }


    private void getFeed() {
        HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/feed?uuid=" + subscriberId + "&page=" + page, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String string = response.body().string();
                if (!"".equals(string)) {
                    List<FeedJson> list = new Gson().fromJson(string, new TypeToken<List<FeedJson>>() {
                    }.getType());

                    feedJsons.addAll(list);
                    dataChangeMutableLiveData.postValue(adapter -> adapter.notifyDataSetChanged());
                    page++;
                } else {
                    //没有下一页了
                }
            }
        });
    }


    void getFirstPage() {
        if (page == 1) {
            getFeed();
        }
    }

    void loadNextPage() {
        getFeed();
    }

    public List<FeedJson> getFeedJsons() {
        return feedJsons;
    }

    interface DataChange {
        /**
         * 用来更新recyclerview数据
         */

        void notifyDataSetChanged(MultiTypeAdapter adapter);
    }


}
