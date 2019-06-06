package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.videoijkplayer.media.MediaItem;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * create by heyang 2017/7/31
 * 负责图片压缩
 */
public class PictureManager {
    public static final String TAG = "PictureManager";
    public static final String saveDirTemp = Environment.getExternalStorageDirectory() + "/data/leshan/back/";
    private boolean isUpSuccess = true;
    private String errMsg;

    /***  存放启动页的 ***/
    public static final String saveSprashDirTemp = App.getInstance().getBaseContext().getFilesDir().getPath() + File.separator + "spash"
            + File.separator;
    public String spashImageFile;

    public DownLoadImageBitmapHelper _downLoadImagHelper;

    private MediaItem startAdware;


    /**
     * 图片压缩备份完成接口
     */
    public interface OnDataComplateInitLister {
        public void getData(ArrayList<MediaModel> list);
    }

    private static PictureManager instance = new PictureManager();

    public ArrayList<MediaModel> getmDllist() {
        return mDllist;
    }

    public String getSignlPath() {
        String path = "";
        if (mDllist != null && mDllist.size() > 0) path = mDllist.get(0).url;
        return path;
    }

    private ArrayList<MediaModel> mDllist;

    public void setmDllist(ArrayList<MediaModel> mDllist) {
        this.mDllist = mDllist;
    }

    public static PictureManager getInstance() {
        return instance;
    }

    public void setCallback(OnDataComplateInitLister callback) {
        this.callback = callback;
    }

    OnDataComplateInitLister callback;

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     */
    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();//递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }        // 目录此时为空，可以删除
        return dir.delete();
    }

    public void setUploadResult(boolean isOk, String err) {
        isUpSuccess = isOk;
        errMsg = err;
    }

    public void clearCache() {
        isUpSuccess = true;
        errMsg = "";
        mDllist=null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = new File(saveDirTemp);
                if (dir.isDirectory()) {
                    String[] children = dir.list();//递归删除目录中的子目录下
                    for (int i = 0; i < children.length; i++) {
                        boolean success = deleteDir(new File(dir, children[i]));
                        if (!success) {
                        }
                    }
                }        // 目录此时为空，可以删除
                dir.delete();
            }
        }).start();
    }

    public void compressionSignImage(String path) {
        callback = null;
        String filename = UtilHelp.getNameFromUrl(path);
        MediaModel model = new MediaModel(filename, path);
        ArrayList<MediaModel> list = new ArrayList<MediaModel>();
        list.add(model);
        compressionImage(list);
    }

    /**
     * 上传图片压缩图片
     */
    public void compressionImage(ArrayList<MediaModel> itemList) {
        File savePath = new File(saveDirTemp);
        if (savePath.exists()) {
            deleteDir(savePath);
        }
        savePath.mkdirs();
        ArrayList<MediaModel> map = new ArrayList<MediaModel>();
        for (int i = 0; i < itemList.size(); i++) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(itemList.get(i).url, opt);
            int bitWidth = opt.outWidth;
            int bitHeight = opt.outHeight;
            int be = 1;
            if (bitWidth > 1024 || bitHeight > 1024) {
                if (bitWidth < bitHeight) {
                    // 高度大于宽度
                    be = bitHeight / 1024;
                } else {
                    // 宽度大于高度
                    be = bitWidth / 1024;
                }
            }
            opt.inJustDecodeBounds = false;
            opt.inSampleSize = be;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                bitmap = BitmapFactory.decodeFile(itemList.get(i).url, opt);
                if (bitmap == null) break;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            } catch (OutOfMemoryError e) {
                opt.inSampleSize = be + 2;
                bitmap = BitmapFactory.decodeFile(itemList.get(i).url, opt);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            }
