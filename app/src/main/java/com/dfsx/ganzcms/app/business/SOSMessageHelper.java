package com.dfsx.ganzcms.app.business;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import rx.functions.Action1;

public class SOSMessageHelper {

    public static final long LONG_LIGHT = 1500;
    public static final long SHORT_LIGHT = 500;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                int what = msg.what;
                if (!isRequestStop) {
                    if (lightOpenMessage != null) {
                        lightOpenMessage.call(msg.what > 0);
                    }
                    sendSOSMessage(what > 0 ? 0 : 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private Action1<Boolean> lightOpenMessage;
    private int count = 0;
    private boolean isRequestStop;
    private boolean isStarted;

    /**
     * SOS 信号灯控制
     *
     * @param lightOpenMessage true则打开灯，false关灯
     */
    public SOSMessageHelper(Action1<Boolean> lightOpenMessage) {
        this.lightOpenMessage = lightOpenMessage;
        count = 0;
    }

    public void start() {
        if (!isStarted) {
            count = 0;
            isRequestStop = false;
            sendSOSMessage(1);
        }
        isStarted = true;
    }

    public void stop() {
        isStarted = false;
        isRequestStop = true;
        handler.removeMessages(0);
        handler.removeMessages(1);
        handler.removeCallbacksAndMessages(null);
        if (lightOpenMessage != null) {
            lightOpenMessage.call(false);
        }
    }

    private void sendSOSMessage(int msgWhat) {
        long delay = 0;
        if (count != 0) {
            delay = (count / 6) % 2 == 0 ? SHORT_LIGHT : LONG_LIGHT;
            if (msgWhat > 0) {//启动亮 暗的时间一直都是短的
                delay = SHORT_LIGHT;
            }
        } else {//开始的消息。 如果是请求亮 则直接打开，如果是请求暗 则延时暗
            delay = msgWhat > 0 ? 0 : SHORT_LIGHT;
        }
        Log.e("TAG", "delay === " + delay);
        handler.sendEmptyMessageDelayed(msgWhat, delay);
        count++;
    }
}
