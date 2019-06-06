package com.dfsx.logonproject.dzfragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.qqtheme.framework.WheelPicker.entity.City;
import cn.qqtheme.framework.WheelPicker.entity.County;
import cn.qqtheme.framework.WheelPicker.entity.Province;
import cn.qqtheme.framework.WheelPicker.picker.AddressPicker;
import cn.qqtheme.framework.init.AddressInitTask;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.CircleImageView;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.HttpUtils;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.core.rx.RxBus;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.logonproject.fragment.AddressChangeFragment;
import com.dfsx.logonproject.fragment.PersonInforFragment;
import com.dfsx.logonproject.model.ChangedUserInfo;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import com.google.zxing.common.StringUtils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by heyang on 2017/10/23
 */

public class UsertBasicFragment2 extends LogonFragment {
    private EditText _signTxt;
    private RadioGroup _sexGroups;
    private TextView _comfirmBtn, _addressTxt;
    private AccountApi _accountApi;
    CircleImageView _UserImage;
    String imagpath;
    private String _provinceStr;
    private String _cityStr;
    private String _regionStr;
    private String _address;
    View _userImageContainer;
    ChangedUserInfo changedUserInfo;
    ProgressDialog _progressDiaog;
    private boolean isComplated = true;

    private TextView topTelPhonTxt;
    private EditText pwdEdt, pwdAgaginEdt, nickNameEdt;
    private ImageView showPwdBtn;
    private boolean isShowPwd = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_user_basic2, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        topTelPhonTxt = (TextView) view.findViewById(R.id.top_tel_txt);
        pwdEdt = (EditText) view.findViewById(R.id.logon_pwd_edt);
        pwdAgaginEdt = (EditText) view.findViewById(R.id.logo_agein_pwd_edt);
        nickNameEdt = (EditText) view.findViewById(R.id.top_nickname_edt);
        showPwdBtn = (ImageView) view.findViewById(R.id.show_pwd_btn);
        showPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowPwd) {
                    isShowPwd = true;
                    pwdEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPwdBtn.setImageResource(R.drawable.show_pwd_icon);
                } else {
                    isShowPwd = false;
                    pwdEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPwdBtn.setImageResource(R.drawable.hide_pwd_icon);
                }
            }
        });
        _userImageContainer = (View) view.findViewById(R.id.user_logo_view);
        _UserImage = (CircleImageView) view.findViewById(R.id.user_imag_btn);
        _sexGroups = (RadioGroup) view.findViewById(R.id.group_sex_atr);
        _comfirmBtn = (TextView) view.findViewById(R.id.comfirm_btn);
        _signTxt = (EditText) view.findViewById(R.id.user_b_sign_txt);
        _addressTxt = (TextView) view.findViewById(R.id.user_b_address);
        _comfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isComplated) return;
                if (!TextUtils.equals(pwdEdt.getText().toString().trim(), pwdAgaginEdt.getText().toString().trim())
                        ) {
                    ToastUtils.toastMsgFunction(getActivity(), "两次输入的密码不一致");
                    return;
                }
                isComplated = false;
                if (_progressDiaog != null && !_progressDiaog.isShowing())
                    _progressDiaog.show();

                onCommit();

//                changedUserInfo.resetValues();
//                makeUploadUserBean(changedUserInfo);
//                changedUserInfo.setSignature(_signTxt.getText().toString());
//
//                //2018-8-8
//                changedUserInfo.setNickname(nickNameEdt.getText().toString());
//                changedUserInfo.setDetail_address(_addressTxt.getText().toString());
//
//                int sex = 0;
//                if (_sexGroups.getCheckedRadioButtonId() == R.id.bar_man_btn) {
//                    sex = 1;
//                } else if (_sexGroups.getCheckedRadioButtonId() == R.id.bar_woman_btn) {
//                    sex = 2;
//                }
//                changedUserInfo.setSex(sex);
//                changedUserInfo.setCity(_cityStr);
//                changedUserInfo.setProvince(_provinceStr);
//                changedUserInfo.setRegion(_regionStr);
//                changedUserInfo.setTelePhone(CoreApp.getInstance().get_telePhone());

