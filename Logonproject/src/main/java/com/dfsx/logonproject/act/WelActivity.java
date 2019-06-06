package com.dfsx.logonproject.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.common.view.IUserAgreement;
import com.dfsx.core.common.view.UserAgreementManger;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.logonproject.busniness.ThirdLoginCallbackHelper;
import com.dfsx.logonproject.dzfragment.*;
import com.dfsx.thirdloginandshare.Constants;
import com.dfsx.thirdloginandshare.login.AbsThirdLogin;
import com.dfsx.thirdloginandshare.login.ThirdLoginFactory;


/**
 * Created by heyang on 2016/10/27
 */
public class WelActivity extends FragmentActivity implements AbsThirdLogin.OnThirdLoginListener{

    private Context context;
    Handler mHandler;
    //    AppApi mApi;
    boolean mbLogining = false;
    boolean misSaveAccount = true;
    TextView mViewUser;
    TextView mViewPass;
    Button mQQLoginBtn;
    //    ThirdPartManager mLoginManger;
    private static final String TAG = LoginActivity.class.getName();

    private CustomeProgressDialog loading;

    AccountApi accountApi;
    private boolean isthirdLogin;

    private String token;
    private String openid;
    private String thirdType;
    private String app_id;
    private View rootView;
    TextView  logonBtn,registerBtn;

    //com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY／／ BUG.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.act_bn_wel, null);
        setContentView(rootView);
        context = this;
        //ThirdPartManager.getInstance().Initilize(this);
        accountApi = new AccountApi(this);
        //        mApi = App.getInstance().getApi();
        mHandler = new Handler();
        //        mLoginManger = ThirdPartManager.getInstance();

