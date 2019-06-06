package com.dfsx.core.common.Util;

import java.io.Serializable;

/**
 * Created by heyang on 2016/3/24.
 * rxbus  发送的消息类型
 */
public class MessageData<T> {

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

    private String msgType;
    private T param;

    public MessageData(String type, T param) {
        this.msgType = type;
        this.param = param;
    }

    /***
     *
     *  上传数据包格式
     */
    public static class upFileDate implements Serializable {
        public int getFvale() {
            return fvale;
        }

        public void setFvale(int fvale) {
            this.fvale = fvale;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        int fvale;
        String describe;

        public upFileDate(int favl, String des) {
            this.fvale = favl;
            this.describe = des;
        }
    }
}
