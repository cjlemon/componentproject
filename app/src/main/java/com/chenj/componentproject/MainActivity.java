package com.chenj.componentproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chenj.iservice.IComputerService;
import com.chenj.iservice.IMobileService;
import com.chenj.iservice.ComponentServiceManager;
import com.chenj.uirouter.UIRouterManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void mobileService(View v) {
        IMobileService service = ComponentServiceManager.singleton().getService("mobile");
        service.mobile(MainActivity.this);

    }

    public void computerService(View v) {
        IComputerService service = ComponentServiceManager.singleton().getService("computer");
        service.computer(MainActivity.this);
    }

    public void uiRouter(View v) {
        UIRouterManager.singleton().openUri(MainActivity.this, "component://mobile/main", null);
    }
}
