package com.dfsx.lzcms.liveroom.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.AppManager;

/**
 * Created by liuwb on 2016/11/1.
 */
public class IsLoginCheck {

    private Context context;

    public IsLoginCheck(Context context) {
        this.context = context;
    }

    public boolean checkLogin() {
        if (AppManager.getInstance().getIApp().isLogin()) {
            return true;
        } else {
            Resources res = context.getResources();
            AlertDialog adig = new AlertDialog.Builder(context).setTitle(res.getString(R.string.check_login_title))
                    .setMessage(res.getString(R.string.check_login_msg))
                    .setPositiveButton(res.getString(R.string.check_login_btn_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(context.getResources().
                            getString(R.string.app_name) + "://go_login"));
                    context.startActivity(intent);
                }
            }).setNegativeButton(res.getString(R.string.check_login_btn_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            }).create();
            adig.show();
        }
        return false;
    }
}
