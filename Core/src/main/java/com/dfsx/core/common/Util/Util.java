package com.dfsx.core.common.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dfsx.core.R;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.img.GlideImgManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2014/12/16.
 */
public class Util {

    public static final String KEY_ACCOUNT_INFO = "key_account_info";
    public static final String KEY_IS_SAVE_ACCOUNT = "key_save_account";
    public static final String KEY_USER_NAME = "key_user_account";
    public static final String KEY_PASSWORD = "key_name_account";
    public static String PUBLIC_VIDEO_PATH = Environment.getExternalStorageDirectory() + "/MOVIE";


    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");


    /**
     * Apply KitKat specific translucency.
     */
    public static SystemBarTintManager applyKitKatTranslucency(Activity activity, int clr) {

        // KitKat translucent navigation/status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }
        SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setTintColor(clr);
        return mTintManager;
    }

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    //查询版本号 是不是6.0
    public static boolean isMMc() {
        return Build.VERSION.SDK_INT >= 23;
    }

    /**
     *   读取assert文件夹下的文件
     */
    public static String readAssertResource(Context context, String strAssertFileName) {
        AssetManager assetManager = context.getAssets();
        String strResponse = "";
        try {
            InputStream ims = assetManager.open(strAssertFileName);
            strResponse = getStringFromInputStream(ims);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResponse;
    }

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
     * 加载失败设置默认错误图片  heynag
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


    private static String getStringFromInputStream(InputStream a_is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(a_is));
            while ((line = br.readLine()) != null) {
                line+="\n";
                sb.append(line);
            }
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, boolean on) {
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

    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static float px2dp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return px / density;
    }

    public static Date stringToDate(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }

    public static long stringToTimestamp(String str) {
        return stringToDate(str).getTime();
    }

    public static String dateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String dateToString(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static String timestampToString(long timestamp) {
        return dateToString(new Date(timestamp));
    }

    public static String timestampToString(long timestamp, String format) {
        return dateToString(new Date(timestamp), format);
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
        fileuri = fileuri.replace("\\", "");
        fileuri = fileuri.replace("\"", "");
        return fileuri;
    }

    //   判断Json的字段是[]  返回空
    public static String getStringIsNull(String str) {
        String result = "";
        if (str != null && !str.equals("")) {
            if (str.indexOf("[") == -1) result = str;
        }
        return result;
    }

    public static String getTimeString(String format, long db) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);//制定日期的显示格式    "yyyy-MM-dd"
        return sdf.format(new Date(db * 1000L));
    }

    public static long stringToLong(String strTime, String formatType) {
        Date date = null; // String类型转成date类型
        try {
            date = stringToDate(strTime, formatType);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    // 网络连接失败返回错误 json
//    public static void checkError(JSONObject object) throws ApiException {
//        if (object == null)
//            throw new ApiException(App.getInstance().getmSession().getContext().getResources().getString(R.string.neterror));
//        String eCode = object.optString("error");
//        if (!eCode.isEmpty()) {
//            String eMsg = object.optString("message");
//            String value = eMsg;
//            value = decodeUnicode(eMsg);
//            throw new ApiException(eCode + ":" + value);
//        }
//    }

    /**
     * 解码 Unicode \\uXXXX
     *
     * @return
     */
//    public static String decodeUnicode(String str) {
//        Charset set = Charset.forName("UTF-16");
//        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
//        Matcher m = p.matcher(str);
//        int start = 0;
//        int start2 = 0;
//        StringBuffer sb = new StringBuffer();
//        while (m.find(start)) {
//            start2 = m.start();
//            if (start2 > start) {
//                String seg = str.substring(start, start2);
//                sb.append(seg);
//            }
//            String code = m.group(1);
//            int i = Integer.valueOf(code, 16);
//            byte[] bb = new byte[4];
//            bb[0] = (byte) ((i >> 8) & 0xFF);
//            bb[1] = (byte) (i & 0xFF);
//            ByteBuffer b = ByteBuffer.wrap(bb);
//            sb.append(String.valueOf(set.decode(b)).trim());
//            start = m.end();
//        }
//        start2 = str.length();
//        if (start2 > start) {
//            String seg = str.substring(start, start2);
//            sb.append(seg);
//        }
//        return sb.toString();
//    }
    public static void LoadThumebImage(final ImageView img, String imageUrl, final ProgressBar spinner) {
        LoadImagGif(img.getContext(), imageUrl, img);
    }


    public static String timespanToString(long span) {
        long seconds = span / 1000;
        long milliseconds = span % 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;

        return String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
    }

    // PAL (1/25)
    public static String frameToString(long duration, int num, int den) {
        long sec = duration * num / den;
        long h = sec / 60 / 60;
        long m = (sec / 60) % 60;
        long s = sec % 60;
        long f = duration - sec * den / num;
        return String.format("%1$02d:%2$02d:%3$02d:%4$02d", h, m, s, f);
    }

    // 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
    public static boolean hasBind(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String flag = sp.getString("bind_flag", "");
        if ("ok".equalsIgnoreCase(flag)) {
            return true;
        }
        return false;
    }

    public static void setBind(Context context, boolean flag) {
        String flagStr = "not";
        if (flag) {
            flagStr = "ok";
        }
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("bind_flag", flagStr);
        editor.commit();
    }

    public static List<String> getTagsList(String originalText) {
        if (originalText == null || originalText.equals("")) {
            return null;
        }
        List<String> tags = new ArrayList<String>();
        int indexOfComma = originalText.indexOf(',');
        String tag;
        while (indexOfComma != -1) {
            tag = originalText.substring(0, indexOfComma);
            tags.add(tag);

            originalText = originalText.substring(indexOfComma + 1);
            indexOfComma = originalText.indexOf(',');
        }

        tags.add(originalText);
        return tags;
    }

    public static String getLogText(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString("log_text", "");
    }

    public static void setLogText(Context context, String text) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("log_text", text);
        editor.commit();
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 将类似于 1479116246349l 这样的格式转化为 小时:分
     *
     * @param time
     * @return
     */
    public static String getTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    /**
     * 判断当前的时间是否比给定的时间更晚
     *
     * @param time
     * @return
     */
    public static boolean isLater(long time) {
        Date now = new Date();
        return time > now.getTime();
    }

    public static boolean isGif(String filePath) {
        boolean isGif = false;
        if (!TextUtils.isEmpty(filePath)) {
            String suffix = FileUtil.getSuffix(filePath.toLowerCase().toString());
            String reg = ".*.gif.*";  //判断字符串中是否含有特定字符串ll
            if (suffix.matches(reg)) {
                isGif = true;
            }
//            if (TextUtils.equals(suffix, ".gif")) {
//                isGif = true;
//            }
        }
        return isGif;
    }

    public static void LoadImagGif(Context context, String filePath, ImageView images) {
        if (isGif(filePath)) {
            Glide.with(context)
                    .load(filePath)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.color.transparent)
                    .error(R.drawable.glide_default_image)
                    .crossFade()
                    .into(images);
        } else {
            LoadImageErrorUrl(images, filePath, null, R.drawable.glide_default_image);
//            Glide.with(context)
//                    .load(filePath)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.color.transparent)
//                    .error(R.drawable.glide_default_image)
//                    .crossFade()
//                    .into(images);
        }
    }

}
