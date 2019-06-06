package com.dfsx.thirdloginandshare.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dfsx.core.img.ImageUtil;
import com.dfsx.thirdloginandshare.Constants;
import com.dfsx.thirdloginandshare.R;
import com.tencent.mm.sdk.modelmsg.*;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;


/**
 * Created by Administrator on 2015/12/23.
 */
public class WXUtil {

    public static final String WEIXIN_REQ_STATE = "get_auth_wx_dfsx";
    private static final int THUMB_SIZE = 150;

    public static IWXAPI getWXApi(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.WeChat_APP_ID, true);
        api.registerApp(Constants.WeChat_APP_ID);
        return api;
    }

    public static void unregisterWX(Context context) {
        getWXApi(context).unregisterApp();
    }

    public static void startWX(Context context) {
        getWXApi(context).openWXApp();
    }

    public static boolean isInstallWXApp(Context context) {
        return getWXApi(context).isWXAppInstalled();
    }

    /**
     * 分享文本到微信
     *
     * @param context
     * @param text
     * @param isWeChatMoment 分享到朋友圈
     */
    public static void sendTextToWX(Context context, String title, String text, boolean isWeChatMoment) {
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.title = "Will be ignored";
        msg.description = text;
        msg.title = title;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = isWeChatMoment ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;

        // 调用api接口发送数据到微信
        getWXApi(context).sendReq(req);
    }

    /**
     * 分享网络图片
     *
     * @param context
     * @param title
     * @param imgUrl         这是网络图片路径
     * @param isWeChatMoment
     */
    public static void sendNetImg(final Context context, final String title,
                                  final String imgUrl, final boolean isWeChatMoment) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    WXImageObject imgObj = new WXImageObject(BitmapFactory.decodeStream(new URL(imgUrl).openStream()));
                    imgObj.imageUrl = imgUrl;

                    WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = imgObj;
                    msg.title = title;

                    Bitmap bmp = BitmapFactory.decodeStream(new URL(imgUrl).openStream());
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                    bmp.recycle();
                    msg.thumbData = bmpToByteArray(thumbBmp, true);

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("img");
                    req.message = msg;
                    req.scene = isWeChatMoment ? SendMessageToWX.Req.WXSceneTimeline :
                            SendMessageToWX.Req.WXSceneSession;
                    getWXApi(context).sendReq(req);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void sendImageAndText(final Context context, final String url,
                                        final String title, final String text,
                                        final String imageUrl,
                                        final boolean isWeChatMoment) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    WXWebpageObject webpage = new WXWebpageObject();
                    webpage.webpageUrl = url;
                    WXMediaMessage msg = new WXMediaMessage(webpage);
                    msg.title = title;
                    msg.description = text;
                    if (msg.description == null)
                        msg.description = "Link分享";
                    if (msg.description.length() > 206)
                        msg.description = msg.description.substring(0, 206);

                    //设置 Bitmap 类型的图片到视频对象里  设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
                    Bitmap thumb = ImageUtil.getThumbil(imageUrl, 30);
                    if (thumb == null)
                        thumb = ImageUtil.decodeSampledBitmapFromResource(context.getResources(), R.drawable.ic_launcher, 30);
                    msg.thumbData = bmpToByteArray(thumb, true);

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("webpage");
                    req.message = msg;
                    req.scene = isWeChatMoment ? SendMessageToWX.Req.WXSceneTimeline :
                            SendMessageToWX.Req.WXSceneSession;
                    getWXApi(context).sendReq(req);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void sendVideo(final Context context, final String videoUrl,
                                 final String title, final String text,
                                 final String imageUrl,
                                 final boolean isWeChatMoment) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    WXVideoObject video = new WXVideoObject();
                    video.videoUrl = videoUrl;
                    WXMediaMessage msg = new WXMediaMessage(video);
                    msg.title = title;
                    msg.description = text;
                    if (msg.description == null)
                        msg.description = "视频分享";

                    //设置 Bitmap 类型的图片到视频对象里  设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
                    Bitmap thumb = ImageUtil.getThumbil(imageUrl, 30);
                    if (thumb == null)
                        thumb = ImageUtil.decodeSampledBitmapFromResource(context.getResources(), R.drawable.ic_launcher, 30);
                    msg.thumbData = bmpToByteArray(thumb, true);
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = buildTransaction("video");
                    req.message = msg;
                    req.scene = isWeChatMoment ? SendMessageToWX.Req.WXSceneTimeline :
                            SendMessageToWX.Req.WXSceneSession;
                    getWXApi(context).sendReq(req);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
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

    public static void sendAuthRequest(Context context) {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = WEIXIN_REQ_STATE;
        getWXApi(context).sendReq(req);
    }

}
