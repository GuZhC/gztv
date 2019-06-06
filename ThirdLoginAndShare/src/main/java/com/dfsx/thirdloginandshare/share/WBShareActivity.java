/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dfsx.thirdloginandshare.share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dfsx.core.img.ImageUtil;
import com.dfsx.core.log.LogUtils;
import com.dfsx.thirdloginandshare.Constants;
import com.dfsx.thirdloginandshare.R;
import com.sina.weibo.sdk.ApiUtils;
import com.sina.weibo.sdk.api.*;
import com.sina.weibo.sdk.api.share.*;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * 该类演示了第三方应用如何通过微博客户端分享文字、图片、视频、音乐等。
 * 执行流程： 从本应用->微博->本应用
 *
 * @author SINA
 * @since 2013-10-22
 */
public class WBShareActivity extends Activity implements IWeiboHandler.Response {
    @SuppressWarnings("unused")
    private static final String TAG = "WBShareActivity";

    private static final int THUMB_SIZE = 150;
    public static final String WEIBO_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    public static final String KEY_SHARE_TYPE = "key_share_type";
    public static final String KEY_SHARE_DATA = "key_share_data";
    public static final int SHARE_CLIENT = 1;
    public static final int SHARE_ALL_IN_ONE = 2;

    /**
     * 微博微博分享接口实例
     */
    private IWeiboShareAPI mWeiboShareAPI = null;

    private int mShareType = SHARE_CLIENT;

    private SinaShareData shareData;

    /**
     * @see {@link Activity#onCreate}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShareType = getIntent().getIntExtra(KEY_SHARE_TYPE, SHARE_CLIENT);
        shareData = (SinaShareData) getIntent().getSerializableExtra(KEY_SHARE_DATA);

        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.WEIBO_APP_KEY);

        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();

        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
//        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
//        }

        if (shareData != null) {
            sendMessage();
        }
    }

    /**
     * @see {@link Activity#onNewIntent}
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        LogUtils.e("TAG", "onNewIntent ------------------");
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseResp 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                //                Toast.makeText(CoreApp.getInstance(), R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
                //分享成功
                ShareUtil.sendShareCallbackMsg(true);
                LogUtils.e("TAG", "ERR_OK---------------");
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                //                Toast.makeText(CoreApp.getInstance(), R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
                ShareUtil.sendShareCallbackMsg(false);
                LogUtils.e("TAG", "ERR_CANCEL---------------");
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                //                Toast.makeText(CoreApp.getInstance(),
                //                        getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResp.errMsg,
                //                        Toast.LENGTH_LONG).show();
                ShareUtil.sendShareCallbackMsg(false);
                LogUtils.e("TAG", "ERR_FAIL---------------");
                break;
        }
        finish();
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     *
     * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
     */
    private void sendMessage() {
        if (mShareType == SHARE_CLIENT) {
            if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
                if (supportApi >= ApiUtils.BUILD_INT_VER_2_2) {
                    sendMultiMessage();
                } else {
                    sendSingleMessage();
                }
            } else {
                Toast.makeText(this, R.string.weibosdk_demo_not_support_api_hint,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (mShareType == SHARE_ALL_IN_ONE) {
            sendMultiMessage();
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    private void sendMultiMessage() {

        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (!TextUtils.isEmpty(shareData.shareSummary)) {
            weiboMessage.textObject = getTextObj();
        }

        //这个分享图片的路径只能是本地图片路径
        //        if (!TextUtils.isEmpty(shareData.shareImageUrl)) {
        //            weiboMessage.imageObject = getImageObj();
        //        }

        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_WEBPAGE) {
            weiboMessage.mediaObject = getWebpageObj();
        } else if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_MUSIC) {
            weiboMessage.mediaObject = getMusicObj();
        } else if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_VIDEO) {
            weiboMessage.mediaObject = getVideoObj();
        } else if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_VOICE) {
            weiboMessage.mediaObject = getVoiceObj();
        } else if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_IMAGE) {
            weiboMessage.imageObject = getImageObj();
        }

