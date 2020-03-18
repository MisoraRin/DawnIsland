package com.yanrou.dawnisland.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.yanrou.dawnisland.R;
import com.yanrou.dawnisland.serieslist.CardViewFactory;
import com.yanrou.dawnisland.span.RoundBackgroundColorSpan;
import com.yanrou.dawnisland.span.SegmentSpacingSpan;

public class SettingsActivity extends AppCompatActivity {
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

    private int padding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        viewFactory = CardViewFactory.getInstance(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        loadSettings();

    }

    void loadSettings(){
        loadSubscriberID();
        loadTimeFormat();
        loadCardViewSettings();
    }

    private void loadSubscriberID(){}

    private void loadTimeFormat() {
        Spinner timeFormatSetter = findViewById(R.id.time_format_setter);
        String[] items = new String[]{"Time FormatA", "Time FormatB"};
        StringArrayAdapter adapter = new StringArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items);
        timeFormatSetter.setAdapter(adapter);
        timeFormatSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                String format = adapter.getItem(position);
                // Here you can do the action you want to...
                Toast.makeText(getApplicationContext(), "Selected time format:"+format, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "TODO: add time format conversion!!");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
    }


    void loadCardViewSettings(){
        // sample card
        cardView = viewFactory.getSeriesCardView(this);
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

        // customization seekbars
        padding = viewFactory.dip2px(8);
        scrollView = findViewById(R.id.settings);

        linearLayout = (LinearLayout) scrollView.getChildAt(0);
        linearLayout.addView(cardView);

        // load saved settings
        linearLayout.addView(generateSeekBar(RADIUS, viewFactory.getCardRadius(), "圆角"));
        linearLayout.addView(generateSeekBar(ELEVATION,viewFactory.getCardElevaion(), "阴影"));
        linearLayout.addView(generateSeekBar(MAIN_TEXT_SIZE, viewFactory.getMainTextSize() - 10,"主字号", 10));
        linearLayout.addView(generateSeekBar(LINE_SPACE_EXTRA, viewFactory.getLineHeight(),"行间距", 20));
        linearLayout.addView(generateSeekBar(SEGMENT_GAP, viewFactory.getSegGap(), "段间距", 25));
        linearLayout.addView(generateSeekBar(TEXT_SCALEX, viewFactory.getLetterSpace(), "字间距", 17));
        linearLayout.addView(generateSeekBar(CARD_MARGIN_TOP, viewFactory.getCardMarginTop(),"卡片间距"));
        linearLayout.addView(generateSeekBar(CARD_MARGIN_LEFT, viewFactory.getCardMarginLeft(), "卡片左边距", 50));
        linearLayout.addView(generateSeekBar(CARD_MARGIN_RIGHT, viewFactory.getCardMarginRight(), "卡片右边距", 50));
        linearLayout.addView(generateSeekBar(HEAD_BAR_MARGIN_TOP, viewFactory.getHeadBarMarginTop(),"头部上边距", 60));
        linearLayout.addView(generateSeekBar(CONTENT_MARGIN_TOP, viewFactory.getContentMarginTop(),"内容上边距", 50));
        linearLayout.addView(generateSeekBar(CONTENT_MARGIN_LEFT, viewFactory.getContentMarginLeft(),"内容左边距", 60));
        linearLayout.addView(generateSeekBar(CONTENT_MARGIN_RIGHT, viewFactory.getContentMarginRight(),"内容右边距", 60));
        linearLayout.addView(generateSeekBar(CONTENT_MARGIN_BOTTOM, viewFactory.getContentMarginBottom(),"内容下边距", 70));


    }

    private LinearLayout generateSeekBar(int id, int value, String itemName) {
        return generateSeekBar(id, value, itemName, 100);
    }

    private LinearLayout generateSeekBar(int id, int value, String itemName, int max) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setPadding(padding, padding, padding, 0);

        TextView textView = new TextView(this);
        textView.setText(itemName);

        TextView number = new TextView(this);
        number.setId(id + anTextViewInt);

        SettingsSeekBar seekBar = new SettingsSeekBar(this);
        seekBar.setId(id);
        seekBar.setMax(max);
        seekBar.setProgress(value);
        seekBar.setOnSeekBarChangeListener(seekBar);

        linearLayout.addView(textView);
        linearLayout.addView(number);
        linearLayout.addView(seekBar, layoutParams);
        return linearLayout;
    }


    class SettingsSeekBar extends androidx.appcompat.widget.AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener{

        public SettingsSeekBar(Context context) {
            super(context);
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

    class StringArrayAdapter extends ArrayAdapter<String>{
        private Context context;
        private String[] values;

        public StringArrayAdapter(Context context, int textViewResourceId,
                                 String[] values){
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public int getCount(){
            return values.length;
        }

        @Override
        public String getItem(int position){
            return values[position];
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getView(position, convertView, parent);
            label.setText(values[position]);
            return label;
        }


        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setText(values[position]);
            return label;
        }

    }
}
