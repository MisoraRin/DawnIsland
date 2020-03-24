package com.yanrou.dawnisland.content;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.yanrou.dawnisland.FooterView;
import com.yanrou.dawnisland.Reference;
import com.yanrou.dawnisland.database.SeriesData;
import com.yanrou.dawnisland.json2class.ReplysBean;
import com.yanrou.dawnisland.json2class.SeriesContentJson;
import com.yanrou.dawnisland.span.SegmentSpacingSpan;
import com.yanrou.dawnisland.util.HttpUtil;
import com.yanrou.dawnisland.util.ReadableTime;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 这个类负责获取原始数据、存储数据、读取数据
 */
class SeriesContentModel {
    private static final String TAG = "SeriesContentModel";

    private String id;


    private SeriesData seriesData;


    /**
     * 标记是否有缓存
     */
    private boolean hasCache = false;


    /**
     * 标记最后一页是否完整
     * 如果为false,则应该用获取到的页面替换当前页
     */
    private boolean wholePage = true;
    private int lastPageCount;
    /**
     * 标记最后一页是否有广告
     */
    private boolean hasAd;
    /**
     * 控制翻页，如果有缓存就会从缓存中获取上次看到的位置,没有的话从第一页开始
     */
    private int backpage = 1;
    private int frontpage = 0;

    private final int GET_FIRST_PAGE = 1001;
    private final int GET_FRONT_PAGE = 1002;
    private final int GET_NEXT_PAGE = 1003;
    private final int GET_JUMP_PAGE = 1004;
    /**
     * 只有state为READY时可以请求新数据
     */
    private final int READY = 1000;

    private int state = 1000;

    private FooterView footerView = new FooterView();


    SeriesContentModel(String id) {
        this.id = id;
    }

    int getReplyCount() {
        return seriesData.getLastReplyCount();
    }