//                Observable.just("")
//                        .observeOn(Schedulers.io())
//                        .flatMap(new Func1<String, Observable<JSONObject>>() {
//                            @Override
//                            public Observable<JSONObject> call(String user) {
//                                String relativeUrl = "";
//                                if (imagpath != null &&
//                                        !TextUtils.isEmpty(imagpath)) {
//                                    try {
//                                        relativeUrl = _accountApi.uploadAvatar(AccountApi.getAvaterUploadURL(), imagpath);
//                                    } catch (ApiException e) {
//                                        isComplated = true;
//                                        e.printStackTrace();
//                                    }
//                                }
//                                changedUserInfo.setAvatar_path(relativeUrl);
////                                makeUploadUserBean(changedUserInfo);
//                                JSONObject params = createParams(changedUserInfo);
////                                changedUserInfo.resetValues();
//                                return Observable.just(params);
//                            }
//                        })
//                        .map(new Func1<JSONObject, Boolean>() {
//                            @Override
//                            public Boolean call(JSONObject params) {
//                                Boolean falg = true;
//                                try {
//                                    JSONObject jbonj = JsonHelper.httpPutJsonWithException(CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current",
//                                            params, CoreApp.getInstance().getCurrentToken());
//                                    if (jbonj != null && jbonj.optString("error").equals("500")) {
//                                        falg = false;
//                                        isComplated = true;
//                                        throw new ApiException("500 个人信息修改没有成功");
//                                    }
//                                } catch (Exception e) {
//                                    errormsg = e.toString();
//                                    falg = false;
//                                    isComplated = true;
//                                    e.printStackTrace();
//                                }
//                                //获取网络头像地址
//                                if (falg) {
//                                    String userString = _accountApi.getCurrentUserInfor();
//                                    changedUserInfo.setAvatar_path(userString);
//                                }
//                                return falg;
//                            }
//                        })
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<Boolean>() {
//                            @Override
//                            public void onCompleted() {
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onNext(Boolean flag) {
//                                closedDialog();
//                                if (flag) {
//                                    Account account = CoreApp.getInstance().getUser();
//                                    if (account != null && account.getUser() != null
//                                            && changedUserInfo != null) {
//                                        account.getUser().setNickname(changedUserInfo.getNickname());
//                                        account.getUser().setSex(changedUserInfo.getSex());
//                                        account.getUser().setAvatar_url(changedUserInfo.getAvatar_path());
//                                        account.getUser().setProvince(changedUserInfo.getProvince());
//                                        account.getUser().setRegion(changedUserInfo.getRegion());
//                                        account.getUser().setCity(changedUserInfo.getCity());
//                                        account.getUser().setDetail_address(changedUserInfo.getDetail_address());
//                                        account.getUser().setSignature(changedUserInfo.getSignature());
//                                        account.getUser().setSignature(changedUserInfo.getSignature());
//                                        account.getUser().setPhone_number(changedUserInfo.getTelePhone());
////                                        CoreApp.getInstance().getUser().setUser(account.getUser());
////                                        changedUserInfo.resetValues();
//                                        RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
//                                    }
//                                    Toast.makeText(getContext(), "完善个人信息成功", Toast.LENGTH_SHORT).show();
//                                    getActivity().finish();
//                                } else {
//                                    String str = JsonCreater.getErrorMsg(errormsg);
//                                    ToastUtils.toastMsgFunction(getActivity(), "完善信息失败" + str);
//                                }
//                            }
//                        });
            }
        });
        _userImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSelectImage();
            }
        });
        _accountApi = new AccountApi(getContext());
        _addressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                frgStartWhiteTopBarActivityForResult(getActivity(), AddressChangeFragment.class.getName(), "编辑地址", "保存", 11);


                Intent intent = new Intent(getActivity(), WhiteTopBarActivity.class);
                intent.putExtra(WhiteTopBarActivity.KEY_FRAGMENT_NAME, AddressChangeFragment.class.getName());
                intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_TITLE, "编辑地址");
                intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_RIGHT_TEXT, "保存");

                String[] provice = {_provinceStr
                        , _cityStr, _regionStr};
                intent.putExtra("provice", provice);
                intent.putExtra("address", _address);
                startActivityForResult(intent, 11);

