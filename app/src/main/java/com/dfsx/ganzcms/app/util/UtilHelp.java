package com.dfsx.ganzcms.app.util;

/**
 * Created by heyang on 2015/3/27.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.business.LanguageUtil;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.img.ImageUtil;
import com.dfsx.core.network.datarequest.GetTokenManager;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.AdsEntry;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UtilHelp {

    public final static int NO_LOGON = 23;
    public final static int TOAST_MSG = 45;

    public static int SELECTED_VIDEO_SIZE_IN_MB = 15;  //视频大小 20M

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年


//    public static final String KEY_USER_NAME = "username";
//    public static final String KEY_PASSWORD = "password";
//    public static final String KEY_IS_SAVE_ACCOUNT = "isSaveAccount";
//    public static final String KEY_IS_TP_ACCOUNT_SAVED = "isTPAccountSaved";
//    public static final String KEY_ACCESS_TOKEN = "token";
//    public static final String KEY_OPEN_ID = "openid";
//    public static final String KEY_TP_TYPE = "type";
//    public static final String KEY_TP_APP_ID = "appid";
//    public static final String KEY_ACCOUNT_INFO = "Account_Info";

    public static boolean COMMANDE_IS_OPEN = false;

    public static String PUBLIC_PATH = Environment.getExternalStorageDirectory() + "/DCIM/axcms/";
    public static String PUBLIC_RECORD_VIDEO_PATH = Environment.getExternalStorageDirectory() + File.separator + "leshan/video/";

    public static final int PAGE_COUNTER = 10;

    public static String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }

    /**
     * heyang
     * 自定义文本信息
     * 显示弹窗 确定、取消
     */
    public static AlertDialog.Builder getConfirmDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton("确定", onClickListener);
        builder.setNegativeButton("取消", null);
        return builder;
    }

    public static AlertDialog.Builder getDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder;
    }

    /**
     * 检查token 是否有效
     */
    public static String checkTokenError(JSONObject obj) {
        String token = null;
        int code = obj.optInt("code");
        if (code == 401) {
            token = GetTokenManager.getInstance().getIGetToken().getTokenSync();
        }
        return token;
    }

    public static void setTimeDate(TextView dateView, long tm) {
        if (LanguageUtil.isTibetanLanguage(App.getInstance().getApplicationContext())) {
            dateView.setText(Util.getTimeString("yyyy-MM-dd HH:mm", tm));
        } else {
            dateView.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", tm * 1000));
        }
    }

    public static void setViewCount(TextView viewView, long viewCount) {
        viewView.setVisibility(View.GONE);
//        viewView.setText(viewCount + App.getInstance().getApplicationContext().getResources().getString(R.string.news_item_viewcount_hit));
        viewView.setText(App.getInstance().getApplicationContext().getResources().getString(R.string.news_item_viewcount_hit) + viewCount);
    }

    /*
     *   获取时间差
     */
    public static String getTimeFormatText(String strformat, long time) {
        long diff = new Date().getTime() - time;
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return getTimeString("yyyy-MM-dd HH:mm", time / 1000);
//            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
//            return r + "个月前";
            return getTimeString("MM-dd HH:mm", time / 1000);
        }
        if (diff > day) {
            r = (diff / day);
            if (r >= 7) {
                return getTimeString("MM-dd HH:mm", time / 1000);
            } else
                return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                //如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /*
    *
    *  得到文件路径 的后缀
     */
    public static String getSubffixFromPath(String paht) {
        String result = "";
        if (paht != null && !TextUtils.isEmpty(paht)) {
            int index = paht.lastIndexOf(".");
            if (index != -1) {
                result = paht.substring(index);
            }
        }
        return result;
    }

    /**
     * 对上传的内容编码
     *
     * @param strformat
     * @return
     */
    public static String setEncoderString(String strformat) {
        String content = strformat;
//        try {
//            content = URLEncoder.encode(content, "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return content;
    }

    /**
     * 对服务器的解码
     *
     * @param strformat
     * @return
     */
    public static String getDecondeString(String strformat) {
        String content = strformat;
//        try {
//            content = URLDecoder.decode(content, "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return content;
    }

    // 网络连接失败返回错误 json
    public static void checkError(JSONObject object) throws ApiException {
        if (object == null) {
            throw new ApiException("连接服务器失败");
        }
        String eCode = object.optString("error");
        if (!eCode.isEmpty()) {
            String eMsg = object.optString("message");
            throw new ApiException(eCode + ":" + eMsg);
        }
    }

    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
//                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String getNameFromUrl(String path) {
        String result = "";
        if (!("").equals(path)) {
            int index = path.lastIndexOf("/");
            if (index != -1) {
                result = path.substring(index + 1);
            }
        }
        return result;
    }

    //heyang  201-8-6  删除文件
    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
