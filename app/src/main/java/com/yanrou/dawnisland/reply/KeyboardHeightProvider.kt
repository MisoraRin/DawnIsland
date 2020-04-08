package com.yanrou.dawnisland.reply

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.yanrou.dawnisland.R

class KeyboardHeightProvider(var context: Context?) : LifecycleObserver {
    private var alertDialog: AlertDialog? = null
    private var prerect: Rect? = null

    private val rect = Rect()
    var observer: KeyboardHeightObserver? = null

    @SuppressLint("ResourceType")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        alertDialog = AlertDialog.Builder(context!!, R.style.MyTransparent).create()
        val window: Window = alertDialog!!.window!!
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        //设置状态栏(StatusBar)颜色透明
        window.statusBarColor = Color.TRANSPARENT
        val layoutParams = window.attributes
        //核心代码是这个属性。
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        layoutParams.dimAmount = 0f
        window.attributes = layoutParams
        window.setWindowAnimations(R.anim.bottom_up_in)
        val view: View? = alertDialog!!.findViewById(android.R.id.content)
        view!!.viewTreeObserver.addOnGlobalLayoutListener {
            view.getWindowVisibleDisplayFrame(rect);
            var change = 0;
            if (prerect != null) {
                change = rect.bottom - prerect!!.bottom;
            }
            notifyKeyboardHeightChanged(change)
            prerect = Rect(rect)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        alertDialog!!.show()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        alertDialog!!.dismiss()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        alertDialog = null
        context = null
    }

    private fun notifyKeyboardHeightChanged(height: Int) {
        observer?.onKeyboardHeightChanged(height)
    }
}