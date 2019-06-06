package com.dfsx.core.img;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.dfsx.core.R;
import com.dfsx.core.common.view.CircleImageView;

import java.io.File;

/**
 * Glide图片加载控件的封装
 * 所有方法必须在主线程调用
 * Created by linfangxing on 2015/10/23.
 */
public class GlideImgManager {
    //不设置加载图
    public static final int LOADING_RES_NO_CHANGE = -1;
    //不设置错误的图
    public static final int ERROR_RES_NO_CHANGE = -1;
    //是否自动格式化图片路径，补全域名或本地路径头
    public static final boolean AUTO_FORMAT = true;

    public static final boolean AUTO_FORMAT_IMAGE_SIZE = true;

    private static GlideImgManager instance = new GlideImgManager();

    //默认配置参数
    private Config config;

    private GlideImgManager() {
        initDefaultConfig();
    }

    public static GlideImgManager getInstance() {
        return instance;
    }

    /**
     * 初始化一些参数
     */
    private void initDefaultConfig() {
        this.config = new Config();
        config.onErrorImgRes = R.drawable.glide_default_image;
        config.onLoadingImgRes = R.drawable.glide_default_image;
    }

    //heyang  2015-11-30  加载指定错误图片
    public Target showImg(Context act, ImageView imgView, String imgUrl, int errorImg, RequestListener listener) {
        return showImg(act, imgView, imgUrl, 0, errorImg, listener);
    }

    /**
     * 显示图片
     *
     * @param imgUrl
     * @param imgView
     */
    public Target showImg(Context act, ImageView imgView, String imgUrl) {
        return showImg(act, imgView, imgUrl, (RequestListener) null);
    }

    public Target showImg(Context act, ImageView imgView, String imgUrl, RequestListener listener) {
        return showImg(act, imgView, imgUrl, config, listener);
    }

    /**
     * 显示图片,可以传入加载中的resid和加载错误的resid
     *
     * @param act
     * @param imgView
     * @param imgUrl
     * @param loadingImgRes resid或者 LOADING_RES_NO_CHANGE
     * @param errorImgRes   resid或者 ERROR_RES_NO_CHANGE
     * @return
     */
    public Target showImg(Context act, ImageView imgView, String imgUrl, int loadingImgRes, int errorImgRes) {
        return showImg(act, imgView, imgUrl, loadingImgRes, errorImgRes, null);
    }

    public Target showImg(Context act, ImageView imgView, String imgUrl, int loadingImgRes, int errorImgRes, RequestListener listener) {
        if (loadingImgRes == LOADING_RES_NO_CHANGE && errorImgRes == ERROR_RES_NO_CHANGE) {
            return showImg(act, imgView, imgUrl);
        }
        Config config = this.config.cloneOne();

        if (loadingImgRes >= 0) {
            config.setLoadingImgRes(loadingImgRes);
        }
        if (errorImgRes >= 0) {
            config.setErrorImgRes(errorImgRes);
        }

        return showImgInternal(act, imgView, imgUrl, config,
                loadingImgRes != LOADING_RES_NO_CHANGE, errorImgRes != ERROR_RES_NO_CHANGE, listener);
    }

    /**
     * 指定config的显示图片
     *
     * @param act
     * @param imgView
     * @param imgUrl
     * @param config
     */
    public Target showImg(Context act, ImageView imgView, String imgUrl, Config config) {
        return showImg(act, imgView, imgUrl, config, null);
    }

    public Target showImg(Context act, ImageView imgView, String imgUrl, Config config, RequestListener listener) {
        return showImgInternal(act, imgView, imgUrl, config, true, true, listener);
    }

