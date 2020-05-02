package com.yanrou.dawnisland.mrecyclerview

import android.graphics.Rect

interface IPinnedHeaderDecoration {
    val pinnedHeaderRect: Rect?
    val pinnedHeaderPosition: Int
}