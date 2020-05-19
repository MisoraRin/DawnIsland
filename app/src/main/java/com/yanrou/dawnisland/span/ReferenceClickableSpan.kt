package com.yanrou.dawnisland.span

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class ReferenceClickableSpan(val seriesId: String, val listener: (seriesId: String) -> Unit) : ClickableSpan() {
    private val end = 0
    override fun onClick(widget: View) = listener(seriesId)
    override fun updateDrawState(ds: TextPaint) {}
}