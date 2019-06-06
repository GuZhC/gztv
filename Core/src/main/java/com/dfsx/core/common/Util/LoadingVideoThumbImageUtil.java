package com.dfsx.core.common.Util;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.widget.ImageView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by heyang on 2017/12/1.
 */
public class LoadingVideoThumbImageUtil {
    private ImageView imageView;
    private String url;
    private int defaultDrawdle;

    public LoadingVideoThumbImageUtil(ImageView imageView, String url, int defaultDrawdle) {
        this.imageView = imageView;
        this.url = url;
        this.defaultDrawdle = defaultDrawdle;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void load() {
        Observable.just(this)
                .observeOn(Schedulers.io())
                .map(new Func1<LoadingVideoThumbImageUtil, Bitmap>() {
                    @Override
                    public Bitmap call(LoadingVideoThumbImageUtil loadingVideoThumImage) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = ThumbnailUtils.createVideoThumbnail(loadingVideoThumImage.getUrl(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                            int mMaxWidth = Math.max(loadingVideoThumImage.getImageView().getWidth(), 250);
                            if (bitmap != null) {
                                bitmap = Bitmap.createScaledBitmap(bitmap, mMaxWidth, mMaxWidth, false);
                                return bitmap;
                            }
                            return null;
                        } catch (Exception e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        imageView.setImageResource(defaultDrawdle);
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setImageResource(defaultDrawdle);
                        }
                    }
                });
    }
}