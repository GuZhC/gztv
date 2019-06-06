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
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

/**
 * heyang  2018-8-9  更新提示说明  新UI
 *  添加 更新提示
 */

public class UpdateDialog2 extends DialogFragment {

    static final String TAG = "UpdateDialog";
    private DownloadManager mMgr;
    private Context mContext;
    private View myView;
    private AlertDialog dialog;
    private TextView titlTxt, versionTxt, describrTxt;
    private TextView canncelBtn, comfirBtn;
    private String describe;
    private String vserion;

    public  UpdateDialog2(String version, String describe) {
        this.vserion = version;
        this.describe = describe;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        myView = LayoutInflater.from(mContext).inflate(R.layout.dialog_update, null);
        titlTxt = myView.findViewById(R.id.update_title);
        versionTxt = myView.findViewById(R.id.tv_version_txt);
        describrTxt = myView.findViewById(R.id.tv_description);
        canncelBtn = myView.findViewById(R.id.btn_cancel);
        comfirBtn = myView.findViewById(R.id.btn_update);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.UpdateDialog);
        String msg = getArguments().getString(Constants.APK_UPDATE_CONTENT);
        msg = msg.replace("\\n", "\n");
        Log.e("TAG", "onCreateDialog ------ ");
//        builder.setMessage(msg)
//                .setPositiveButton(R.string.dialogPositiveButton, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                        //                        goToDownload();
//                        String downLoadUrl = getArguments().getString(Constants.APK_DOWNLOAD_URL);
//                        DownLoadDatamanager.getInstance(mContext).
//                                setUrl(downLoadUrl);
//                        Log.e("TAG", "down load click ---- downUrl == " + downLoadUrl);
//                        checkPermission();
//                        //                        dismiss();
//                    }
//                })
//                .setNegativeButton(R.string.dialogNegativeButton, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                        dismiss();
//                    }
//                });
        // Create the AlertDialog object and return it
        dialog = builder.create();
        dialog.setView(myView);
        setWindowSize(mContext);
        iniView();
        return dialog;
    }

    public void iniView() {
        canncelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
//                Toast.makeText(mContext, "cdcdcd", Toast.LENGTH_LONG).show();
            }
        });
        comfirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownLoadDatamanager.getInstance(mContext).
                        setUrl(getArguments().getString(Constants.APK_DOWNLOAD_URL));
                checkPermission();
//                dismiss();

//                Toast.makeText(mContext, "cdcdcd", Toast.LENGTH_LONG).show();
            }
        });
        titlTxt.setText("更新提示");
        versionTxt.setText("版本号v" + vserion);
        describrTxt.setText(describe);
    }

    private void setWindowSize(Context context) {
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (getScreenWidth(context) * 0.7f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
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

    public void setCusTOmVie() {
//        if (dialog != null)
//            dialog.setView(myView);
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
                        intent.setAction(Intent.ACTION_VIEW);
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

    public void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void checkPermission() {
        new TedPermission(mContext).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (DownLoadDatamanager.getInstance() != null) {
                    DownLoadDatamanager.getInstance().download();
                }
                Log.e("TAG", "onPermissionGranted -------------- request download");
                closeDialog();
                //                goToDownload();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(mContext, "没有权限", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "onPermissionDenied -------------- no Permission");
            }
        }).setDeniedMessage(mContext.getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
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
