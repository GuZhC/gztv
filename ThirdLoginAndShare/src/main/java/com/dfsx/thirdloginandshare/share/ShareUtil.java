package com.dfsx.thirdloginandshare.share;

import android.content.Context;
import com.dfsx.core.rx.RxBus;
import com.dfsx.thirdloginandshare.AndroidShare;
import com.dfsx.thirdloginandshare.ShareCallBackEvent;

/**
 * Created by liuwb on 2016/7/14.
 */
public class ShareUtil {

    /**
     * 发送分享回调事件
     *
     * @param isShareSuccess
     */
    public static void sendShareCallbackMsg(Boolean isShareSuccess) {
        RxBus.getInstance().post(new ShareCallBackEvent(isShareSuccess));
    }


    /**
     * 是用默认的界面分享内容
     *
     * @param context
     * @param content
     */
    public static void share(Context context, ShareContent content) {
        AndroidShare share = new AndroidShare(context, content);
        share.show();
    }
}
