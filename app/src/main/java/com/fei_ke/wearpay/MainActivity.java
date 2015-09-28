package com.fei_ke.wearpay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fei_ke.wearpay.commen.Constans;
import com.fei_ke.wearpay.commen.EncodingHandlerUtils;
import com.fei_ke.wearpay.common.Common;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import hugo.weaving.DebugLog;


/**
 * Created by fei-ke on 2015/9/26.
 */
public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private ImageView imageViewQRCode;
    private ImageView imageViewBarCode;
    private GoogleApiClient mGoogleApiClient;

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
                sendCode();
            }
        });

        imageViewQRCode = (ImageView) findViewById(R.id.iv_qrcode);
        imageViewBarCode = (ImageView) findViewById(R.id.iv_barcode);

        IntentFilter filter = new IntentFilter(Constans.ACTION_SEND_CODE);
        registerReceiver(receiver, filter);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    @DebugLog
    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.DataApi.addListener(mGoogleApiClient, new DataApi.DataListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEventBuffer) {
                for (DataEvent event : dataEventBuffer) {
                    Uri uri = event.getDataItem().getUri();
                    String path = uri.getPath();
                    if (Common.PATH_CODE.equals(path)) {
                        // Get the node id of the node that created the data item from the host portion of
                        // the uri.
                        String nodeId = uri.getHost();
                        // Set the data of the message to be the bytes of the Uri.
                        byte[] payload = uri.toString().getBytes();
                    }
                }
            }
        });
    }

    @DebugLog
    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    @DebugLog
    public void onConnectionFailed(ConnectionResult result) {
    }

    private void sendCode() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(Common.PATH_CODE);
        putDataMapReq.getDataMap().putLong(Common.KEY_CODE, System.currentTimeMillis());
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    @DebugLog
                    public void onResult(DataApi.DataItemResult result) {
                        if (result.getStatus().isSuccess()) {
                            Log.i("", "Data item set: " + result.getDataItem().getUri());
                        } else {
                            Log.i("", "发送失败");

                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(Constans.EXTRA_CODE);
            Log.i("receive code", code);
            Toast.makeText(context, code, Toast.LENGTH_SHORT).show();
            Bitmap bitmapQR = EncodingHandlerUtils.createQRCode(code, 500);
            imageViewQRCode.setImageBitmap(bitmapQR);

            Bitmap bitmapBar = EncodingHandlerUtils.createBarcode(code, 500, 300);
            imageViewBarCode.setImageBitmap(bitmapBar);
            sendPhoto(toAsset(bitmapQR),toAsset(bitmapBar) );

        }
    };


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
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
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

    /**
     * Sends the asset that was created form the photo we took by adding it to the Data Item store.
     */
    private void sendPhoto(Asset qrAsset, Asset barAsset) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(Common.PATH_QR_CODE);
        dataMap.getDataMap().putAsset(Common.KEY_QR_CODE, qrAsset);
        dataMap.getDataMap().putAsset(Common.KEY_BAR_CODE, barAsset);
        PutDataRequest request = dataMap.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    @DebugLog
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                    }
                });

    }
}
