package com.fei_ke.wearpay.wear;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fei_ke.wearpay.common.Common;
import com.fei_ke.wearpay.common.WearService;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hugo.weaving.DebugLog;

/**
 * Created by fei-ke on 2015/9/30.
 */
public class WatchService extends WearService {
    private WearPayBinder wearPayBinder = new WearPayBinder(this);

    @Nullable
    @Override
    @DebugLog
    public IBinder onBind(Intent intent) {
        return wearPayBinder;
    }

    @DebugLog
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @DebugLog
    private void launchWallet(final String witch) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Collection<String> nodes = getNodes();
                for (String n : nodes) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, n, Common.PATH_LAUNCH_WALLET, witch.getBytes());
                }
            }
        }).start();
    }

    @DebugLog
    private void finishWallet(final String witch) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Collection<String> nodes = getNodes();
                for (String n : nodes) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, n, Common.PATH_FINISH_WALLET, witch.getBytes());
                }
            }
        }).start();
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
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

                new AsyncTask<Asset, Void, Bitmap[]>() {
                    @Override
                    protected Bitmap[] doInBackground(Asset... assets) {

                        return new Bitmap[]{loadBitmapFromAsset(assets[0]), loadBitmapFromAsset(assets[1])};
                    }

                    @Override
                    protected void onPostExecute(Bitmap[] bitmaps) {
                        wearPayBinder.onCodeChange(bitmaps[0], bitmaps[1]);
                    }
                }.execute(barPhoto, qrPhoto);

            }
        }
    }


    public static class WearPayBinder extends Binder {
        private WatchService host;

        public WearPayBinder(WatchService host) {
            this.host = host;
        }

        private List<OnCodeChangeListener> changeListeners;

        public void addChangeListener(OnCodeChangeListener changeListener) {
            if (changeListeners == null) {
                changeListeners = new ArrayList<>();
            }
            changeListeners.add(changeListener);
        }

        public void removeChangeListener(OnCodeChangeListener changeListener) {
            if (changeListeners != null) {
                changeListeners.remove(changeListener);
            }
        }

        public void onCodeChange(Bitmap barCode, Bitmap qrCode) {
            dispatchOnCodeChange(barCode, qrCode);
        }

        private void dispatchOnCodeChange(Bitmap barCode, Bitmap qrCode) {
            if (changeListeners != null) {
                for (OnCodeChangeListener l : changeListeners) {
                    l.onCodeChange(barCode, qrCode);
                }
            }
        }

        public void launchWallet(String witch) {
            host.launchWallet(witch);
        }

        public void finishWallet(String witch) {
            host.finishWallet(witch);
        }
    }

    interface OnCodeChangeListener {
        void onCodeChange(Bitmap barCode, Bitmap qrCode);
    }

}
