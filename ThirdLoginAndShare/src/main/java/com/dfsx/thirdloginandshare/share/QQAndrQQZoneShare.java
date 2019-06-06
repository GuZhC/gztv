package com.dfsx.thirdloginandshare.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.dfsx.core.CoreApp;
import com.dfsx.thirdloginandshare.R;
import com.dfsx.thirdloginandshare.activity.QQCallBackAct;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/24.
 */
public class QQAndrQQZoneShare extends AbsShare {
    public static final String ACTION = "com.tixa.plugin.share.myShare_QQAndrQQZoneShare";

    private int mExtarFlag = 0x00;

    private boolean isShareToQZone = false;

    public QQAndrQQZoneShare(Context context, boolean isShareToQZone) {
        super(context);
        this.isShareToQZone = isShareToQZone;
    }

    @Override
    public void share(ShareContent content) {
        if (content != null) {
            if (isShareToQZone) {
                shareQZone(content.getTitle(), content.getContent(), content.getUrl(),
                        content.getPicUrl());
            } else {
                if (content.type == ShareContent.UrlType.Image)
                    shareQQ(content.getPicUrl());
                else
                    shareQQ(content.getTitle(), content.getUrl(), content.getContent(),
                            content.getPicUrl());
            }
        }
    }


    //专门用来分享图片的  imageUrl必须为本地文件
    private void shareQQ(String imageUrl) {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, CoreApp.getInstance().
                getResources().getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);

        Intent intent = new Intent(context, QQCallBackAct.class);
        intent.putExtra(QQCallBackAct.KEY_ACT_TYPE, QQCallBackAct.TYPE_SHARE_ACT);
        intent.putExtra(QQCallBackAct.KEY_SHARE_DATA, params);
        intent.putExtra(QQCallBackAct.KEY_SHARE_TYPE, QQCallBackAct.TYPE_SHARE_QQ);
        intent.putExtra(QQCallBackAct.KEY_RESPONSE_ACTION, ACTION);
        context.startActivity(intent);
    }

    /**
     * 这个方法只支持图文
     * 其他功能没有加
     *
     * @param title
     * @param text
     * @param imageUrl
     */
    private void shareQQ(String title, String targetUrl,
                         String text, String imageUrl) {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        if (TextUtils.isEmpty(text)) {
            text = "资源自东方盛行CMS";
        }
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, text);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, CoreApp.getInstance().
                getResources().getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);

        Intent intent = new Intent(context, QQCallBackAct.class);
        intent.putExtra(QQCallBackAct.KEY_ACT_TYPE, QQCallBackAct.TYPE_SHARE_ACT);
        intent.putExtra(QQCallBackAct.KEY_SHARE_DATA, params);
        intent.putExtra(QQCallBackAct.KEY_SHARE_TYPE, QQCallBackAct.TYPE_SHARE_QQ);
        intent.putExtra(QQCallBackAct.KEY_RESPONSE_ACTION, ACTION);
        context.startActivity(intent);
    }

    /**
     * 分享QQ空间单张图片
     *
     * @param title
     * @param text
     * @param targetUrl
     * @param imgeUrl
     */
    private void shareQZone(String title,
                            String text, String targetUrl, String imgeUrl) {

        //heyang 2018/2/9  图片为空分享qq空间导致分享失败
        ArrayList<String> imageUrls = new ArrayList<String>();
        if (!TextUtils.isEmpty(imgeUrl)) {
            imageUrls.add(imgeUrl);
        }

        shareQZone(QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT,
                title, text, targetUrl, imageUrls, null);
    }

    /**
     * 分享QQ空间多张图片
     *
     * @param title
     * @param text
     * @param targetUrl
     * @param imgeUrlArr
     */
    private void shareQZone(String title,
                            String text, String targetUrl, String[] imgeUrlArr) {
        ArrayList<String> imageUrls = new ArrayList<String>();
        for (String img : imgeUrlArr) {
            imageUrls.add(img);
        }
        shareQZone(QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT,
                title, text, targetUrl, imageUrls, null);
    }

    /**
     * 分享QQ空间多张图片
     *
     * @param title
     * @param text
     * @param targetUrl
     * @param imageUrls
     */
    private void shareQZone(String title,
                            String text, String targetUrl, ArrayList<String> imageUrls) {
        shareQZone(QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT,
                title, text, targetUrl, imageUrls, null);
    }


    /**
     * 支持分享多种类型
     *
     * @param shareType QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT,//默认分享图文
     *                  QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE,
     *                  QzoneShare.SHARE_TO_QZONE_TYPE_APP,
     *                  QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD
     * @param title
     * @param text
     * @param targetUrl
     * @param imageUrls
     * @param videoPath
     */
    private void shareQZone(int shareType, String title,
                            String text, String targetUrl, ArrayList<String> imageUrls,
                            String videoPath) {
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, text);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);

        // 支持传多个imageUrl
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

        if (shareType == QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHVIDEO) {
            params.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, videoPath);
        }

        Intent intent = new Intent(context, QQCallBackAct.class);
        intent.putExtra(QQCallBackAct.KEY_ACT_TYPE, QQCallBackAct.TYPE_SHARE_ACT);
        intent.putExtra(QQCallBackAct.KEY_SHARE_DATA, params);
        intent.putExtra(QQCallBackAct.KEY_SHARE_TYPE, QQCallBackAct.TYPE_SHARE_QZONE);
        intent.putExtra(QQCallBackAct.KEY_RESPONSE_ACTION, ACTION);
        context.startActivity(intent);

    }
}
