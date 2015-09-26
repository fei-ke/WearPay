package com.fei_ke.wearpay.hook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.fei_ke.wearpay.commen.Constans.WECHAT_PACKAGE;

/**
 * Hook Wechat and Alipay
 * Created by fei-ke on 2015/9/26.
 */
public class Hook implements IXposedHookLoadPackage {
    public static boolean hasHookedWechat;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpParam) throws Throwable {
        if (WECHAT_PACKAGE.equals(lpParam.packageName) && !hasHookedWechat) {
            hasHookedWechat = true;
            new HookWechat().hook(lpParam.classLoader);
        }
    }
}
