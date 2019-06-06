package com.dfsx.ganzcms.app.push;

import android.util.Log;

import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Extension;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wen on 2017/3/23.
 */

public class MessagePushManager {
    private static String TAG = "PushSubcribeManager";
    private static MessagePushManager instance = new MessagePushManager();
    private ArrayList<OnPushListener> listeners = new ArrayList<OnPushListener>();

    public static MessagePushManager getInstance() {
        return instance;
    }

    public void addOnMessagePushReceiveListener(OnPushListener l) {
        listeners.add(l);
    }


    public void removeOnMessagePushReceiveListener(OnPushListener l) {
        listeners.remove(l);
    }


    public void onNoticeReceive(String title, String summary, Map<String, String> extraMap) {
        String json = extraMap.get("info");
        if (json == null) {
            Log.e(TAG, "推送的map中不含key 'info'");
            return;
        }
        Gson gson = new Gson();
        Extension extension = gson.fromJson(json, Extension.class);
        extension.setMessageType(extension.getMessageType());
        for (OnPushListener l : listeners) {
            List<Extension.MessageType> list = l.getFilter().getFilterList();
            //如果没有设置过滤，那么所有类型的消息都会发送
            if (list == null)
                l.onMessageReceive(extension);
            else {
                if (list.contains(extension.getMessageType()))
                    l.onMessageReceive(extension);
            }
        }
    }

    public void onMessageReceive(CPushMessage cPushMessage) {
    }

    public static class MessageFilter {

        public List<Extension.MessageType> getFilterList() {
            return filterList;
        }

        public void setFilterList(List<Extension.MessageType> filterList) {
            this.filterList = filterList;
        }

        List<Extension.MessageType> filterList = new ArrayList<Extension.MessageType>();

        public void addToFilter(Extension.MessageType type) {
            filterList.add(type);
        }
    }
}
