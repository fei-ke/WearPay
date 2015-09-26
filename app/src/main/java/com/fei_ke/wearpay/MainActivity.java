package com.fei_ke.wearpay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fei_ke.wearpay.commen.Constans;
import com.fei_ke.wearpay.commen.EncodingHandlerUtils;


/**
 * Created by fei-ke on 2015/9/26.
 */
public class MainActivity extends Activity {
    private ImageView imageViewQRCode;
    private ImageView imageViewBarCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constans.ACTION_LAUNCH_WECHAT_WALLET);
                sendBroadcast(intent);
            }
        });
        imageViewQRCode = (ImageView) findViewById(R.id.iv_qrcode);
        imageViewBarCode = (ImageView) findViewById(R.id.iv_barcode);

        IntentFilter filter = new IntentFilter(Constans.ACTION_SEND_CODE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(Constans.EXTRA_CODE);
            Toast.makeText(context, code, Toast.LENGTH_SHORT).show();
            Bitmap bitmap = EncodingHandlerUtils.createQRCodeBitmap(code, 1000);
            imageViewQRCode.setImageBitmap(bitmap);

        }
    };
}