    /**
     * 最终都要走到这里处理
     *
     * @param act
     * @param imgView
     * @param imgUrl
     * @param config
     * @param needLoadingRes
     * @param needErrorRes
     * @param imgLoadListener
     * @return
     */
    private Target showImgInternal(Context act, ImageView imgView, String imgUrl, Config config,
                                   boolean needLoadingRes, boolean needErrorRes, RequestListener imgLoadListener) {
        if (config == null) {
            return null;
        }

        if (AUTO_FORMAT) {
            imgUrl = ImageUrlUtil.formatPictureUrl(imgUrl);
        }

        if (AUTO_FORMAT_IMAGE_SIZE) {
            imgUrl = formatPictureSizeUrl(imgUrl, imgView);
        }
        try {
            RequestManager requestManager;
            if (act instanceof FragmentActivity) {
                FragmentActivity fAct = (FragmentActivity) act;
                requestManager = Glide.with(fAct);
            } else if (act instanceof Activity) {
                Activity aAct = (Activity) act;
                requestManager = Glide.with(aAct);
            } else {
                requestManager = Glide.with(act);
            }
            DrawableTypeRequest req = requestManager.load(imgUrl);
            if (!imgView.getClass().getName().contains("ImageView") || (imgView instanceof CircleImageView)) {//如果显示的ImageView是自定义的ImageView
                req.dontAnimate();
            }
            if (needLoadingRes && config.onLoadingImgRes >= 0) req.placeholder(config.onLoadingImgRes);
            if (needErrorRes && config.onErrorImgRes >= 0) req.error(config.onErrorImgRes);
            if (imgLoadListener != null) req.listener(imgLoadListener);
            Target target = req.into(imgView);
            return target;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String formatPictureSizeUrl(String url, ImageView view) {
        boolean isDfsxImage = !TextUtils.isEmpty(url) &&
                (
                        url.contains("/general/pictures/") ||
                                url.contains("/vms/pictures/") ||
                                url.contains("/live/pictures/") ||
                                url.contains("/community/pictures/")
                );
        boolean isViewSize = view.getWidth() > 0 || view.getHeight() > 0;
        boolean isHasResize = !TextUtils.isEmpty(url) && url.contains("?w=") &&
                url.contains("&h=") && url.contains("&s=");
        boolean isNeedFormatPicture = isDfsxImage && isViewSize && !isHasResize &&
                ((url.startsWith("http://")) || (url.startsWith("https://")));
        if (isNeedFormatPicture) {
            int scaleType = 1;
            String sizeStr = "?w=" + view.getWidth() + "&h=" + view.getHeight() + "&s=" + scaleType;
            String newUrl = url + sizeStr;
            //            LogUtils.e("TAG", "newUrl === " + newUrl);
            return newUrl;
        }
        return url;
    }

    public void showGif(Context act, ImageView imageView, int resId) {
        GlideDrawableImageViewTarget imageViewTarget = new
                GlideDrawableImageViewTarget(imageView);
        Glide.with(act).load(resId).into(imageViewTarget);
    }

    public void showGif(Context act, ImageView imageView, String imageUrl) {
        if (AUTO_FORMAT) {
            imageUrl = ImageUrlUtil.formatPictureUrl(imageUrl);
        }
        Glide.with(act)
                .load(imageUrl)
                .asGif()
                .placeholder(R.drawable.icon_img_loading)
                .crossFade()
                .into(imageView);
    }

    /**
     * 查找本地缓存
     *
     * @param imgUrl
     * @return
     */
    public void findInCacheOrDownload(Context context, String imgUrl, SimpleTarget<File> listener) {
        Glide.with(context).load(imgUrl).downloadOnly(listener);
    }

    /**
     * 清除内存缓存
     *
     * @param context
     */
    public void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }

    /**
     * 清除磁盘缓存
     *
     * @param context
     */
    public void clearDiskCache(final Context context) {
        new Thread() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }.start();
    }

    /**
     * 构建器
     */
    public static class Config {
        private int onErrorImgRes;
        private int onLoadingImgRes;

        public Config setErrorImgRes(int resId) {
            this.onErrorImgRes = resId;
            return this;
        }

        public Config setLoadingImgRes(int resId) {
            this.onLoadingImgRes = resId;
            return this;
        }

        public Config cloneOne() {
            Config config = new Config();
            config.onErrorImgRes = onErrorImgRes;
            config.onLoadingImgRes = onLoadingImgRes;
            return config;
        }
    }
}