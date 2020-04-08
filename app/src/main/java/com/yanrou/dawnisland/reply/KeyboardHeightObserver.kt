package com.yanrou.dawnisland.reply

interface KeyboardHeightObserver {
    /**
     * @param height 0未改变，小于0键盘弹出，大于0键盘收起
     */
    fun onKeyboardHeightChanged(height: Int)
}