package com.dfsx.logonproject.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.*;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.ClearEditText;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.common.view.IUserAgreement;
import com.dfsx.core.common.view.UserAgreementManger;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.act.LoginActivity;
import com.dfsx.logonproject.act.LogonContancts;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.logonproject.busniness.ThirdLoginCallbackHelper;
import com.dfsx.logonproject.dzfragment.RegVerifyFragment;
import com.dfsx.logonproject.fragment.BaseResultFragment;
import com.dfsx.thirdloginandshare.Constants;
import com.dfsx.thirdloginandshare.login.AbsThirdLogin;
import com.dfsx.thirdloginandshare.login.ThirdLoginFactory;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;

/**
 * Created by heyang on 2017/10/23
 */

public class RegisterActivity extends Activity implements View.OnClickListener, AbsThirdLogin.OnThirdLoginListener, EditChangedLister.EditeTextStatuimpl {
    private ImageView _backBtn;
    private TextView _logoBtn;
    private ClearEditText _telephoneEdt;
    private TextView _verifyBtn;
    private ImageView _weixinBtn, _qqBtn, _weiBoBtn;
    private AccountApi _AccountApi;
    private View rootView;
    private CustomeProgressDialog loading;
    private boolean isthirdLogin;
    private AbsThirdLogin thirdLogin;
    private String userName;
    private String password;
    private String token;
    private String openid;
    private String thirdType;
    private String app_id;
    private boolean mbLogining = false;
    private Context _conText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.frag_reg_wel, null);
        setContentView(rootView);
        _conText = this;
        _logoBtn = (TextView) rootView.findViewById(R.id.reg_logon_btn);
        _logoBtn.setOnClickListener(this);
        _backBtn = (ImageView) rootView.findViewById(R.id.wel_back_btn);
        _backBtn.setOnClickListener(this);
        _telephoneEdt = (ClearEditText) rootView.findViewById(R.id.wel_telephone_edt);
        _verifyBtn = (TextView) rootView.findViewById(R.id.get_verift_btn);
        _verifyBtn.setOnClickListener(this);
        _weixinBtn = (ImageView) rootView.findViewById(R.id.bottom_weixin_btn);
        _weixinBtn.setOnClickListener(this);
        _qqBtn = (ImageView) rootView.findViewById(R.id.bottom_qq_btn);
        _qqBtn.setOnClickListener(this);
        _weiBoBtn = (ImageView) rootView.findViewById(R.id.bottom_weibo_btn);
        _weiBoBtn.setOnClickListener(this);
        _AccountApi = new AccountApi(this);
        _telephoneEdt.setClearIconVisible(true);
        _telephoneEdt.addTextChangedListener(new EditChangedLister(this));
        initAction();
        ThirdLoginCallbackHelper.getInstance().addOnThirdLoginListener(this);
    }

    public void initAction() {
        RxView.clicks(_verifyBtn)
                .throttleFirst(3000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        requestVerityCode();
                    }
                });
    }

    public void requestVerityCode() {
        //获取验证码
        String telephone = _telephoneEdt.getText().toString().trim();
        if (telephone == null || TextUtils.isEmpty(telephone)) {
            Toast.makeText(_conText, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        CoreApp.getInstance().set_telePhone(telephone);
        try {
            //1:注册   2修改手机号  3找回密码
            _AccountApi.sendPhoneMessage(telephone, 1, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
//                        Intent intent = new Intent();
//                        intent.putExtra("isSend", true);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isSend", true);
                    WhiteTopBarActivity.startAct(_conText, RegVerifyFragment.class.getName(), "验证手机号", "", bundle);
                    finish();
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                    ToastUtils.toastApiexceFunction(RegisterActivity.this, e);
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == _logoBtn) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (v == _backBtn) {
            finish();
        }
//        else if (v == _verifyBtn) {
//
//        }
        else if (v == _weixinBtn) {
            WechatLogin();
            //            isAgrrrmentUser(AbsThirdLogin.Weixin);
        } else if (v == _qqBtn) {
            QQLogin();
            //            isAgrrrmentUser(AbsThirdLogin.Qq);
        } else if (v == _weiBoBtn) {
            WeiboLogin();
            //            isAgrrrmentUser(AbsThirdLogin.Sinaweibo);
        }
    }

    //弹出用户协议，是否同意
    public void isAgrrrmentUser(final int type) {
        String str = Util.readAssertResource(_conText, "useragreement.txt");
        UserAgreementManger.getInstance().showUserAgreementWindow(RegisterActivity.this, rootView, str, new IUserAgreement.CallBack() {
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


    private void QQLogin() {
        thirdLogin(AbsThirdLogin.Qq);
    }

    private void WechatLogin() {
        thirdLogin(AbsThirdLogin.Weixin);
    }

    private void WeiboLogin() {
        thirdLogin(AbsThirdLogin.Sinaweibo);
    }

    protected void thirdLogin(int type) {
        loading = CustomeProgressDialog.show(_conText, "正在登录...");
        isthirdLogin = true;
        thirdLogin = ThirdLoginFactory.createThirdLogin(_conText, type, ThirdLoginCallbackHelper.getInstance());
        thirdLogin.login();
    }

    private void doThirdLogin(String access_token, String openId, int oauthtype) {
        if (loading == null || !loading.isShowing()) {
            loading = CustomeProgressDialog.show(_conText, "正在登录...");
        }
        token = access_token;
        openid = openId;
        thirdType = getThirdTypeString(oauthtype);
        app_id = getThirdClientId(oauthtype);
        _AccountApi.thirdLogin(thirdType, app_id,
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

    private DataRequest.DataCallback<Account> callback = new DataRequest.DataCallback<Account>() {
        @Override
        public void onSuccess(boolean isAppend, Account data) {
            if (isthirdLogin) {
                saveLoginToken(token, openid, thirdType, app_id);
            }
            if (loading != null) {
                loading.dismiss();
            }
            RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
            finish();
            mbLogining = false;

            //阿里云推送需要在登录成功之后设置DeviceId
            _AccountApi.submitDeviceId(_conText, CoreApp.getInstance().getDeviceId(), new DataRequest.DataCallback<Void>() {
                @Override
                public void onSuccess(boolean isAppend, Void data) {
                    Log.e("AliyunPush", "更新推送设备成功");
                }

                @Override
                public void onFail(ApiException e) {
                    Log.e("AliyunPush", "更新推送设备失败");
                }
            });

            //            Toast.makeText(_conText, "请输入手机号验证", Toast.LENGTH_SHORT).show();
            //处理验证手机号
            //            Intent intent = new Intent();
            //            intent.putExtra("isThrid", true);
            //            DefaultFragmentActivity.start(_conText, RegVerifyFragment.class.getName(), intent);
        }

        @Override
        public void onFail(ApiException e) {
            mbLogining = false;
            e.printStackTrace();
            Toast.makeText(_conText, /*"登录失败"*/ e.getMessage(), Toast.LENGTH_SHORT).show();
            if (loading != null) {
                loading.dismiss();
            }
        }
    };

    public void saveLoginToken(String token,
                               String openid,
                               String type,
                               String app_id) {
        SharedPreferences mySharedPreferences = getSharedPreferences(LogonContancts.KEY_ACCOUNT_INFO, Activity.MODE_PRIVATE);
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

    /**
     * 判断手机格式是否正确
     *
     * @param mobiles
     * @return 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     */
    public boolean isMobileNO(String mobiles) {
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][34578]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if (_verifyBtn == null) return;
        if (isHas) {
            _verifyBtn.setBackgroundColor(0xff003263);
        } else {
            _verifyBtn.setBackgroundColor(0xff8c8c8c);
        }
    }
}
