package com.chenj.computercomponent;

import android.util.Log;

import com.chenj.iservice.ComponentServiceManager;
import com.chenj.iservice.IApplicationLike;

/**
 * @author chenjun
 * create at 2019-06-14
 */
public class ComputerAppLike implements IApplicationLike {
    @Override
    public void onCreate() {
        ComponentServiceManager.singleton().registerService("computer", new ComputerService());
        Log.e("mtag", "Computer onCreate");
    }
}
