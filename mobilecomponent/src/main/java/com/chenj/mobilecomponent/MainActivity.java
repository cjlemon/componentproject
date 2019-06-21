package com.chenj.mobilecomponent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chenj.componentannotation.Host;
import com.chenj.componentannotation.Path;
import com.chenj.iservice.IComputerService;
import com.chenj.iservice.ComponentServiceManager;

@Host("mobile")
@Path("main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_activity_main);
        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IComputerService service = ComponentServiceManager.singleton().getService("computer");
                service.computer(MainActivity.this);
            }
        });
    }
}
