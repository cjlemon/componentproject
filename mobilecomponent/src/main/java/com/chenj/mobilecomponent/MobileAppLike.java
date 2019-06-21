package com.chenj.mobilecomponent;

import com.chenj.iservice.IApplicationLike;
import com.chenj.iservice.ComponentServiceManager;
import com.chenj.uirouter.UIRouterManager;

/**
 * @author chenjun
 * create at 2019-05-31
 */
public class MobileAppLike implements IApplicationLike {

    @Override
    public void onCreate(){
        UIRouterManager.singleton().registerUIRouter(new MainActivity$mobile$UIRouter());
        ComponentServiceManager.singleton().registerService("mobile", new MobileService());
    }
}
