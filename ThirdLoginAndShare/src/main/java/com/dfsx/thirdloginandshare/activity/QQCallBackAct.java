package com.dfsx.thirdloginandshare.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.dfsx.core.rx.RxBus;
import com.dfsx.thirdloginandshare.share.QQAndrQQZoneShare;
import com.dfsx.thirdloginandshare.share.ShareUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by Administrator on 2015/12/25.
 */
public class QQCallBackAct extends Activity {

    public static final String KEY_SHARE_DATA = "com.tixa.plugin.activity.QQCallBackAct_share_data";
    public static final String KEY_ACT_TYPE = "com.tixa.plugin.activity.QQCallBackAct_ACT_TYPE";
    public static final String KEY_RESPONSE_ACTION = "com.tixa.plugin.activity.QQCallBackAct_RESPONSE_TYPE";
    public static final String KEY_SHARE_TYPE = "com.tixa.plugin.activity.QQCallBackAct_share_type";

    public static final String KEY_RESPONSE_DATA = "com.tixa.plugin.activity.QQCallBackAct_RESPONSE_DATA";
    public static final String KEY_RESPONSE_OPENID = "com.tixa.plugin.activity.QQCallBackAct_RESPONSE_openid";
    public static final String KEY_ACCESS_TOKEN = "com.tixa.plugin.activity.QQCallBackAct_RESPONSE_ACCESS_TOKEN";

    public static final int TYPE_SHARE_ACT = 1;
    public static final int TYPE_LOGIN_ACT = 2;
    public static final int TYPE_SHARE_QQ = 0;
    public static final int TYPE_SHARE_QZONE = 1;

    private int currentType = TYPE_SHARE_ACT;
    private Tencent mTencent;
    private Activity context;
    private String responseAction;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        mTencent = Tencent.createInstance(com.dfsx.thirdloginandshare.Constants.QQ_APP_ID,
                this.getApplicationContext());
        Intent intent = getIntent();
        currentType = intent.getIntExtra(KEY_ACT_TYPE, TYPE_SHARE_ACT);
        responseAction = intent.getStringExtra(KEY_RESPONSE_ACTION);
        if (currentType == TYPE_SHARE_ACT) {//QQ的分享类型
            if (!isQQClientAvailable(context)) {
                Toast.makeText(context, "没有安装QQ", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            int shareType = intent.getIntExtra(KEY_SHARE_TYPE, TYPE_SHARE_QQ);
            Bundle shareBundle = intent.getBundleExtra(KEY_SHARE_DATA);
            if (shareType == TYPE_SHARE_QQ) {//QQ分享
                doShareQQ(shareBundle);
            } else {//QQ空间分享
                if (shareBundle.getInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE) == QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT) {
                    doShareToQzone(shareBundle);
                } else {
                    doPublishToQzone(shareBundle);
                }
            }
        } else {//QQ的登录类型
//            if (!mTencent.isSessionValid()) {
                mTencent.login(context, "all", loginUiListener);
//            }
        }
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    IUiListener listener = new IUiListener() {
        @Override
        public void onCancel() {
            Toast.makeText(context, "onCancel", Toast.LENGTH_SHORT).show();
            postError();
            finish();
        }

        @Override
        public void onComplete(Object response) {
            //            Toast.makeText(context, "onComplete", Toast.LENGTH_SHORT).show();
            if (QQAndrQQZoneShare.ACTION.equals(responseAction)) {
                ShareUtil.sendShareCallbackMsg(true);
            } else {
                Intent intent = new Intent(responseAction);
                JSONObject json = (JSONObject) response;
                intent.putExtra(KEY_RESPONSE_DATA, json.toString());
                intent.putExtra(KEY_RESPONSE_OPENID, mTencent.getOpenId());
                intent.putExtra(KEY_ACCESS_TOKEN, mTencent.getAccessToken());
                RxBus.getInstance().post(intent);
            }

            //            EventBus.getDefault().post(intent);
            ///////QQ分享成功
            finish();
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(context, "onError", Toast.LENGTH_SHORT).show();
            postError();
            finish();
        }
    };

    private void postError() {
        if (QQAndrQQZoneShare.ACTION.equals(responseAction)) {
            ShareUtil.sendShareCallbackMsg(false);
        } else {
            Intent nullIntent = new Intent(responseAction);
            RxBus.getInstance().post(nullIntent);
        }

    }

    IUiListener loginUiListener = new IUiListener() {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            Toast.makeText(context, "授权成功", Toast.LENGTH_SHORT).show();
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);
            //乐山APP不要求在客户端去获取用户信息.直接返回
            //updateUserInfo();
            Intent intent = new Intent(responseAction);
            intent.putExtra(KEY_RESPONSE_DATA, "");
            intent.putExtra(KEY_RESPONSE_OPENID, mTencent.getOpenId());
            intent.putExtra(KEY_ACCESS_TOKEN, mTencent.getAccessToken());
            RxBus.getInstance().post(intent);
            finish();
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();
            postError();
            finish();
        }

        @Override
        public void onCancel() {
            Toast.makeText(context, "取消授权", Toast.LENGTH_SHORT).show();
            postError();
            finish();
        }
    };

    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            UserInfo mInfo = new UserInfo(context, mTencent.getQQToken());
            mInfo.getUserInfo(listener);
        }
    }

    private void doShareQQ(final Bundle shareBundle) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mTencent != null) {
                    mTencent.shareToQQ(context, shareBundle, listener);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void doShareToQzone(final Bundle params) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                mTencent.shareToQzone(context, params, listener);
            }
        });
    }

    private void doPublishToQzone(final Bundle params) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mTencent.publishToQzone(context, params, listener);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentType == TYPE_SHARE_ACT) {
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        } else {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginUiListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTencent != null) {
            mTencent.releaseResource();
        }
    }
}
