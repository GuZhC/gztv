package com.dfsx.videoijkplayer.util;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/7/7.
 */
public class NetworkChangeManager implements NetworkChangeReceiver.OnNetworkChangeListener {

    private static NetworkChangeManager instance = new NetworkChangeManager();

    private ArrayList<NetworkChangeReceiver.OnNetworkChangeListener> listeners = new ArrayList<NetworkChangeReceiver.
            OnNetworkChangeListener>();

    private NetworkChangeManager() {

    }

    public static NetworkChangeManager getInstance() {
        return instance;
    }

    public void addOnNetworkChangeListener(NetworkChangeReceiver.OnNetworkChangeListener l) {
        listeners.add(l);
    }

    public void removeOnNetworkChangeListener(NetworkChangeReceiver.OnNetworkChangeListener l) {
        if (l != null) {
            listeners.remove(l);
        }
    }

    @Override
    public void onChange(int networkType) {
        notifyListener(networkType);
    }


    private void notifyListener(int type) {
        for (NetworkChangeReceiver.OnNetworkChangeListener l : listeners) {
            if (l != null) {
                l.onChange(type);
            }
        }
    }
}
