package com.fei_ke.wearpay.hook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.fei_ke.wearpay.commen.Constans.ALIPAY_PACKAGE;
import static com.fei_ke.wearpay.commen.Constans.WECHAT_PACKAGE;

/**
 * Hook Wechat and Alipay
 * Created by fei-ke on 2015/9/26.
 */
public class Hook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpParam) throws Throwable {
        String packageName = lpParam.packageName;

        if (WECHAT_PACKAGE.equals(packageName) && lpParam.isFirstApplication) {
            new HookWechat().hook(lpParam.classLoader);
            return;
        }

        if (ALIPAY_PACKAGE.equals(packageName) && lpParam.isFirstApplication) {
            new HookAlipay().hook(lpParam.classLoader);
            return;
        }
    }
}
