package com.dfsx.thirdloginandshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.dfsx.core.rx.RxBus;
import com.dfsx.thirdloginandshare.R;
import com.dfsx.thirdloginandshare.login.AbsThirdLogin;
import com.dfsx.thirdloginandshare.login.WXUtil;
import com.dfsx.thirdloginandshare.share.ShareUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by liuwb on 2016/7/14.
 */
public class BaseWXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXUtil.getWXApi(this);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Bundle b = new Bundle();
                resp.toBundle(b);
                String state = b.getString("_wxapi_sendauth_resp_state");
                if (WXUtil.WEIXIN_REQ_STATE.equals(state)) {
                    Intent intent = new Intent(AbsThirdLogin.ACTION_WX_AUTH);
                    intent.putExtra(AbsThirdLogin.KEY_AUTH_DATA, b);
                    //                    EventBus.getDefault().post(intent);
                    RxBus.getInstance().post(intent);

                    //登录之后不再启动登录页面
                    //                    Intent intentLogin = new Intent(Intent.ACTION_VIEW);
                    //
                    //                    String url = getApplicationContext().getResources().getString(R.string.app_name) +
                    //                            "://" + getApplicationContext().getPackageName() + ".dfsx_third_login/";
                    //                    intentLogin.setData(Uri.parse(url));
                    //                    startActivity(intentLogin);
                }
                //微信分享成功回调
                ShareUtil.sendShareCallbackMsg(true);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                postErrorOrCancle();
                ShareUtil.sendShareCallbackMsg(false);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                postErrorOrCancle();
                ShareUtil.sendShareCallbackMsg(false);
                break;
            default:
                result = R.string.errcode_unknown;
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                postErrorOrCancle();
                ShareUtil.sendShareCallbackMsg(false);
                break;
        }
        finish();
    }

    private void postErrorOrCancle() {
        Intent nullIntent = new Intent(AbsThirdLogin.ACTION_WX_AUTH);
        RxBus.getInstance().post(nullIntent);
    }
}
