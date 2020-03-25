package com.yanrou.dawnisland;

import android.app.Application;

import com.susion.rabbit.Rabbit;
import com.susion.rabbit.base.config.RabbitConfig;
import com.tencent.bugly.crashreport.CrashReport;
import com.yanrou.dawnisland.gen.DaoMaster;
import com.yanrou.dawnisland.gen.DaoSession;

import org.greenrobot.greendao.database.Database;
import org.litepal.LitePal;

/**
 * @author suche
 */
public class MyApplication extends Application {
    static private DaoSession daoSession;

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

    }

    static public DaoSession getDaoSession() {
        return daoSession;
    }
}
