package com.chenj.computercomponent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chenj.iservice.ComponentServiceManager;
import com.chenj.iservice.IMobileService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.computer_activity_main);
        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMobileService service = ComponentServiceManager.singleton().getService("mobile");
                if (service != null){
                    service.mobile(MainActivity.this);
                }
            }
        });
    }
}