//            file.delete();
        } else {
//            Log.i("文件不存在！" + "\n");
        }
    }

    public static void applyKitKatTranslucency(Activity activity, int clr) {

        // KitKat translucent navigation/status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }
        SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setTintColor(clr);

    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static String getContentProviderAsVideo(Context c, String filname) {
        // TODO Auto-generated method stub
        String result = "";
        String[] projection = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.LATITUDE,
                MediaStore.Video.Media.LONGITUDE};
//        String orderBy = MediaStore.Video.Media.DISPLAY_NAME;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        List<HashMap<String, String>> listImage = new ArrayList<HashMap<String, String>>();
        Cursor cursor = c.getContentResolver().query(uri, projection, MediaStore.Audio.Media.DISPLAY_NAME + "=?",
                new String[]{filname}, null);
//        Cursor cursor = c.getContentResolver().query(uri, projection, null, null, null);
        if (null == cursor) {
            return result;
        }
        result = getVideoColumnData(cursor);
        return result;
    }

    private static String getVideoColumnData(Cursor cur) {
        String str = "";
        if (cur.moveToFirst()) {
            int _id;
            String name;
            double laf;
            double longf;

            int _idColumn = cur.getColumnIndex(MediaStore.Video.Media._ID);
            int nameColumn = cur.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
            int lafColumn = cur.getColumnIndex(MediaStore.Video.Media.LATITUDE);
            int longfColumn = cur.getColumnIndex(MediaStore.Video.Media.LONGITUDE);
            do {
                // Get the field values
                _id = cur.getInt(_idColumn);
                name = cur.getString(nameColumn);
                laf = cur.getDouble(lafColumn);
                longf = cur.getDouble(longfColumn);
                str = laf + "," + longf;
//                Log.i("cdcsd", _id + " album:" + nameColumn + " lafColumn:" + lafColumn
//                        + "longfColumn: " + longfColumn + "");
                break;
            } while (cur.moveToNext());
        }
        return str;
    }

    public static fileEntry imgToBase64(String imgPath, Bitmap bitmap, Context c) {
        if (imgPath != null && imgPath.length() > 0) {
            bitmap = readBitmap(imgPath, c);
        }
        if (bitmap == null) {
            //bitmap not found!!
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();

            byte[] imgBytes = out.toByteArray();
            int len = imgBytes.length;
            String s = Base64.encodeToString(imgBytes, Base64.DEFAULT);
            fileEntry file = new fileEntry(s, len);
            return file;
//            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // 加载本地的文件
    private static Bitmap readBitmap(String imgPath, Context context) {
        try {
//            DisplayImageOptions options = new DisplayImageOptions.Builder()
//                    .cacheInMemory(false)
//                    .cacheOnDisk(true)
//                    .bitmapConfig(Bitmap.Config.RGB_565)
//                    .build();
//            String imagUrl = ImageDownloader.Scheme.FILE.wrap(imgPath);
//            Bitmap  bmp= ImageLoader.getInstance().loadImageSync(imagUrl, options);


            String url = "file://" + imgPath;
            try {
                return BitmapFactory.decodeFile(imgPath);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                return null;
            }


//            BitmapFactory.Options newOpts = new BitmapFactory.Options();
//            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
//            newOpts.inJustDecodeBounds = true;
//            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);//此时返回bm为空
//
//            newOpts.inJustDecodeBounds = false;
//            int w = newOpts.outWidth;
//            int h = newOpts.outHeight;
//            //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//            float hh = 800f;//这里设置高度为800f
//            float ww = 480f;//这里设置宽度为480f
//            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//            int be = 1;//be=1表示不缩放
//            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
//                be = (int) (newOpts.outWidth / ww);
//            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//                be = (int) (newOpts.outHeight / hh);
//            }
//            if (be <= 0)
//                be = 1;
//            newOpts.inSampleSize = 2;//设置缩放比例
//            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//            bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
//            return bitmap;


//            final ImageView view = new ImageView(context);
//            Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>(250, 250) {
//                @Override
//                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    view.setImageBitmap(resource);
//                }
//            });
//            return view.getDrawingCache();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static File getUriFile(Context context, Uri uri) {
        String path = getUriPath(context, uri);
        if (path == null) {
            return null;
        }
        return new File(path);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getUriPath(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if ("com.google.android.apps.photos.content".equals(uri.getAuthority())) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    public static String getImagePath(String filepath_src) {
        String filepath = filepath_src.toLowerCase();

        int nStartIndex = filepath.indexOf("src=");
        int nEndIndex = filepath.indexOf(".jpg");
        String fileuri = "";
        if (nEndIndex == -1) {
            nEndIndex = filepath.indexOf(".png");
            if (nEndIndex > nStartIndex)
                fileuri = filepath_src.substring(nStartIndex + 5, nEndIndex + 4);
            if (nEndIndex == -1) {
                nEndIndex = filepath.indexOf(".jpeg");
                if (nEndIndex > nStartIndex)
                    fileuri = filepath_src.substring(nStartIndex + 5, nEndIndex + 5);
            }
        } else {
            if (nEndIndex > nStartIndex)
                fileuri = filepath_src.substring(nStartIndex + 5, nEndIndex + 4);
        }
        return fileuri;
    }

    public static int getImageWidth(String filepath) {
        int nStartIndex = filepath.indexOf("width=");
        int nEndIndex = filepath.indexOf("height");
        String fileuri = "";
        if (nEndIndex > nStartIndex)
            fileuri = filepath.substring(nStartIndex + 7, nEndIndex - 2);
        int width = Integer.parseInt(fileuri);
        return width;
    }

    public static int getImageHeight(String filepath) {
        int nStartIndex = filepath.indexOf("height=");
        int nEndIndex = filepath.indexOf("alt");
        String fileuri = "";
        if (nEndIndex > nStartIndex)
            fileuri = filepath.substring(nStartIndex + 8, nEndIndex - 2);
        int height = Integer.parseInt(fileuri);
        return height;
    }


    public static JSONObject getJsonObject(String key, Object value) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (key != "" && value != "")
                jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static fileEntry imgToBase64(String imgPath, Bitmap bitmap, int nSize) {
        if (imgPath != null && imgPath.length() > 0) {
            bitmap = ImageUtil.getSmallBitmap(imgPath);
        }
        if (bitmap == null) {
            //bitmap not found!!
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            nSize= out.size();

            out.flush();
            out.close();

            byte[] imgBytes = out.toByteArray();
            int len = imgBytes.length;
            String s = Base64.encodeToString(imgBytes, Base64.DEFAULT);
            fileEntry file = new fileEntry(s, len);
            return file;
//            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void LoadImageFormUrl(final ImageView img, String imageUrl, final ProgressBar spinner) {
//        DisplayImageOptions options = UtilHelp.GetDisplayImageOptions();
//        ImageLoader.getInstance().displayImage(imageUrl, img, options, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                if (spinner != null)
//                    spinner.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                String message = null;
//                switch (failReason.getType()) {
//                    case IO_ERROR:
//                        message = "Input/Output error";
//                        break;
//                    case DECODING_ERROR:
//                        message = "Image can't be decoded";
//                        break;
//                    case NETWORK_DENIED:
//                        message = "Downloads are denied";
//                        break;
//                    case OUT_OF_MEMORY:
//                        message = "Out Of Memory error";
//                        break;
//                    case UNKNOWN:
//                        message = "Unknown error";
//                        break;
//                }
//                if(spinner!=null)
//                    spinner.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if(spinner!=null) spinner.setVisibility(View.GONE);
//            }
//        });

        if (spinner != null) {
            spinner.setVisibility(View.VISIBLE);
        }
        GlideImgManager.getInstance().showImg(img.getContext(), img, imageUrl,
                new RequestListener<String, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                        if (spinner != null) {
                            spinner.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                        if (spinner != null) {
                            spinner.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
    }

    /*
 *  编辑框 显示或隐藏输入法
 */
    public static void onFocusChange(final boolean hasFocus, final EditText edt) {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFocus) {
                    // 显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    // 隐藏输入法
                    imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    /**
     * 加载失败设置默认错误图片
     */
    public static void LoadImageErrorUrl(final ImageView img, String imageUrl, final ProgressBar spinner, int errolimg) {
        if (spinner != null) {
            spinner.setVisibility(View.VISIBLE);
        }
        GlideImgManager.getInstance().showImg(img.getContext(), img, imageUrl, errolimg,
                new RequestListener<String, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                        if (spinner != null) {
                            spinner.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                        if (spinner != null) {
                            spinner.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
    }

    public static String checkUrl(String urlStr) {
        int index = urlStr.indexOf("\"");
        if (index != -1)
            urlStr = urlStr.substring(index + 1);
        index = urlStr.lastIndexOf("\"");
        if (index != -1)
            urlStr = urlStr.substring(0, index);
        return urlStr;
    }

    public static void LoadNewsThumebImage(final ImageView img, String imageUrl, final ProgressBar spinner) {

//        DisplayImageOptions options = UtilHelp.GetDisplayImageOptions();
//        ImageLoader.getInstance().displayImage(imageUrl, img, options, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                if (spinner != null)
//                    spinner.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                String message = null;
//                switch (failReason.getType()) {
//                    case IO_ERROR:
//                        message = "Input/Output error";
//                        break;
//                    case DECODING_ERROR:
//                        message = "Image can't be decoded";
//                        break;
//                    case NETWORK_DENIED:
//                        message = "Downloads are denied";
//                        break;
//                    case OUT_OF_MEMORY:
//                        message = "Out Of Memory error";
//                        break;
//                    case UNKNOWN:
//                        message = "Unknown error";
//                        break;
//                }
//                if(spinner!=null)
//                    spinner.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if(spinner!=null) spinner.setVisibility(View.GONE);
//            }
//        });

        if (spinner != null) {
            spinner.setVisibility(View.VISIBLE);
        }
        GlideImgManager.getInstance().showImg(img.getContext(), img, imageUrl,
                new RequestListener<String, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                        if (spinner != null) {
                            spinner.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                        if (spinner != null) {
                            spinner.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
    }


    public static String GetSimpleDate(Long ctime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//制定日期的显示格式
        return sdf.format(new Date(ctime * 1000L));
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }


    //added by hazg 20150520
    public static String getMetaData(Context context, String metaKey) {
        Bundle metaData = null;
        String result = null;

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                result = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * @return
     */
    public static class fileEntry {
        public String str;
        public int nsize;


        public fileEntry(String ucode, int size) {
            this.str = ucode;
            this.nsize = size;
        }
    }

    public static long ChekcMediaFileSize(File mediaFile, boolean isVideo) {

        /** Get length of file in bytes */
        long fileSizeInBytes = mediaFile.length();

        /** Convert the bytes to Kilobytes (1 KB = 1024 Bytes) */
        long fileSizeInKB = fileSizeInBytes / 1024;

        /** Convert the KB to MegaBytes (1 MB = 1024 KBytes) */
        long fileSizeInMB = fileSizeInKB / 1024;

        int requireSize = 0;
        if (isVideo) {
            requireSize = UtilHelp.SELECTED_VIDEO_SIZE_IN_MB;
        } else {
//            requireSize = UtilHelp.SELECTED_IMAGE_SIZE_IN_MB;
        }
        if (fileSizeInMB <= requireSize) {
            return 0;
        }
        return fileSizeInMB;
    }

    //视频
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }


    /**
     * @param base64Data
     * @param imgName
     * @param imgFormat  图片格式
     */
    public static void base64ToBitmap(String base64Data, String imgName, String imgFormat) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        File myCaptureFile = new File("/sdcard/", imgName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myCaptureFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boolean isTu = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        if (isTu) {
            // fos.notifyAll();
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static Drawable resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return new BitmapDrawable(resizedBitmap);
    }

    //2015-7-16

    /**
     * 获取屏幕的宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * 获取屏幕中控件顶部位置的高度--即控件顶部的Y点
     *
     * @return
     */
    public static int getScreenViewTopHeight(View view) {
        return view.getTop();
    }

    /**
     * 获取屏幕中控件底部位置的高度--即控件底部的Y点
     *
     * @return
     */
    public static int getScreenViewBottomHeight(View view) {
        return view.getBottom();
    }

    /**
     * 获取屏幕中控件左侧的位置--即控件左侧的X点
     *
     * @return
     */
    public static int getScreenViewLeftHeight(View view) {
        return view.getLeft();
    }

    /**
     * 获取屏幕中控件右侧的位置--即控件右侧的X点
     *
     * @return
     */
    public static int getScreenViewRightHeight(View view) {
        return view.getRight();
    }

    /////////////////////////////
    public static String SDPATH = Environment.getExternalStorageDirectory()
            + "/Photos/";

    public static void saveBitmap(Bitmap bm, String picName) {
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            File f = new File(SDPATH, picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }

    public static void delFile(String fileName) {
        File file = new File(SDPATH + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir();
        }
        dir.delete();
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    public static Drawable loadImageFromUrl(String url) throws IOException {
        try {
            URL m = new URL(url);
            InputStream i = (InputStream) m.getContent();
            Drawable d = Drawable.createFromStream(i, "src");
            return d;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public int getDateDays (String date1, String date2)
//    {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
//        try {
//            Date date = sdf.parse(date1);// 通过日期格式的parse()方法将字符串转换成日期              Date dateBegin = sdf.parse(date2);
//            long betweenTime = date.getTime() - dateBegin.getTime();
//            betweenTime  = betweenTime  / 1000 / 60 / 60 / 24;
//        } catch(Exception e)
//        {
//        }
//        return (int)betweenTime;
//    }


    public static long dateDiff(String startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String endTime = sdf.format(new Date());

        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff = 0;
        long day = 0;
        // 获得两个时间的毫秒时间差异
        try {
            diff = sd.parse(endTime).getTime()
                    - sd.parse(startTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day = diff / nd;// 计算差多少天
        long hour = diff % nd / nh;// 计算差多少小时
        long min = diff % nd % nh / nm;// 计算差多少分钟
        long sec = diff % nd % nh % nm / ns;// 计算差多少秒
        // 输出结果
        System.out.println("时间相差：" + day + "天" + hour + "小时" + min
                + "分钟" + sec + "秒。");
        if (day >= 1) {
            return day;
        } else {
            if (day == 0) {
                return 0;
            } else {
                return 0;
            }

        }
    }

    public static String getCompressImage(String path) {
        FileOutputStream b = null;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        //不能直接保存在系统相册位置
        File file = new File(path);
        int index = path.lastIndexOf('/');
        String name = path.substring(index + 1);
        String fileName = path + "." + name;
        try {
            b = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件,100  不压缩
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }


    public static String getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = 2;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        if (bitmap == null) return "";
        return compressImage(bitmap, srcPath);//压缩好比例大小后再进行质量压缩
    }

    public static String compressImage(Bitmap image, String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int degree = getPicRotate(path);
        if (degree != 0) {
            Matrix m = new Matrix();
            int width = image.getWidth();
            int height = image.getHeight();
            m.setRotate(90); // 旋转angle度
            image = Bitmap.createBitmap(image, 0, 0, width, height, m, true);// 从新生成图
        }
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        FileOutputStream fout = null;
        int index = path.lastIndexOf('/');
        String name = path.substring(index + 1);
        String fileName = path + "." + name;
        try {
            fout = new FileOutputStream(path);
            try {
                fout.write(baos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static void compressBmpToFile(Bitmap bmp, String file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;//个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getPicRotate(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    public static String SceneList2String(List SceneList)
            throws IOException {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(SceneList);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String SceneListString = new String(Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭objectOutputStream
        objectOutputStream.close();
        return SceneListString;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Integer> String2SceneList(String SceneListString)
            throws StreamCorruptedException, IOException,
            ClassNotFoundException {
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        ArrayList<Integer> SceneList = (ArrayList<Integer>) objectInputStream
                .readObject();
        objectInputStream.close();
        return SceneList;
    }

    public static String parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text = "";
        String imgPath = "";
        String content = "";
//        ArrayList<String>  alist=null;
//        alist=new ArrayList<String>();
        try {
            myParser.nextTag();
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        String name = myParser.getName();
                        if (myParser.getName().equals("div")) {
                            String temperature = myParser.getAttributeValue(null, "class");
                        }
                        break;

                    case XmlPullParser.START_TAG:
                        name = myParser.getName();
//                        classtag = myParser.getAttributeValue(null, "class");
                        if (name.equals("img")) {
                            imgPath = myParser.getAttributeValue(null, "src");
//                            alive.thumb = text;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText().toString().trim();
                        if (!("\n").equals(text)) {
                            content = text;
                        }
                        break;

                    case XmlPullParser.END_TAG:
//                        name = myParser.getName();
//                        if (name.equals("country")) {
//                            String country = text;
//                        }
                        break;
                }
                event = myParser.next();
                if (imgPath.endsWith(".jpg") && !("").equals(content)) {
                    return imgPath + "," + content;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getTimeString(String format, long db) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);//制定日期的显示格式    "yyyy-MM-dd"
        return sdf.format(new Date(db * 1000L));
    }

    //取json数组中的第一个数据
    public static String getStringByName(String var, JSONObject jsobj) {
        JSONObject obj = jsobj.optJSONObject(var);
        if (obj != null) {
            JSONArray result = obj.optJSONArray("und");
            if (result != null && result.length() > 0) {
                try {
                    return ((JSONObject) result.get(0)).getString("value");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static String getVideoUrlByName(String var, JSONObject jsobj) {
        JSONObject obj = jsobj.optJSONObject(var);
        if (obj != null) {
            JSONArray result = obj.optJSONArray("und");
            if (result != null && result.length() > 0) {
                try {
                    return ((JSONObject) result.get(0)).getString("uri");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }


    public static String getSimpleDt(String strDate) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat ft = new SimpleDateFormat("MM月dd日");
        String str = "";
        try {
            Date dt = sd.parse(strDate);
            str = ft.format(dt);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return str;
    }

    public static String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;


        int minutes = (totalSeconds / 60) % 60;


        int hours = totalSeconds / 3600;

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
//            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
            return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
        }
    }

    public static String getStringByName(String var, String favel, JSONObject jsobj) {
        JSONObject obj = jsobj.optJSONObject(var);
        if (obj != null) {
            JSONArray result = obj.optJSONArray("und");
            if (result != null && result.length() > 0) {
                try {
                    return ((JSONObject) result.get(0)).getString(favel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static String durtionoString(int coins) {
        String coinsDescribeText;
        if (coins >= 10) {
            coinsDescribeText = String.format("%d", coins) + "秒";
        } else {
            coinsDescribeText = String.format("%2d", coins) + "秒";
        }
        return coinsDescribeText;
    }

    private static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static SpannableString createAdwareTime(Context context, int len) {
        String strText = "关闭广告";
        int index = 0;
        if (len > 0) {
            strText = durtionoString(len) + "\b后关闭广告";
            index = strText.indexOf("\b");
        }
        SpannableString sb = new SpannableString(strText);
        if (index > 0) {
            sb.setSpan(new AbsoluteSizeSpan(sp2px(context, 14)), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new AbsoluteSizeSpan(sp2px(context, 12)), index, strText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffffff")), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#80ffffff")), index, strText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            index = strText.length();
            sb.setSpan(new AbsoluteSizeSpan(sp2px(context, 12)), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#80ffffff")), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sb;
    }

    // 解析视频多条地址
    public static void parseVideoVersions(JSONObject jsonObject, AdsEntry.AdItem adItem, Gson gs) {
        JSONObject video = jsonObject.optJSONObject("video");
        if (video == null) return;
        JSONArray varr = video.optJSONArray("versions");
        if (!(varr == null || varr.length() == 0)) {
            AdsEntry.VideoAdItem bean = new AdsEntry.VideoAdItem();
            bean.setDuration(video.optInt("duration"));
            Map<String, AdsEntry.VideoVersion> verlist = new HashMap<>();
            AdsEntry.VideoVersion srcTag = null;
            for (int k = 0; k < varr.length(); k++) {
                JSONObject itemds = null;
                try {
                    itemds = (JSONObject) varr.get(k);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AdsEntry.VideoVersion entrys = gs.fromJson(itemds.toString(), AdsEntry.VideoVersion.class);
                String urlPath = entrys.getUrl().toLowerCase().toString();
                String dup = FileUtil.getSuffix(urlPath);
                if (entrys != null) {
                    if (TextUtils.equals(entrys.getName(), "源版本"))
                        srcTag = entrys;
                    verlist.put(dup, entrys);
                }
            }
            if (!(verlist == null || verlist.isEmpty())) {
                AdsEntry.VideoVersion entrytag = verlist.get(".mp4");
                if (entrytag != null) {
                    String name = entrytag.getName();  //待判断的字符串
                    String reg = ".*源版本.*";  //判断字符串中是否含有特定字符串ll
                    if (!name.matches(reg)) {
                        bean.setVersions(entrytag);
                    }
                }
                if (bean.getVersions() == null) {
                    AdsEntry.VideoVersion entryu8 = verlist.get(".m3u8");
                    if (entryu8 != null) {
                        bean.setVersions(entryu8);
                    } else {
                        if (srcTag != null)
                            bean.setVersions(srcTag);
                    }
                }
            }
            adItem.setVideoAdItem(bean);
        }
    }
    /**
     *
     * @param time  秒
     * @return
     */
    public static String getFormatMinute(long time){
        StringBuilder stringBuilder = new StringBuilder();
        long minute = time/60;
        long sec = time%60;
        stringBuilder.append(minute < 10 ? "0"+ minute : minute);
        stringBuilder.append(":").append(sec < 10 ? "0" + sec : sec);
        return stringBuilder.toString();
    }
}