//   InputStream is = new ByteArrayInputStream(baos.toByteArray());
            String im = saveDirTemp + i + ".jpg";
            File newFile = new File(saveDirTemp, i + ".jpg");//path,i+".png"
            //     newFile.delete();
            if (!newFile.exists()) {
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileOutputStream newimage = new FileOutputStream(im);
                newimage.write(baos.toByteArray());
                newimage.close();
//                map.put("img"+i,newFile);
                MediaModel mode = new MediaModel(itemList.get(i).name, im);
                map.add(mode);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (map == null || map.isEmpty()) map = itemList;
        if (callback != null) {
            callback.getData(map);
        }
        setmDllist(map);
//        callback.getData(map);
//        setmDllist(map);
    }

    public boolean isUpSuccess() {
        return isUpSuccess;
    }

    public void setIsUpSuccess(boolean isUpSuccess) {
        this.isUpSuccess = isUpSuccess;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getDoadSprashImage() {
        return saveSprashDirTemp + "splash_welcome.jpg";
    }

    public void saveSplashImageToSdCard2() {
        if (spashImageFile == null || TextUtils.isEmpty(spashImageFile)) return;
        deleteDir(new File(getDoadSprashImage()));
        boolean success = false;
        String imageName = "splash_welcome.jpg";
//        File storeDir = App.getInstance().getBaseContext().getExternalFilesDir(null);
        final File saveFilePath = new File(saveSprashDirTemp, imageName);
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Looper.prepare();
                try {
                    Bitmap saveTempBitmap = Glide.
                            with(App.getInstance().getBaseContext()).
                            load(spashImageFile).
                            asBitmap().
                            into(-1, -1).
                            get();
                    saveBitmapToFile(saveTempBitmap, saveFilePath.toString());
                    return true;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                if (isSuccess) {
                    spashImageFile = "";
                    //      saveSplashPath(saveFilePath.toString());
//                    App.getInstance().getBaseContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + saveFilePath.toString())));
                }
//                Toast.makeText(App.getInstance().getBaseContext(), "图片保存到 " + saveFilePath.toString(), Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public void saveToLocal(final String path) {
        spashImageFile = path;
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveSplashImageToSdCard2();
            }
        }).start();
    }

    public void saveSplashPath(String msg) {
        SharedPreferences sharedPreferences = App.getInstance().getBaseContext()
                .getSharedPreferences(SPLASH_DATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SPLASH_PATH, msg);
        editor.commit();
    }

//    public static boolean isSplashImageChange() {
//        SharedPreferences sharedPreferences = App.getInstance().getBaseContext()
//                .getSharedPreferences(SPLASH_DATE, Context.MODE_PRIVATE);
//        boolean change = sharedPreferences.getBoolean(SPLASH_TAG, false);
//        return change;
//    }

    public String getSplashImagePath() {
        SharedPreferences sharedPreferences = App.getInstance().getBaseContext()
                .getSharedPreferences(SPLASH_DATE, Context.MODE_PRIVATE);
        String path = sharedPreferences.getString(SPLASH_PATH, "");
        return path;
    }

    public void openOrAd(boolean flag) {
        SharedPreferences sharedPreferences = App.getInstance().getBaseContext()
                .getSharedPreferences(SPLASH_VALIDATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SPLASH_VALIDATE, flag);
        editor.commit();
    }

    public boolean getAdValaidate() {
        SharedPreferences sharedPreferences = App.getInstance().getBaseContext()
                .getSharedPreferences(SPLASH_VALIDATE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SPLASH_VALIDATE, true);
    }


    public void saveBitmapToFile(Bitmap bitmap, String filename) {
        FileOutputStream fos = null;
        File file = new File(filename);
        if (file.exists()) {
            return;
        }
        if (!file.getParentFile().exists()) {
            // 如果目标文件所在的目录不存在，则创建父目录
            if (!file.getParentFile().mkdirs()) {
                return;
            }
        }
        try {
            fos = new FileOutputStream(file);
            fos.write(bitmap2byte(bitmap));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] bitmap2byte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private static final String SPLASH_DATE = "SPLASH_DATE.vlaues";
    private static final String SPLASH_VALIDATE = "SPLASH_DATE.VALIDATE";
    private static final String SPLASH_PATH = "SPLASH_DATA_PATH.vlaues";

    /***
     * 不加载缓存图片
     * @param filePath
     * @param images
     */
    public void LoadImageWithOutCache(Context context,String filePath, ImageView images) {
        Glide.with(context)
                .load(filePath)
//                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.NONE)//图片缓存策略,这个一般必须有
//                    .C()//对图片进行裁剪
                .skipMemoryCache(true)
                .crossFade()//让图片显示的时候带有淡出的效果
                .into(images);
    }

    public MediaItem getStartAdware() {
        return startAdware;
    }

    public void setStartAdware(MediaItem startAdware) {
        this.startAdware = startAdware;
    }

    public MediaItem getPopAdsware() {
        return popAdsware;
    }

    public void setPopAdsware(MediaItem popAdsware) {
        this.popAdsware = popAdsware;
    }

    public MediaItem popAdsware;

    public boolean isIsshowAd() {
        return isshowAd;
    }

    public void setIsshowAd(boolean isshowAd) {
        this.isshowAd = isshowAd;
    }

    boolean isshowAd = false;
    String adPath = "";

    public String getAdPath() {
        return adPath;
    }

    public void setAdPath(String adPath) {
        this.adPath = adPath;
    }


}
