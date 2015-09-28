package com.fei_ke.wearpay.wear;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ImageView;

import com.fei_ke.wearpay.common.Common;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;

public class MainActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private GridViewPager mPager;
    private QRCodeFragment qrCodeFragment;
    private QRCodeFragment barCodeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        qrCodeFragment = new QRCodeFragment();
        barCodeFragment = new QRCodeFragment();

        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mPager = (GridViewPager) findViewById(R.id.pager);
                DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
                dotsPageIndicator.setDotSpacing(48);

                final MyPagerAdapter adapter = new MyPagerAdapter(getFragmentManager());
                mPager.setAdapter(adapter);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @DebugLog
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        System.out.println("注册监听器");
        sendCode();
    }

    @DebugLog
    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    protected void onStop() {
        super.onStop();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @DebugLog
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (Common.PATH_CODE.equals(path)) {
                //
            } else if (Common.PATH_QR_CODE.equals(path)) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                final Asset qrPhoto = dataMapItem.getDataMap().getAsset(Common.KEY_QR_CODE);
                final Asset barPhoto = dataMapItem.getDataMap().getAsset(Common.KEY_BAR_CODE);

                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        return loadBitmapFromAsset(mGoogleApiClient, qrPhoto);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        qrCodeFragment.setCodeImage(bitmap);
                    }
                }.execute();
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        return loadBitmapFromAsset(mGoogleApiClient, barPhoto);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        barCodeFragment.setCodeImage(bitmap);
                    }
                }.execute();

            }
        }
    }

    @DebugLog
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void sendCode() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(Common.PATH_CODE);
        putDataMapReq.getDataMap().putString(Common.KEY_CODE, "hello world");
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            @DebugLog
            public void onResult(DataApi.DataItemResult result) {
                if (result.getStatus().isSuccess()) {
                    Log.d("", "Data item set: " + result.getDataItem().getUri());
                } else {
                    Log.d("", "发送失败");

                }
            }
        });
    }

    /**
     * Extracts {@link android.graphics.Bitmap} data from the
     * {@link com.google.android.gms.wearable.Asset}
     */
    private Bitmap loadBitmapFromAsset(GoogleApiClient apiClient, Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                apiClient, asset).await().getInputStream();

        if (assetInputStream == null) {
            return null;
        }
        return BitmapFactory.decodeStream(assetInputStream);
    }

    private class MyPagerAdapter extends FragmentGridPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int row) {
            return 2;
        }

        @Override
        public Fragment getFragment(int row, int column) {
            if (column == 0) return qrCodeFragment;
            if (column == 1) return barCodeFragment;
            return null;
        }

    }
}
