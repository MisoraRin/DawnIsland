package com.yanrou.dawnisland;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;
import com.yanrou.dawnisland.entities.DawnDatabase;
import com.yanrou.dawnisland.util.ReadableTime;

import java.io.File;

import rxhttp.RxHttpPlugins;
import rxhttp.wrapper.param.RxHttp;
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
//        Rabbit.INSTANCE.init(this, new RabbitConfig());

        dawnDatabase = Room.databaseBuilder(
                getApplicationContext(), DawnDatabase.class, "dawnDB"
        )
                .fallbackToDestructiveMigration()
                .build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        MMKV.initialize(this);
        ReadableTime.initialize(this);
        initRxHttpCache(this);
    }

    static public DawnDatabase getDaoSession() {
        return dawnDatabase;
    }

    public void initRxHttpCache(Context context) {
        RxHttp.init(null);
        //设置缓存目录为：Android/data/{app包名目录}/cache/RxHttpCache
        File cacheDir = new File(context.getExternalCacheDir(), "RxHttpCache");
        //设置最大缓存为10M，缓存有效时长为60秒
        RxHttpPlugins.setCache(cacheDir, 10 * 1024 * 1024);
    }
}
