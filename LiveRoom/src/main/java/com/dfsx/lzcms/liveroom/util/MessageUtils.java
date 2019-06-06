package com.dfsx.lzcms.liveroom.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.dfsx.core.CoreApp;
import com.dfsx.core.img.ImageUtil;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuwb on 2016/10/20.
 */
public class MessageUtils {

    public static SpannableString getGiftString(Context context) {
        String giftStr = "[gift]";
        String text = "送" + giftStr + "x1";

        int start = text.indexOf(giftStr);
        int end = start + giftStr.length();

        SpannableString ss = new SpannableString(text);
        Drawable giftDrawable = context.getResources().getDrawable(R.drawable.icon_test_gift);
        giftDrawable.setBounds(0, 0, giftDrawable.getIntrinsicWidth(), giftDrawable.getIntrinsicHeight());
        ss.setSpan(new ImageSpan(giftDrawable), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static void getGiftString(Context context, String giftImagePath, final ICallBack<CharSequence> callBack) {
        String giftStr = "[gift]";
        final String text = "送" + giftStr + "x1";

        final int start = text.indexOf(giftStr);
        final int end = start + giftStr.length();

        Glide.with(context).load(giftImagePath).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
                if (glideDrawable != null) {
                    SpannableString ss = new SpannableString(text);
                    glideDrawable.setBounds(0, 0, glideDrawable.getIntrinsicWidth(), glideDrawable.getIntrinsicHeight());
                    ss.setSpan(new ImageSpan(glideDrawable), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    callBack.callBack(ss);
                }
            }
        });

    }

    public static Bitmap downImageByOkHttp(String imageUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get()
                .url(imageUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                byte[] bytes = response.body().bytes();
                Bitmap bitmap = byteToBitmap(bytes);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap byteToBitmap(byte[] imgByte) {
        InputStream input = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        int length = imgByte.length;
        if (length < 50 * 1024) {//50k
            options.inSampleSize = 1;
        } else if (length < 100 * 1024) {//100k
            options.inSampleSize = 2;
        } else if (length < 300 * 1024) {//300
            options.inSampleSize = 4;
        } else {
            options.inSampleSize = 8;
        }
        input = new ByteArrayInputStream(imgByte);
        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(
                input, null, options));
        bitmap = (Bitmap) softRef.get();
        if (imgByte != null) {
            imgByte = null;
        }
        try {
            if (input != null) {
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
