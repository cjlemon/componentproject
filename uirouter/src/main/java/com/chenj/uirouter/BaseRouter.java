package com.chenj.uirouter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenjun
 * create at 2019-05-30
 */
public abstract class BaseRouter implements IUIRouter {

    private Map<String, Class<?>> mPathMap;

    public BaseRouter(){
        mPathMap = new HashMap<>();
        UIRouterManager.singleton().registerUIRouter(this);
    }

    @Override
    public boolean openUri(Context context, Uri uri, Bundle bundle) {
        return openUri(context, uri, bundle, -1);
    }

    @Override
    public boolean openUri(Context context, String url, Bundle bundle) {
        return openUri(context, url, bundle, -1);
    }

    @Override
    public boolean openUri(Context context, Uri uri, Bundle bundle, int requestCode) {
        String host = uri.getHost();
        if (host == null){
            return false;
        }
        if (!host.equals(getHost())){
            return false;
        }
        String path = uri.getPath();
        String authority = uri.getAuthority();
        String lastPathSegment = uri.getLastPathSegment();
        Log.e("mtag", path + "  " + authority + "  " + lastPathSegment);
        if (!mPathMap.containsKey(lastPathSegment)){
            return false;
        }
        Class<?> aClass = mPathMap.get(lastPathSegment);
        Intent intent = new Intent(context, aClass);
        if (bundle != null){
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean openUri(Context context, String url, Bundle bundle, int requestCode) {
        Uri uri = Uri.parse(url);
        return openUri(context, uri, bundle, requestCode);
    }

    public void registerPath(String path, Class<?> cls){
        mPathMap.put(path, cls);
    }
}
