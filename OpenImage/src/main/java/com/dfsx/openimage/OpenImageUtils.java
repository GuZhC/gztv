package com.dfsx.openimage;

import android.content.Context;
import android.content.Intent;

/**
 * Created by liuwb on 2016/8/29.
 */
public class OpenImageUtils {

    public static void openImage(Context context, String imageUrls, int position) {
        Intent intent = new Intent();
        intent.putExtra(ShowWebImageActivity.IMAGE_URLS, imageUrls);
        intent.putExtra(ShowWebImageActivity.POSITION, position);
        intent.setClass(context, ShowWebImageActivity.class);
        context.startActivity(intent);
    }

    /**
     * 添加一个直接船体数组显示图片的跳转方式
     *
     * @param context
     * @param position
     * @param imageUrls
     */
    public static void openImage(Context context, int position, String... imageUrls) {
        Intent intent = new Intent();
        intent.putExtra(ShowWebImageActivity.IMAGE_URL_ARRAY, imageUrls);
        intent.putExtra(ShowWebImageActivity.POSITION, position);
        intent.setClass(context, ShowWebImageActivity.class);
        context.startActivity(intent);
    }
}
