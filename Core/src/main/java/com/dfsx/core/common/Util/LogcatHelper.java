package com.dfsx.core.common.Util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.CoreApp;
import com.dfsx.core.R;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * log日志统计保存
 *
 * @author liuwenbo
 */

public class LogcatHelper {

    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;
    private static Object object = new Object();

    /**
     * 初始化目录
     */
    public void init(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            PATH_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "dfsx";
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                    + File.separator + "dfsx";
        }
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
        PATH_LOGCAT += "/live";
        file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
        PATH_LOGCAT += "/log";
        file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static LogcatHelper getInstance(Context context) {
        synchronized (object) {
            if (INSTANCE == null) {
                INSTANCE = new LogcatHelper(context);
            }
        }
        return INSTANCE;
    }

    private LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs(false);
            mLogDumper = null;
        }
        mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs(false);
            mLogDumper = null;
        }
    }

    public void stop(boolean isUpload) {
        if (mLogDumper != null) {
            mLogDumper.stopLogs(isUpload);
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;
        private File localFile;

        private boolean isUploadFileToService;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                localFile = new File(dir, "GPS-"
                        + getFileName() + ".log");
                out = new FileOutputStream(localFile);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */

            cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            // cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";

        }

        public void stopLogs(boolean isUploadFileToService) {
            mRunning = false;
            this.isUploadFileToService = isUploadFileToService;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((getDateEN() + "  " + line + "\n")
                                .getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
                if (isUploadFileToService) {
                    new postLogoToDfsxService(CoreApp.getInstance().getApplicationContext(),
                            localFile.getPath()).start();
                }

            }

        }

    }

    class postLogoToDfsxService extends Thread {
        private static final int MAX_POST_LOG_SIZE = 5 * 1024 * 1024;//5M
        //用来存储设备信息
        private Map<String, String> infos = new HashMap<String, String>();

        private Context context;
        private String logFile;

        public postLogoToDfsxService(Context context, String logFilePtah) {
            this.context = context;
            this.logFile = logFilePtah;
        }

        @Override
        public void run() {
            super.run();
            if (logFile != null) {
                collectDeviceInfo();
                StringBuffer sb = new StringBuffer();
                for (Map.Entry<String, String> entry : infos.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sb.append(key + "=" + value + "\n");
                }

                String logString = readReverse(logFile, "utf-8")
                        .toString();
                sb.append(logString);
                postExceptionToServer(sb);
            }
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
                jsonObject.put("name", context.getApplicationContext()
                        .getResources().getString(R.string.app_name));
                String version = "";
                if (!TextUtils.isEmpty(infos.get("versionName"))) {
                    version += infos.get("versionName");
                }
                if (!TextUtils.isEmpty(infos.get("versionCode"))) {
                    version += infos.get("versionCode");
                }
                jsonObject.put("package", context.getApplicationInfo().packageName);
                jsonObject.put("version", version);
                jsonObject.put("timestamp", System.currentTimeMillis());
                jsonObject.put("body", sb.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String res = HttpUtil.execute(url, new HttpParameters(jsonObject), null);
            Log.w("TAG", "post log res == " + res);
        }

        /**
         * 从文件末尾开始读取文件，并逐行打印
         *
         * @param filename file path
         * @param charset  character
         */
        protected StringBuffer readReverse(String filename, String charset) {
            StringBuffer sb = new StringBuffer();
            RandomAccessFile rf = null;
            try {
                rf = new RandomAccessFile(filename, "r");
                long fileLength = rf.length();
                long start = rf.getFilePointer();// 返回此文件中的当前偏移量
                long readIndex = start + fileLength - 1;
                String line;
                rf.seek(readIndex);// 设置偏移量为文件末尾
                int c = -1;
                while (readIndex > start) {
                    c = rf.read();
                    if (c == '\n' || c == '\r') {
                        line = rf.readLine();
                        String readString = "";
                        if (line != null) {
                            readString = new String(line.getBytes("ISO-8859-1"), charset);
                        } else {
                            readString = line;
                        }
                        sb.append("\n");
                        sb.append(readString);
                        readIndex--;
                    }
                    readIndex--;
                    rf.seek(readIndex);
                    if (readIndex == 0) {// 当文件指针退至文件开始处，输出第一行
                        sb.append("\n");
                        sb.append(rf.readLine());
                    }
                    String currentReadString = sb.toString();
                    int size = currentReadString.getBytes("utf-8").length;
                    if (size >= MAX_POST_LOG_SIZE) {//最大读取
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rf != null)
                        rf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb;
        }

        /**
         * 收集设备参数信息
         */
        public void collectDeviceInfo() {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
                if (pi != null) {
                    String versionName = pi.versionName == null ? "null" : pi.versionName;
                    String versionCode = pi.versionCode + "";
                    infos.put("versionName", versionName);
                    infos.put("versionCode", versionCode);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("TAG", "an error occured when collect package info", e);
            }
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    infos.put(field.getName(), field.get(null).toString());
                    Log.d("TAG", field.getName() + " : " + field.get(null));
                } catch (Exception e) {
                    Log.e("TAG", "an error occured when collect crash info", e);
                }
            }
        }
    }


    static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;// 2012年10月03日 23:41:31
    }

    static String getDateEN() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format1.format(new Date(System.currentTimeMillis()));
        return date1;// 2012-10-03 23:41:31
    }
}
