package com.dfsx.videoijkplayer.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by liuwb on 2016/7/7.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private int latestNetType = -1;

    public NetworkChangeReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            int type = NetworkUtil.getConnectivityStatus(context);
            if (type != latestNetType) {//避免重复多次调用
                latestNetType = type;
                NetworkChangeManager.getInstance().onChange(type);
            }
        }
    }

    public interface OnNetworkChangeListener {
        /**
         * 使用NetworkUtil的type做标记
         *
         * @param networkType
         */
        void onChange(int networkType);
    }
}