    private void getPageFromNet(final int page) {
        Log.d(TAG, "getNextPage: " + "开始请求下一页");
        HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/thread?id=" + id + "&page=" + page, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //TODO 记得处理失败的情况
                Log.d(TAG, "onFailure: 失败了失败了" + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: 成功了成功了");

                String s = response.body().string();
                /*
                  先判断串是否存在
                  这里有堵的成分，如果不是第一页就不判断，可能会快一点
                  如果为真表示串已经被删了
                  TODO 这个地方仅仅考虑了没有缓存的情况，加上缓存功能后还需要改进
                  TODO 例如已缓存的串被删除，用户在下拉的时候会触发刷新，这里的就不适用了
                 */
                if (page == 1 && "\"\\u8be5\\u4e3b\\u9898\\u4e0d\\u5b58\\u5728\"".equals(s)) {
                    footerView.text = "该串已被删除";
                    items.add(footerView);
                    presenter.loadFirstPageSuccess(items);
                    return;
                }

                /*
                  解析串
                 */
                final SeriesContentJson seriesContentJson = new Gson().fromJson(s, SeriesContentJson.class);


                /**
                 * 防止翻页翻过，这一句表示已经翻到底了
                 * TODO 等待处理
                 */
                Log.d(TAG, "onResponse: " + (page != 1) + page);
                if ((page != 1) && ((seriesContentJson.getReplys().size() == 1 && "9999999".equals(seriesContentJson.getReplys().get(0).getSeriesId())) || seriesContentJson.getReplys().size() == 0)) {
                    Log.d(TAG, "onResponse: 空页");
                    return;
                }
                /**
                 * 保存页面数据用于下次加载
                 */
                if (seriesData.getSeriesContentJsons().size() < page + 1) {
                    int d = page + 1 - seriesData.getSeriesContentJsons().size();
                    for (int i = 0; i < d; i++) {
                        seriesData.getSeriesContentJsons().add("");
                    }
                }
                seriesData.getSeriesContentJsons().set(page, s);
                seriesData.save();

                formatContent(seriesContentJson, page);
                //上面这一步做完我们获得了一个处理好的列表
            }
        });
    }

    /**
     *
     * 用来获取引用内容
     * 引用内容分为串内引用和串外引用，这个是用来获取串外引用的跳转原帖链接的
     *
     * @param html 获取到的html文本
     * @return
     */
    private Reference decodeHtml(String html) {
        Reference reference = new Reference();
        Document doc = Jsoup.parse(html);
        List<Element> elements = doc.getAllElements();
        for (Element element : elements) {
            String className = element.className();
            if ("h-threads-item-reply h-threads-item-ref".equals(className)) {
                reference.id = element.attr("data-threads-id");
            } else if ("h-threads-img-a".equals(className)) {
                reference.image = element.attr("href");
            } else if ("h-threads-img".equals(className)) {
                reference.thumb = element.attr("src");
            } else if ("h-threads-info-title".equals(className)) {
                reference.title = element.text();
            } else if ("h-threads-info-email".equals(className)) {
                // TODO email or user ?
                reference.user = element.text();
            } else if ("h-threads-info-createdat".equals(className)) {
                reference.time = element.text();
            } else if ("h-threads-info-uid".equals(className)) {
                String user = element.text();
                if (user.startsWith("ID:")) {
                    reference.userId = user.substring(3);
                } else {
                    reference.userId = user;
                }
                reference.admin = element.childNodeSize() > 1;
            } else if ("h-threads-info-id".equals(className)) {
                String href = element.attr("href");
                if (href.startsWith("/t/")) {
                    int index = href.indexOf('?');
                    if (index >= 0) {
                        reference.postId = href.substring(3, index);
                    } else {
                        reference.postId = href.substring(3);
                    }
                }
            } else if ("h-threads-content".equals(className)) {
                reference.content = element.html();
            }
        }
        return reference;
    }

    /**
     * 用于处理获取到的json数据
     *
     * @param seriesContentJson gson格式化json后产生的类
     */
    private void formatContent(SeriesContentJson seriesContentJson, int page) {
        if (page == 1) {
            Log.d(TAG, "onResponse: 第一页");
            seriesData.setLastPage(1);
            seriesData.setLastReplyCount(seriesContentJson.getReplyCount());
            seriesData.getPo().add(seriesContentJson.getUserid());
            seriesData.save();

            po.add(seriesContentJson.getUserid());

            seriesContentJson.getReplys().add(0, new ReplysBean(
                    seriesContentJson.getSeriesId(),
                    seriesContentJson.getUserid(),
                    seriesContentJson.getAdmin(),
                    seriesContentJson.getTitle(),
                    seriesContentJson.getEmail(),
                    seriesContentJson.getNow(),
                    seriesContentJson.getContent(),
                    seriesContentJson.getImg(),
                    seriesContentJson.getExt(),
                    seriesContentJson.getName(),
                    seriesContentJson.getSage()
            ));
        }

        List<ContentItem> contentItems = new ArrayList<>();
        List<ReplysBean> replysBeans = seriesContentJson.getReplys();
        ReplysBean temp;
        /*
          在这里预处理内容，保证显示时可以直接显示
         */
        for (int i = 0; i < replysBeans.size(); i++) {
            temp = replysBeans.get(i);
            ContentItem contentItem = new ContentItem();
            /*
              处理时间
             */
            contentItem.time = ReadableTime.getDisplayTime(temp.getNow());
            /*
              处理饼干
              PO需要加粗
              普通饼干是灰色，po是黑色，红名是红色
             */
            SpannableStringBuilder cookie = new SpannableStringBuilder(temp.getUserid());
            ForegroundColorSpan normalColor = new ForegroundColorSpan(Color.parseColor("#B0B0B0"));
            ForegroundColorSpan quoteColor = new ForegroundColorSpan(Color.parseColor("#19A8A8")); // primary color
            ForegroundColorSpan adminColor = new ForegroundColorSpan(Color.parseColor("#FF0F0F"));
            ForegroundColorSpan poColor = new ForegroundColorSpan(Color.parseColor("#000000"));

            StyleSpan styleSpanBold = new StyleSpan(Typeface.BOLD);

            if (temp.getAdmin() == 1) {
                cookie.setSpan(adminColor, 0, cookie.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            } else if (po.contains(temp.getUserid())) {
                cookie.setSpan(poColor, 0, cookie.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }

            if (po.contains(temp.getUserid())) {
                cookie.setSpan(styleSpanBold, 0, cookie.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            contentItem.cookie = cookie;
            /*
              处理内容
              主要是处理引用串号
             */
            SpannableStringBuilder contentSpan = new SpannableStringBuilder(Html.fromHtml(temp.getContent()));
            /*
              这一句是添加段间距
              if用来判断作者有没有自己加空行，加了的话就不加段间距
             */
            if (!contentSpan.toString().contains("\n\n")) {
                contentSpan.setSpan(new SegmentSpacingSpan(0, 20), 0, contentSpan.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            int index = -1;
            int hideStart, hideEnd;
            hideStart = contentSpan.toString().indexOf("[h]");
            hideEnd = contentSpan.toString().indexOf("[/h]");

            while (hideStart != -1 && hideEnd != -1 && hideStart < hideEnd) {
                contentSpan.delete(hideStart, hideStart + 3);
                Log.d(TAG, "onResponse: " + contentSpan.toString().substring(hideEnd));
                contentSpan.delete(hideEnd - 3, hideEnd + 1);

                BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor("#555555"));
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.TRANSPARENT);
                contentSpan.setSpan(backgroundColorSpan, hideStart, hideEnd - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                contentSpan.setSpan(foregroundColorSpan, hideStart, hideEnd - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                    }

                    @Override
                    public void onClick(@NonNull View widget) {
                        if (widget instanceof TextView) {
                            CharSequence charSequence = ((TextView) widget).getText();
                            if (charSequence instanceof Spannable) {
                                ((Spannable) charSequence).removeSpan(backgroundColorSpan);
                                ((Spannable) charSequence).removeSpan(foregroundColorSpan);
                                ((TextView) widget).setHighlightColor(Color.TRANSPARENT);
                            }
                        }
                    }
                };
                contentSpan.setSpan(clickableSpan, hideStart, hideEnd - 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                index = hideEnd - 3;
                hideStart = contentSpan.toString().indexOf("[h]", index);
                hideEnd = contentSpan.toString().indexOf("[/h]", index);
            }

            /*****************************************************************8
             * temporary solution for quotation, needs rework after restructuring
             * 支持点击展开
             * 暂时先取消掉这一段
             * TODO 读取设置选择这里是打开对话框还是直接展开
             * TODO **目前仅支持串内引用**
             */

            ForegroundColorSpan[] foregroundColorSpans = contentSpan.getSpans(0, contentSpan.length(), ForegroundColorSpan.class);
            Log.d(TAG, "onResponse: " + foregroundColorSpans.length);
            if (foregroundColorSpans.length != 0) {
                Log.d(TAG, "onResponse: 进入选字阶段！");
                for (int j = 0; j < foregroundColorSpans.length; j++) {

                    int start = contentSpan.getSpanStart(foregroundColorSpans[j]);
                    int end = contentSpan.getSpanEnd(foregroundColorSpans[j]);

                    CharSequence charSequence = contentSpan.subSequence(start, end);

                    if (charSequence.toString().contains(">>No.")) {
                        Log.d(TAG, "onResponse: 起" + start + " 末" + end + contentSpan.subSequence(start, end).toString().substring(5));
                        String seriesId = contentSpan.subSequence(start, end).toString().substring(5);
                        CharSequence originalText = contentSpan.subSequence(end, contentSpan.length());
                        Log.d(TAG, "onResponse: seriesID" + seriesId + " ");
                        ReplysBean quote = null;
                        for (ReplysBean d : replysBeans) {
                            if (seriesId.equals(d.getSeriesId())){
                                quote = d;
                                break;
                            }
                        }
                        if (quote != null){
                            ReplysBean finalQuote = quote;
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View widget) {
                                    if (widget instanceof TextView) {
                                        Log.d(TAG,"Clicked on quote " + finalQuote.getSeriesId());
                                        CharSequence charSequence = ((TextView) widget).getText();
                                        if (charSequence instanceof Spannable) {
                                            charSequence = charSequence.subSequence(start,end) + "\n"
                                                    + finalQuote.getUserid() +" " + finalQuote.getNow() + "\n"
                                                    + finalQuote.getContent();
                                            int divider = charSequence.length();
                                            Spannable s = new SpannableString(charSequence+ "\n" + Html.fromHtml(originalText.toString()));
                                            s.setSpan(quoteColor, start, divider,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                            ((TextView) widget).setText(s);
                                        }
                                    }
                                }

                                @Override
                                public void updateDrawState(@NonNull TextPaint ds) {
                                    super.updateDrawState(ds);
                                }
                            };
                            contentSpan.setSpan(clickableSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }

                    }
                }
            }

            contentItem.content = contentSpan;

            if (temp.getSage() == 1) {
                contentItem.sega = View.VISIBLE;
            } else {
                contentItem.sega = View.GONE;
            }

            contentItem.seriesId = temp.getSeriesId();

            if (temp.getExt() != null && (!"".equals(temp.getExt()))) {
                contentItem.hasImage = true;
                contentItem.imgurl = temp.getImg() + temp.getExt();
            } else {
                contentItem.hasImage = false;
            }

            StringBuilder nametitleBulider = new StringBuilder();
            if (temp.getTitle() != null && !temp.getTitle().equals("无标题")) {
                nametitleBulider.append("标题：").append(temp.getTitle());
                contentItem.hasTitleOrName = true;
            }
            if (temp.getName() != null && !temp.getName().equals("无名氏")) {
                if (nametitleBulider.length() != 0) {
                    nametitleBulider.append("\n");
                }
                nametitleBulider.append("作者：").append(temp.getName());
                contentItem.hasTitleOrName = true;
            }
            contentItem.titleAndName = nametitleBulider.toString();
            contentItems.add(contentItem);
        }
        /*数据处理完成，下面将数据添加进去*/
        //*********************************************************************
        int adindex = 0;
        if (page == 1) {
            adindex = 1;
        }
        if (state == GET_NEXT_PAGE) {
            if (wholePage) {
                items.addAll(items.size() - 1, contentItems);
                //只在第一次添加的时候判定是否有广告，因为后面都不会变了
                hasAd = "9999999".equals(seriesContentJson.getReplys().get(adindex).getSeriesId());
                Log.d(TAG, "formatContent: " + hasAd + seriesContentJson.getReplys().get(adindex).getSeriesId());
            } else {
                //这里就是加载最后一页不全的时候，要添加数据
                //最后一页是否有广告

                Log.d(TAG, "formatContent: 新获取的" + "9999999".equals(seriesContentJson.getReplys().get(adindex).getSeriesId()) + "已加载的" + hasAd);
                if (hasAd) {
                    //有广告
                    if (!"9999999".equals(seriesContentJson.getReplys().get(adindex).getSeriesId())) {
                        //新获取的页面没广告,加一条空数据在开头
                        Log.d(TAG, "formatContent: 加了一条数据");
                        contentItems.add(adindex, new ContentItem());
                    }
                } else {
                    //没有广告
                    if ("9999999".equals(seriesContentJson.getReplys().get(adindex).getSeriesId())) {
                        //新获取的页面有广告，需要对齐数据
                        Log.d(TAG, "formatContent: 删了一条数据");
                        contentItems.remove(adindex);
                    }
                }
                items.addAll(items.size() - 1, contentItems.subList(lastPageCount, contentItems.size()));
            }
            presenter.loadMoreSuccess();
        } else if (state == GET_FIRST_PAGE) {
            items.addAll(contentItems);
            footerView.text = "加载完成";
            items.add(footerView);
            if (seriesContentJson.getReplys().size() == 1) {
                hasAd = false;
            } else {
                hasAd = "9999999".equals(seriesContentJson.getReplys().get(adindex).getSeriesId());
            }
            presenter.loadFirstPageSuccess(items);
        } else if (state == GET_FRONT_PAGE) {
            items.addAll(0, contentItems);
            presenter.refreshSuccess(contentItems.size());
        } else if (state == GET_JUMP_PAGE) {
            items.clear();
            items.addAll(contentItems);
            items.add(footerView);
            presenter.jumpSuccess();
        }

        /**
         * 存一下最新看到的页数和最新的回复数
         */
        seriesData.setLastPage(page);
        seriesData.setLastReplyCount(seriesContentJson.getReplyCount());
        seriesData.save();
        /**
         * 已经搞定了所有的事了，开始修改页数
         * 如果是frontpage就减一，backpage并且当前页面已经满了就加一
         */
        Log.d(TAG, "formatContent: 页数" + page);
        if (page == backpage) {
            //这个if用来判断这一页是否完整，如果完整才能到下一页
            Log.d(TAG, "formatContent: 这一页有几个回复" + seriesContentJson.getReplys().size());
            if (seriesContentJson.getReplys().size() == 20 || (seriesContentJson.getReplys().size() == 19 && !"9999999".equals(seriesContentJson.getReplys().get(0).getSeriesId()))) {
                Log.d(TAG, "formatContent: case1+1");
                wholePage = true;
                backpage++;
            } else if (page == 1 && (seriesContentJson.getReplys().size() == 21 || (seriesContentJson.getReplys().size() == 20 && !seriesContentJson.getReplys().get(0).getSeriesId().equals("9999999")))) {
                Log.d(TAG, "formatContent: case2+1");
                wholePage = true;
                backpage++;
            } else {
                //不完整需要做标记，下次刷新的时候特殊处理
                Log.d(TAG, "formatContent: case3不加1 " + contentItems.size());
                wholePage = false;
                lastPageCount = contentItems.size();
            }
        } else {
            frontpage--;
        }

        state = READY;
    }

    /**
     * 首次加载
     * 如果上次看过，将读取上次的阅读位置然后恢复
     * 顺便把缓存做进去吧
     */
    public void loadFirst() {
        if (state != READY) {
            return;
        }
        state = GET_FIRST_PAGE;
        List<SeriesData> seriesDatas = LitePal.where("seriesid = ?", id).find(SeriesData.class, true);
        //没找到
        if (seriesDatas.size() == 0) {
            seriesData = new SeriesData();
            seriesData.setSeriesid(id);
            hasCache = false;
        } else {
            //找到了
            seriesData = seriesDatas.get(0);
            //虽然缓存了但是没有数据
            if (seriesDatas.get(0).getSeriesContentJsons().size() == 0) {
                seriesData.delete();
                loadFirst();
            }
            backpage = seriesData.getLastPage();
            frontpage = backpage - 1;
            po = seriesData.getPo();
            hasCache = true;
        }
        loadPage(backpage);
    }

    private void loadPage(int page) {
        //若存在缓存则检测数组下标是否越界并获取缓存
        if (hasCache && seriesData.getSeriesContentJsons().size() > page && seriesData.getSeriesContentJsons().get(page) != null && !"".equals(seriesData.getSeriesContentJsons().get(page))) {
            Log.d(TAG, "loadPage: 缓存加载" + seriesData.getSeriesContentJsons().get(page));
            SeriesContentJson seriesContentJson = new Gson().fromJson(seriesData.getSeriesContentJsons().get(page), SeriesContentJson.class);
            formatContent(seriesContentJson, page);
        } else {
            getPageFromNet(page);
        }
    }

    /**
     * 加载下一页
     */
    public void loadNextPage() {
        if (state != READY) {
            return;
        }

        state = GET_NEXT_PAGE;
        if (!wholePage) {
            Log.d(TAG, "loadPage: 因为是最后一页并且不齐全，所以要从网络加载");
            getPageFromNet(backpage);
        } else {
            loadPage(backpage);
        }

        Log.d(TAG, "loadNextPage: 加载下一页");
    }

    public void loadFrontPage() {
        if (state != READY) {
            return;
        }
        state = GET_FRONT_PAGE;
        //TODO 这里可以考虑提供上拉刷新缓存功能
        if (frontpage <= 0) {
            presenter.refreshSuccess(0);
            state = READY;
            return;
        }
        loadPage(frontpage);
    }

    public void jumpPage(int page) {
        if (state != READY) {
            return;
        }
        state = GET_JUMP_PAGE;
        backpage = page;
        frontpage = backpage - 1;
        loadPage(page);
    }
}
