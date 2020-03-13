package com.yanrou.dawnisland;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

public class CustomizeSizeActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "CustomizeSizeActivity";
    private final int anTextViewInt = 1000;

    private LinearLayout linearLayout;
    private CardViewFactory.MyCardView cardView;
    private CardViewFactory viewFactory;

    private final int MAIN_TEXT_SIZE = 0;
    private final int RADIUS = 1;
    private final int ELEVATION = 2;
    private final int CARD_MARGIN_TOP = 3;
    private final int CARD_MARGIN_LEFT = 4;
    private final int CARD_MARGIN_RIGHT = 5;
    private final int CONTENT_MARGIN_TOP = 6;
    private final int CONTENT_MARGIN_LEFT = 7;
    private final int CONTENT_MARGIN_RIGHT = 8;
    private final int CONTENT_MARGIN_BOTTOM = 9;
    private final int HEAD_BAR_MARGIN_TOP = 10;
    private final int TEXT_SCALEX = 11;
    private final int LINE_SPACE_EXTRA = 12;
    private final int SEGMENT_GAP = 13;
    private ScrollView scrollView;
    private SharedPreferences sharedPreferences;
    private ConstraintLayout SeriesConstraintLayout;
    private CharSequence charSequence;


    void initView() {
        int pad = viewFactory.dip2px(12);
        cardView = viewFactory.getSeriesCardView(this);
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(cardView);

        linearLayout.addView(generateSeekBar(RADIUS, "圆角"));
        linearLayout.addView(generateSeekBar(ELEVATION, "阴影"));
        linearLayout.addView(generateSeekBar(MAIN_TEXT_SIZE, "主字号", 10));
        linearLayout.addView(generateSeekBar(LINE_SPACE_EXTRA, "行间距", 20));
        linearLayout.addView(generateSeekBar(SEGMENT_GAP, "段间距", 25));
        linearLayout.addView(generateSeekBar(TEXT_SCALEX, "字间距", 17));
        linearLayout.addView(generateSeekBar(CARD_MARGIN_TOP, "卡片间距"));
        linearLayout.addView(generateSeekBar(CARD_MARGIN_LEFT, "卡片左边距", 50));
        linearLayout.addView(generateSeekBar(CARD_MARGIN_RIGHT, "卡片右边距", 50));
        linearLayout.addView(generateSeekBar(HEAD_BAR_MARGIN_TOP, "头部上边距", 60));
        linearLayout.addView(generateSeekBar(CONTENT_MARGIN_TOP, "内容上边距", 50));
        linearLayout.addView(generateSeekBar(CONTENT_MARGIN_LEFT, "内容左边距", 60));
        linearLayout.addView(generateSeekBar(CONTENT_MARGIN_RIGHT, "内容右边距", 60));
        linearLayout.addView(generateSeekBar(CONTENT_MARGIN_BOTTOM, "内容下边距", 70));
        scrollView = new ScrollView(this);
        scrollView.addView(linearLayout);

    }

    private LinearLayout generateSeekBar(int id, String itemName) {
        return generateSeekBar(id, itemName, 100);
    }

    private LinearLayout generateSeekBar(int id, String itemName, int max) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int pad = viewFactory.dip2px(8);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setPadding(pad, pad, pad, 0);

        TextView textView = new TextView(this);
        textView.setText(itemName);

        TextView number = new TextView(this);
        number.setId(id + anTextViewInt);

        SeekBar seekBar = new SeekBar(this);
        seekBar.setId(id);
        seekBar.setMax(max);
        seekBar.setOnSeekBarChangeListener(this);

        linearLayout.addView(textView);
        linearLayout.addView(number);
        linearLayout.addView(seekBar, layoutParams);
        return linearLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewFactory = CardViewFactory.getInstance(this);
        initView();
        setContentView(scrollView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SeriesConstraintLayout = cardView.findViewById(R.id.SeriesListContraintLayout);
        cardView.getCookieView().setText("cookie");
        cardView.getTimeView().setText("2小时前");

        SpannableString spannableString = new SpannableString("欢乐恶搞" + " · " + 12);
        spannableString.setSpan(new RoundBackgroundColorSpan(Color.parseColor("#12DBD1"), Color.parseColor("#FFFFFF")), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        cardView.getForumAndRelpycount().setText(spannableString, TextView.BufferType.SPANNABLE);

        SpannableString exampleText = new SpannableString("北分则易红在保，干品政两报米术，料询容保美。\n该府术没也例空解，法露作长心录。 六深事会部青目传向市始，西法医很呀体近数片。\n活林变须阶候业精六只团起已市，下头却广局正支。");
        exampleText.setSpan(new SegmentSpacingSpan(sharedPreferences.getInt(CardViewFactory.LINE_HEIGHT, 0), sharedPreferences.getInt(CardViewFactory.SEG_GAP, 0)), 0, exampleText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        cardView.getContentView().setText(exampleText, TextView.BufferType.SPANNABLE);
        cardView.getImageContent().setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));

        ((SeekBar) linearLayout.findViewById(RADIUS)).setProgress(viewFactory.getCardRadius());
        ((SeekBar) linearLayout.findViewById(ELEVATION)).setProgress(viewFactory.getCardElevaion());
        ((SeekBar) linearLayout.findViewById(MAIN_TEXT_SIZE)).setProgress(viewFactory.getMainTextSize() - 10);
        ((SeekBar) linearLayout.findViewById(LINE_SPACE_EXTRA)).setProgress(viewFactory.getLineHeight());
        ((SeekBar) linearLayout.findViewById(SEGMENT_GAP)).setProgress(viewFactory.getSegGap());
        ((SeekBar) linearLayout.findViewById(TEXT_SCALEX)).setProgress(viewFactory.getLetterSpace());
        ((SeekBar) linearLayout.findViewById(CARD_MARGIN_TOP)).setProgress(viewFactory.getCardMarginTop());
        ((SeekBar) linearLayout.findViewById(CARD_MARGIN_LEFT)).setProgress(viewFactory.getCardMarginLeft());
        ((SeekBar) linearLayout.findViewById(CARD_MARGIN_RIGHT)).setProgress(viewFactory.getCardMarginRight());
        ((SeekBar) linearLayout.findViewById(HEAD_BAR_MARGIN_TOP)).setProgress(viewFactory.getHeadBarMarginTop());
        ((SeekBar) linearLayout.findViewById(CONTENT_MARGIN_TOP)).setProgress(viewFactory.getContentMarginTop());
        ((SeekBar) linearLayout.findViewById(CONTENT_MARGIN_LEFT)).setProgress(viewFactory.getContentMarginLeft());
        ((SeekBar) linearLayout.findViewById(CONTENT_MARGIN_RIGHT)).setProgress(viewFactory.getContentMarginRight());
        ((SeekBar) linearLayout.findViewById(CONTENT_MARGIN_BOTTOM)).setProgress(viewFactory.getContentMarginBottom());


    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        ViewGroup.MarginLayoutParams CardLayoutParams = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        TextView cookie = cardView.findViewById(R.id.SeriesListCookie);
        ConstraintLayout.LayoutParams cookieLayoutParams = (ConstraintLayout.LayoutParams) cookie.getLayoutParams();
        final View contentView = cardView.findViewById(R.id.SeriesListContent);
        switch (seekBar.getId()) {
            case MAIN_TEXT_SIZE:
                progress += CardViewFactory.MAIN_TEXT_MIN_SIZE;

                ((TextView) contentView).setTextSize(progress);
                sharedPreferences.edit().putInt(CardViewFactory.MAIN_TEXT_SIZE, progress).apply();
                break;
            case RADIUS:
                cardView.setRadius(progress);
                sharedPreferences.edit().putInt(CardViewFactory.CARD_RADIUS, progress).apply();
                break;
            case ELEVATION:
                cardView.setElevation(progress);
                sharedPreferences.edit().putInt(CardViewFactory.CARD_ELEVATION, progress).apply();
                break;
            case CARD_MARGIN_TOP:
                CardLayoutParams.topMargin = progress;
                cardView.setLayoutParams(CardLayoutParams);
                sharedPreferences.edit().putInt(CardViewFactory.CARD_MARGIN_TOP, progress).apply();
                break;
            case CARD_MARGIN_LEFT:
                CardLayoutParams.setMarginStart(progress);
                cardView.setLayoutParams(CardLayoutParams);
                sharedPreferences.edit().putInt(CardViewFactory.CARD_MARGIN_LEFT, progress).apply();
                break;
            case CARD_MARGIN_RIGHT:
                CardLayoutParams.setMarginEnd(progress);
                cardView.setLayoutParams(CardLayoutParams);
                sharedPreferences.edit().putInt(CardViewFactory.CARD_MARGIN_RIGHT, progress).apply();
                break;
            case HEAD_BAR_MARGIN_TOP:
                SeriesConstraintLayout.setPadding(SeriesConstraintLayout.getPaddingLeft(), progress, SeriesConstraintLayout.getPaddingRight(), SeriesConstraintLayout.getPaddingBottom());
                sharedPreferences.edit().putInt(CardViewFactory.HEAD_BAR_MARGIN_TOP, progress).apply();
                break;
            case CONTENT_MARGIN_TOP:
                ConstraintLayout.LayoutParams contentLayoutParams = (ConstraintLayout.LayoutParams) contentView.getLayoutParams();
                contentLayoutParams.topMargin = progress;
                contentView.setLayoutParams(contentLayoutParams);
                sharedPreferences.edit().putInt(CardViewFactory.CONTENT_MARGIN_TOP, progress).apply();
                break;
            case CONTENT_MARGIN_LEFT:
                SeriesConstraintLayout.setPadding(progress, SeriesConstraintLayout.getPaddingTop(), SeriesConstraintLayout.getPaddingRight(), SeriesConstraintLayout.getPaddingBottom());
                sharedPreferences.edit().putInt(CardViewFactory.CONTENT_MARGIN_LEFT, progress).apply();
                break;
            case CONTENT_MARGIN_RIGHT:
                SeriesConstraintLayout.setPadding(SeriesConstraintLayout.getPaddingLeft(), SeriesConstraintLayout.getPaddingTop(), progress, SeriesConstraintLayout.getPaddingBottom());
                sharedPreferences.edit().putInt(CardViewFactory.COTENT_MARGIN_RIGHT, progress).apply();
                break;
            case CONTENT_MARGIN_BOTTOM:
                SeriesConstraintLayout.setPadding(SeriesConstraintLayout.getPaddingLeft(), SeriesConstraintLayout.getPaddingTop(), SeriesConstraintLayout.getPaddingRight(), progress);
                sharedPreferences.edit().putInt(CardViewFactory.CONTENT_MARGIN_BOTTOM, progress).apply();
                break;
            case TEXT_SCALEX:
                float i = progress * 1.0f;
                i /= 50;
                ((TextView) contentView).setLetterSpacing(i);
                sharedPreferences.edit().putInt(CardViewFactory.LETTER_SPACE, progress).apply();
                break;
            case LINE_SPACE_EXTRA:
                charSequence = ((TextView) contentView).getText();
                if (charSequence instanceof SpannableString) {
                    SegmentSpacingSpan[] segmentSpacingSpans = ((SpannableString) charSequence).getSpans(0, charSequence.length(), SegmentSpacingSpan.class);
                    segmentSpacingSpans[0].setmHeight(progress);
                }
                contentView.requestLayout();
                sharedPreferences.edit().putInt(CardViewFactory.LINE_HEIGHT, progress).apply();
                break;
            case SEGMENT_GAP:
                charSequence = ((TextView) contentView).getText();
                if (charSequence instanceof SpannableString) {
                    SegmentSpacingSpan[] segmentSpacingSpans = ((SpannableString) charSequence).getSpans(0, charSequence.length(), SegmentSpacingSpan.class);
                    segmentSpacingSpans[0].setSegmentGap(progress);
                }
                contentView.requestLayout();
                sharedPreferences.edit().putInt(CardViewFactory.SEG_GAP, progress).apply();
                break;

        }
        TextView textView = linearLayout.findViewById(seekBar.getId() + anTextViewInt);
        textView.setText(String.valueOf(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
