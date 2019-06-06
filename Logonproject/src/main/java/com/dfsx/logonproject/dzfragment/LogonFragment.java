package com.dfsx.logonproject.dzfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.act.LoginActivity;
import com.dfsx.logonproject.act.LogonContancts;
import com.dfsx.logonproject.act.RegisterActivity;
import com.dfsx.logonproject.busniness.AccountApi;

/**
 * Created by heyang on 2017/10/23
 */

public class LogonFragment extends Fragment implements View.OnClickListener,EditChangedLister.EditeTextStatuimpl {

    ImageView _backBtn;
    TextView registerBtn;
    EditText _telephoneTxt, _passEdt;
    TextView _comfireBtn;
    TextView _forgetBtn;
    protected AccountApi _accountApi;
    protected String telePhone;
    protected String password;
    boolean mbLogining = false;
    boolean misSaveAccount = true;
    protected Context _content;
    boolean _isOnClick = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _accountApi = new AccountApi(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_reg_logon, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        _content = getActivity();
        registerBtn = (TextView) view.findViewById(R.id.logon_register_btn);
        registerBtn.setOnClickListener(this);
        _backBtn = (ImageView) view.findViewById(R.id.back_btn);
        _backBtn.setOnClickListener(this);
        _telephoneTxt = (EditText) view.findViewById(R.id.logo_telephont_edt);
        _passEdt = (EditText) view.findViewById(R.id.logon_pass_edt);
        _comfireBtn = (TextView) view.findViewById(R.id.logo_comfirm_btn);
        _comfireBtn.setOnClickListener(this);
        _forgetBtn = (TextView) view.findViewById(R.id.logon_forget_btn);
        _forgetBtn.setOnClickListener(this);
        _telephoneTxt.addTextChangedListener(new EditChangedLister(this));
    }

    @Override
    public void onClick(View v) {
        if (v == _backBtn) {
            getActivity().finish();
        } else if (v == registerBtn) {
//            DefaultFragmentActivity.start(getActivity(), RegWelFragment.class.getName());
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
        } else if (v == _comfireBtn) {
            _isOnClick = true;
            telePhone = _telephoneTxt.getText().toString().trim();
            password = _passEdt.getText().toString().trim();
            try {
                _accountApi.login(telePhone, password, callback);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        } else if (v == _forgetBtn) {
//            DefaultFragmentActivity.start(getActivity(), ResetPassVeriFragment.class.getName());
            WhiteTopBarActivity.startAct(getActivity(), ResetPassVeriFragment.class.getName(), "验证手机号");
            getActivity().finish();
        }
    }

    protected DataRequest.DataCallback<Account> callback = new DataRequest.DataCallback<Account>() {
        @Override
        public void onSuccess(boolean isAppend, Account data) {
            saveLoginRequestData(telePhone, password);
//            if (loading != null) {
//                loading.dismiss();
//            }
            RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
            if (_isOnClick)
                getActivity().finish();
            mbLogining = false;

            //阿里云推送需要在登录成功之后设置DeviceId
            _accountApi.submitDeviceId(_content, CoreApp.getInstance().getDeviceId(), new DataRequest.DataCallback<Void>() {
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
            Toast.makeText(_content, JsonCreater.getErrorMsgFromApi(e.toString()), Toast.LENGTH_SHORT).show();
        }
    };

    private void saveLoginRequestData(String username, String password) {
        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences(LogonContancts.KEY_ACCOUNT_INFO, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(LogonContancts.KEY_IS_SAVE_ACCOUNT, misSaveAccount);
        editor.putString(LogonContancts.KEY_USER_NAME, username);
        editor.putString(LogonContancts.KEY_PASSWORD, password);
        editor.commit();
    }

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if(_comfireBtn==null) return;
        if (isHas) {
            _comfireBtn.setBackgroundColor(getResources().getColor(R.color.public_purple_bkg));
        } else {
            _comfireBtn.setBackgroundColor(0xff8c8c8c);
        }
    }
}
