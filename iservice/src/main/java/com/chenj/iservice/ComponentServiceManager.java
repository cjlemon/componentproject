package com.chenj.iservice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenjun
 * create at 2019-06-14
 */
public class ComponentServiceManager {

    private static volatile ComponentServiceManager manager;

    public static ComponentServiceManager singleton(){
        if (manager == null){
            synchronized (ComponentServiceManager.class){
                if (manager == null){
                    manager = new ComponentServiceManager();
                }
            }
        }
        return manager;
    }

    private Map<String, IComponentService> mServiceMap;

    private ComponentServiceManager(){
        mServiceMap = new HashMap<>();
    }

    public void registerService(String key, IComponentService service){
        mServiceMap.put(key, service);
    }

    public <T extends IComponentService> T getService(String key){
        IComponentService service = mServiceMap.get(key);
        if (service == null){
            return null;
        }
        return (T)service;
    }

}
