package com.yanrou.dawnisland

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import timber.log.Timber
import java.lang.Math.abs

class DawnViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var swipeLastX: Float = 0f
    private var swipeStartX: Float = 0f
    private var swipeCancel = false

    private var xDistance: Float = 0f
    private var yDistance: Float = 0f
    private var xLast: Float = 0f
    private var yLast: Float = 0f
    private val touchSlop: Int = android.view.ViewConfiguration.get(context).scaledTouchSlop
    private val swipeSlop: Int = 48
    private var isTouching = false

    var drawerListener: (() -> Unit)? = null

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var handled = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                swipeStartX = event.x
                swipeLastX = 0f
                swipeCancel = false

                yDistance = 0f
                xDistance = yDistance
                xLast = event.x
                yLast = event.y
                return super.onInterceptTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                val curX = event.x
                val curY = event.y
                xDistance = abs(curX - xLast)
                yDistance = abs(curY - yLast)
                xLast = curX
                yLast = curY
                handled = abs(xDistance) >= touchSlop && yDistance * 4 < xDistance * 3
            }
        }

        // Fixed: IllegalArgumentException: pointerIndex out of range
        return try {
            handled || super.onInterceptTouchEvent(event)
        } catch (e: Throwable) {
            e.printStackTrace()

            return handled
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (currentItem == 0) {
            val action = ev.action
            val x = ev.x
            when (action) {
                MotionEvent.ACTION_DOWN -> isTouching = true
                MotionEvent.ACTION_MOVE -> {
                    isTouching = true
                    Timber.d("x: $x, lastX: $swipeLastX")
                    // 如果手指往回收，则取消开启侧栏
                    if (x < swipeLastX) {
                        swipeCancel = true
                    }
                    swipeLastX = x
                    // writer.drawerDelegate.drawer.setDrawerToOffset(x - swipeStartX)
                    // writer.drawerDelegate.drawer.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    if (!swipeCancel && (x - swipeStartX) > swipeSlop) {
                        drawerListener?.let { it() }
                    }
                    isTouching = false
                }
                MotionEvent.ACTION_CANCEL -> isTouching = false
            }
        }
        return super.onTouchEvent(ev)
    }

}