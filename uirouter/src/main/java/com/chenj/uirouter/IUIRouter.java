package com.chenj.uirouter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

/**
 * @author chenjun
 * create at 2019-05-28
 */
public interface IUIRouter {

    boolean openUri(Context context, String url, Bundle bundle);

    boolean openUri(Context context, Uri uri, Bundle bundle);

    boolean openUri(Context context, String url, Bundle bundle, int requestCode);

    boolean openUri(Context context, Uri uri, Bundle bundle, int requestCode);

    String getHost();
}
