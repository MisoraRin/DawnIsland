package com.yanrou.dawnisland.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun copyToClipboard(context: Context, content: CharSequence?) {
    val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(null, content)) //参数一：标签，可为空，参数二：要复制到剪贴板的文本
}