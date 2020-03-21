package com.yanrou.dawnisland;

import android.app.Application;

import com.susion.rabbit.Rabbit;
import com.susion.rabbit.base.config.RabbitConfig;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePal;

/**
 * @author suche
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        CrashReport.initCrashReport(getApplicationContext(), "65043f91b1", false);
        Rabbit.INSTANCE.init(this, new RabbitConfig());

    }
}
