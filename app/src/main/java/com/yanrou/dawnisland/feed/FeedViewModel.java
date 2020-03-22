package com.yanrou.dawnisland.feed;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

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

    private MutableLiveData<Integer> insertPosMutableLiveData = new MutableLiveData<>(0);

    private List<FeedJson> feedJsons;
    private String subscriberId;
    private int page = 1;
    private String TAG = "Feed";

    public FeedViewModel(Application application) {
        super(application);
        feedJsons = new ArrayList<>();

        subscriberId = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("subscriber_id", "666");

        loadNextPage();
    }



    MutableLiveData<Integer> getInsertPosMutableLiveData() {
        return insertPosMutableLiveData;
    }


    private void getFeedByPage(Integer page) {
        Log.i(TAG, "Requesting subscriptions for page "+ page + " with uuid:" + subscriberId);
        HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/feed?uuid=" + subscriberId + "&page=" + page, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String string = response.body().string();
                if (!"[]".equals(string)) {
                    List<FeedJson> list = new Gson().fromJson(string, new TypeToken<List<FeedJson>>() {
                    }.getType());

                    feedJsons.addAll(list);
                    Log.i(TAG, "added subscriptions to view");
                    insertPosMutableLiveData.postValue(insertPosMutableLiveData.getValue()+list.size());

                } else {
                    //没有下一页了
                }
            }
        });
    }

    void loadNextPage() {
        getFeedByPage(page);
        page++;
    }

    public List<FeedJson> getFeedJsons() {
        return feedJsons;
    }


}
