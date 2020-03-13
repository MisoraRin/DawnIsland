package com.yanrou.dawnisland;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.card.MaterialCardView;

public class CardViewFactory {
    private static CardViewFactory cardViewFactory;

    public static final String MAIN_TEXT_SIZE = "main_text_size";
    public static final String CARD_RADIUS = "card_radius";
    public static final String CARD_ELEVATION = "card_elevation";
    public static final String CARD_MARGIN_TOP = "card_margin_top";
    public static final String CARD_MARGIN_LEFT = "card_margin_left";
    public static final String CARD_MARGIN_RIGHT = "card_margin_right";
    public static final String HEAD_BAR_MARGIN_TOP = "head_bar_margin_top";
    public static final String CONTENT_MARGIN_TOP = "content_margin_top";
    public static final String CONTENT_MARGIN_LEFT = "content_margin_left";
    public static final String COTENT_MARGIN_RIGHT = "cotent_margin_right";
    public static final String CONTENT_MARGIN_BOTTOM = "content_margin_bottom";
    public static final String LETTER_SPACE = "letter_space";
    public static final String LINE_HEIGHT = "line_height";
    public static final String SEG_GAP = "seg_gap";
    public static final int MAIN_TEXT_MIN_SIZE = 10;


    private int DEFAULT_CARDVIEW_PADDING = 15;
    private int DEFAULT_CARDVIEW_MARGINSTART = 10;
    private int DEFAULT_CARDVIEW_MARGINEND = 10;
    private int DEFAULT_CARDVIEW_MARGINTOP = 16;
    private int DEFAULT_CARDVIEW_MARGINBOTTOM = 6;

    private MyCardView cardView;
    private ConstraintLayout constraintLayout;
    private TextView cookieView;
    private TextView timeView;
    private TextView forumAndRelpycount;
    private TextView contentView;
    private SpannableString exampleText;
    private ImageView imageContent;

    private DisplayMetrics displayMetrics;

    private final SharedPreferences sharedPreferences;

    private int mainTextSize;
    private int cardRadius;
    private int cardElevaion;
    private int cardMarginTop;
    private int cardMarginLeft;
    private int cardMarginRight;
    private int headBarMarginTop;
    private int contentMarginTop;
    private int contentMarginLeft;
    private int contentMarginRight;
    private int contentMarginBottom;
    private int lineHeight;
    private int letterSpace;
    private int segGap;

    public int getMainTextSize() {
        return mainTextSize;
    }

    public int getCardRadius() {
        return cardRadius;
    }

    public int getCardElevaion() {
        return cardElevaion;
    }

    public int getCardMarginTop() {
        return cardMarginTop;
    }

    public int getCardMarginLeft() {
        return cardMarginLeft;
    }

    public int getCardMarginRight() {
        return cardMarginRight;
    }

    public int getHeadBarMarginTop() {
        return headBarMarginTop;
    }

    public int getContentMarginTop() {
        return contentMarginTop;
    }

    public int getContentMarginLeft() {
        return contentMarginLeft;
    }

    public int getContentMarginRight() {
        return contentMarginRight;
    }

