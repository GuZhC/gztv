package com.dfsx.core.network;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.act.ApiVersionErrorActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.network.datarequest.GetTokenManager;
import com.dfsx.core.network.datarequest.IGetToken;

/**
 * 公共的错误码处理
 * Created by liuwb on 2017/7/17.
 */
public class CommonHttpErrorCodeHelper {

    private int errorCode;

    private Handler handler = new Handler(Looper.getMainLooper());

    public CommonHttpErrorCodeHelper() {
    }

    public void parser(int errorCode) {
        this.errorCode = errorCode;
        if (errorCode == 401) {//表示token失效

        } else if (errorCode == 402) {//客户端版本不匹配
            if (!isNoteUpdateAndUpdated()) {//判断视为了防止重复显示
                Intent intent = new Intent(CoreApp.getInstance(), ApiVersionErrorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                CoreApp.getInstance().getApplicationContext().startActivity(intent);
            }
        } else if (errorCode == 1100) {//用户未验证
            handler.post(new Runnable() {
                @Override
                public void run() {
                    IntentUtil.gotoCheckteleVerify(CoreApp.getInstance().getApplicationContext());
                }
            });

        }
    }

    /**
     * 是否显示或者正在跟新
     *
     * @return
     */
    public boolean isNoteUpdateAndUpdated() {
        SharedPreferences preferences = CoreApp.getInstance().getApplicationContext()
                .getSharedPreferences("APP_UPDATE_STATE", 0);
        return preferences.getBoolean("APP_UPDATE_STATE_IS_UPDATING", false);
    }

    public static void setAPPUpdateState(boolean isUpdated) {
        SharedPreferences preferences = CoreApp.getInstance().getApplicationContext()
                .getSharedPreferences("APP_UPDATE_STATE", 0);
        preferences
                .edit()
                .putBoolean("APP_UPDATE_STATE_IS_UPDATING", isUpdated)
                .commit();
    }
}
