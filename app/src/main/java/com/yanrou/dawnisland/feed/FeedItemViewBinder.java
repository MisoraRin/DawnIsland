package com.yanrou.dawnisland.feed;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
