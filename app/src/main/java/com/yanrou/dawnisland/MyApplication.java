package com.yanrou.dawnisland;

import android.app.Application;

import com.susion.rabbit.Rabbit;
import com.susion.rabbit.base.config.RabbitConfig;
import com.tencent.bugly.crashreport.CrashReport;
import com.yanrou.dawnisland.gen.DaoMaster;
import com.yanrou.dawnisland.gen.DaoSession;
import com.yanrou.dawnisland.util.SeriesContentService;

import org.greenrobot.greendao.database.Database;
import org.litepal.LitePal;

import retrofit2.Retrofit;

/**
 * @author suche
 */
public class MyApplication extends Application {
    static private DaoSession daoSession;
    static private SeriesContentService retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        CrashReport.initCrashReport(getApplicationContext(), "65043f91b1", false);
        Rabbit.INSTANCE.init(this, new RabbitConfig());

        // note: DevOpenHelper is for dev only, use a OpenHelper subclass instead
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "DawnDB");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://nmb.fastmirror.org/")
                .build()
                .create(SeriesContentService.class);
    }

    static public DaoSession getDaoSession() {
        return daoSession;
    }

    public static SeriesContentService getRetrofit() {
        return retrofit;
    }
}
