package com.dfsx.lzcms.liveroom.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import com.dfsx.core.file.SDCardUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class AndroidUtil {
    /**
     * whether the mobile phone network is Connecting
     *
     * @param context
     * @return
     */
    public static boolean isConnectInternet(Context context) {

        ConnectivityManager conManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    public static String getUniqueId(Context context) {
        if (!TextUtils.isEmpty(getDiviceId(context))) {
            return getDiviceId(context);
        } else if (!TextUtils.isEmpty(getAndroidId(context))) {
            return getAndroidId(context);
        } else {
            return getOtherUniqueId(context);
        }
    }

    public static String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "127.0.0.1";
    }

    /**
     * 获取
     *
     * @param context
     * @return
     */
    public static String getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                return versionName + "" + versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", "an error occured when collect package info", e);
        }
        return null;
    }

    /**
     * @param context
     * @return
     */
    public static String getOtherUniqueId(Context context) {
        UUID deviceUuid = new UUID(getSerialNumber(context).hashCode(), ((long) getMacAdress(context).hashCode() << 32) | getBrandInfoId().hashCode());
        return deviceUuid.toString();
    }

    private static String getBrandInfoId() {
        try {
            return "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getAndroidId(Context context) {
        try {
            return Secure
                    .getString(context.getContentResolver(), Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getDiviceId(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getSerialNumber(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimSerialNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getMacAdress(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return wm.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getDeviceWidth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }


    /**
     * 获取包信息
     *
     * @param context
     * @return
     */
    public static String getPakageName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            // int versionCode = info.versionCode;
            // String versionName = info.versionName;
            String packageName = info.packageName;
            return packageName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static void CopyToClipboard(Context context, String str) {
        ClipboardManager c = (ClipboardManager) context
                .getSystemService(Activity.CLIPBOARD_SERVICE);
        c.setText(str);
        Toast.makeText(context, "已复制到剪切板", Toast.LENGTH_LONG).show();
    }

    public static String getFromClipboard(Context context) {
        ClipboardManager c = (ClipboardManager) context
                .getSystemService(Activity.CLIPBOARD_SERVICE);
        return c.getText().toString();
    }

    /**
     * 获取应用程序名称
     *
     * @param context
     * @return
     */
    public static String getApplicationName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);

            String ret = (String) info.applicationInfo.loadLabel(context.getPackageManager());
            return ret;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "未定义";
        }
    }

    /**
     * 存在并且容量大于10MB
     *
     * @return
     */
    public static boolean isSDCardExistAndNotFull() {
        return isSDCardExistAndNotFull(10);
    }

    /**
     * 存在并且容量大于指定MB
     *
     * @param minMB
     * @return
     */
    public static boolean isSDCardExistAndNotFull(long minMB) {
        if (!isSDCardExist()) {
            return false;
        }

        long leftMB = getSDFreeSize();
        return leftMB >= minMB;
    }

    /**
     * SD卡是否存在
     *
     * @return
     */
    public static boolean isSDCardExist() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取SD卡可用空间 MB
     *
     * @return
     */
    public static long getSDFreeSize() {
        return SDCardUtil.getSDFreeSize();
    }
}
