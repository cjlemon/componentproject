package com.chenj.componentproject;

import android.app.Application;
import android.util.Log;

/**
 * @author chenjun
 * create at 2019-06-17
 */
public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("mtag", "AppApplication onCreate");
    }
}
