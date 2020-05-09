package com.yanrou.dawnisland

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option: Int = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            decorView.systemUiVisibility = option
            statusBarColor = Color.TRANSPARENT
//            statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
        }
        val mainFragment = supportFragmentManager.findFragmentByTag("main_fragment")
        if (mainFragment == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, MainFragment.newInstance(), "main_fragment").commit()
        }
        resourceInitialization()
    }


    private fun resourceInitialization() {
        // default subscriptionId
        PreferenceManager.getDefaultSharedPreferences(baseContext).run {
            val mFeedsId = getString("subscriber_id", "666")
            if (mFeedsId == "666") {
                edit().putString("subscriber_id", UUID.randomUUID().toString()).apply()
            }
        }
    }
}