package com.yanrou.dawnisland.settings;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.yanrou.dawnisland.R;
import com.yanrou.dawnisland.serieslist.CardViewFactory;
import com.yanrou.dawnisland.span.RoundBackgroundColorSpan;
import com.yanrou.dawnisland.span.SegmentSpacingSpan;

public class SizesCustomizationActivity extends AppCompatActivity {

  private int DEMO_CARDVIEW_MARGINBOTTOM = 15;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sizes_customization);

    addDemoCardToScreen();
  }

  /** create Demo card in Activity, however changed by children fragment
   *
   */
  private void addDemoCardToScreen(){
    CardViewFactory viewFactory = CardViewFactory.getInstance(this);
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    CardViewFactory.MyCardView cardView = viewFactory.getSeriesCardView(this);
    cardView.getCookieView().setText("cookie");
    cardView.getTimeView().setText("2小时前");

    SpannableString spannableString = new SpannableString("欢乐恶搞" + " · " + 12);
    spannableString.setSpan(new RoundBackgroundColorSpan(Color.parseColor("#12DBD1"), Color.parseColor("#FFFFFF")), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    cardView.getForumAndRelpycount().setText(spannableString, TextView.BufferType.SPANNABLE);

    SpannableString exampleText = new SpannableString("北分则易红在保，干品政两报米术，料询容保美。\n该府术没也例空解，法露作长心录。 六深事会部青目传向市始，西法医很呀体近数片。\n活林变须阶候业精六只团起已市，下头却广局正支。");
    exampleText.setSpan(new SegmentSpacingSpan(sharedPreferences.getInt(CardViewFactory.LINE_HEIGHT, 0), sharedPreferences.getInt(CardViewFactory.SEG_GAP, 0)), 0, exampleText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    cardView.getContentView().setText(exampleText, TextView.BufferType.SPANNABLE);
    cardView.getImageContent().setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));

    ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    marginLayoutParams.setMarginStart(viewFactory.getCardMarginLeft());
    marginLayoutParams.setMarginEnd(viewFactory.getCardMarginRight());
    marginLayoutParams.topMargin = viewFactory.getCardMarginTop();
    marginLayoutParams.bottomMargin = DEMO_CARDVIEW_MARGINBOTTOM;
    cardView.setLayoutParams(marginLayoutParams);

    LinearLayout li = findViewById(R.id.demo_card_container);
    li.addView(cardView);
  }
}