        new NetImageMultiMessageAsyncTask().execute(weiboMessage);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     */
    private void sendSingleMessage() {

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_TEXT) {
            weiboMessage.mediaObject = getTextObj();
        } else if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_IMAGE) {
            weiboMessage.mediaObject = getImageObj();
        } else if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_WEBPAGE) {
            weiboMessage.mediaObject = getWebpageObj();
        } else if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_MUSIC) {
            weiboMessage.mediaObject = getMusicObj();
        } else if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_VIDEO) {
            weiboMessage.mediaObject = getVideoObj();
        } else if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_VOICE) {
            weiboMessage.mediaObject = getVoiceObj();
        }

        new NetImageSingleMessageAsyncTask().execute(weiboMessage);
    }

    /**
     * 获取分享的文本模板。
     *
     * @return 分享的文本模板
     */
    private String getSharedText() {
        return shareData.shareSummary;
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getSharedText();
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        imageObject.imagePath = shareData.shareImageUrl;
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        try {
            mediaObject.identify = Utility.generateGUID();
            mediaObject.title = shareData.mediaTitle;
            mediaObject.description = shareData.mediaDescription;

            //            Bitmap bmp = BitmapFactory.decodeStream(new URL(shareData.shareImageUrl).openStream());
            //            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            //            // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            //            mediaObject.setThumbImage(thumbBmp);
            mediaObject.actionUrl = shareData.actionUrl;
            mediaObject.defaultText = shareData.defaultText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaObject;

    }

    /**
     * 创建多媒体（音乐）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private MusicObject getMusicObj() {

        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        try {
            musicObject.identify = Utility.generateGUID();
            musicObject.title = shareData.mediaTitle;
            musicObject.description = shareData.mediaDescription;

            //            Bitmap bmp = BitmapFactory.decodeStream(new URL(shareData.shareImageUrl).openStream());
            //            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            //            // 设置 Bitmap 类型的图片到视频对象里        设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            //            musicObject.setThumbImage(thumbBmp);
            musicObject.actionUrl = shareData.actionUrl;
            musicObject.dataUrl = shareData.mediaDataUrl;
            musicObject.dataHdUrl = shareData.mediaDataHdUrl;
            musicObject.duration = shareData.mediaDuration;
            musicObject.defaultText = shareData.defaultText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return musicObject;

    }

    /**
     * 创建多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     */
    private VideoObject getVideoObj() {
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = shareData.mediaTitle;
        videoObject.description = shareData.mediaDescription;
        // 设置 Bitmap 类型的图片到视频对象里  设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        Bitmap thumb = ImageUtil.getThumbil(shareData.shareImageUrl, 30);
        if (thumb == null)
            thumb = ImageUtil.decodeSampledBitmapFromResource(this.getResources(), R.drawable.ic_launcher, 30);
        videoObject.setThumbImage(thumb);
        videoObject.actionUrl = shareData.actionUrl;
        videoObject.dataUrl = shareData.mediaDataUrl;
        videoObject.dataHdUrl = shareData.mediaDataHdUrl;
        videoObject.duration = shareData.mediaDuration;
        videoObject.defaultText = shareData.defaultText;
        return videoObject;
    }

    /**
     * 创建多媒体（音频）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private VoiceObject getVoiceObj() {
        // 创建媒体消息
        VoiceObject voiceObject = new VoiceObject();
        try {
            voiceObject.identify = Utility.generateGUID();
            voiceObject.title = shareData.mediaTitle;
            voiceObject.description = shareData.mediaDescription;
            //            Bitmap bmp = BitmapFactory.decodeStream(new URL(shareData.shareImageUrl).openStream());
            //            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            //            // 设置 Bitmap 类型的图片到视频对象里      设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            //            voiceObject.setThumbImage(thumbBmp);
            voiceObject.actionUrl = shareData.actionUrl;
            voiceObject.dataUrl = shareData.mediaDataUrl;
            voiceObject.dataHdUrl = shareData.mediaDataHdUrl;
            voiceObject.duration = shareData.mediaDuration;
            voiceObject.defaultText = shareData.defaultText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voiceObject;
    }

    public static class SinaShareData implements Serializable {
        public int shareDataType;
        public String shareSummary;
        public String shareImageUrl;
        public String mediaTitle;
        public String mediaDescription;
        public String actionUrl;
        public String defaultText;
        public String mediaDataUrl;//音乐的源地址，低质量，适用于移动端 2/3G 注意：长度不得超过 512Bytes
        public String mediaDataHdUrl;//高品质音乐的源地址，高质量，适用于 PC、WIFI 注意：长度不得超过 512Bytes
        public int mediaDuration;//媒体时长(单位：秒)

        public SinaShareData() {

        }

    }

    class NetImageMultiMessageAsyncTask extends AsyncTask<WeiboMultiMessage, WeiboMultiMessage, WeiboMultiMessage> {

        @Override
        protected WeiboMultiMessage doInBackground(WeiboMultiMessage... params) {
            if (params != null && params.length > 0) {
                WeiboMultiMessage weiboMultiMessage = params[0];
                if (weiboMultiMessage.mediaObject != null) {
                    Bitmap thumbBmp = ImageUtil.getThumbil(shareData.shareImageUrl, 30);
                    if (thumbBmp == null) {
                        thumbBmp = ImageUtil.decodeSampledBitmapFromResource(WBShareActivity.this.getResources(), R.drawable.ic_launcher, 30);
                    }
                    weiboMultiMessage.mediaObject.thumbData = bmpToByteArray(thumbBmp, true);
                }
                return weiboMultiMessage;
            }
            return null;
        }

        @Override
        protected void onPostExecute(WeiboMultiMessage weiboMessage) {
            super.onPostExecute(weiboMessage);
            if (weiboMessage == null) {
                return;
            }
            Log.e("TAG", "===============================");
            // 2. 初始化从第三方到微博的消息请求
            SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
            // 用transaction唯一标识一个请求
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.multiMessage = weiboMessage;

            // 3. 发送请求消息到微博，唤起微博分享界面
            boolean isOk = false;
            if (mShareType == SHARE_CLIENT) {
                isOk = mWeiboShareAPI.sendRequest(WBShareActivity.this, request);
            } else if (mShareType == SHARE_ALL_IN_ONE) {
                AuthInfo authInfo = new AuthInfo(WBShareActivity.this, Constants.WEIBO_APP_KEY,
                        Constants.REDIRECT_URL, WEIBO_SCOPE);
                Oauth2AccessToken accessToken = AccessTokenKeeper.
                        readAccessToken(getApplicationContext());
                String token = "";
                if (accessToken != null) {
                    token = accessToken.getToken();
                }
                Log.v("TAG", "token == " + token);
                isOk = mWeiboShareAPI.sendRequest(WBShareActivity.this, request, authInfo, token, new WeiboAuthListener() {
                    @Override
                    public void onWeiboException(WeiboException arg0) {
                        arg0.printStackTrace();
                        Log.e("TAG", "==================WeiboException===");
                    }

                    @Override
                    public void onComplete(Bundle bundle) {
                        // TODO Auto-generated method stub
                        Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                        AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
                    }

                    @Override
                    public void onCancel() {
                        Log.e("TAG", "==================onCancel===");
                    }
                });

            }
            LogUtils.e("TAG", "isOk == " + isOk);
            if (!isOk) {
                Toast.makeText(WBShareActivity.this, "启动微博失败", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    class NetImageSingleMessageAsyncTask extends AsyncTask<WeiboMessage, WeiboMessage, WeiboMessage> {

        @Override
        protected WeiboMessage doInBackground(WeiboMessage... params) {
            if (params != null && params.length > 0) {
                WeiboMessage weiboMessage = params[0];
                if (shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_TEXT ||
                        shareData.shareDataType == BaseMediaObject.MEDIA_TYPE_IMAGE) {
                    return weiboMessage;
                }
                Bitmap thumbBmp = ImageUtil.getThumbil(shareData.shareImageUrl, 30);
                if (thumbBmp == null) {
                    thumbBmp = ImageUtil.decodeSampledBitmapFromResource(WBShareActivity.this.getResources(), R.drawable.ic_launcher, 30);
                }
                weiboMessage.mediaObject.thumbData = bmpToByteArray(thumbBmp, true);
                return weiboMessage;
            }
            return null;
        }

        @Override
        protected void onPostExecute(WeiboMessage weiboMessage) {
            super.onPostExecute(weiboMessage);
            if (weiboMessage == null) {
                return;
            }
            // 2. 初始化从第三方到微博的消息请求
            SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
            // 用transaction唯一标识一个请求
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.message = weiboMessage;

            // 3. 发送请求消息到微博，唤起微博分享界面
            boolean isOK = mWeiboShareAPI.sendRequest(WBShareActivity.this, request);
            if (!isOK) {
                Toast.makeText(WBShareActivity.this, "启动微博失败", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bitmap, final boolean needRecycle) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        int size = baos.toByteArray().length / 1024;
        while (size > 30 && options > 0) {
            baos.reset();// 重置baos即清空baos
            // 这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            size = baos.toByteArray().length / 1024;
            options -= 10;
        }
        if (needRecycle) {
            bitmap.recycle();
        }
        byte[] result = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
