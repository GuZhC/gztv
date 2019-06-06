package com.dfsx.videoijkplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import com.dfsx.videoijkplayer.util.NetworkUtil;

/**
 * 检查网络并显示弹窗
 * Created by liuwb on 2016/10/18.
 */
public class NetChecker {

    public static final String SP_SET_NET_PLAY = "com.dfsx.videoijkplayer.NetNotes_SET_NET_PLAY";

    private CheckCallBack checkCallBack;
    private Context context;

    private boolean isDialogShowing;
    private boolean setAnyNetIsOk;//设置任何网络都行

    /**
     * 是否需把设置的数据持久化
     */
    private boolean isSaveSettingToLocal = false;

    private Object checkTag;

    public NetChecker(Context context, CheckCallBack checkCallBack) {
        this.checkCallBack = checkCallBack;
        this.context = context;
    }


    public void checkNet(Object checkTag) {
        this.checkTag = checkTag;
        if (getSettingsNetPlay(context)) {
            checkCallBack.callBack(true, checkTag);
            return;
        }
        if (isDialogShowing) {
            return;
        }
        int netConnectedStatus = NetworkUtil.getConnectivityStatus(context);
        if (netConnectedStatus == NetworkUtil.TYPE_WIFI) {
            checkCallBack.callBack(true, checkTag);
        } else {
            showAlertDialog(netConnectedStatus);
        }
    }

    public void showAlertDialog() {
        showAlertDialog(NetworkUtil.getConnectivityStatus(context));
    }

    public void showAlertDialog(int netConnectedStatus) {
        String notesText = netConnectedStatus == NetworkUtil.TYPE_NOT_CONNECTED ? context.getResources().getString(R.string.note_not_net) :
                context.getResources().getString(R.string.note_network);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage(notesText);
        if (netConnectedStatus == 0) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    checkCallBack.callBack(false, checkTag);
                }
            });
        } else {
            builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    checkCallBack.callBack(true, checkTag);
                    saveSetNetPlay(context);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    checkCallBack.callBack(false, checkTag);
                }
            });
        }
        AlertDialog adig = builder.create();
        if (context != null && context instanceof Activity) {
            if (!((Activity) context).isFinishing()) {
                adig.show();
                isDialogShowing = true;
            }
        }
        adig.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isDialogShowing = false;
            }
        });
    }

    private void saveSetNetPlay(Context context) {
        if (!isSaveSettingToLocal) {
            setAnyNetIsOk = true;
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_SET_NET_PLAY, 0);
        sp.edit().putBoolean(SP_SET_NET_PLAY, true).commit();
    }

    private boolean getSettingsNetPlay(Context context) {
        if (!isSaveSettingToLocal) {
            return setAnyNetIsOk;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_SET_NET_PLAY, 0);
        return sp.getBoolean(SP_SET_NET_PLAY, false);
    }

    public interface CheckCallBack {
        void callBack(boolean isCouldPlay, Object tag);
    }

}
