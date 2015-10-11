package com.fei_ke.wearpay.wear;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.view.WindowManager;

import com.fei_ke.wearpay.common.Common;

public class CodeActivity extends Activity implements WatchService.OnCodeChangeListener {
    private WatchService.WearPayBinder wearPayBinder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            wearPayBinder = (WatchService.WearPayBinder) iBinder;
            wearPayBinder.addChangeListener(CodeActivity.this);
            wearPayBinder.setOnPaySuccessListener(new WatchService.OnPaySuccessListener() {
                @Override
                public void onPaySuccess() {
                    Intent intent = new Intent(CodeActivity.this, ConfirmationActivity.class);
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.pay_success));
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            wearPayBinder = null;
        }
    };

    private GridViewPager mPager;
    private QRCodeFragment qrCodeFragment;
    private BarcodeFragment barCodeFragment;

    private String witch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        // Brightness boost
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);

        //bind watch service
        Intent intent = new Intent(this, WatchService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        //get witch wallet to show
        witch = getIntent().getStringExtra(Common.KEY_WITCH);

        //init
        int iconRes = witch.equals(Common.LAUNCH_ALIPAY)
                ? R.drawable.icon_alipay
                : R.drawable.icon_wechat;
        qrCodeFragment = CodeFragment.newQRCodeFragment(iconRes);
        barCodeFragment = CodeFragment.newBarcodeFragment(iconRes);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mPager = (GridViewPager) findViewById(R.id.pager);
                DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
                dotsPageIndicator.setDotSpacing((int) getResources().getDimension(R.dimen.dots_spacing));
                dotsPageIndicator.setPager(mPager);

                final MyPagerAdapter adapter = new MyPagerAdapter(getFragmentManager());
                mPager.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (wearPayBinder != null) {
            wearPayBinder.finishWallet(witch);
            unbindService(serviceConnection);
        }
    }

    @Override
    public void onCodeChange(Bitmap barCode, Bitmap qrCode) {
        barCodeFragment.setCodeImage(barCode);
        qrCodeFragment.setCodeImage(qrCode);
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
            if (column == 0) return barCodeFragment;
            if (column == 1) return qrCodeFragment;
            return null;
        }

    }
}
