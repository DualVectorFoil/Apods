package com.erjiguan.apods.base;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class BaseApplication extends Application {

    public static Context mContext;

    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mRefWatcher = setupLeakCanary();
    }

    public static synchronized Context getContext() {
        return mContext;
    }

    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((BaseApplication) context.getApplicationContext()).mRefWatcher;
    }
}
