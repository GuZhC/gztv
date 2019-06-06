package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dfsx.lzcms.liveroom.business.ICallBack;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DownLoadImageBitmapHelper {

    private Context context;

    private Handler handler = new Handler(Looper.getMainLooper());

    private BlockingQueue<ImageData> blockingQueue;

    private ICallBack<Bitmap> bitmapICallBack;

    public Bitmap getBitmapMianThread(String imageUrl, int defaultRes) {
        this.bitmapICallBack = null;
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

    public void downBitmap(String imageUrl, int defaultRes, ICallBack<Bitmap> callBack) {
        blockingQueue = new ArrayBlockingQueue<ImageData>(1);
        handler.post(new GlideAsyncLoadBitmapRunnable(imageUrl, defaultRes));
        this.bitmapICallBack = callBack;
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
            BitmapTypeRequest bitmapTypeRequest = Glide.with(context).
                    load(imageUrl)
                    .asBitmap();
            if (defaultRes != 0) {
                bitmapTypeRequest.error(defaultRes)
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
            } else {
                bitmapTypeRequest
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                blockingQueue.add(new ImageData(true, bitmap));
                                if (bitmapICallBack != null) {
                                    bitmapICallBack.callBack(bitmap);
                                }
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                blockingQueue.add(new ImageData(false, null));
                                if (bitmapICallBack != null) {
                                    bitmapICallBack.callBack(null);
                                }
                            }
                        });
            }

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
