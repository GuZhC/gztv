package com.loveplusplus.update;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class UpdateDialog extends DialogFragment {

    static final String TAG = "UpdateDialog";
    DownloadManager mMgr;
    Context mContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.newUpdateAvailable);
        String msg = getArguments().getString(Constants.APK_UPDATE_CONTENT);
        msg = msg.replace("\\n", "\n");
        builder.setMessage(msg)
                .setPositiveButton(R.string.dialogPositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
//                        goToDownload();
                        DownLoadDatamanager.getInstance(mContext).
                                setUrl(getArguments().getString(Constants.APK_DOWNLOAD_URL));
                        checkPermission();
//                        dismiss();
                    }
                })
                .setNegativeButton(R.string.dialogNegativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void goToDownload() {
        String url = getArguments().getString(Constants.APK_DOWNLOAD_URL);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(getString(R.string.newUpdateAvailable));
        String appName = getString(mContext.getApplicationInfo().labelRes);
        request.setTitle(appName);
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName + ".apk");

// get download service and enqueue file

        mMgr = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        final long id = mMgr.enqueue(request);

        //Intent intent=new Intent(mContext.getApplicationContext(),DownloadService.class);
        //intent.putExtra(Constants.APK_DOWNLOAD_URL, getArguments().getString(Constants.APK_DOWNLOAD_URL));
        //mContext.startService(intent);


        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intentd) {
                long reference = intentd.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (reference == id) {

                    try {
                        mContext.unregisterReceiver(this);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        String uri = validDownload(id);
                        if (uri != null) {
                            intent.setDataAndType(Uri.parse(uri),
                                    "application/vnd.android.package-archive");
                            mContext.startActivity(intent);
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

    private void checkPermission() {
        new TedPermission(mContext).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (DownLoadDatamanager.getInstance() != null) {
                    DownLoadDatamanager.getInstance().download();
                }
//                goToDownload();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(mContext, "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(mContext.getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    /**
     * Check if download was valid, see issue
     * http://code.google.com/p/android/issues/detail?id=18462
     *
     * @param long1
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
