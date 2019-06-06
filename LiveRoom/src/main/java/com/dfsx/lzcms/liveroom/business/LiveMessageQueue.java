package com.dfsx.lzcms.liveroom.business;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.dfsx.lzcms.liveroom.model.LiveMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by liuwb on 2017/7/21.
 */
public class LiveMessageQueue<T extends LiveMessage> {
    private static final long MIN_DELAY_TIME = 1000;

    private static final long MAX_UPDATE_MESSAGE_NUM = 20;

    private static final int WHAT_LIVE_MESSAGE = 123;

    public static final int STATE_PAUSE = 2;
    public static final int STATE_RUN = 1;
    public static final int STATE_TOP = 0;


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_LIVE_MESSAGE) {
                if (readState == STATE_RUN) {
                    //设置循环数据
                    long curTime = System.currentTimeMillis();
                    long delayTime = 0;
                    if (latestPostMessageTime <= 0 ||
                            curTime - latestPostMessageTime > MIN_DELAY_TIME) {
                        delayTime = 0;
                    } else {
                        delayTime = MIN_DELAY_TIME;
                    }
                    if (messageCallback != null) {
                        messageCallback.onLiveMessageCallback(getUpdateMessageList());
                    }
                    latestPostMessageTime = curTime;
                    if (messageList.size() > 0) {
                        sendHandleMessage(delayTime);
                    } else {
                        readState = STATE_PAUSE;
                    }
                    Log.e("TAG", "queue time === " + delayTime);
                }
            }
        }
    };

    private int readState;
    private long latestPostMessageTime;
    private LiveMessageCallback messageCallback;

    private LinkedBlockingDeque<T> messageList = new LinkedBlockingDeque<>();

    public void push(T message) {
        messageList.add(message);
        if (readState != STATE_RUN) {
            start();
        }
    }

    private ArrayList<T> getUpdateMessageList() {
        ArrayList<T> tempList = new ArrayList<>();
        for (int i = 0; i < MAX_UPDATE_MESSAGE_NUM && i < messageList.size(); i++) {
            T m = messageList.poll();
            if (m != null) {
                tempList.add(m);
            }
        }
        return tempList;
    }

    public void start() {
        readState = STATE_RUN;
        sendHandleMessage(0);
    }

    private void sendHandleMessage(long delayTime) {
        handler.sendMessageDelayed(getHandlerMessage(), delayTime);
    }

    public void stop() {
        readState = STATE_TOP;
    }

    public void release() {
        messageList.clear();
    }

    private Message getHandlerMessage() {
        Message message = handler.obtainMessage(WHAT_LIVE_MESSAGE);
        return message;
    }

    public void setLiveMessageCallback(LiveMessageCallback<T> callback) {
        this.messageCallback = callback;
    }

    public interface LiveMessageCallback<T extends LiveMessage> {
        void onLiveMessageCallback(List<T> messageList);
    }
}
