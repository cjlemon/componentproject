package com.chenj.uirouter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenjun
 * create at 2019-05-27
 * ui跳转调度中心
 */
public class UIRouterManager {

    private static final String SCHEME = "component";
    private static volatile UIRouterManager routerManager;

    private List<String> mHostWhiteList;
    private Map<String, IUIRouter> mRouterMap;

    private UIRouterManager(){
        mRouterMap = new HashMap<>();
        mHostWhiteList = new ArrayList<>();
    }

    public static UIRouterManager singleton(){
        if (routerManager == null){
            synchronized (UIRouterManager.class){
                if (routerManager == null){
                    routerManager = new UIRouterManager();
                }
            }
        }
        return routerManager;
    }

    public void registerUIRouter(@NonNull IUIRouter router){
        String host = router.getHost();
        mHostWhiteList.add(host);
        mRouterMap.put(host, router);
    }

    public boolean openUri(Context context, Uri uri, Bundle bundle){
        return openUri(context, uri, bundle, -1);
    }

    public boolean openUri(Context context, String url, Bundle bundle){
        return openUri(context, url, bundle, -1);
    }

    public boolean openUri(Context context, Uri uri, Bundle bundle, int requestCode){
        String scheme = uri.getScheme();
        //校验协议
        if (!SCHEME.equals(scheme)){
            return false;
        }
        String host = uri.getHost();
        //校验host
        if (!mHostWhiteList.contains(host)){
            return false;
        }
        IUIRouter router = mRouterMap.get(host);
        if (router == null){
            return false;
        }
        return router.openUri(context, uri, bundle, requestCode);
    }

    public boolean openUri(Context context, String url, Bundle bundle, int requestCode){
        Uri uri = Uri.parse(url);
        return openUri(context, uri, bundle, requestCode);
    }
}
