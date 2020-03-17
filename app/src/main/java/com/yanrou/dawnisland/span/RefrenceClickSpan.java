package com.yanrou.dawnisland.span;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yanrou.dawnisland.Reference;
import com.yanrou.dawnisland.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RefrenceClickSpan extends ClickableSpan {
    private int end;
    private String seriesId;

    @Override
    public void onClick(@NonNull View widget) {
        if (widget instanceof TextView) {
            CharSequence charSequence = ((TextView) widget).getText();

            if (charSequence instanceof Spannable) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
                HttpUtil.sendOkHttpRequest("https://adnmb2.com/Home/Forum/ref?id=" + seriesId, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        //TODO 加载失败处理
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Reference reference = new Reference();

                        MyQuoteSpan myQuoteSpan = new MyQuoteSpan(Color.parseColor("#14A8A8"), 10, 20);

                        SpannableString content = new SpannableString("\n" + Html.fromHtml(reference.content));
                        spannableStringBuilder.insert(end, content);
                        spannableStringBuilder.setSpan(myQuoteSpan, end, end + content.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.setSpan(new MyLineBackgroundSpan(Color.parseColor("#EBEBEB")), end + 1, end + content.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                });
            }
        }

    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        //super.updateDrawState(ds);
    }
}
