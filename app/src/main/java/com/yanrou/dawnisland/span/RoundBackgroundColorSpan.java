package com.yanrou.dawnisland.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.style.ReplacementSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoundBackgroundColorSpan extends ReplacementSpan {
    private static final String TAG = "RoundBackgroundColorSpa";

    private int bgColor;
    private int textColor;

    public RoundBackgroundColorSpan(int bgColor, int textColor) {
        super();
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    int radiu = 20;
    int size;
    int top, bottom;
    boolean first = true;
    int height = 2;

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        if (first) {
            Paint.FontMetricsInt tempFM = paint.getFontMetricsInt();
            int height = tempFM.bottom - tempFM.top;
            height /= 6;
            top = tempFM.top - height;
            bottom = tempFM.bottom + height;
            radiu = (bottom - top) / 2;
            first = false;
        }
        if (fm != null && fm.top != 0) {
            Log.d(TAG, "getSize: " + fm);
            fm.top = top;
            fm.bottom = bottom;
            Log.d(TAG, "getSize: " + fm);
            Log.d(TAG, "getSize: returnSize" + size);

        }
        size = ((int) paint.measureText(text, start, end) + radiu * 2);
        return size;

    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color1 = paint.getColor();
        int alpht = paint.getAlpha();
        Shader defaultShader = paint.getShader();
        paint.setAlpha(255);

        LinearGradient linearGradient = new LinearGradient(
                0, top, x + ((int) paint.measureText(text, start, end)) + radiu * 2, bottom,
                Color.parseColor("#2195da"),
                Color.parseColor("#3ae4cd"),
                Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);

        canvas.drawRoundRect(new RectF(0, top, x + ((int) paint.measureText(text, start, end) + radiu * 2), bottom), radiu, radiu, paint);

        paint.setShader(defaultShader);

        paint.setColor(this.textColor);
        canvas.drawText(text, start, end, x + radiu, y, paint);

        paint.setColor(color1);
        paint.setAlpha(alpht);
    }
}
