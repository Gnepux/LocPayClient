package com.nuaa.locpayclient;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class MyApplication extends Application {

    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        Stetho.initializeWithDefaults(this);
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
