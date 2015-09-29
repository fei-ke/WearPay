package com.fei_ke.wearpay.wear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fei_ke.wearpay.common.Common;

/**
 * Created by 杨金阳 on 2015/9/29.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CodeActivity.class);
                intent.putExtra(Common.KEY_WITCH, Common.LAUNCH_WECHAT);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CodeActivity.class);
                intent.putExtra(Common.KEY_WITCH, Common.LAUNCH_ALIPAY);
                startActivity(intent);
            }
        });
    }
}
