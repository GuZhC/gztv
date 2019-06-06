package com.dfsx.logonproject.dzfragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.logonproject.fragment.BaseResultFragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by heyang on 2017/10/23
 */

public class RegVerifyFragment extends BaseResultFragment implements View.OnClickListener, EditChangedLister.EditeTextStatuimpl {

    EditText _verityEdt;
    TextView _retryBtn, _comfirmBtn, _telePhoneTxt, dzAgressBtn;
    ImageView _backBtn;
    private AccountApi _accountApi;
    private TimeCount time;
    private boolean _isAutoSend = false;
    private boolean _startCount = false;
    private boolean isRegister = false;  //是注册
    private EditText _telePhoneText;
    private View topTitleView;
    int flag = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_reg_verfity, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            _isAutoSend = getArguments().getBoolean("isSend");
            isRegister = getArguments().getBoolean("isRegister");
            flag = getArguments().getInt("gotocheck");
        }
        dzAgressBtn = (TextView) view.findViewById(R.id.dz_agreee_btn);
        dzAgressBtn.setOnClickListener(this);
        topTitleView = (View) view.findViewById(R.id.top_back);
        _telePhoneText = (EditText) view.findViewById(R.id.pa_v_telpe_edt);
        _verityEdt = (EditText) view.findViewById(R.id.verfy_number_edt);
        _retryBtn = (TextView) view.findViewById(R.id.verity_retry_btn);
        _retryBtn.setOnClickListener(this);
        _comfirmBtn = (TextView) view.findViewById(R.id.verity_comfirm_btn);
        _comfirmBtn.setOnClickListener(this);
        _backBtn = (ImageView) view.findViewById(R.id.back_btn);
        _backBtn.setOnClickListener(this);
        _telePhoneTxt = (TextView) view.findViewById(R.id.verity_telephone_txt);
        _verityEdt.addTextChangedListener(new EditChangedLister(this));
        _accountApi = new AccountApi(getContext());
        time = new TimeCount(60000, 1000);
        if (_isAutoSend) {
            _startCount = true;
            time.start();
            _telePhoneTxt.setText(getActivity().getResources().getString(R.string.register_verifty_hit) + CoreApp.getInstance().get_telePhone());
        } else {
            View parent = (View) _telePhoneText.getParent();
            parent.setVisibility(View.VISIBLE);
            _retryBtn.setBackgroundColor(getActivity().getResources().getColor(R.color.public_purple_bkg));
            _retryBtn.setText("获取");
        }
        if (getActivity() instanceof DefaultFragmentActivity && flag == 1) {
            topTitleView.setVisibility(View.VISIBLE);
        }
//        if (isRegister) {
//            View paran = (View) dzAgressBtn.getParent();
//            if (paran != null)
//                paran.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if (_comfirmBtn == null) return;
        if (isHas) {
            _comfirmBtn.setBackgroundColor(getActivity().getResources().getColor(R.color.public_purple_bkg));
        } else {
            _comfirmBtn.setBackgroundColor(getActivity().getResources().getColor(R.color.button_enable_color));
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
            _retryBtn.setBackgroundColor(Color.parseColor("#999999"));
            _retryBtn.setClickable(false);
            _retryBtn.setText("(" + millisUntilFinished / 1000 + ") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            rsetTime();
        }
    }

    public void rsetTime() {
        if (getActivity() == null) return;
        _startCount = false;
        _retryBtn.setText("重新获取验证码");
        _retryBtn.setClickable(true);
//        _retryBtn.setBackgroundColor(Color.parseColor("#7068f5"));
        _retryBtn.setBackgroundColor(getActivity().getResources().getColor(R.color.public_purple_bkg));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (time != null && _startCount)
            time.cancel();
    }

    @Override
    public void onClick(View v) {
        if (v == _retryBtn) {
            //重新发送短信
            String telephonr;
            int type = 1;
            if (_isAutoSend) {
                telephonr = CoreApp.getInstance().get_telePhone();
            } else {
                if (!isRegister) {
                    type = 2;
                }
                telephonr = _telePhoneText.getText().toString().trim();
            }
            if (telephonr == null || TextUtils.isEmpty(telephonr)) {
                Toast.makeText(getActivity(), "手机号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            _verityEdt.setText("");
            try {
                _accountApi.sendPhoneMessage(telephonr, type, new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {

                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        ToastUtils.toastApiexceFunction(getActivity(), e);
                    }
                });
                if (time != null) {
                    time.start();
                }
            } catch (ApiException e) {
                e.printStackTrace();
                ToastUtils.toastApiexceFunction(getActivity(), e);
            }
        } else if (v == _comfirmBtn) {
            final String verfyNumber = _verityEdt.getText().toString().trim();
            if (verfyNumber == null || TextUtils.isEmpty(verfyNumber)) {
                Toast.makeText(getActivity(), "验证码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            String telephonr = "";
            if (_isAutoSend || isRegister) {
                if (isRegister) {
                    telephonr = _telePhoneText.getText().toString().trim();
                    CoreApp.getInstance().set_telePhone(telephonr);
                } else
                    telephonr = CoreApp.getInstance().get_telePhone();
                try {
                    _accountApi.checkVerifyValid(telephonr, verfyNumber, 1, new DataRequest.DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(boolean isAppend, Boolean data) {
                            if (data) {
                                if (_isAutoSend || isRegister) {
                                    CoreApp.getInstance().set_verfityNumber(verfyNumber);
//                                    DefaultFragmentActivity.start(getContext(), UserInfoFragment.class.getName());
                                    WhiteTopBarActivity.startAct(getContext(), UsertBasicFragment2.class.getName(), "完善个人信息");
                                    getActivity().finish();
                                } else {
                                    Account user = CoreApp.getInstance().getUser();
                                    if (user != null) {
                                        user.getUser().setIs_verified(true);
                                    }
                                    Toast.makeText(getContext(), "验证成功", Toast.LENGTH_SHORT).show();
                                    onFocusChange(false, _verityEdt);
                                    getActivity().finish();
//                                    finishActivityWithResult("veritelphone", verfyNumber, 12);
                                }
                            } else {
                                Toast.makeText(getContext(), "验证失败", Toast.LENGTH_SHORT).show();
                                time.cancel();
                                rsetTime();
                                _verityEdt.setText("");
                                _verityEdt.setHint(getActivity().getResources().getString(R.string.register_verifty_retry_hit));
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
            } else {
                telephonr = _telePhoneText.getText().toString().trim();
                //验证手机号
                try {
                    _accountApi.checkTeleValid(telephonr, verfyNumber, new DataRequest.DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(boolean isAppend, Boolean data) {
                            if (data) {
                                Account user = CoreApp.getInstance().getUser();
                                if (user != null) {
                                    user.getUser().setIs_verified(true);
                                }
                                Toast.makeText(getContext(), "更换手机号成功", Toast.LENGTH_SHORT).show();
//                                getActivity().finish();
                                onFocusChange(false, _verityEdt);
                                finishActivityWithResult("veritelphone", _telePhoneText.getText().toString().trim(), 12);
                            } else {
                                Toast.makeText(getContext(), "更换手机号失败", Toast.LENGTH_SHORT).show();
                                time.cancel();
                                rsetTime();
                                _verityEdt.setText("");
                                _verityEdt.setHint(getActivity().getResources().getString(R.string.register_verifty_retry_hit));
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

        } else if (v == _backBtn) {
            getActivity().finish();
        } else if (v == dzAgressBtn) {
//            DefaultFragmentActivity.start(getActivity(),LiveUserAgreement.class.getName);
        }
    }

}
