package com.dfsx.lzcms.liveroom.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GiftSpannableStringSyncHelper {

    private Context context;

    private Handler handler = new Handler(Looper.getMainLooper());

    private BlockingQueue<ImageData> blockingQueue;

    public GiftSpannableStringSyncHelper(Context context) {
        this.context = context;
    }

    /**
     * must be used by child thread
     * 必须在子线程中
     *
     * @param num
     * @param giftImagePath
     * @param defaultRes
     * @return
     */
    public SpannableString getGiftContentString(int num,
                                                String giftImagePath,
                                                int defaultRes) {
        String giftStr = "[gift]";
        String text = "送" + giftStr + "x" + num;

        int start = text.indexOf(giftStr);
        int end = start + giftStr.length();

        SpannableString ss = new SpannableString(text);
        if (context == null) {
            return ss;
        }
        Drawable giftDrawable = null;
        Bitmap bitmap = null;
        if (TextUtils.isEmpty(giftImagePath)) {
            giftDrawable = context.getResources().getDrawable(defaultRes);
        } else {
            try {
                bitmap = getBitmapMianThread(giftImagePath, defaultRes);
                if (bitmap != null) {
                    giftDrawable = new BitmapDrawable(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (giftDrawable == null) {
                giftDrawable = context.getResources().getDrawable(defaultRes);
            }
        }

        int width = Math.max(giftDrawable.getIntrinsicWidth(), 80);
        int minHeight = giftDrawable.getIntrinsicWidth() == 0 ? 80 :
                (int) (width * (giftDrawable.getIntrinsicHeight() /
                        ((float) giftDrawable.getIntrinsicWidth())));
        if (minHeight <= 0) {
            minHeight = 80;
        }
        int height = Math.max(giftDrawable.getIntrinsicHeight(), minHeight);
        giftDrawable.setBounds(0, 0, width, height);
        ss.setSpan(new ImageSpan(giftDrawable), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private Bitmap downBitmap(String imageUrl, int defaultRes) {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context).
                    load(imageUrl)
                    .asBitmap()
                    .error(defaultRes)
                    .into(-1, -1)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap getBitmapMianThread(String imageUrl, int defaultRes) {
        Bitmap bitmap = null;
        blockingQueue = new ArrayBlockingQueue<ImageData>(1);
        handler.post(new GlideAsyncLoadBitmapRunnable(imageUrl, defaultRes));
        try {
            bitmap = blockingQueue.poll(5, TimeUnit.SECONDS)
                    .bitmap;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    class GlideAsyncLoadBitmapRunnable implements Runnable {

        private String imageUrl;
        private int defaultRes;

        public GlideAsyncLoadBitmapRunnable(String imageUrl, int defaultRes) {
            this.imageUrl = imageUrl;
            this.defaultRes = defaultRes;
        }

        @Override
        public void run() {
            Glide.with(context).
                    load(imageUrl)
                    .asBitmap()
                    .error(defaultRes)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            blockingQueue.add(new ImageData(true, bitmap));
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            blockingQueue.add(new ImageData(false, null));
                        }
                    });
        }
    }

    class ImageData {
        public boolean isOk;
        public Bitmap bitmap;

        public ImageData() {

        }

        public ImageData(boolean isOk, Bitmap bitmap) {
            this.isOk = isOk;
            this.bitmap = bitmap;
        }
    }


}
