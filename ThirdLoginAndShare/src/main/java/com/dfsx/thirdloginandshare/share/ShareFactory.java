package com.dfsx.thirdloginandshare.share;

import android.content.Context;

/**
 * Created by Administrator on 2015/12/24.
 */
public class ShareFactory {

    public static AbsShare createShare(Context context, SharePlatform platform) {
        AbsShare share = null;
        if (SharePlatform.Wechat_FRIENDS == platform) {
            share = new WXShare(context, true);
        } else if (SharePlatform.Wechat == platform) {
            share = new WXShare(context, false);
        } else if (SharePlatform.QQ == platform) {
            share = new QQAndrQQZoneShare(context, false);
        } else if (SharePlatform.QQ_ZONE == platform) {
            share = new QQAndrQQZoneShare(context, true);
        } else if (SharePlatform.SMS == platform) {
            share = new SmsShare(context);
        } else if (SharePlatform.WeiBo == platform) {
            share = new SinaShare(context);
        }
        return share;
    }
}
