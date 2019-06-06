package com.dfsx.logonproject.dzfragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.act.LogonContancts;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.logonproject.fragment.BaseResultFragment;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;

/**
 * Created by heyang on 2017/10/23
 */

public class UserInfoFragment extends LogonFragment {
    private EditText _pass;
    private EditText _againPass;
    private EditText _userNickname;
    private TextView _comfirmBtn, _telpeHitTxt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.frag_use_info, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        _pass = (EditText) view.findViewById(R.id.user_pass_edittext);
        _againPass = (EditText) view.findViewById(R.id.user_again_pass_edt);
        _userNickname = (EditText) view.findViewById(R.id.user_nicknanme_edt);
        _telpeHitTxt = (TextView) view.findViewById(R.id.telephon_txt);
        _comfirmBtn = (TextView) view.findViewById(R.id.user_comfierm_btn);
//        _comfirmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onComfirm();
//            }
//        });
        telePhone = CoreApp.getInstance().get_telePhone();
        _telpeHitTxt.setText(telePhone);
        initAction();
        super._content = getActivity();
    }

    public void initAction() {
        RxView.clicks(_comfirmBtn)
                .throttleFirst(3000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        onComfirm();
                    }
                });
    }

    public void onComfirm() {
        //  注册接口
        password = _pass.getText().toString().trim();
        String againPass = _againPass.getText().toString().trim();
        String nickName = _userNickname.getText().toString().trim();
        if (!isValidate(password, againPass)) return;
        telePhone = CoreApp.getInstance().get_telePhone();
        String verity = CoreApp.getInstance().get_verfityNumber();
        try {
            _accountApi.registerDzUser(nickName, password, telePhone, verity, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
                    if ((Boolean) data) {
                        //默认登陆
                        try {
                            _accountApi.login(telePhone, password, callback);
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                        // 信息完善页面
                        DefaultFragmentActivity.start(getContext(), UsertBasicFragment.class.getName());
                        getActivity().finish();
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidate(String pwd, String againPwd) {
        boolean flag = true;
        if (pwd == null || TextUtils.isEmpty(pwd)) {
            Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
            flag = false;
        }
//        if (againPwd == null || TextUtils.isEmpty(againPwd)) {
//            Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
//            flag = false;
//        }
        if (!TextUtils.equals(pwd, againPwd)) {
            Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        return flag;
    }
}
