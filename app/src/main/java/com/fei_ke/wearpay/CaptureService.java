package com.fei_ke.wearpay;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import static com.fei_ke.wearpay.commen.Constans.*;

import java.util.List;

import hugo.weaving.DebugLog;

/**
 * Created by fei-ke on 2015/9/26.
 */
public class CaptureService extends AccessibilityService {

    @Override
    @DebugLog
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == eventType) {
            if (WECHAT_WALLET_ACTIVITY_NAME.equals(event.getClassName())) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c6a");
                    CharSequence text = infos.get(0).getText();
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
