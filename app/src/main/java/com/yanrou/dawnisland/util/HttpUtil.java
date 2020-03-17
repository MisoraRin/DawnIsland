package com.yanrou.dawnisland.util;

import com.google.gson.Gson;
import com.yanrou.dawnisland.json2class.ReplysBean;
import com.yanrou.dawnisland.json2class.SeriesContentJson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HttpUtil {
    public static String cookie;
    private static final String TAG = "HttpUtil";

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).header("Cookie", "userhash=" + cookie).build();
        client.newCall(request).enqueue(callback);
    }

    //同步的方式获取页数
    public static int getReplyCount(String id) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://nmb.fastmirror.org/Api/thread?id=" + id + "&page=1").build();
        try {
            Response response = client.newCall(request).execute();
            String s = response.body().string();
            if (s.equals("\"\\u8be5\\u4e3b\\u9898\\u4e0d\\u5b58\\u5728\"")) {
                return 0;
            }
            SeriesContentJson seriesContentJson = new Gson().fromJson(s, SeriesContentJson.class);
            return seriesContentJson.getReplyCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }


    //异步的方式用来一次性获取所有内容
    public static void getAllPage(final String id) {
        System.out.println("请求页数");
        //先拿到回复数
        HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/thread?id=" + id + "&page=1", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("失败了" + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                SeriesContentJson seriesContentJson = new Gson().fromJson(s, SeriesContentJson.class);
                int replycount = seriesContentJson.getReplyCount();
                int page = replycount / 19 + 1;
                System.out.println(page + "页内容");
                getAllPage(id, page);
            }
        });
    }


    private static void getAllPage(String id, int page) {
        final SeriesContentJson[] seriesContentJsonList = new SeriesContentJson[page];
        for (int i = 0; i < page; i++) {
            final int finalI = i + 1;
            System.out.println("开始分配请求" + finalI);
            final int finalI1 = i;
            HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/thread?id=" + id + "&page=" + finalI, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("获取内容时失败");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String s = response.body().string();
                    seriesContentJsonList[finalI1] = new Gson().fromJson(s, SeriesContentJson.class);
                    System.out.println(finalI + "页数据返回");
                    for (int i = 0; i < seriesContentJsonList.length; i++) {
                        if (seriesContentJsonList[i] == null) {
                            System.out.println(i + "页数据为空");
                            return;
                        }
                    }
                    getAllPage(seriesContentJsonList);
                }
            });
        }
    }

    private static void getAllPage(SeriesContentJson[] seriesContentJsons) {
        System.out.println("数据获取完全" + seriesContentJsons.length);
        List<ReplysBean> replys = new ArrayList<>();
        for (int i = 0; i < seriesContentJsons.length; i++) {
            replys.addAll(seriesContentJsons[i].getReplys());
        }
        System.out.println("replys" + replys.size());
    }
}
