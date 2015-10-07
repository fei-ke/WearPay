package com.fei_ke.wearpay.wear;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.fei_ke.wearpay.common.Common;

import hugo.weaving.DebugLog;

/**
 * Created by 杨金阳 on 2015/9/29.
 */
public class MainActivity extends Activity {
    private WatchService.WearPayBinder wearPayBinder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        @DebugLog
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            wearPayBinder = (WatchService.WearPayBinder) iBinder;
        }

        @Override
        @DebugLog
        public void onServiceDisconnected(ComponentName componentName) {
            wearPayBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, WatchService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wearPayBinder != null) {
            unbindService(serviceConnection);
        }
    }

    void initUI() {
        findViewById(R.id.btn_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wearPayBinder != null) {
                    wearPayBinder.launchWallet(Common.LAUNCH_WECHAT);

                    Intent intent = new Intent(MainActivity.this, CodeActivity.class);
                    intent.putExtra(Common.KEY_WITCH, Common.LAUNCH_WECHAT);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.btn_alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wearPayBinder != null) {
                    wearPayBinder.launchWallet(Common.LAUNCH_ALIPAY);

                    Intent intent = new Intent(MainActivity.this, CodeActivity.class);
                    intent.putExtra(Common.KEY_WITCH, Common.LAUNCH_ALIPAY);
                    startActivity(intent);
                }
            }
        });
    }
}
