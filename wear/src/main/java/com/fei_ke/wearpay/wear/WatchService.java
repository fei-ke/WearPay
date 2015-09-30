package com.fei_ke.wearpay.wear;

import android.content.Intent;
import android.content.ServiceConnection;

import com.fei_ke.wearpay.common.WearService;

/**
 * Created by 杨金阳 on 2015/9/30.
 */
public class WatchService extends WearService {
    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }
}
