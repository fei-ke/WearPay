package com.fei_ke.wearpay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fei_ke.wearpay.commen.Constans;
import com.fei_ke.wearpay.commen.EncodingHandlerUtils;
import com.fei_ke.wearpay.common.Common;
import com.fei_ke.wearpay.common.WearService;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import hugo.weaving.DebugLog;

import static com.fei_ke.wearpay.common.Common.PATH_FINISH_WALLET;
import static com.fei_ke.wearpay.common.Common.PATH_LAUNCH_WALLET;

/**
 * Created by fei-ke on 2015/9/29.
 */
public class WatchServices extends WearService {
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        @DebugLog
        public void onReceive(Context context, Intent intent) {
            tryConnectGoogleApi();

            String code = intent.getStringExtra(Constans.EXTRA_CODE);
            sendToWatch(code);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(Constans.ACTION_SEND_CODE);
        registerReceiver(receiver, filter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    @DebugLog
    public void onMessageReceived(MessageEvent messageEvent) {
        String path = messageEvent.getPath();
        if (path.equals(PATH_LAUNCH_WALLET)) {
            String witch = new String(messageEvent.getData());
            if (Common.LAUNCH_ALIPAY.equals(witch)) {
                launchAlipayWallet();
            } else if (Common.LAUNCH_WECHAT.equals(witch)) {
                launchWechatWallet();
            }
        } else if (path.equals(PATH_FINISH_WALLET)) {
            String witch = new String(messageEvent.getData());
            if (Common.LAUNCH_ALIPAY.equals(witch)) {
                finishAlipayWallet();
            } else if (Common.LAUNCH_WECHAT.equals(witch)) {
                finishWechatWallet();
            }
        }
    }


    /**
     * Builds an {@link com.google.android.gms.wearable.Asset} from a bitmap. The image that we get
     * back from the camera in "data" is a thumbnail size. Typically, your image should not exceed
     * 320x320 and if you want to have zoom and parallax effect in your app, limit the size of your
     * image to 640x400. Resize your image before transferring to your wearable device.
     */
    private static Asset toAsset(Bitmap bitmap) {
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        } finally {
            if (null != byteStream) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }


    @DebugLog
    private void sendToWatch(String code) {
        Bitmap bitmapQR = EncodingHandlerUtils.createQRCode(code, 500);
        Bitmap bitmapBar = EncodingHandlerUtils.createBarcode(code, 500, 300);
        PutDataMapRequest dataMap = PutDataMapRequest.create(Common.PATH_QR_CODE);
        dataMap.getDataMap().putAsset(Common.KEY_QR_CODE, toAsset(bitmapQR));
        dataMap.getDataMap().putAsset(Common.KEY_BAR_CODE, toAsset(bitmapBar));
        PutDataRequest request = dataMap.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    @DebugLog
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if (dataItemResult.getStatus().isSuccess()) {
                            System.out.println("发送成功");
                        } else {
                            System.out.println("发送失败");
                        }
                    }
                });
    }

    public void launchWechatWallet() {
        Intent intent = new Intent(Constans.ACTION_LAUNCH_WECHAT_WALLET);
        intent.setPackage(Constans.WECHAT_PACKAGE);
        sendBroadcast(intent);
    }

    public void launchAlipayWallet() {
        Intent intent = new Intent(Constans.ACTION_LAUNCH_ALIPAY_WALLET);
        intent.setPackage(Constans.ALIPAY_PACKAGE);
        sendBroadcast(intent);
    }

    public void finishWechatWallet() {
        Intent intent = new Intent(Constans.ACTION_FINISH_WECHAT_WALLET);
        sendBroadcast(intent);
    }

    public void finishAlipayWallet() {
        Intent intent = new Intent(Constans.ACTION_FINISHI_ALIPAY_WALLET);
        sendBroadcast(intent);
    }
}
