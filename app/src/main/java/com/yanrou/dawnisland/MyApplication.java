package com.yanrou.dawnisland;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePal;

//import com.susion.rabbit.Rabbit;
//import com.susion.rabbit.base.config.RabbitConfig;

/**
 * @author suche
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        CrashReport.initCrashReport(getApplicationContext(), "65043f91b1", false);
//        Rabbit.INSTANCE.init(this, new RabbitConfig());

    }
}
