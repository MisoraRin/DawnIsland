package com.yanrou.dawnisland;

import android.app.Application;

import androidx.room.Room;

import com.susion.rabbit.Rabbit;
import com.susion.rabbit.base.config.RabbitConfig;
import com.tencent.bugly.crashreport.CrashReport;
import com.yanrou.dawnisland.entities.DawnDatabase;


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



    }

    static public DawnDatabase getDaoSession() {
        return dawnDatabase;
    }

}
