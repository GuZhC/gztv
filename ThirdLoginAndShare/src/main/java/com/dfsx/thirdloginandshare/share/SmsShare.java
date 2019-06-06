package com.dfsx.thirdloginandshare.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dfsx.core.CoreApp;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.thirdloginandshare.R;

import java.io.File;
import java.net.URLConnection;

/**
 * 短信分享
 * Created by liuwb on 2015/12/25.
 */
public class SmsShare extends AbsShare{

    public static final String MMS_TEMP_IMG = "mms_temp_image.png";

    public SmsShare(Context context) {
        super(context);
    }

    @Override
    public void share(final ShareContent content) {
        if(!TextUtils.isEmpty(content.getPicUrl())) {
            GlideImgManager.getInstance().findInCacheOrDownload(context,
                    content.getPicUrl(), new SimpleTarget<File>() {
                        @Override
                        public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                            if(resource.exists()){
                                Intent sendIntent;
                                String dir = Environment.getExternalStorageDirectory()
                                        + File.separator + "dfsx" + File.separator + "sms_share"
                                        + File.separator;
                                File file = new File(dir + MMS_TEMP_IMG);
                                FileUtil.copyfile(resource, file, true);
                                try {
                                    sendIntent = getSmsIntent(null, CoreApp.getInstance().
                                            getApplicationContext().getResources().getString(R.string.app_name),
                                            content.getContent(),file);
                                    context.startActivity(sendIntent);
                                }catch (Exception e) {
                                    try {
                                        Intent var9 = getMmsIntent(null,
                                                CoreApp.getInstance().
                                                getApplicationContext().getResources().getString(R.string.app_name),
                                                content.getContent(), file);
                                        var9.setPackage("com.android.mms");
                                        context.startActivity(var9);
                                    } catch (Throwable var13) {
                                        try {
                                            Intent var10 = getMmsIntent(null,
                                                    CoreApp.getInstance().
                                                            getApplicationContext().getResources().getString(R.string.app_name),
                                                    content.getContent(), file);
                                            context.startActivity(var10);
                                        } catch (Throwable var12) {
                                            var12.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    });
        }else {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("vnd.android-dir/mms-sms");
            sendIntent.putExtra("sms_body", content.getContent());
            context.startActivity(sendIntent);
        }

    }

    private Intent getSmsIntent(String address, String subject,
                                String text, File file) {
        Intent var5 = new Intent("android.intent.action.SEND_MSG");
        var5.putExtra("address", address);
        var5.setFlags(268435456);
        var5.putExtra("android.intent.extra.SUBJECT", subject);
        var5.putExtra("sms_body", text);
        var5.setType("text/plain");
        String var6 = file.getAbsolutePath();
        String var7 = URLConnection.getFileNameMap().getContentTypeFor(var6);
        if(var7 == null || var7.length() <= 0) {
            String var8 = var6.trim().toLowerCase();
            if(var8.endsWith("png")) {
                var7 = "image/png";
            } else if(!var8.endsWith("jpg") && !var8.endsWith("jpeg")) {
                if(var8.endsWith("gif")) {
                    var7 = "image/gif";
                } else {
                    var7 = "*/*";
                }
            } else {
                var7 = "image/jpeg";
            }
        }

        var5.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
        var5.setType(var7);
        return var5;
    }

    private Intent getMmsIntent(String address, String subject, String text, File file) {
        Intent var5 = new Intent("android.intent.action.SEND", Uri.parse("mms://"));
        var5.putExtra("address", address);
        var5.setFlags(268435456);
        if(subject != null) {
            var5.putExtra("android.intent.extra.SUBJECT", subject);
        }

        if(text != null) {
            var5.putExtra("sms_body", text);
            var5.putExtra("android.intent.extra.TEXT", text);
            var5.setType("text/plain");
        }

        String var6 = file.getAbsolutePath();
        String var7 = URLConnection.getFileNameMap().getContentTypeFor(var6);
        if(var7 == null || var7.length() <= 0) {
            String var8 = var6.trim().toLowerCase();
            if(var8.endsWith("png")) {
                var7 = "image/png";
            } else if(!var8.endsWith("jpg") && !var8.endsWith("jpeg")) {
                if(var8.endsWith("gif")) {
                    var7 = "image/gif";
                } else {
                    var7 = "*/*";
                }
            } else {
                var7 = "image/jpeg";
            }
        }

        var5.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
        var5.setType(var7);
        return var5;
    }
}
