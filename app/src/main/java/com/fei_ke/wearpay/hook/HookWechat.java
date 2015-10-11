package com.fei_ke.wearpay.hook;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.fei_ke.wearpay.commen.Constans;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.fei_ke.wearpay.commen.Constans.ACTION_FINISH_WECHAT_WALLET;
import static com.fei_ke.wearpay.commen.Constans.ACTION_LAUNCH_WECHAT_WALLET;
import static com.fei_ke.wearpay.commen.Constans.ACTION_SEND_CODE;
import static com.fei_ke.wearpay.commen.Constans.EXTRA_CODE;
import static com.fei_ke.wearpay.commen.Constans.THIS_PACKAGE_NAME;
import static com.fei_ke.wearpay.commen.Constans.WECHAT_CORE_SERVICE_NAME;
import static com.fei_ke.wearpay.commen.Constans.WECHAT_WALLET_ACTIVITY_NAME;
import static com.fei_ke.wearpay.commen.Constans.WECHAT_PAY_SUCCESS_ACTIVITY_NAME;

/**
 * Created by fei-ke on 2015/9/26.
 */
public class HookWechat {
    public void hook(final ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final Class<?> walletActivityClass = XposedHelpers.findClass(WECHAT_WALLET_ACTIVITY_NAME, classLoader);

                XposedHelpers.findAndHookMethod(WECHAT_CORE_SERVICE_NAME, classLoader, "onCreate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        registerLaunchReceiver((Context) param.thisObject, walletActivityClass);
                    }
                });

                final FinishActivityReceiver receiver = new FinishActivityReceiver();
                XposedHelpers.findAndHookMethod(walletActivityClass, "onCreate", Bundle.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                final Activity activity = (Activity) param.thisObject;
                                hookCode(activity);

                                IntentFilter intentFilter = new IntentFilter(ACTION_FINISH_WECHAT_WALLET);
                                receiver.setActivity(activity);
                                activity.registerReceiver(receiver, intentFilter);
                            }
                        }
                );
                XposedHelpers.findAndHookMethod(walletActivityClass, "onDestroy", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        final Activity activity = (Activity) param.thisObject;
                        activity.unregisterReceiver(receiver);
                    }
                });

                //when pay success, send a broadcast
                XposedHelpers.findAndHookMethod(WECHAT_PAY_SUCCESS_ACTIVITY_NAME, classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Intent intent = new Intent(Constans.ACTION_PAY_SUCCESS);
                        ((Context) param.thisObject).sendBroadcast(intent);
                    }
                });
            }
        });

    }

    private static boolean hasRegister;

    private void registerLaunchReceiver(Context context, final Class<?> walletActivityClass) {
        if (hasRegister) return;
        hasRegister = true;
        IntentFilter intentFilter = new IntentFilter(ACTION_LAUNCH_WECHAT_WALLET);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                XposedBridge.log("receive action: " + intent.getAction());
                launchWechatWallet(context, walletActivityClass);
            }
        };

        context.registerReceiver(receiver, intentFilter);
    }

    private void hookCode(final Activity activity) {
        TextView textView = (TextView) activity.findViewById(0x7f0e0f82);
        if (textView != null) {
            textView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String code = s.toString().replace(" ", "");
                    Intent intent = new Intent(ACTION_SEND_CODE);
                    intent.setPackage(THIS_PACKAGE_NAME);
                    intent.putExtra(EXTRA_CODE, code);
                    activity.sendBroadcast(intent);
                }
            });
        }

    }

    private void launchWechatWallet(Context context, Class walletActivityClass) {
        Intent launch = new Intent(context, walletActivityClass);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
    }

}
