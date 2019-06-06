package com.dfsx.core.common.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import com.dfsx.core.R;
import com.dfsx.core.common.act.WhiteTopBarActivity;

/**
 * Created by liuwb on 2016/9/18.
 */
public class IntentUtil {

    public static final String KEY_FRAGMENT_NAME = "fragment_name";
    public static final String KEY_FRAGMENT_TILTE = "fragment_title";
    public static final String KEY_FRAGMENT_URL = "fragment_url";

    public static final String ACTION_LOGIN_OK = "com.dfsx.lscms.app.util.IntentConstants_LOGIN_OK_ACTION";
    public static final String ACTION_SCROLL_ITEM_OK = "com.dfsx.lscms.app.util.IntentConstants_UPDTA_SCORLLITEM";

    public static final String ACTION_LOGIN_OUT = "com.dfsx.core.common.Util.platform.logout";

    //圈子发表评论更新
    public static final String ACTION_COMNITY_COMNEND_OK = "com.dfsx.lscms.app.util.IntentConstants_UPDTA_COMMEND_ATTEION";

    //用户 添加 关注 或取消关注
    public static final String ACTION_UPDTA_ATTEION_OK = "com.dfsx.lscms.app.util.IntentConstants_UPDTA_ATTEION";

    public static final String ACTION_UPDTA_ATTEION_DEF_OK = "com.dfsx.lscms.app.util.IntentConstants_UPDTA_ATTEION_Def";

    //圈子发表点赞 更新
    public static final String ACTION_COMNITY_PRAISE_OK = "com.dfsx.lscms.app.util.IntentConstants_UPDTA_PRAISE";


    //heyang  上传完成通知
    public static final String ACTION_UPLOAD_COMPLATE_OK = "com.dfsx.lscms.app.util.IntentConstants_UPLOAD_COMPLATE_OK ";

    //我的 收藏有更新
    public static final String UPDATE_FAVIRITY_MSG = "com.dfsx.core.common.uset.raltion";


    public static final String UPDATE_FAVIRITY_DEF_MSG = "com.dfsx.core.common.uset.raltion_FAVIRITY";

    //广播serivce 像UI 发送的消息 以及消息类型 heyang  2017-3-29
    public static final String REVICE_ACTION_RADIO_MSG = "com.dfsx.core.common.radio.msg";
    public static final String PLAY_RADIO_ACTION = "com.dfsx.lscms.radio.PLAY_ACTION";
    public static final String PAUSE_RADIO_ACTION = "com.dfsx.lscms.radio.PAUSE_ACTION";
    public static final String STOP_RADIO_ACTION = "com.dfsx.lscms.radio.STOP_ACTION";
    public static final String PLAY_RADIO_ERROR = "com.dfsx.lscms.radio.RADIO_ERROR ";

    /**
     * 签到成功的消息
     */
    public static final String ACTION_QIAN_DAO_SUCCESS = "com.dfsx.core.common.Util.qian.dao";

    public static final String ACTION_UPFILE_PROGRESS_MSG = "com.dfsx.lscms.app.util.IntentConstants_UPFILE_PROGRESS_MSG";
    public static final String RX_UPFILE_PROGRESS_MSG = "com.dfsx.lscms.app.util.Rx_UPFILE_PROGRESS_MSG";

    /*
     *  跳转到个人主页面
     */
    public static void gotoPersonHomeAct(Context context, long useId) {
        //        Intent intent = new Intent();
        //        intent.setAction("com.dfsx.intent.personhome.action.VIEW");
        //        intent.addCategory("android.intent.category.perhome");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("id", useId);
        Uri uri = Uri.parse(context.getResources().
                getString(R.string.app_name) + "://go_home/");
        //        uri.s
        //        intent.setDataAndType(uri.parse(useId + ""), "application/useid");//数据（酱油）
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setData(uri);
        context.startActivity(intent);
    }


    public static void gotoCheckteleVerify(Context context) {
        //        Intent intent = new Intent();
        //        intent.setAction("com.dfsx.intent.personhome.action.VIEW");
        //        intent.addCategory("android.intent.category.perhome");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(context.getResources().
                getString(R.string.app_name) + "://go_checktele/");
        intent.setData(uri);
        intent.putExtra("gotocheck", 1);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 调往登录页面
     *
     * @param context
     */
    public static void goToLogin(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(context.getResources().
                getString(R.string.app_name) + "://go_login/");
        intent.setData(uri);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 调往登录页面
     *
     * @param context
     */
    public static void goReport(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(context.getResources().
                getString(R.string.app_name) + "://com.dfsx.lscms_report/");
        intent.setData(uri);
        context.startActivity(intent);
    }

    /*
 *  heyang  2018-7-18
 *  跳转到圈子发布界面
 */
    public static void gotoCommunPubishAct(Context context, long id,String  path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("id", id);
        intent.putExtra("tppe", 1);
        intent.putExtra("path", path);
        Uri uri = Uri.parse(context.getResources().
                getString(R.string.app_name) + "://go_communitPub/");
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 去通用的白色导航条页面
     *
     * @param context
     * @param fragmentName
     * @param title
     */
    public static void goWhiteTopBarAct(Context context, String fragmentName,
                                        String title) {
        goWhiteTopBarAct(context, fragmentName, title, null);
    }

    public static void goWhiteTopBarAct(Context context, String fragmentName,
                                        String title, String rightTitle) {
        WhiteTopBarActivity.startAct(context, fragmentName, title);
        //        Intent intent = new Intent(context, CommMenuActivity.class);
        //        intent.putExtra(KEY_FRAGMENT_NAME, fragmentName);
        //        intent.putExtra(KEY_FRAGMENT_TILTE, title);
        //        context.startActivity(intent);
    }
}
