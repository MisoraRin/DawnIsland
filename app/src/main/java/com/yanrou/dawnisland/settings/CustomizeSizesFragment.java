package com.yanrou.dawnisland.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.card.MaterialCardView;
import com.yanrou.dawnisland.R;
import com.yanrou.dawnisland.span.SegmentSpacingSpan;

public class CustomizeSizesFragment extends PreferenceFragmentCompat{
  private static final String TAG="CustmoizeSizesFragment";
  private static final String MAIN_TEXT_SIZE = "main_text_size";
  private static final String CARD_RADIUS = "card_radius";
  private static final String CARD_ELEVATION = "card_elevation";
  private static final String CARD_MARGIN_TOP = "card_margin_top";
  private static final String CARD_MARGIN_LEFT = "card_margin_left";
  private static final String CARD_MARGIN_RIGHT = "card_margin_right";
  private static final String HEAD_BAR_MARGIN_TOP = "head_bar_margin_top";
  private static final String CONTENT_MARGIN_TOP = "content_margin_top";
  private static final String CONTENT_MARGIN_LEFT = "content_margin_left";
  private static final String CONTENT_MARGIN_RIGHT = "content_margin_right";
  private static final String CONTENT_MARGIN_BOTTOM = "content_margin_bottom";
  private static final String LETTER_SPACE = "letter_space";
  private static final String LINE_HEIGHT = "line_height";
  private static final String SEGMENT_GAP = "seg_gap";
  
  private View sampleCard;

  private SharedPreferences.OnSharedPreferenceChangeListener listener =
      new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
          Log.i("Shared preference", "Preference " + key + " was updated to: " + String.valueOf(sharedPreferences.getInt(key, -1)));

          Integer progress = sharedPreferences.getInt(key, -1);
          if (progress < 0){
            Log.e(TAG, "Error on retrieving/saving SharedPreference with key "+key );
            return;
          }
//          LinearLayout li = getActivity().findViewById(R.id.demo_card_container);
//          CardViewFactory.MyCardView cardView = (CardViewFactory.MyCardView) li.getChildAt(0);
//          Log.d(TAG, "shred pref "+ cardView.getCookieView().getText());
          updateDemoCard(key, progress);
        }
      };

  @Override
  public void onResume() {
    super.onResume();
    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
  }

  @Override
  public void onPause() {
    super.onPause();
    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
  }



  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.sizes_preferences, rootKey);

  }

  private void updateDemoCard(String key, Integer progress){
//    LinearLayout li = getActivity().findViewById(R.id.demo_card_container);
//    CardViewFactory.MyCardView cardView = (CardViewFactory.MyCardView) li.getChildAt(0);
    MaterialCardView cardView = getActivity().findViewById(R.id.sample_card);
    ConstraintLayout SeriesConstraintLayout = cardView.findViewById(R.id.SeriesListContraintLayout);
    ViewGroup.MarginLayoutParams CardLayoutParams = (ViewGroup.MarginLayoutParams)
        cardView.findViewById(R.id.SeriesListContraintLayout).getLayoutParams();
    TextView cookie = cardView.findViewById(R.id.SeriesListCookie);
    ConstraintLayout.LayoutParams cookieLayoutParams = (ConstraintLayout.LayoutParams) cookie.getLayoutParams();
    View contentView = cardView.findViewById(R.id.SeriesListContent);
    CharSequence charSequence = ((TextView) contentView).getText();

      switch (key) {
        case MAIN_TEXT_SIZE:
          ((TextView) contentView).setTextSize(progress);
          break;
        case CARD_RADIUS:
          cardView.setRadius(progress);
          break;
        case CARD_ELEVATION:
          cardView.setElevation(progress);
          break;
        case CARD_MARGIN_TOP:
          CardLayoutParams.topMargin = progress;
          cardView.setLayoutParams(CardLayoutParams);
          break;
        case CARD_MARGIN_LEFT:
          CardLayoutParams.setMarginStart(progress);
          cardView.setLayoutParams(CardLayoutParams);
          break;
        case CARD_MARGIN_RIGHT:
          CardLayoutParams.setMarginEnd(progress);
          cardView.setLayoutParams(CardLayoutParams);
          break;
        case HEAD_BAR_MARGIN_TOP:
          SeriesConstraintLayout.setPadding(SeriesConstraintLayout.getPaddingLeft(), progress, SeriesConstraintLayout.getPaddingRight(), SeriesConstraintLayout.getPaddingBottom());
          break;
        case CONTENT_MARGIN_TOP:
          ConstraintLayout.LayoutParams contentLayoutParams = (ConstraintLayout.LayoutParams) contentView.getLayoutParams();
          contentLayoutParams.topMargin = progress;
          contentView.setLayoutParams(contentLayoutParams);
          break;
        case CONTENT_MARGIN_LEFT:
          SeriesConstraintLayout.setPadding(progress, SeriesConstraintLayout.getPaddingTop(), SeriesConstraintLayout.getPaddingRight(), SeriesConstraintLayout.getPaddingBottom());

          break;
        case CONTENT_MARGIN_RIGHT:
          SeriesConstraintLayout.setPadding(SeriesConstraintLayout.getPaddingLeft(), SeriesConstraintLayout.getPaddingTop(), progress, SeriesConstraintLayout.getPaddingBottom());

          break;
        case CONTENT_MARGIN_BOTTOM: // ??check
          SeriesConstraintLayout.setPadding(SeriesConstraintLayout.getPaddingLeft(), SeriesConstraintLayout.getPaddingTop(), SeriesConstraintLayout.getPaddingRight(), progress);

          break;
        case LETTER_SPACE: // ??check
          float i = progress * 1.0f;
          i /= 50;
          ((TextView) contentView).setLetterSpacing(i);
          break;
        case LINE_HEIGHT: // ??check
          if (charSequence instanceof SpannableString) {
            SegmentSpacingSpan[] segmentSpacingSpans = ((SpannableString) charSequence).getSpans(0, charSequence.length(), SegmentSpacingSpan.class);
            segmentSpacingSpans[0].setmHeight(progress);
          }
          contentView.requestLayout();
          break;
        case SEGMENT_GAP: // ??check
          if (charSequence instanceof SpannableString) {
            SegmentSpacingSpan[] segmentSpacingSpans = ((SpannableString) charSequence).getSpans(0, charSequence.length(), SegmentSpacingSpan.class);
            segmentSpacingSpans[0].setSegmentGap(progress);
          }
          contentView.requestLayout();
          break;
      }
  }
}