//                new AddressInitTask(getActivity(), new AddressPicker.OnAddressPickListener() {
//                    @Override
//                    public void onAddressPicked(Province province, City city, County county) {
//                        _provinceStr = province.getAreaName();
//                        _cityStr = city.getAreaName();
//                        _regionStr = county.getAreaName();
//                        String addree = province.getAreaName() + city.getAreaName() + county.getAreaName();
//                        _addressTxt.setText(addree);
//                    }
//                }).execute("四川", "成都", "高新区");
            }
        });
        changedUserInfo = new ChangedUserInfo();
        _progressDiaog = new ProgressDialog(getActivity());
        _progressDiaog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progressDiaog.setMessage("提交中");
        topTelPhonTxt.setText(iniTelePhone());
    }

    protected void frgStartWhiteTopBarActivityForResult(Context context, String fragName, String title, String rightTitle, int requestCode) {
        Intent intent = new Intent(context, WhiteTopBarActivity.class);
        intent.putExtra(WhiteTopBarActivity.KEY_FRAGMENT_NAME, fragName);
        intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_TITLE, title);
        intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_RIGHT_TEXT, rightTitle);
        startActivityForResult(intent, requestCode);
    }

    public void updatePersonInfo() {
        changedUserInfo.resetValues();
//        makeUploadUserBean(changedUserInfo);
        changedUserInfo.setSignature(_signTxt.getText().toString());

        //2018-8-8
        changedUserInfo.setNickname(nickNameEdt.getText().toString());
        changedUserInfo.setDetail_address(_address);

        int sex = 0;
        if (_sexGroups.getCheckedRadioButtonId() == R.id.bar_man_btn) {
            sex = 1;
        } else if (_sexGroups.getCheckedRadioButtonId() == R.id.bar_woman_btn) {
            sex = 2;
        }
        changedUserInfo.setSex(sex);
        changedUserInfo.setCity(_cityStr);
        changedUserInfo.setProvince(_provinceStr);
        changedUserInfo.setRegion(_regionStr);
        changedUserInfo.setTelePhone(CoreApp.getInstance().get_telePhone());
    }

    protected void makeUploadUserBean(ChangedUserInfo changedUserInfo) {
        if (CoreApp.getInstance().getUser() == null) return;
        Account.UserBean userBean = CoreApp.getInstance().getUser().getUser();
        if (changedUserInfo.getNickname() == null)
            changedUserInfo.setNickname(userBean.getNickname());
        if (changedUserInfo.getEmail() == null)
            changedUserInfo.setEmail(userBean.getEmail());
        if (changedUserInfo.getMobile() == null)
            changedUserInfo.setMobile(userBean.getMobile());
        if (changedUserInfo.getSex() == -1)
            changedUserInfo.setSex(userBean.getSex());
        if (changedUserInfo.getSignature() == null)
            changedUserInfo.setSignature(userBean.getSignature());
        if (changedUserInfo.getProvince() == null)
            changedUserInfo.setProvince(userBean.getProvince());
        if (changedUserInfo.getCity() == null)
            changedUserInfo.setCity(userBean.getCity());
        if (changedUserInfo.getRegion() == null)
            changedUserInfo.setRegion(userBean.getRegion());
        if (changedUserInfo.getAvatar_path() == null)
            changedUserInfo.setAvatar_id(userBean.getAvatar_id());
        if (changedUserInfo.getDetail_address() == null)
            changedUserInfo.setDetail_address(userBean.getDetail_address());
        if (changedUserInfo.getBirthday() == -1)
            changedUserInfo.setBirthday(userBean.getBirthday());
    }

    private boolean isSuccess = true;

    public void onCommit() {
        errormsg = "";
        isSuccess = true;
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .concatMap(new Func1<String, Observable<Bundle>>() {
                    @Override
                    public Observable<Bundle> call(String str) {
                        Observable.OnSubscribe<Bundle> subscribe = new Observable.OnSubscribe<Bundle>() {
                            @Override
                            public void call(final Subscriber<? super Bundle> subscriber) {
                                String name = nickNameEdt.getText().toString();
                                String pass = pwdEdt.getText().toString();
                                String telephone = CoreApp.getInstance().get_telePhone();
                                String verity = CoreApp.getInstance().get_verfityNumber();
                                try {
                                    boolean result = _accountApi.resgisterSysnic(name, pass, telephone, verity);
                                    if (!result) {
                                        isSuccess = false;
                                        errormsg = "连接超时,注册用户失败";
                                        Log.e("TAG", "onCommit() resgisterSysnic  failed ");
                                    }
                                } catch (ApiException e) {
                                    isSuccess = false;
                                    Log.e("TAG", "onCommit() resgisterSysnic  ApiException " + e.toString());
                                    isComplated = true;
                                    isSuccess = false;
                                    errormsg = e.toString();
                                    e.printStackTrace();
                                }
                                if (isSuccess) {
                                    try {
                                        _accountApi.loginSysnic(telephone, pass);
                                    } catch (ApiException e) {
                                        Log.e("TAG", "onCommit() loginSysnic  ApiException " + e.toString());
                                        isSuccess = false;
                                        isComplated = true;
                                        errormsg = e.toString();
                                        e.printStackTrace();
                                    }
                                }
                                Bundle bundle = new Bundle();
                                subscriber.onNext(bundle);
                            }
                        };
                        return Observable.create(subscribe);
                    }
                })
                .flatMap(new Func1<Bundle, Observable<JSONObject>>() {
                    @Override
                    public Observable<JSONObject> call(Bundle user) {
                        updatePersonInfo();
                        String relativeUrl = "";
                        if (isSuccess && imagpath != null &&
                                !TextUtils.isEmpty(imagpath)) {
                            try {
                                relativeUrl = _accountApi.uploadAvatar(AccountApi.getAvaterUploadURL(), imagpath);
                            } catch (ApiException e) {
                                Log.e("TAG", "onCommit() uploadAvatar ApiException" + e.toString());
                                isSuccess = false;
                                errormsg = e.toString();
                                isComplated = true;
                                e.printStackTrace();
                            }
                        }
                        changedUserInfo.setAvatar_path(relativeUrl);
                        makeUploadUserBean(changedUserInfo);
                        JSONObject params = createParams(changedUserInfo);
                        return Observable.just(params);
                    }
                })
                .map(new Func1<JSONObject, Boolean>() {
                    @Override
                    public Boolean call(JSONObject params) {
                        if (!isSuccess)
                            return false;
                        try {
//                            String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current";
                            Log.e("TAG", "onCommit() token==" + CoreApp.getInstance().getCurrentToken());
//                            String response = HttpUtil.exePut(url, new HttpParameters(params), CoreApp.getInstance().getCurrentToken());
                            JSONObject jbonj = JsonHelper.httpPutJsonWithException(CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current",
                                    params, CoreApp.getInstance().getCurrentToken());
//                            JSONObject jbonj = JsonCreater.jsonParseString(response);
                            if (!(jbonj == null || TextUtils.isEmpty(jbonj.toString()))) {
                                if (jbonj.has("error")) {
                                    isSuccess = false;
                                    throw new ApiException(jbonj.optString("message"));
                                }
                            }
                        } catch (Exception e) {
                            Log.e("TAG", "onCommit() httpPutJsonWithException ApiException:" + e.toString());
                            errormsg = e.toString();
                            isSuccess = false;
                            isComplated = true;
                            e.printStackTrace();
                        }
                        //获取网络头像地址
                        if (isSuccess) {
                            try {
                                String userString = _accountApi.getCurrentUserInfor();
                                changedUserInfo.setAvatar_path(userString);
                            } catch (Exception e) {
                                Log.e("TAG", "onCommit() getCurrentUserInfor ApiException:" + e.toString());
//                                errormsg = e.toString();
//                                isSuccess = false;
                                isComplated = true;
                                e.printStackTrace();
                            }
                        }
                        return isSuccess;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean flag) {
                        closedDialog();
                        if (flag) {
                            Account account = CoreApp.getInstance().getUser();
                            if (account != null && account.getUser() != null
                                    && changedUserInfo != null) {
                                account.getUser().setNickname(changedUserInfo.getNickname());
                                account.getUser().setSex(changedUserInfo.getSex());
                                account.getUser().setAvatar_url(changedUserInfo.getAvatar_path());
                                account.getUser().setProvince(changedUserInfo.getProvince());
                                account.getUser().setRegion(changedUserInfo.getRegion());
                                account.getUser().setCity(changedUserInfo.getCity());
                                account.getUser().setDetail_address(changedUserInfo.getDetail_address());
                                account.getUser().setSignature(changedUserInfo.getSignature());
                                account.getUser().setSignature(changedUserInfo.getSignature());
                                account.getUser().setPhone_number(changedUserInfo.getTelePhone());
//                                        CoreApp.getInstance().getUser().setUser(account.getUser());
//                                        changedUserInfo.resetValues();
                                RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
                            }
                            Toast.makeText(getContext(), "完善个人信息成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            isComplated = true;
                            String str = JsonCreater.getErrorMsg(errormsg);
                            ToastUtils.toastMsgFunction(getActivity(), "完善信息失败" + str);
                        }
                    }
                });
    }

    protected JSONObject createParams(ChangedUserInfo changedUserInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nickname", changedUserInfo.getNickname());
            if (!CoreApp.getInstance().isOpenVerityTele()) {
                jsonObject.put("email", changedUserInfo.getEmail());
            }
            jsonObject.put("mobile", changedUserInfo.getMobile());
            jsonObject.put("sex", changedUserInfo.getSex());
            jsonObject.put("signature", changedUserInfo.getSignature());
            jsonObject.put("province", changedUserInfo.getProvince());
            jsonObject.put("city", changedUserInfo.getCity());
            jsonObject.put("birthday", changedUserInfo.getBirthday());
            jsonObject.put("region", changedUserInfo.getRegion());
            jsonObject.put("detail_address", changedUserInfo.getDetail_address());
            if (changedUserInfo.getAvatar_path() != null)
                jsonObject.put("avatar_path", changedUserInfo.getAvatar_path());
            else
                jsonObject.put("avatar_id", changedUserInfo.getAvatar_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    String errormsg = "";

    public String iniTelePhone() {
        String tel = CoreApp.getInstance().get_telePhone();
        String str = "当期注册账号 ";
        str += tel;
        return str;
    }

    public void closedDialog() {
        isComplated = true;
        if (_progressDiaog != null && _progressDiaog.isShowing())
            _progressDiaog.dismiss();
    }

    public void gotoSelectImage() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ImageFragmentActivity.class);
                intent.putExtra(ImageFragmentActivity.KEY_SINGLE_MODE, true);
                startActivityForResult(intent, 7);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case -1:
                return;
            case 7:
                if (data != null) {
                    ArrayList<MediaModel> selectedList = data.
                            getParcelableArrayListExtra("list");
                    if (selectedList == null || selectedList.size() == 0)
                        break;
                    String filePath = selectedList.get(0).url;
                    if (new File(filePath).exists()) {
                        imagpath = filePath;
                        if (!("").equals(imagpath)) {
                            Util.LoadImageErrorUrl(_UserImage, imagpath, null, R.drawable.icon_defalut_no_login_logo);
                        } else {
//                            _UserImage.setImageResource(R.drawable.icon_defalut_no_login_logo);
                        }
                    }
                }
                break;
            case 11:
                if (data == null) return;
                String[] provice = data.getStringArrayExtra("provice");
                String address = data.getStringExtra("address");
                String strformat = "";
                if (!(provice == null || provice.length == 0)) {
                    strformat = provice[0] + provice[1] + provice[2];
                    _provinceStr = provice[0];
                    _cityStr = provice[1];
                    _regionStr = provice[2];
//                    changedUserInfo.setProvince(provice[0]);
//                    changedUserInfo.setCity(provice[1]);
//                    changedUserInfo.setRegion(provice[2]);
                }
                strformat += address;
                _addressTxt.setText(strformat);
                _address = address;
//                changedUserInfo.setDetail_address(address);
                break;
        }
        //    super.onActivityResult(requestCode, resultCode, data);
    }
}
