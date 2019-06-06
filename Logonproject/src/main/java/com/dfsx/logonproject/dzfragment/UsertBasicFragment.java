package com.dfsx.logonproject.dzfragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.CircleImageView;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.core.rx.RxBus;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.logonproject.fragment.PersonInforFragment;
import com.dfsx.logonproject.model.ChangedUserInfo;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by heyang on 2017/10/23
 */

public class UsertBasicFragment extends PersonInforFragment {
    private EditText _signTxt;
    private ImageView _topjumpBtn;
    private RadioGroup _sexGroups;
    private TextView _comfirmBtn, _addressTxt;
    private AccountApi _accountApi;
    CircleImageView _UserImage;
    String imagpath;
    private String _provinceStr;
    private String _cityStr;
    private String _regionStr;
    View _userImageContainer;
    ChangedUserInfo changedUserInfo;
    ProgressDialog _progressDiaog;
    private boolean isComplated = true;
    private String userImageString;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_user_basic, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        _UserImage = (CircleImageView) view.findViewById(R.id.user_imag_btn);
        _sexGroups = (RadioGroup) view.findViewById(R.id.group_sex_atr);
        _comfirmBtn = (TextView) view.findViewById(R.id.comfirm_btn);
        _signTxt = (EditText) view.findViewById(R.id.user_b_sign_txt);
        _addressTxt = (TextView) view.findViewById(R.id.user_b_address);
        _topjumpBtn = (ImageView) view.findViewById(R.id.top_jump_btn);
        _topjumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 直接进入app
//                RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
                getActivity().finish();
            }
        });
        _comfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isComplated) return;
                isComplated = false;
                if (_progressDiaog != null && !_progressDiaog.isShowing())
                    _progressDiaog.show();
                changedUserInfo.resetValues();
                makeUploadUserBean(changedUserInfo);
                changedUserInfo.setSignature(_signTxt.getText().toString());
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
                rx.Observable.just("")
                        .observeOn(Schedulers.io())
                        .flatMap(new Func1<String, Observable<JSONObject>>() {
                            @Override
                            public Observable<JSONObject> call(String user) {
                                String relativeUrl = "";
                                if (imagpath != null &&
                                        !TextUtils.isEmpty(imagpath)) {
                                    try {
                                        relativeUrl = _accountApi.uploadAvatar(AccountApi.getAvaterUploadURL(), imagpath);
                                    } catch (ApiException e) {
                                        isComplated = true;
                                        e.printStackTrace();
                                    }
                                }
                                changedUserInfo.setAvatar_path(relativeUrl);
//                                makeUploadUserBean(changedUserInfo);
                                JSONObject params = createParams(changedUserInfo);
//                                changedUserInfo.resetValues();
                                return Observable.just(params);
                            }
                        })
                        .map(new Func1<JSONObject, Boolean>() {
                            @Override
                            public Boolean call(JSONObject params) {
                                Boolean falg = true;
                                try {
                                    JSONObject jbonj = JsonHelper.httpPutJsonWithException(CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current",
                                            params, CoreApp.getInstance().getCurrentToken());
                                    if (jbonj != null && jbonj.optString("error").equals("500")) {
                                        falg = false;
                                        isComplated = true;
                                        throw new ApiException("500 个人信息修改没有成功");
                                    }
                                } catch (Exception e) {
                                    falg = false;
                                    isComplated = true;
                                    e.printStackTrace();
                                }
                                //获取网络头像地址
                                if (falg) {
                                    String userString = null;
                                    try {
                                        userString = _accountApi.getCurrentUserInfor();
                                    } catch (ApiException e) {
                                        e.printStackTrace();
                                    }
                                    changedUserInfo.setAvatar_path(userString);
                                }
                                return falg;
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
                                        account.getUser().setSex(changedUserInfo.getSex());
                                        account.getUser().setAvatar_url(changedUserInfo.getAvatar_path());
                                        account.getUser().setProvince(changedUserInfo.getProvince());
                                        account.getUser().setRegion(changedUserInfo.getRegion());
                                        account.getUser().setCity(changedUserInfo.getCity());
                                        account.getUser().setSignature(changedUserInfo.getSignature());
                                        account.getUser().setSignature(changedUserInfo.getSignature());
                                        account.getUser().setPhone_number(changedUserInfo.getTelePhone());
//                                        CoreApp.getInstance().getUser().setUser(account.getUser());
//                                        changedUserInfo.resetValues();
                                        RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
                                    }
                                    Toast.makeText(getContext(), "完善个人信息成功", Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                }
                            }
                        });
            }
        });
        _UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSelectImage();
            }
        });
        _accountApi = new AccountApi(getContext());
        _addressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddressInitTask(getActivity(), new AddressPicker.OnAddressPickListener() {
                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
//                        changedUserInfo.setProvince(province.getAreaName());
//                        changedUserInfo.setCity(city.getAreaName());
//                        changedUserInfo.setRegion(county.getAreaName());
                        _provinceStr = province.getAreaName();
                        _cityStr = city.getAreaName();
                        _regionStr = county.getAreaName();
                        String addree = province.getAreaName() + city.getAreaName() + county.getAreaName();
                        _addressTxt.setText(addree);
                    }
                }).execute("四川", "成都", "高新区");
            }
        });
        changedUserInfo = new ChangedUserInfo();
        _progressDiaog = new ProgressDialog(getActivity());
        _progressDiaog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progressDiaog.setMessage("提交中");
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
        }
    //    super.onActivityResult(requestCode, resultCode, data);
    }
}
