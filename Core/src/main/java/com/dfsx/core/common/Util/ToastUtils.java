package com.dfsx.core.common.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.dfsx.core.R;
import com.dfsx.core.exception.ApiException;

/**
 * Created by heyang on 2017/12/1.
 */
public class ToastUtils {


    public static void showLong(Context context,String  msg) {
        if (context != null)
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void toastMsgFunction(Context context,String  msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastReplayOk(Context context) {
        String  msg=context.getResources().getString(R.string.news_act_replay_success_hit);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static void toastNoFunction(Context context) {
        Toast.makeText(context, "功能暂未开通", Toast.LENGTH_SHORT).show();
    }

    public static void toastApiexceFunction(Context context, ApiException e) {
        Toast.makeText(context,JsonCreater.getErrorMsgFromApi(e.toString()), Toast.LENGTH_SHORT).show();
    }

    public static void toastNoCommendFunction(Context context) {
        String  msg=context.getResources().getString(R.string.news_act_nocommemnd_hit);
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastNoContentCommendFunction(Context context) {
        String  msg=context.getResources().getString(R.string.news_act_replay_hit);
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastPraiseMsgFunction(Context context) {
        Toast.makeText(context,"成功点赞", Toast.LENGTH_SHORT).show();
    }
}
