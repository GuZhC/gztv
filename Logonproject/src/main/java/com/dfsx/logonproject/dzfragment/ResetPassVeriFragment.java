package com.dfsx.logonproject.dzfragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.logonproject.fragment.BaseResultFragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by heyang on 2017/10/23
 */

public class ResetPassVeriFragment extends Fragment implements View.OnClickListener, EditChangedLister.EditeTextStatuimpl {

    EditText _telephoneEdt, _verityNumEdt;
    TextView _getVertyBtn, _comfirembtn;
    private TimeCount time;
    AccountApi _accoutApi;
    String verityNumber;
    String telephonr;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_reset_verity_pass, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _telephoneEdt = (EditText) view.findViewById(R.id.pa_v_telpe_edt);
        _verityNumEdt = (EditText) view.findViewById(R.id.pa_v_edt);
        _getVertyBtn = (TextView) view.findViewById(R.id.pa_v_get_btn);
        _getVertyBtn.setOnClickListener(this);
        _comfirembtn = (TextView) view.findViewById(R.id.pa_v_comfirm_btn);
        _comfirembtn.setOnClickListener(this);
//        _getVertyBtn.setBackgroundColor(Color.parseColor("#7068f5"));
//        _getVertyBtn.setBackgroundColor(getActivity().getResources().getColor(R.color.public_purple_bkg));
        time = new TimeCount(60000, 1000);
        _accoutApi = new AccountApi(getContext());
        _verityNumEdt.addTextChangedListener(new EditChangedLister(this));
        if (getArguments() != null) {
            String tele = getArguments().getString("tele");
            if (!TextUtils.isEmpty(tele)) {
                _telephoneEdt.setText(tele);
            }
        }
    }

    public void rsetTime() {
        if (getActivity() == null) return;
        _getVertyBtn.setText("重新获取验证码");
        _getVertyBtn.setClickable(true);
        if (getActivity() == null) {
            _getVertyBtn.setBackgroundColor(0xff5193EA);
            return;
        }
        _getVertyBtn.setBackgroundColor(getActivity().getResources().getColor(R.color.public_purple_bkg));
    }

    @Override
    public void onClick(View v) {
        if (v == _getVertyBtn) {
            String telephonr = _telephoneEdt.getText().toString().trim();
            try {
                _accoutApi.sendPhoneMessage(telephonr, 3, new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {

                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()), Toast.LENGTH_SHORT).show();
                    }
                });
                if (time != null) {
                    time.start();
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
            time.start();
        } else if (v == _comfirembtn) {
            //验证验证码是否有效
            //跳转到新设密码界面
            verityNumber = _verityNumEdt.getText().toString().trim();
            if (verityNumber == null || TextUtils.isEmpty(verityNumber)) {
                Toast.makeText(getActivity(), "验证码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            telephonr = _telephoneEdt.getText().toString().trim();
            try {
                _accoutApi.checkVerifyValid(telephonr, verityNumber, 3, new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        if (data) {
                            CoreApp.getInstance().set_verfityNumber(verityNumber);
                            CoreApp.getInstance().set_telePhone(telephonr);
//                            DefaultFragmentActivity.start(getContext(), ResetPassFragment.class.getName());
                            WhiteTopBarActivity.startAct(getContext(), ResetPassFragment.class.getName(), "重置密码");
                            getActivity().finish();
                        } else {
                            if (time != null) time.cancel();
                            rsetTime();
                            _verityNumEdt.setText("");
                            _verityNumEdt.setHint(getActivity().getResources().getString(R.string.register_verifty_retry_hit));
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
        if (_comfirembtn == null) return;
        if (isHas) {
            _comfirembtn.setBackgroundColor(getActivity().getResources().getColor(R.color.public_purple_bkg));
        } else {
            _comfirembtn.setBackgroundColor(getActivity().getResources().getColor(R.color.button_enable_color));
        }
    }

    /**
     * 倒计时类
     */
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            _getVertyBtn.setBackgroundColor(Color.parseColor("#999999"));
            _getVertyBtn.setClickable(false);
            _getVertyBtn.setText("(" + millisUntilFinished / 1000 + ") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            rsetTime();
        }
    }
}
