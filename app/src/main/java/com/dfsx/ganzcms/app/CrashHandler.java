package com.dfsx.ganzcms.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.*;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuwb on 2017/4/6.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            exitApp();
        }
    }

    private void exitApp() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.e(TAG, "error : ", e);
        }
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        LogUtils.e("TAG", "crash exception ----------------- ");
        ex.printStackTrace();
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        Observable.just(ex)
                .observeOn(Schedulers.io())
                .map(new Func1<Throwable, Boolean>() {
                    @Override
                    public Boolean call(Throwable throwable) {
                        saveCrashInfo2File(throwable);
                        return true;
                    }
                }).subscribe();

        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        postExceptionToServer(sb);
        String fileName = saveExceptionToFile(sb);
        if (fileName != null) return fileName;
        return null;
    }

    /**
     * 上传到东方盛行的服务器
     *
     * @param sb 异常信息字符串
     */
    private void postExceptionToServer(StringBuffer sb) {
        String url = "http://www.dfsxcms.cn:8181" + "/services/logs";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("OS", "android");
            jsonObject.put("name", mContext.getApplicationContext().getResources().getString(R.string.app_name));
            String version = "";
            if (!TextUtils.isEmpty(infos.get("versionName"))) {
                version += infos.get("versionName");
            }
            if (!TextUtils.isEmpty(infos.get("versionCode"))) {
                version += infos.get("versionCode");
            }
            jsonObject.put("package", mContext.getApplicationInfo().packageName);
            jsonObject.put("version", version);
            jsonObject.put("timestamp", System.currentTimeMillis());
            jsonObject.put("body", sb.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataRequest<Boolean> request = new DataRequest<Boolean>(mContext) {
            @Override
            public Boolean jsonToBean(JSONObject json) {
                if (json != null) {
                    Log.d("http", "upload response == " + json.toString());
                }
                return true;
            }
        };
        request.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setJsonParams(jsonObject)
                .setRequestType(DataReuqestType.POST)
                .build(), false)
                .setCallback(new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        Log.d("http", "upload exception success");
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        Log.e("http", "upload exception fail");
                    }
                });
    }

    private String saveExceptionToFile(StringBuffer sb) {
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getPath();
                if (!TextUtils.isEmpty(path) && path.endsWith("/")) {
                    path += "dfsx/leshancms/log/";
                } else {
                    path += "/dfsx/leshancms/log/";
                }
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
}
