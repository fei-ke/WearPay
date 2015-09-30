package com.fei_ke.wearpay.hook;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.fei_ke.wearpay.commen.Constans.ACTION_FINISHI_ALIPAY_WALLET;
import static com.fei_ke.wearpay.commen.Constans.ACTION_LAUNCH_ALIPAY_WALLET;
import static com.fei_ke.wearpay.commen.Constans.ACTION_SEND_CODE;
import static com.fei_ke.wearpay.commen.Constans.ALIPAY_PACKAGE;
import static com.fei_ke.wearpay.commen.Constans.ALIPAY_WALLET_ACTIVITY_NAME;
import static com.fei_ke.wearpay.commen.Constans.EXTRA_CODE;
import static com.fei_ke.wearpay.commen.Constans.THIS_PACKAGE_NAME;

/**
 * Created by fei-ke on 2015/9/26.
 */
public class HookAlipay {
    private static boolean hasHooked;

    public void hook(final ClassLoader classLoader) {

        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (ALIPAY_PACKAGE.equals(getProcessName((Context) param.thisObject))) {
                    registerLaunchReceiver((Context) param.thisObject, classLoader);
                }
            }
        });


        final FinishActivityReceiver receiver = new FinishActivityReceiver();
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String name = param.thisObject.getClass().getName();
                        if (name.equals(ALIPAY_WALLET_ACTIVITY_NAME)) {
                            final Activity activity = (Activity) param.thisObject;

                            IntentFilter intentFilter = new IntentFilter(ACTION_FINISHI_ALIPAY_WALLET);
                            receiver.setActivity(activity);
                            activity.registerReceiver(receiver, intentFilter);

                            hookCode(activity);
                        }
                    }
                }

        );
        XposedHelpers.findAndHookMethod(Activity.class, "onDestroy", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final Activity activity = (Activity) param.thisObject;
                String name = activity.getClass().getName();
                if (name.equals(ALIPAY_WALLET_ACTIVITY_NAME)) {
                    activity.unregisterReceiver(receiver);
                }
            }
        });
    }


    private void registerLaunchReceiver(Context context, final ClassLoader classLoader) {

        IntentFilter intentFilter = new IntentFilter(ACTION_LAUNCH_ALIPAY_WALLET);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                XposedBridge.log("receive action: " + intent.getAction());
                final Class<?> walletActivityClass = XposedHelpers.findClass(ALIPAY_WALLET_ACTIVITY_NAME, classLoader);
                launchWallet(context, walletActivityClass);
            }
        };

        context.registerReceiver(receiver, intentFilter);
    }

    private void hookCode(final Activity activity) {
        if (hasHooked) return;
        hasHooked = true;

        Class<?> classTarget = XposedHelpers.findClass("com.alipay.mobile.onsitepay9.payer.fragments.BarcodePayFragment", activity.getClassLoader());
        final Field codeField = XposedHelpers.findField(classTarget, "v");

        XposedHelpers.findAndHookMethod(classTarget, "a", boolean.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String code = (String) codeField.get(param.thisObject);

                Intent intent = new Intent(ACTION_SEND_CODE);
                intent.setPackage(THIS_PACKAGE_NAME);
                intent.putExtra(EXTRA_CODE, code);

                activity.sendBroadcast(intent);
            }
        });
    }

    private void launchWallet(Context context, Class walletActivityClass) {
        Intent launch = new Intent(context, walletActivityClass);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
    }

    public String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }
}