    public int getContentMarginBottom() {
        return contentMarginBottom;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public int getLetterSpace() {
        return letterSpace;
    }

    public int getSegGap() {
        return segGap;
    }

    private CardViewFactory(Context context) {

        displayMetrics = context.getResources().getDisplayMetrics();

        DEFAULT_CARDVIEW_PADDING = dip2px(DEFAULT_CARDVIEW_PADDING);
        DEFAULT_CARDVIEW_MARGINSTART = dip2px(DEFAULT_CARDVIEW_MARGINSTART);
        DEFAULT_CARDVIEW_MARGINEND = dip2px(DEFAULT_CARDVIEW_MARGINEND);
        DEFAULT_CARDVIEW_MARGINTOP = dip2px(DEFAULT_CARDVIEW_MARGINTOP);
        DEFAULT_CARDVIEW_MARGINBOTTOM = dip2px(DEFAULT_CARDVIEW_MARGINBOTTOM);

        /**
         * 获取存储
         */
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        readSetting();
    }

    public static CardViewFactory getInstance(Context context) {
        if (cardViewFactory != null) {
            cardViewFactory.readSetting();
            return cardViewFactory;
        }
        cardViewFactory = new CardViewFactory(context);
        cardViewFactory.readSetting();
        return cardViewFactory;
    }

    private void readSetting() {
        mainTextSize = sharedPreferences.getInt(MAIN_TEXT_SIZE, 15);
        cardRadius = sharedPreferences.getInt(CARD_RADIUS, dip2px(5));
        cardElevaion = sharedPreferences.getInt(CARD_ELEVATION, dip2px(2));
        cardMarginTop = sharedPreferences.getInt(CARD_MARGIN_TOP, DEFAULT_CARDVIEW_MARGINTOP);
        cardMarginLeft = sharedPreferences.getInt(CARD_MARGIN_LEFT, DEFAULT_CARDVIEW_MARGINSTART);
        cardMarginRight = sharedPreferences.getInt(CARD_MARGIN_RIGHT, DEFAULT_CARDVIEW_MARGINEND);
        headBarMarginTop = sharedPreferences.getInt(HEAD_BAR_MARGIN_TOP, DEFAULT_CARDVIEW_PADDING);
        contentMarginTop = sharedPreferences.getInt(CONTENT_MARGIN_TOP, dip2px(8));
        contentMarginLeft = sharedPreferences.getInt(CONTENT_MARGIN_LEFT, DEFAULT_CARDVIEW_PADDING);
        contentMarginRight = sharedPreferences.getInt(COTENT_MARGIN_RIGHT, DEFAULT_CARDVIEW_PADDING);
        contentMarginBottom = sharedPreferences.getInt(CONTENT_MARGIN_BOTTOM, DEFAULT_CARDVIEW_PADDING);
        letterSpace = sharedPreferences.getInt(LETTER_SPACE, 0);
        lineHeight = sharedPreferences.getInt(LINE_HEIGHT, 0);
        segGap = sharedPreferences.getInt(SEG_GAP, 0);
    }

    public MyCardView getSeriesCardView(Context context) {
        /**
         * 创建CardView
         */
        cardView = new MyCardView(context);
        cardView.setId(R.id.SeriesListCard);
        /**
         * 设置CardView layout属性
         */
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        marginLayoutParams.setMarginStart(cardMarginLeft);
        marginLayoutParams.setMarginEnd(cardMarginRight);
        marginLayoutParams.topMargin = cardMarginTop;
        cardView.setLayoutParams(marginLayoutParams);


        /**
         * 获取点击效果资源
         */
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
        int[] attribute = new int[]{android.R.attr.selectableItemBackground};
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);
        Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();

        /**
         * 设置点击效果
         */
        cardView.setForeground(drawable);
        cardView.setClickable(true);
        /**
         * 设置背景颜色
         */
        cardView.setCardBackgroundColor(Color.parseColor("#Ffffff"));
        cardView.setRadius(cardRadius);
        cardView.setElevation(cardElevaion);

        constraintLayout = new ConstraintLayout(context);
        constraintLayout.setId(R.id.SeriesListContraintLayout);
        constraintLayout.setPadding(contentMarginLeft, headBarMarginTop, contentMarginRight, contentMarginBottom);

        /**
         * cookie TextView
         */
        cookieView = new TextView(context);
        cookieView.setId(R.id.SeriesListCookie);
        cookieView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        ConstraintLayout.LayoutParams cookieLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cookieLayoutParams.topToTop = R.id.SeriesListContraintLayout;
        cookieLayoutParams.startToStart = R.id.SeriesListContraintLayout;

        cookieView.setLayoutParams(cookieLayoutParams);


        /**
         * time TextView
         */
        timeView = new TextView(context);
        timeView.setId(R.id.SeriesListTime);

        ConstraintLayout.LayoutParams timeLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeLayoutParams.startToEnd = R.id.SeriesListCookie;
        timeLayoutParams.setMarginStart(dip2px(8));
        timeLayoutParams.topToTop = R.id.SeriesListContraintLayout;
        timeView.setLayoutParams(timeLayoutParams);

        /**
         * forum TextView
         */
        forumAndRelpycount = new TextView(context);
        forumAndRelpycount.setId(R.id.SeriesListForum);
        ConstraintLayout.LayoutParams forumLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        forumLayoutParams.topToTop = R.id.SeriesListContraintLayout;
        forumLayoutParams.endToEnd = R.id.SeriesListContraintLayout;
        /***
         * xml中使用padding代替了margin，因为要绘制标签背景，这里暂时空着8，因为考虑使用Span进行绘制,所以size，color之类的属性都暂时不写
         * 省略的属性有padding、textColor、textSize、background
         * */
        forumAndRelpycount.setLayoutParams(forumLayoutParams);
        forumAndRelpycount.setTextSize(Dimension.SP, 12);

        /**
         * content TextView
         */
        contentView = new TextView(context);
        contentView.setId(R.id.SeriesListContent);

        ConstraintLayout.LayoutParams contentLayoutParam = new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentLayoutParam.topToBottom = R.id.SeriesListCookie;
        contentLayoutParam.topMargin = contentMarginTop;
        contentLayoutParam.endToStart = R.id.SeriesListImageView2;
        contentLayoutParam.setMarginEnd(dip2px(2));
        contentLayoutParam.startToStart = R.id.SeriesListContraintLayout;

        contentView.setLayoutParams(contentLayoutParam);
        contentView.setTextColor(Color.BLACK);
        contentView.setTextSize(mainTextSize);
        contentView.setMaxLines(10);

        float trueLetterSpace = letterSpace * 1.0f;
        trueLetterSpace /= 50;
        contentView.setLetterSpacing(trueLetterSpace);

        imageContent = new ImageView(context);
        imageContent.setId(R.id.SeriesListImageView2);

        ConstraintLayout.LayoutParams imageLayoutParam = new ConstraintLayout.LayoutParams(250, 250);

        imageLayoutParam.topToTop = R.id.SeriesListContent;
        imageLayoutParam.endToEnd = R.id.SeriesListContraintLayout;

        imageContent.setLayoutParams(imageLayoutParam);


        cardView.addView(constraintLayout);
        constraintLayout.addView(cookieView);
        constraintLayout.addView(timeView);
        constraintLayout.addView(forumAndRelpycount);
        constraintLayout.addView(contentView);
        constraintLayout.addView(imageContent);

        cardView.setConstraintLayout(constraintLayout);
        cardView.setCookieView(cookieView);
        cardView.setTimeView(timeView);
        cardView.setForumAndRelpycount(forumAndRelpycount);
        cardView.setContentView(contentView);
        cardView.setImageContent(imageContent);

        return cardView;
    }

