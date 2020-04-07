package com.yanrou.dawnisland.trend;

import android.util.Log;

import com.google.gson.Gson;
import com.yanrou.dawnisland.OnFinish;
import com.yanrou.dawnisland.json2class.ReplysBean;
import com.yanrou.dawnisland.json2class.SeriesContentJson;
import com.yanrou.dawnisland.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TrendList {
    public TrendList(OnFinish onFinish) {
        this.onFinish = onFinish;
    }

    OnFinish onFinish;

    public void getTodayTrend(final List<TrendItem> trendItems) {
        System.out.println("开始获取趋势榜");
        HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/thread?id=15347469&page=1", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("失败了" + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();

                SeriesContentJson seriesContentJson;
                try {
                    seriesContentJson = new Gson().fromJson(s, SeriesContentJson.class);
                } catch (Exception e) {
                    return;
                }
                int replycount = seriesContentJson.getReplyCount();
                int page = replycount / 19;
                if ((replycount % 19) > 0) {
                    page++;
                }
                System.out.println(page + "页内容");
                getLastPage(page, trendItems);
            }
        });
    }

    void getLastPage(final int page, final List<TrendItem> trendItems) {
        HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/thread?id=15347469&page=" + page, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: 获取失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                Log.d(TAG, "onResponse: " + s);
                SeriesContentJson seriesContentJson = new Gson().fromJson(s, SeriesContentJson.class);
                List<ReplysBean> replys = seriesContentJson.getReplys();
                Log.d(TAG, "onResponse: " + replys.size());
                for (int i = replys.size() - 1; i >= 0; i--) {
                    if (replys.get(i).getUserid().equals("m9R9kaD") && replys.get(i).getContent().contains("Trend")) {//防止有人回帖 更新：我没想到po自己也会来回复，结果又出bug了
                        String content = replys.get(i).getContent();
                        getTrendList(content, trendItems);

                        return;
                    }
                }
                //如果都到这里了，说明这一页没有内容，翻到上一页
                getLastPage(page - 1, trendItems);

            }
        });
    }

    void getTrendList(String str, List<TrendItem> trendItems) {
        //Log.d(TAG, "getTrendList: "+str);
        String pattern = "(\\d{1,2})\\.\\sTrend\\s(\\d+)\\s\\[(.+?)\\].+?No\\.(\\d+)</font>(?:<br\\s/>\\n)+(.+?)(?:<br\\s/>\\n)+—";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = r.matcher(str);
        while (m.find()) {
            trendItems.add(new TrendItem(m.group(1), m.group(2), m.group(5), m.group(3), m.group(4)));
        }
        Log.d(TAG, "getTrendList: 趋势串获取完毕" + trendItems.size());
        onFinish.tellDataGetFinish();
    }
}
