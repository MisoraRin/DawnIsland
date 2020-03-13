package com.yanrou.dawnisland;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import androidx.annotation.NonNull;

public class MyLineBackgroundSpan implements LineBackgroundSpan {

    private final int mColor;

    public MyLineBackgroundSpan(int mColor) {
        this.mColor = mColor;
    }

    @Override
    public void drawBackground(@NonNull Canvas canvas, @NonNull Paint paint, int left, int right, int top, int baseline, int bottom, @NonNull CharSequence text, int start, int end, int lineNumber) {
        final int originColor = paint.getColor();
        paint.setColor(mColor);
        canvas.drawRect(left, top, right, bottom, paint);
        paint.setColor(originColor);
    }
}
