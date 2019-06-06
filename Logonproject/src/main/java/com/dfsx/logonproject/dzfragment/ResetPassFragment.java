package com.dfsx.logonproject.dzfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.logonproject.fragment.BaseResultFragment;

/**
 * Created by heyang on 2017/10/23
 */

public class ResetPassFragment extends BaseResultFragment implements View.OnClickListener, EditChangedLister.EditeTextStatuimpl {
    EditText _newPassEdt;
    EditText _againPassEdt;
    TextView _comfirebtn;
    private AccountApi _accountApi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_reset_pass, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //    super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof WhiteTopBarActivity) {
            ((WhiteTopBarActivity) getActivity()).getTopBarLeft().
                    setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
        }
        _newPassEdt = (EditText) view.findViewById(R.id.pass_new_edt);
        _againPassEdt = (EditText) view.findViewById(R.id.pass_again_edt);
        _newPassEdt.addTextChangedListener(new EditChangedLister(this));
        _comfirebtn = (TextView) view.findViewById(R.id.comfirm_btn);
        _comfirebtn.setOnClickListener(this);
        _accountApi = new AccountApi(getContext());
    }

    @Override
    public void onClick(View v) {
        if (v == _comfirebtn) {
            final String telePhone = CoreApp.getInstance().get_telePhone();
            final String pass = _newPassEdt.getText().toString().trim();
            String agapass = _againPassEdt.getText().toString().trim();
            String verity = CoreApp.getInstance().get_verfityNumber();
            if (pass == null || TextUtils.isEmpty(pass)) {
                Toast.makeText(getContext(), "新密码不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            if (!TextUtils.equals(pass, agapass)) {
                Toast.makeText(getContext(), "两次输入的密码不一致", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                _accountApi.resetPassWord(telePhone, pass, verity, new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        if ((Boolean) data) {
                            Toast.makeText(getContext(), "密码修改成功", Toast.LENGTH_LONG).show();
                            Account user = CoreApp.getInstance().getUser();
                            if (user == null) {
                                //忘记密码 重置密码  保存本地
                                saveLoginRequestData(telePhone, "");
                            }
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "密码修改失败", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        ToastUtils.toastApiexceFunction(getActivity(), e);
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if (_comfirebtn == null) return;
        if (isHas) {
            _comfirebtn.setBackgroundColor(getActivity().getResources().getColor(R.color.public_purple_bkg));
        } else {
            _comfirebtn.setBackgroundColor(getActivity().getResources().getColor(R.color.button_enable_color));
        }
    }
}