        ImageView backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WelActivity.this.onBackPressed();
                // LoginActivity.this.finish();
            }
        });

        logonBtn = (TextView) findViewById(R.id.logo_telephont_edt);
        registerBtn = (TextView) findViewById(R.id.logon_pass_edt);
        logonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent=new Intent(WelActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent=new Intent(WelActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView mWeiboLoginBtn = (ImageView) findViewById(R.id.bottom_weibo_btn);
        mWeiboLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeiboLogin();
                //                isAgrrrmentUser(AbsThirdLogin.Sinaweibo);
            }
        });

        ImageView mQQLoginBtn = (ImageView) findViewById(R.id.bottom_qq_btn);
        mQQLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                isAgrrrmentUser(AbsThirdLogin.Qq);
                QQLogin();
                // LoginActivity.this.finish();
            }
        });


        ImageView mWechatLoginBtn = (ImageView) findViewById(R.id.bottom_weixin_btn);
        mWechatLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                isAgrrrmentUser(AbsThirdLogin.Weixin);
                WechatLogin();
                //                LoginActivity.this.finish();
            }
        });

        TextView registerBtn = (TextView) findViewById(R.id.logon_register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                IntentUtil.gotoCheckteleVerify(view.getContext());

                Intent intent = new Intent(WelActivity.this, RegisterActivity.class);
                startActivity(intent);

                //                startActivityForResult(intent, 2);
                //                WhiteTopBarActivity.startAct(context,ResetPassFragment.class.getName(),"重置密码");
            }
        });

        TextView _forgetBtn = (TextView) findViewById(R.id.logon_forget_btn);
        _forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WhiteTopBarActivity.startAct(WelActivity.this, ResetPassVeriFragment.class.getName(), "验证手机号");
                finish();
            }
        });

        initThirdLogin();
        //        Bundle bundle = getIntent().getExtras();
        //        if (bundle != null) {
        //            SendAuth.Resp resp = new SendAuth.Resp();
        //            resp.fromBundle(bundle);
        //            LoginToken(0, 0, getIntent());
        //        }

    }

    //弹出用户协议，是否同意
    public void isAgrrrmentUser(final int type) {
        String str = Util.readAssertResource(WelActivity.this, "useragreement.txt");
        UserAgreementManger.getInstance().showUserAgreementWindow(WelActivity.this, rootView, str, new IUserAgreement.CallBack() {
            @Override
            public void callback(boolean isUserAgree) {
                if (isUserAgree) {
                    switch (type) {
                        case AbsThirdLogin.Qq:
                            QQLogin();
                            break;
                        case AbsThirdLogin.Weixin:
                            WechatLogin();
                            break;
                        case AbsThirdLogin.Sinaweibo:
                            WeiboLogin();
                            break;
                    }
                }
            }
        });
    }

    private void initThirdLogin() {
        ThirdLoginCallbackHelper.getInstance().addOnThirdLoginListener(this);
        if (ThirdLoginCallbackHelper.getInstance().isAvailableCallBack()) {
            boolean isError = ThirdLoginCallbackHelper.getInstance().isError();
            if (isError) {
                Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                doThirdLogin(ThirdLoginCallbackHelper.getInstance().getAccessToken(),
                        ThirdLoginCallbackHelper.getInstance().getOpenId(),
                        ThirdLoginCallbackHelper.getInstance().getThirdType());
            }
        }
    }

    /**
     * @see {@link Activity#onNewIntent}
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    private DataRequest.DataCallback<Account> callback = new DataRequest.DataCallback<Account>() {
        @Override
        public void onSuccess(boolean isAppend, Account data) {
            if (isthirdLogin) {
                saveLoginToken(token, openid, thirdType, app_id);
            } else {
//                saveLoginRequestData(_userNameEdt.getText().toString(), _userPwdEdt.getText().toString());
            }
            if (loading != null) {
                loading.dismiss();
            }
            RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
            WelActivity.this.finish();
            mbLogining = false;

            //阿里云推送需要在登录成功之后设置DeviceId
            accountApi.submitDeviceId(WelActivity.this, CoreApp.getInstance().getDeviceId(), new DataRequest.DataCallback<Void>() {
                @Override
                public void onSuccess(boolean isAppend, Void data) {
                    Log.e("AliyunPush", "更新推送设备成功");
                }

                @Override
                public void onFail(ApiException e) {
                    Log.e("AliyunPush", "更新推送设备失败");
                }
            });
        }

        @Override
        public void onFail(ApiException e) {
            mbLogining = false;
            e.printStackTrace();
            Toast.makeText(context, JsonCreater.getErrorMsgFromApi(e.toString()), Toast.LENGTH_SHORT).show();
            if (loading != null) {
                loading.dismiss();
            }
        }
    };

    private void LoginToken(final int requestCode, final int resultCode, final Intent data) {
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.d(TAG, "-->onActivityResult " + requestCode + " resultCode=" + resultCode);
        //        super.onActivityResult(requestCode, resultCode, data);
        //        LoginToken(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 2: {
                    String name = data.getStringExtra("name");
                    String pass = data.getStringExtra("pass");
                    try {
                        accountApi.login(name, pass, callback);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void QQLogin() {
        thirdLogin(AbsThirdLogin.Qq);

    }

    private void WechatLogin() {
        thirdLogin(AbsThirdLogin.Weixin);
    }

    private void WeiboLogin() {
        thirdLogin(AbsThirdLogin.Sinaweibo);
    }

    private AbsThirdLogin thirdLogin;

    protected void thirdLogin(int type) {
        loading = CustomeProgressDialog.show(this, "正在登录...");
        isthirdLogin = true;
        thirdLogin = ThirdLoginFactory.createThirdLogin(this, type, ThirdLoginCallbackHelper.getInstance());
        thirdLogin.login();
    }

    private void doThirdLogin(String access_token, String openId, int oauthtype) {
        if (loading == null || !loading.isShowing()) {
            loading = CustomeProgressDialog.show(this, "正在登录...");
        }
        token = access_token;
        openid = openId;
        thirdType = getThirdTypeString(oauthtype);
        app_id = getThirdClientId(oauthtype);
        accountApi.thirdLogin(thirdType, app_id,
                token, openId, callback);
    }

    public String getThirdTypeString(int type) {
        if (type == AbsThirdLogin.Qq) {
            return "qq";
        } else if (type == AbsThirdLogin.Sinaweibo) {
            return "weibo";
        } else {
            return "weixin";
        }
    }

    public String getThirdClientId(int type) {
        if (type == AbsThirdLogin.Qq) {
            return Constants.QQ_APP_ID;
        } else if (type == AbsThirdLogin.Sinaweibo) {
            return Constants.WEIBO_APP_KEY;
        } else {
            return Constants.WeChat_APP_ID;
        }
    }


    private void saveLoginRequestData(String username, String password) {
        if (misSaveAccount) {
            SharedPreferences mySharedPreferences = getSharedPreferences(LogonContancts.KEY_ACCOUNT_INFO, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putBoolean(LogonContancts.KEY_IS_SAVE_ACCOUNT, misSaveAccount);
            editor.putString(LogonContancts.KEY_USER_NAME, username);
            editor.putString(LogonContancts.KEY_PASSWORD, password);
            editor.commit();
        }
    }

    private String readLoginNameData() {
        SharedPreferences mySharedPreferences = getSharedPreferences(LogonContancts.KEY_ACCOUNT_INFO, Activity.MODE_PRIVATE);
        String userName = mySharedPreferences.getString(LogonContancts.KEY_USER_NAME, "");
        return userName;
    }

    public void saveLoginToken(String token,
                               String openid,
                               String type,
                               String app_id) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(LogonContancts.KEY_ACCOUNT_INFO, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(LogonContancts.KEY_IS_TP_ACCOUNT_SAVED, true);
        editor.putString(LogonContancts.KEY_ACCESS_TOKEN, token);
        editor.putString(LogonContancts.KEY_OPEN_ID, openid);
        editor.putString(LogonContancts.KEY_TP_TYPE, type);
        editor.putString(LogonContancts.KEY_TP_APP_ID, app_id);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        if (loading != null) {
            loading.dismiss();
        }
        ThirdLoginCallbackHelper.getInstance().removeOnThirdLoginListener(this);
        super.onDestroy();
    }

    @Override
    public void onThirdLoginCompelete(String access_token, String openId, int oauthtype) {
        if (thirdLogin != null) {
            thirdLogin.onDestory();
        }//结束的时候调用登录流程完毕，防止内存泄露哦
        doThirdLogin(access_token, openId, oauthtype);
    }

    @Override
    public void onThirdLoginError(int oauthtype) {
        if (loading != null) {
            loading.dismiss();
        }
        if (thirdLogin != null) {
            thirdLogin.onDestory();
        }//结束的时候调用登录流程完毕，防止内存泄露哦
    }

}
