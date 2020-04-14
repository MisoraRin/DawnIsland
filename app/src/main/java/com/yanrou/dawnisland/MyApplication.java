package com.yanrou.dawnisland;

import android.app.Application;

import androidx.room.Room;

import com.susion.rabbit.Rabbit;
import com.susion.rabbit.base.config.RabbitConfig;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;
import com.yanrou.dawnisland.entities.DawnDatabase;

import timber.log.Timber;


/**
 * @author suche
 */
public class MyApplication extends Application {
    static private DawnDatabase dawnDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "65043f91b1", false);
        Rabbit.INSTANCE.init(this, new RabbitConfig());

        dawnDatabase = Room.databaseBuilder(
            getApplicationContext(), DawnDatabase.class, "dawnDB"
        )
            .fallbackToDestructiveMigration()
            .build();

      if (BuildConfig.DEBUG) {
        Timber.plant(new Timber.DebugTree());
      }
        MMKV.initialize(this);
    }

    static public DawnDatabase getDaoSession() {
        return dawnDatabase;
    }

}
