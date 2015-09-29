package com.fei_ke.wearpay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fei_ke.wearpay.commen.Constans;


/**
 * Created by fei-ke on 2015/9/26.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constans.ACTION_LAUNCH_WECHAT_WALLET);
                sendBroadcast(intent);
            }
        });
        findViewById(R.id.alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constans.ACTION_LAUNCH_ALIPAY_WALLET);
                sendBroadcast(intent);
            }
        });
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constans.ACTION_FINISHI_ALIPAY_WALLET);
                sendBroadcast(intent);
            }
        });
        startService(new Intent(this, WatchServices.class));

    }
}
