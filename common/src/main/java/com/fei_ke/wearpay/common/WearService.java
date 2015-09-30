package com.fei_ke.wearpay.common;

import android.content.Intent;
import android.content.ServiceConnection;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by 杨金阳 on 2015/9/29.
 */
public class WearService extends WearableListenerService {
    protected GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        tryConnectGoogleApi();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tryConnectGoogleApi();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }


    @Override
    public void onPeerConnected(Node peer) {
        tryConnectGoogleApi();
    }

    protected void tryConnectGoogleApi() {
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        tryConnectGoogleApi();
        return super.bindService(service, conn, flags);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        mGoogleApiClient.disconnect();
    }
}
