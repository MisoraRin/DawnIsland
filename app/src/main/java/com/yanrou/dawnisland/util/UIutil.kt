package com.yanrou.dawnisland.util

import android.animation.ObjectAnimator
import android.view.View

fun View.startRotationAnim(start: Float, end: Float) {
    // 第二个参数"rotation"表明要执行旋转
    // 0f -> 360f，从旋转360度，也可以是负值，负值即为逆时针旋转，正值是顺时针旋转。
    val anim = ObjectAnimator.ofFloat(this, "rotation", start, end)
    // 动画的持续时间，执行多久？
    anim.duration = 500
    // 回调监听
    anim.addUpdateListener { animation ->
        val value = animation.animatedValue as Float
    }
    // 正式开始启动执行动画
    anim.start()
}