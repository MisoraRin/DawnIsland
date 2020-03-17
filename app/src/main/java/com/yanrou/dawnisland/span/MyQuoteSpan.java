package com.yanrou.dawnisland.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.Px;

public class MyQuoteSpan implements LeadingMarginSpan {

    @ColorInt
    private final int mColor;
    @Px
    private final int mStripeWidth;
    @Px
    private final int mGapWidth;


    public MyQuoteSpan(int mColor, int mStripeWidth, int mGapWidth) {
        this.mColor = mColor;
        this.mStripeWidth = mStripeWidth;
        this.mGapWidth = mGapWidth;

    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mGapWidth + mStripeWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        Paint.Style style = p.getStyle();
        int color = p.getColor();

        p.setStyle(Paint.Style.FILL);
        p.setColor(mColor);

        c.drawRect(x, top, x + dir * mStripeWidth, bottom, p);

        p.setStyle(style);
        p.setColor(color);
    }
}
