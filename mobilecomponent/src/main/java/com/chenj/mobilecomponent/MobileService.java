package com.chenj.mobilecomponent;

import android.content.Context;
import android.widget.Toast;

import com.chenj.iservice.IMobileService;

/**
 * @author chenjun
 * create at 2019-06-14
 */
class MobileService implements IMobileService {
    @Override
    public void mobile(Context context) {
        Toast.makeText(context, "mobile", Toast.LENGTH_SHORT).show();
    }
}
