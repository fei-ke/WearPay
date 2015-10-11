package com.fei_ke.wearpay.commen;


import com.fei_ke.wearpay.BuildConfig;

/**
 * Created by fei-ke on 2015/9/26.
 */
public class Constans {
    public static final String WECHAT_PACKAGE = "com.tencent.mm";
    public static final String WECHAT_WALLET_ACTIVITY_NAME = "com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI";
    public static final String WECHAT_PAY_SUCCESS_ACTIVITY_NAME = "com.tencent.mm.plugin.wallet_core.ui.WalletOrderInfoUI";
    public static final String WECHAT_CORE_SERVICE_NAME = "com.tencent.mm.booter.CoreService";

    public static final String ACTION_LAUNCH_WECHAT_WALLET = "com.fei_ke.wearpay.action.launchwechat";
    public static final String ACTION_FINISH_WECHAT_WALLET = "com.fei_ke.wearpay.action.finishwechat";
    public static final String ACTION_LAUNCH_ALIPAY_WALLET = "com.fei_ke.wearpay.action.launchalipay";
    public static final String ACTION_FINISHI_ALIPAY_WALLET = "com.fei_ke.wearpay.action.finishalipay";

    public static final String ACTION_SEND_CODE = "com.fei_ke.wearpay.action.sendcode";
    public static final String ACTION_PAY_SUCCESS = "com.fei_ke.wearpay.action.pay_success";
    public static final String EXTRA_CODE = "code";
    public static final String EXTRA_QRCODE = "qrcode";
    public static final String EXTRA_BARCODE = "barcode";

    public static final String ALIPAY_PACKAGE = "com.eg.android.AlipayGphone";
    public static final String ALIPAY_WALLET_ACTIVITY_NAME = "com.alipay.mobile.onsitepay9.payer.OspTabHostActivity";
    public static final String ALIPAY_CORE_SERVICE_NAME = "com.alipay.android.phone.nfd.nfdservice.ui.app.NfdService";

    public static final String THIS_PACKAGE_NAME = BuildConfig.APPLICATION_ID;
}
