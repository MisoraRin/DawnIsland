package com.yanrou.dawnisland.feed;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.drakeet.multitype.ViewDelegate;
import com.yanrou.dawnisland.content.SeriesContentActivity;
import com.yanrou.dawnisland.json2class.FeedJson;
import com.yanrou.dawnisland.serieslist.CardViewFactory;

import org.jetbrains.annotations.NotNull;

/**
 * @author suche
 */
public class FeedItemViewBinder extends ViewDelegate<FeedJson, CardViewFactory.MyCardView> {
    private static final String TAG = "FeedItemViewBinder";

    @Override
    public void onBindView(@NotNull CardViewFactory.MyCardView myCardView, FeedJson feedJson) {
        myCardView.forum = "收藏夹";
        myCardView.id = feedJson.getId();
        myCardView.getContentView().setText(feedJson.getContent());
        myCardView.getCookieView().setText(feedJson.getUserid());
        if (feedJson.getExt() != null && !feedJson.getExt().equals("")) {
            myCardView.getImageContent().setVisibility(View.VISIBLE);
            Glide.with(myCardView.getContext())
                    .load("https://nmbimg.fastmirror.org/thumb/" + feedJson.getImg() + feedJson.getImg())
                    .override(250, 250)
                    .into(myCardView.getImageContent());
        } else {
            myCardView.getImageContent().setVisibility(View.GONE);
        }
    }

    @NotNull
    @Override
    public CardViewFactory.MyCardView onCreateView(@NotNull Context context) {
        Log.d(TAG, "onBindView: 创建对象");
        final CardViewFactory.MyCardView seriesCardView = CardViewFactory.getInstance(context).getSeriesCardView(context);
        seriesCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SeriesContentActivity.class);
            intent.putExtra("id", seriesCardView.id);
            intent.putExtra("forumTextView", seriesCardView.forum);
            context.startActivity(intent);
        });
        return seriesCardView;
    }
}
