package com.yanrou.dawnisland;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;
import android.util.Log;

public class SegmentSpacingSpan implements LineHeightSpan {
    private static final String TAG = "SegmentSpacingSpan";

    private int mHeight;

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
        seg = false;
        line = false;
    }

    public void setSegmentGap(int segmentGap) {
        this.segmentGap = segmentGap;
        seg = false;
        line = false;
    }

    private int segmentGap;

    public SegmentSpacingSpan(int mHeight, int segmentGap) {
        this.mHeight = mHeight;
        this.segmentGap = segmentGap;
    }

    private int lineH = 0;
    private int segH = 0;
    boolean seg = false, line = false;

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
        final int originHeight = fm.descent - fm.ascent;
        // If original height is not positive, do nothing.
        if (originHeight <= 0) {
            return;
        }
        if (text.subSequence(start, end).toString().contains("\n")) {
            if (seg) {
                fm.descent = segH;
                Log.d(TAG, "chooseHeight: 换行段非第一次");
            } else {
                fm.descent += segmentGap;
                segH = fm.descent;
                seg = true;
                Log.d(TAG, "chooseHeight: 换行段第一次");
            }
            Log.d(TAG, "chooseHeight: 这是换行段" + fm.descent);
        }
        if (!text.subSequence(start, end).toString().contains("\n")) {
            if (line) {
                fm.descent = lineH;

            } else {
                fm.descent += mHeight;
                lineH = fm.descent;
                line = true;
            }

            Log.d(TAG, "chooseHeight: 这是非换行段" + fm.descent);
        }
    }
}
