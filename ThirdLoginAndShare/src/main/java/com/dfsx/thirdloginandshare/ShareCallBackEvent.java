package com.dfsx.thirdloginandshare;

/**
 * 分享的操作的回调事件
 * Created by liuwb on 2017/1/23.
 */
public class ShareCallBackEvent {
    private boolean isShareSuccess;

    public ShareCallBackEvent() {

    }

    public ShareCallBackEvent(boolean isShareSuccess) {
        this.isShareSuccess = isShareSuccess;
    }

    public boolean isShareSuccess() {
        return isShareSuccess;
    }

    public void setShareSuccess(boolean shareSuccess) {
        isShareSuccess = shareSuccess;
    }
}
