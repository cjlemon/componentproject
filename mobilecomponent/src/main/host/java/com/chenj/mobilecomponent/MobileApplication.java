package com.chenj.mobilecomponent;

import android.app.Application;

/**
 * @author chenjun
 * create at 2019-06-14
 */
public class MobileApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAppLike appLike = new MobileAppLike();
        appLike.onCreate();
    }
}
