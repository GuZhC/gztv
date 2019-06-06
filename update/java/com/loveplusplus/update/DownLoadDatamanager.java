package com.loveplusplus.update;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 启动下载的数据处理类
 * Created by liuwb on 2016/10/19.
 */
public class DownLoadDatamanager {
    static final String SP_DOWN_LOAD = "com.loveplusplus.update.update_down_load_info";
    static final String TAG = "UpdateDialog";
    private static DownLoadDatamanager instance = null;

    private Context mContext;

    private String url;

    private DownloadManager mMgr;
    private long currentDownLoadId;

    private Handler handler = new Handler(Looper.getMainLooper());

    private DownLoadDatamanager(Context context) {
        this.mContext = context;
    }

    public synchronized static DownLoadDatamanager getInstance(Context context) {
        if (instance == null) {
            instance = new DownLoadDatamanager(context);
        }
        return instance;
    }

    public static DownLoadDatamanager getInstance() {
        return instance;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    private String getString(int is) {
        return mContext.getResources().getString(is);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void download() {
        Log.e("TAG", "start download ------------- apkurl =" + url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(getString(R.string.newUpdateAvailable));
        String appName = getString(mContext.getApplicationInfo().labelRes);
        request.setTitle(appName);
        //设置类型为.apk
        request.setMimeType("application/vnd.android.package-archive");
        Log.e("TAG", "apk name == " + appName);
        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName + ".apk");

        // get download service and enqueue file

        mMgr = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        if (checkIsNeedDownLoadApk()) {
            currentDownLoadId = mMgr.enqueue(request);
            Log.e("TAG", "DownloadManager start --- " + currentDownLoadId);
            saveDownloadId(url, currentDownLoadId);
        }

        //Intent intent=new Intent(mContext.getApplicationContext(),DownloadService.class);
        //intent.putExtra(Constants.APK_DOWNLOAD_URL, getArguments().getString(Constants.APK_DOWNLOAD_URL));
        //mContext.startService(intent);


        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intentd) {
                long reference = intentd.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.e("TAG", "Download ok ----------- " + reference);
                if (reference == currentDownLoadId) {
                    try {
                        mContext.unregisterReceiver(this);
                        String uri = validDownload(currentDownLoadId);
                        if (uri != null) {
                            installAPk(uri);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }


            }

        };
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mContext.registerReceiver(receiver, filter);

    }

    protected void installAPk(String apkUri) {
        Intent intent = new Intent(mContext, InstallActivity.class);
        intent.putExtra("APK_URI", apkUri);
        mContext.startActivity(intent);
        //        try {
        //            Intent intent = new Intent(Intent.ACTION_VIEW);
        //            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //            intent.setAction(android.content.Intent.ACTION_VIEW);
        //            intent.setDataAndType(Uri.parse(apkUri),
        //                    "application/vnd.android.package-archive");
        //            mContext.startActivity(intent);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
    }


    private void saveDownloadId(String downUrl, long id) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_DOWN_LOAD, 0);
        sp.edit().putLong(downUrl, id).commit();
    }

    public long getSaveDownLoadId(String downUrl) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_DOWN_LOAD, 0);
        return sp.getLong(downUrl, -1);
    }

    private void clearDownloadId(String downUrl) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_DOWN_LOAD, 0);
        sp.edit().remove(downUrl).commit();

        Log.e("TAG", "after clear == " + getSaveDownLoadId(downUrl));
    }

    /**
     * 检查是否要要下APK。 在正在下载中就不要继续添加下载任务了。
     * 入过下载失败也要重新下载
     *
     * @return
     */
    private boolean checkIsNeedDownLoadApk() {
        long downId = getSaveDownLoadId(url);
        if (downId != -1) {
            Cursor c = mMgr.query(new DownloadManager.Query().setFilterById(downId));
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    Log.e(TAG, "已经下载完成");
                    String apkUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(apkUri)) {
                        try {
                            File apkFile = new File(new URI(apkUri));
                            boolean isExist = apkFile != null && apkFile.isFile();
                            Log.e(TAG, "isExist == " + isExist);
                            if (isExist) {
                                installAPk(apkUri);
                                return false;
                            }
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                    }
                    return true;
                } else if (status == DownloadManager.STATUS_RUNNING) {
                    Log.e("TAG", "数据正在下载中...");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "已在更新中", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return false;
                } else {
                    int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
                    Log.e("TAG", "Download not correct, status [" + status + "] reason [" + reason + "]");
                    mMgr.remove(downId);
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * Check if download was valid, see issue
     * http://code.google.com/p/android/issues/detail?id=18462
     *
     * @param downloadId
     * @return
     */
    private String validDownload(long downloadId) {

        String uri = null;

        Log.d(TAG, "Checking download status for id: " + downloadId);

        //Verify if download is a success
        Cursor c = mMgr.query(new DownloadManager.Query().setFilterById(downloadId));

        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                //return true; //Download is valid, celebrate
            } else {
                int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
                Log.d(TAG, "Download not correct, status [" + status + "] reason [" + reason + "]");
            }
        }
        return uri;
    }
}
