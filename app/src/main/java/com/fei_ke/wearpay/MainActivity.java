package com.fei_ke.wearpay;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fei_ke.wearpay.commen.Constans;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;


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
                startAlipay();
            }
        });
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        startService(new Intent(this, WatchServices.class));
    }

    void startAlipay(){
        Log.i("tag",isRunning()+"");
        if(isRunning()){
            Intent intent = new Intent(Constans.ACTION_LAUNCH_ALIPAY_WALLET);
            sendBroadcast(intent);
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doStartApplicationWithPackageName(Constans.ALIPAY_PACKAGE);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startAlipay();
                }
            }).start();
        }
    }

    public boolean isRunning(){
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo rsi : am.getRunningServices(Integer.MAX_VALUE)) {

            String pkgName = rsi.service.getPackageName();
            if(pkgName.equals(Constans.ALIPAY_PACKAGE)){
                if(rsi.process.equals(Constans.ALIPAY_PACKAGE)){
                    return true;
                }
            }
        }
        return false;
    }

    private void doStartApplicationWithPackageName(String packagename) {

        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = ����packname
            String packageName = resolveinfo.activityInfo.packageName;
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Constans.ACTION_LAUNCH_ALIPAY_WALLET);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // ����ComponentName����1:packagename����2:MainActivity·��
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }
}
