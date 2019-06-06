package com.dfsx.selectedmedia;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.ImageView;


public class MyVideoThumbLoader {
    private ImageView imgView;
    private String path;
    //创建cache
    private LruCache<String, Bitmap> lruCache;

    public MyVideoThumbLoader(){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取最大的运行内存
        int maxSize = maxMemory /4;//拿到缓存的内存大小 35
        lruCache = new LruCache<String, Bitmap>(maxSize){
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //这个方法会在每次存入缓存的时候调用
                return value.getByteCount();
            }
        };
    }

    public  void  del(String  path)
    {
        lruCache.remove(path);
    }

    public void addVideoThumbToCache(String path, Bitmap bitmap) {
        if (getVideoThumbToCache(path) == null) {
            //当前地址没有缓存时，就添加
            lruCache.put(path, bitmap);
        }
    }

    public Bitmap getVideoThumbToCache(String path) {

        return lruCache.get(path);

    }

    public void showThumbByAsynctack(String path, ImageView imgview) {

        if (getVideoThumbToCache(path) == null) {
            //异步加载
            imgview.setTag(path);
            new MyBobAsynctack(imgview, path).execute(path);
        } else {
            imgview.setImageBitmap(getVideoThumbToCache(path));
        }

    }

class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
    private ImageView imgView;
    private String path;

    public MyBobAsynctack(ImageView imageView, String path) {
        this.imgView = imageView;
        this.path = path;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        //这里的创建缩略图方法是调用VideoUtil类的方法，也是通过 android中提供的 ThumbnailUtils.createVideoThumbnail(vidioPath, kind);
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(params[0], MediaStore.Images.Thumbnails.MINI_KIND);

        //加入缓存中
        if (getVideoThumbToCache(params[0]) == null && bitmap!=null) {
            addVideoThumbToCache(path, bitmap);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imgView.getTag().equals(path)) {//通过 Tag可以绑定 图片地址和 imageView，这是解决Listview加载图片错位的解决办法之一
            imgView.setImageBitmap(bitmap);
        }
    }
}
}