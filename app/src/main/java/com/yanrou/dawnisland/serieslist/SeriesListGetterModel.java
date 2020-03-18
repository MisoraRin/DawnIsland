package com.yanrou.dawnisland.serieslist;

import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanrou.dawnisland.Fid2Name;
import com.yanrou.dawnisland.FooterView;
import com.yanrou.dawnisland.json2class.TimeLineJson;
import com.yanrou.dawnisland.span.RoundBackgroundColorSpan;
import com.yanrou.dawnisland.util.HttpUtil;
import com.yanrou.dawnisland.util.ReadableTime;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SeriesListGetterModel {
    static final int FirstPage = 1;
    static final int NextPage = 2;
    static final int Refresh = 3;

    static final int LOADING = 1001;
    static final int COMPLETE = 1002;
    static final int FAIL = 1003;
    private final Map<Integer, String> fid2Name;

    int loadingState = COMPLETE;

    int fid = -1;
    int page = 1;
    private Gson gson;
    private FooterView footerView;

    public SeriesListGetterModel(SeriesListGetterPresenter presenter) {
        fid2Name = Fid2Name.getDb();
        this.presenter = presenter;
        gson = new Gson();
    }

    SeriesListGetterPresenter presenter;

    /**
     * 当前所有列表
     */
    List<Object> items = new ArrayList<>();
    /**
     * 保存了当前所有串号，用于快速排查重复串
     */
    HashSet<String> seriesSet = new HashSet<>();

    /**
     * @param state 用来表示不同的加载情况，方便结束以后调用
     */
    void getNextPage(int state) {
        if (loadingState == LOADING) {
            return;
        }
        loadingState = LOADING;
        String url;
        if (fid == -1) {
            url = "https://nmb.fastmirror.org/Api/timeline?page=";
        } else {
            url = "https://nmb.fastmirror.org/Api/showf?id=" + fid + "&page=";
        }
        HttpUtil.sendOkHttpRequest(url + page, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loadingState = FAIL;

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                loadingState = COMPLETE;
                List<TimeLineJson> list = gson.fromJson(response.body().string(), new TypeToken<List<TimeLineJson>>() {
                }.getType());
                TimeLineJson temp;
                List<SeriesCardView> seriesCardViews = new ArrayList<>(20);
                for (int i = 0; i < list.size(); i++) {
                    //判断是否重复
                    if (seriesSet.add(list.get(i).getId())) {
                        //不重复
                        temp = list.get(i);
                        SeriesCardView seriesCardView = new SeriesCardView();
                        /**
                         * 预处理数据
                         */
                        seriesCardView.id = temp.getId();
                        if (fid == -1) {
                            seriesCardView.forum = fid2Name.get(temp.getFid());
                        } else {
                            seriesCardView.forum = fid2Name.get(fid);
                        }

                        //格式化时间
                        seriesCardView.time = ReadableTime.getDisplayTime(temp.getNow());
                        //预处理内容
                        seriesCardView.content = Html.fromHtml(temp.getContent());

                        SpannableString cookie = new SpannableString(temp.getUserid());
                        if (temp.getAdmin() == 1) {
                            cookie.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0F0F")), 0, cookie.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        seriesCardView.cookie = cookie;

                        if (fid == -1) {
                            SpannableString spannableString = new SpannableString(seriesCardView.forum + " · " + temp.getReplyCount());
                            spannableString.setSpan(new RoundBackgroundColorSpan(Color.parseColor("#12DBD1"), Color.parseColor("#FFFFFF")), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new RelativeSizeSpan(1.0f), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            seriesCardView.forumAndReply = spannableString;
                        } else {
                            seriesCardView.forumAndReply = new SpannableString(String.valueOf(temp.getReplyCount()));
                        }

                        if (temp.getSage() == 1) {
                            seriesCardView.sage = View.VISIBLE;
                        } else {
                            seriesCardView.sage = View.GONE;
                        }

                        if (temp.getExt() != null && (!"".equals(temp.getExt()))) {
                            seriesCardView.haveImage = true;
                            seriesCardView.imageUri = temp.getImg() + temp.getExt();
                        } else {
                            seriesCardView.haveImage = false;
                        }
                        seriesCardViews.add(seriesCardView);

                    }
                }
                //到这里的的时候 seriesCardViews里面放了去过重的一整页数据
                switch (state) {
                    case FirstPage:
                        items.addAll(seriesCardViews);
                        footerView = new FooterView();
                        footerView.text = "加载大成功";
                        items.add(footerView);
                        presenter.getFirstPageSuccess(items);
                        break;
                    case NextPage:
                        items.addAll(items.size() - 1, seriesCardViews);
                        presenter.getNextPageSuccess();
                        break;
                    case Refresh:
                        items.clear();
                        items.addAll(seriesCardViews);
                        footerView = new FooterView();
                        footerView.text = "加载大成功";
                        items.add(footerView);
                        presenter.refreshSuccess();
                    default:
                        break;
                }
                page++;
            }
        });
    }

    void refresh() {
        page = 1;
        seriesSet.clear();
        getNextPage(Refresh);
    }

    void changeForum(int fid) {
        page = 1;
        this.fid = fid;
        seriesSet.clear();
        getNextPage(Refresh);
    }
}
