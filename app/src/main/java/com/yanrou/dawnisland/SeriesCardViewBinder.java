package com.yanrou.dawnisland;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drakeet.multitype.ViewDelegate;

import org.jetbrains.annotations.NotNull;


public class SeriesCardViewBinder extends ViewDelegate<SeriesCardView, CardViewFactory.MyCardView> {
    public SeriesCardViewBinder(Context activityContext) {
        this.activityContext = activityContext;
    }

    Context activityContext;

    private static final String TAG = "SeriesCardViewBinder";

    @Override
    public void onBindView(@NotNull CardViewFactory.MyCardView myCardView, SeriesCardView seriesCardView) {

        myCardView.id = seriesCardView.id;
        myCardView.forum = seriesCardView.forum;
        myCardView.getCookieView().setText(seriesCardView.cookie);
        myCardView.getTimeView().setText(seriesCardView.time);
        myCardView.getForumAndRelpycount().setText(seriesCardView.forumAndReply, TextView.BufferType.SPANNABLE);
        myCardView.getForumAndRelpycount().requestLayout();
        myCardView.getForumAndRelpycount().invalidate();
        myCardView.getContentView().setText(seriesCardView.content);
        if (seriesCardView.haveImage) {
            myCardView.getImageContent().setVisibility(View.VISIBLE);
            Glide.with(myCardView.getContext())
                    .load("https://nmbimg.fastmirror.org/thumb/" + seriesCardView.imageUri)
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