    public int dip2px(float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, displayMetrics);
    }

    class MyCardView extends MaterialCardView {
        String id;
        String forum;
        private ConstraintLayout constraintLayout;
        private TextView cookieView;
        private TextView timeView;
        private TextView forumAndRelpycount;
        private TextView contentView;
        private ImageView imageContent;

        public MyCardView(Context context) {
            super(context);
        }


        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public void setConstraintLayout(ConstraintLayout constraintLayout) {
            this.constraintLayout = constraintLayout;
        }

        public TextView getCookieView() {
            return cookieView;
        }

        public void setCookieView(TextView cookieView) {
            this.cookieView = cookieView;
        }

        public TextView getTimeView() {
            return timeView;
        }

        public void setTimeView(TextView timeView) {
            this.timeView = timeView;
        }

        public TextView getForumAndRelpycount() {
            return forumAndRelpycount;
        }

        public void setForumAndRelpycount(TextView forumAndRelpycount) {
            this.forumAndRelpycount = forumAndRelpycount;
        }

        public TextView getContentView() {
            return contentView;
        }

        public void setContentView(TextView contentView) {
            this.contentView = contentView;
        }

        public ImageView getImageContent() {
            return imageContent;
        }

        public void setImageContent(ImageView imageContent) {
            this.imageContent = imageContent;
        }
    }
}
