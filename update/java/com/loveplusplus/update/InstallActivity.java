package com.loveplusplus.update;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.net.URI;

public class InstallActivity extends Activity {

    private Activity mContext;
    private String apkUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        apkUri = getIntent().getStringExtra("APK_URI");
        if (TextUtils.isEmpty(apkUri)) {
            finish();
            return;
        }
        checkPermissionAndInstall(apkUri);
    }


    @TargetApi(26)
    private void checkPermissionAndInstall(final String apkUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean haveInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
            SharedPreferences sp = getSharedPreferences("APP", 0);
            /**
             * 只提示一次，后面直接安装
             */
            haveInstallPermission = haveInstallPermission || sp.getBoolean(getPackageName(), false);
            Log.e("TAG", "haveInstallPermission == " + haveInstallPermission);
            if (haveInstallPermission) {
                installAPk(apkUri);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
                builder.setTitle("提示");
                builder.setCancelable(false);
                builder.setMessage("安装应用需要打开未知来源权限，请去设置中开启应用权限，以允许安装来自此来源的应用");
                builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startInstallPermissionSettingActivity();
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                if (mContext != null && !mContext.isFinishing()) {
                    builder.show();
                    sp.edit().putBoolean(getPackageName(), true).commit();
                }
            }
        } else {
            installAPk(apkUri);
        }

    }

    protected void installAPk(String apkUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            Log.e("TAG", "apkUri == " + apkUri);
            Uri uri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                uri = Uri.parse(apkUri);
            } else {
                File apkFile = new File(new URI(apkUri));
                Log.e("TAG", "packageName== " + mContext.getPackageName());
                String authorities = getFileProviderName(mContext);
                uri = FileProvider.getUriForFile(mContext, authorities, apkFile);
                Log.e("TAG", "apk Uri == " + uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(uri,
                    "application/vnd.android.package-archive");
            this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    private void startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + mContext.getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        mContext.startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            installAPk(apkUri);
        } else {
            finish();
        }
    }

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
    }
}
