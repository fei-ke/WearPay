package com.fei_ke.wearpay.hook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by fei-ke on 2015/9/29.
 */
public class FinishActivityReceiver extends BroadcastReceiver {
    private Activity activity;

    public FinishActivityReceiver(Activity activity) {
        this.activity = activity;
    }

    public FinishActivityReceiver() {
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        activity.finish();
    }
}
