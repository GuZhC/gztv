package com.dfsx.logonproject.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import cn.qqtheme.framework.WheelPicker.entity.City;
import cn.qqtheme.framework.WheelPicker.entity.County;
import cn.qqtheme.framework.WheelPicker.entity.Province;
import cn.qqtheme.framework.WheelPicker.picker.AddressPicker;
import cn.qqtheme.framework.WheelPicker.picker.OptionPicker;
import cn.qqtheme.framework.init.AddressInitTask;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.model.LoginParams;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.logonproject.R;
import com.dfsx.logonproject.busniness.AccountApi;
import com.dfsx.logonproject.dzfragment.RegVerifyFragment;
import com.dfsx.logonproject.dzfragment.ResetPassVeriFragment;
import com.dfsx.logonproject.model.ChangedUserInfo;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by heyang on  2016/10/27
 * 修改部分  wenxiaolong
 */
public class PersonInforFragment extends BaseResultFragment implements View.OnClickListener {
    public static String PUBLIC_PATH = Environment.getExternalStorageDirectory() + "/DCIM/Camera/";   //  保存的拍摄的照片
    LinearLayout mPasswordlayout;
    LinearLayout mMaillayout;
    LinearLayout mNickNameayout;
    LinearLayout mHeadImgLayout;
    LinearLayout mHeadSignLayout;
    LinearLayout personAreaLayout;
    LinearLayout personSexLayout;
    CircleButton mUserImag;
    TextView mNicknameTx;
    TextView mSexTx;
    TextView mAreaTx;
    TextView mSingnTx;
    ImageView vefifiedImg;
    View bacnBtn;
    Button mLogoutBtn;
    AccountApi mUserApi;
    TextView topBarRight;
    ProgressDialog progressDialog;
    ChangedUserInfo changedUserInfo;
    String imagpath;
    String relativeUrl;
    TextView _telePhoneTxt;
    TextView emainTxtHit;

    protected View _passWordItemView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myperson_info, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initUserInfo();
    }

    private void initUserInfo() {
        if (!CoreApp.getInstance().isOpenVerityTele())
            emainTxtHit.setText("更改邮箱");
        Account user = CoreApp.getInstance().getUser();
        if (user != null) {
            if (!TextUtils.isEmpty(user.getUser().getAvatar_url())) {
                Util.LoadImageErrorUrl(mUserImag, user.getUser().getAvatar_url(), null, R.drawable.icon_defalut_no_login_logo);
            } else {
                mUserImag.setImageResource(R.drawable.icon_defalut_no_login_logo);
            }
            mNicknameTx.setText(user.getUser().getNickname());
            mSexTx.setText(getSexStr(user.getUser().getSex()));
            String area = user.getUser().getCity();
            if (!TextUtils.isEmpty(area))
                mAreaTx.setText(area);
            String sign = user.getUser().getSignature();
            if (!TextUtils.isEmpty(sign))
                mSingnTx.setText(sign);
//            mEmailTx.setText(user.getUser().getEmail());
            if (CoreApp.getInstance().isOpenVerityTele()) {
                _telePhoneTxt.setText(user.getUser().getPhone_number());
            } else {
                String email = user.getUser().getEmail();
                if (!TextUtils.isEmpty(email))
                    _telePhoneTxt.setText(email);
            }
            if (user.getUser().is_verified())
                vefifiedImg.setVisibility(View.VISIBLE);

            //个人资料里面区分登录的类型，如果是第三方登录就隐藏修改密码的选项
            if (user.loginInfo != null &&
                    user.loginInfo.loginType == LoginParams.LOGIN_TYPE_THIRD_USER) {
                _passWordItemView.setVisibility(View.GONE);
            } else {
                _passWordItemView.setVisibility(View.VISIBLE);
            }
        }
//        if (getActivity() instanceof WhiteTopBarActivity) {
//            WhiteTopBarActivity activity1 = (WhiteTopBarActivity) getActivity();
//            topBarRight = activity1.getTopBarRightText();
//            topBarRight.setTextColor(Color.BLUE);
//            topBarRight.setOnClickListener(this);
//        }
        changedUserInfo = new ChangedUserInfo();
        mUserApi = new AccountApi(getActivity());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在提交");
    }

    private void initView(View view) {
        mUserImag = (CircleButton) view.findViewById(R.id.person_img_img);
        mNicknameTx = (TextView) view.findViewById(R.id.person_nick_tx);
        mSexTx = (TextView) view.findViewById(R.id.person_sex_tx);
        mAreaTx = (TextView) view.findViewById(R.id.person_area_tx);
//        mEmailTx = (TextView) view.findViewById(R.id.person_mail_tx);
        bacnBtn = (View) view.findViewById(R.id.back_finish_per);
        topBarRight = (TextView) view.findViewById(R.id.right_text);
        bacnBtn.setOnClickListener(this);
        topBarRight.setOnClickListener(this);
        emainTxtHit = (TextView) view.findViewById(R.id.telephone_btn);
        mSingnTx = (TextView) view.findViewById(R.id.person_sign_tx);
        _telePhoneTxt = (TextView) view.findViewById(R.id.person_telephone_tx);
        mLogoutBtn = (Button) view.findViewById(R.id.person_logout_tx);
        vefifiedImg = (ImageView) view.findViewById(R.id.accreditation_img);
        personSexLayout = (LinearLayout) view.findViewById(R.id.person_sex_layout);
        personAreaLayout = (LinearLayout) view.findViewById(R.id.person_area_layout);
        mPasswordlayout = (LinearLayout) view.findViewById(R.id.person_pwd_layout);
        mMaillayout = (LinearLayout) view.findViewById(R.id.person_mail_layout);
        mNickNameayout = (LinearLayout) view.findViewById(R.id.person_name_layout);
        mHeadImgLayout = (LinearLayout) view.findViewById(R.id.person_head_layout);
        mHeadSignLayout = (LinearLayout) view.findViewById(R.id.person_sign_layout);
        _passWordItemView = view.findViewById(R.id.person_password_item);
        mHeadSignLayout.setOnClickListener(this);
        mNickNameayout.setOnClickListener(this);
        mHeadImgLayout.setOnClickListener(this);
        mPasswordlayout.setOnClickListener(this);
        mMaillayout.setOnClickListener(this);
        mLogoutBtn.setOnClickListener(this);
        personAreaLayout.setOnClickListener(this);
        personSexLayout.setOnClickListener(this);
    }

    protected void makeUploadUserBean(ChangedUserInfo changedUserInfo) {
        if(CoreApp.getInstance().getUser()==null) return;
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
        if(changedUserInfo.getDetail_address()==null)
            changedUserInfo.setDetail_address(userBean.getDetail_address());
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
            jsonObject.put("region", changedUserInfo.getRegion());
            jsonObject.put("detail_address",changedUserInfo.getDetail_address());
            if (changedUserInfo.getAvatar_path() != null)
                jsonObject.put("avatar_path", changedUserInfo.getAvatar_path());
            else
                jsonObject.put("avatar_id", changedUserInfo.getAvatar_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void modifyUserInfo() {
        if (imagpath == null && changedUserInfo.hasNoModified()) {
            showShortToast("你还没有修改过个人信息");
            return;
        }
        progressDialog.show();
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (imagpath != null && !imagpath.equals("")) {
                    try {
                        relativeUrl = AccountApi.uploadAvatarHttpUtl(AccountApi.getAvaterUploadURL(), imagpath);
                    } catch (ApiException e) {
                        subscriber.onError(e);
                        return;
                    }
                    changedUserInfo.setAvatar_path(relativeUrl);
                    imagpath = null;
                }
                makeUploadUserBean(changedUserInfo);
                JSONObject params = createParams(changedUserInfo);
//                changedUserInfo.resetValues();
                System.out.println(params.toString() + "  我提交的信息");
                try {
                    JSONObject jbonj = JsonHelper.httpPutJsonWithException(CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current",
                            params, CoreApp.getInstance().getUser().getToken());
                    if (jbonj != null && jbonj.optString("error").equals("500")) {
//                        throw new ApiException("500 个人信息修改没有成功");
                        throw new ApiException(jbonj.toString());
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                    return;
                }
                subscriber.onCompleted();
                try {
                    JSONObject jObject = JsonHelper.httpGetJson(CoreApp.getInstance().getPotrtServerUrl() +
                            "/public/users/current", CoreApp.getInstance().getUser().getToken());
                    System.out.println(jObject.toString() + "  新的资料 jObject");
                    updateUserInfo(jObject);
                } catch (Exception e) {

                }
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        changedUserInfo.resetValues();
                        showLongToast("个人信息修改成功");
                        if (topBarRight != null)
                            topBarRight.setTextColor(0xff8c8c8c);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (topBarRight != null)
                            topBarRight.setTextColor(0xff8c8c8c);
                        Toast.makeText(getContext(), JsonCreater.getErrorMsgFromApi(e.toString()), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onNext(String s) {
                        if (topBarRight != null)
                            topBarRight.setTextColor(0xff8c8c8c);
                        progressDialog.dismiss();
                    }
                });
    }

    private void testGetUserInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = JsonHelper.httpGetJson("http://192.168.6.206:8001/public/users/current", CoreApp.getInstance().getUser().getToken());
                    System.out.println(jsonObject.toString() + " ------------------------获取个人信息");

                } catch (Exception e) {

                }
            }
        }).start();
    }

    /**
     * 先获取用户信息jsonObject  然后更新本地用户信息,并保存至文件
     *
     * @param jsonObject
     */
    private void updateUserInfo(JSONObject jsonObject) {
        Account.UserBean user = CoreApp.getInstance().getUser().getUser();
        user.setNickname(jsonObject.optString("nickname"));
        user.setEmail(jsonObject.optString("email"));
        user.setMobile(jsonObject.optString("mobile"));
        user.setSex(jsonObject.optInt("sex"));
        user.setSignature(jsonObject.optString("signature"));
        user.setProvince(jsonObject.optString("province"));
        user.setCity(jsonObject.optString("city"));
        user.setRegion(jsonObject.optString("region"));
        user.setAvatar_id(jsonObject.optInt("avatar_id"));
        user.setAvatar_url(jsonObject.optString("avatar_url"));
        AccountApi.saveAccount(CoreApp.getInstance().getUser());
        //发出通知让 MYFragment更新用户信息
        RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
        if (view == bacnBtn) {
            getActivity().finish();
        } else if (view == mPasswordlayout) {
            if (CoreApp.getInstance().isOpenVerityTele()) {
                String tele = _telePhoneTxt.getText().toString().trim();
                Bundle bundle = new Bundle();
                if (!TextUtils.isEmpty(tele)) {
                    bundle.putString("tele", tele);
                }
                WhiteTopBarActivity.startAct(getActivity(), ResetPassVeriFragment.class.getName(), "验证手机号", "", bundle);
            } else {
                WhiteTopBarActivity.startAct(getActivity(), PassChangeFragment.class.getName(), "更改密码", "保存");
            }
        } else if (view == mMaillayout) {
            if (CoreApp.getInstance().isOpenVerityTele()) {
                frgStartWhiteTopBarActivityForResult(getActivity(), RegVerifyFragment.class.getName(), "验证手机号", "", 12);
            } else {
                frgStartWhiteTopBarActivityForResult(getActivity(), MailChangeFragment.class.getName(), "更改邮箱", "保存", 1);
            }
        } else if (view == mNickNameayout) {
            frgStartWhiteTopBarActivityForResult(getActivity(), NickNameChangeFragment.class.getName(), "更改昵称", "保存", 2);
        } else if (view == mHeadSignLayout) {
            frgStartWhiteTopBarActivityForResult(getActivity(), SignChangeFragment.class.getName(), "个性签名", "保存", 4
                    , mSingnTx.getText().toString());
        } else if (view == topBarRight) {
            modifyUserInfo();
        } else if (view == personSexLayout) {
            OptionPicker picker = new OptionPicker(getActivity(), new String[]{
                    "保密", "男", "女"
            });
            picker.setOffset(2);
            picker.setSelectedIndex(1);
            picker.setTextSize(14);
            picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                @Override
                public void onOptionPicked(int position, String option) {
                    mSexTx.setText(option);
                    changedUserInfo.setSex(position);
                }
            });
            picker.show();
        } else if (view == personAreaLayout) {
            new AddressInitTask(getActivity(), new AddressPicker.OnAddressPickListener() {
                @Override
                public void onAddressPicked(Province province, City city, County county) {
                    changedUserInfo.setProvince(province.getAreaName());
                    changedUserInfo.setCity(city.getAreaName());
                    changedUserInfo.setRegion(county.getAreaName());
                    mAreaTx.setText(county.getAreaName());
                }
            }).execute("贵州", "毕节", "纳雍");
        } else {
            if (view == mHeadImgLayout) {
                //                Intent intent = new Intent(Intent.ACTION_PICK, null);
                //                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                //                        "image/*");
                //                intent.putExtra("crop", "true");
                //                intent.putExtra("aspectX", 1);
                //                intent.putExtra("aspectY", 1);
                //                fileUri = getOutputMediaFileUri(1);
                //                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                //                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                //                startActivityForResult(intent, 3);

                gotoSelectImage();

//                Intent intent = new Intent(getContext(), ImageFragmentActivity.class);
//                intent.putExtra(ImageFragmentActivity.KEY_SINGLE_MODE, true);
//                startActivityForResult(intent, 3);
            } else if (view == mLogoutBtn) {
                //阿里云推送需要在退出账号之前更新设备id为null
                mUserApi.submitDeviceId(getActivity(), null, new DataRequest.DataCallback<Void>() {
                    @Override
                    public void onSuccess(boolean isAppend, Void data) {
                        Log.e("AliyunPush", "更新推送设备成功");
                    }

                    @Override
                    public void onFail(ApiException e) {
                        Log.e("AliyunPush", "更新推送设备失败");
                    }
                });

                int a = 0;
                try {
                    mUserApi.logout(new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            Toast.makeText(getActivity(), "退出登录", Toast.LENGTH_LONG).show();
                            saveLogoInfo();
                            CoreApp.getInstance().setCurrentAccount(null);
                            mUserApi.clearAccountFromCache();
                            RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
                            getActivity().finish();
                        }

                        @Override
                        public void onFail(ApiException e) {
                            e.printStackTrace();
                            saveLogoInfo();
                            //后端建议为即使请求失败了 也可以作为退出登录成功处理
                            mUserApi.clearAccountFromCache();
                            CoreApp.getInstance().setCurrentAccount(null);
                            RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
                            getActivity().finish();
                            /*String statusCode = e.toString();
                            statusCode = statusCode.substring(statusCode.length() - 4, statusCode.length() - 1);
                            if (TextUtils.equals(statusCode, "200")) {
                                //success
                                mUserApi.clearAccountFromCache();
                                CoreApp.getInstance().setCurrentAccount(null);
                                RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
                                getActivity().finish();
                            } else {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                            }*/
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void gotoSelectImage() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(getContext(), ImageFragmentActivity.class);
                intent.putExtra(ImageFragmentActivity.KEY_SINGLE_MODE, true);
                startActivityForResult(intent, 3);
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

    public void saveLogoInfo() {
        if (!CoreApp.getInstance().isOpenVerityTele()) return;
        String newTelephone = _telePhoneTxt.getText().toString().trim();
        if (!TextUtils.isEmpty(newTelephone)) {
            saveLoginRequestData(newTelephone, "");
        }
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(PUBLIC_PATH);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String str;
        boolean isUpdate = false;
        switch (requestCode) {
            case -1:
                return;
            case 1:
                if (data == null) return;
                str = data.getStringExtra("mail");
                changedUserInfo.setEmail(str);
//                mEmailTx.setText(str);
                _telePhoneTxt.setText(str);
                isUpdate = true;
                break;
            case 2:
                if (data == null) return;
                str = data.getStringExtra("nickname");
                changedUserInfo.setNickname(str);
                mNicknameTx.setText(str);
                isUpdate = true;
                break;
            case 3:
                if (data != null) {
                    ArrayList<MediaModel> selectedList = data.
                            getParcelableArrayListExtra("list");
                    if (selectedList == null || selectedList.size() == 0)
                        break;
                    String filePath = selectedList.get(0).url;
                    if (new File(filePath).exists()) {
                        imagpath = filePath;
                        if (!("").equals(imagpath)) {
                            Util.LoadImageErrorUrl(mUserImag, imagpath, null, R.drawable.icon_defalut_no_login_logo);
                        } else {
                            mUserImag.setImageResource(R.drawable.icon_defalut_no_login_logo);
                        }
                    }
                    isUpdate = true;
                }
                break;
            case 4:
                if (data == null) return;
                str = data.getStringExtra("signature");
                changedUserInfo.setSignature(str);
                mSingnTx.setText(str);
                isUpdate = true;
                break;
            case 12: {
                if (data == null) return;
                str = data.getStringExtra("veritelphone");
                if (!TextUtils.isEmpty(str)) {
                    Account user = CoreApp.getInstance().getUser();
                    if (user != null) {
                        if (user.getUser() == null) return;
                        Account.UserBean bean = user.getUser();
                        if (bean != null) {
                            bean.setPhone_number(str);
                            CoreApp.getInstance().setCurrentAccount(user);
                        }
                    }
                    _telePhoneTxt.setText(str);
                }
            }
            break;
        }
        if (isUpdate) {
            topBarRight.setTextColor(getActivity().getResources().getColor(R.color.public_purple_bkg));
        } else {
            topBarRight.setTextColor(getActivity().getResources().getColor(R.color.button_enable_color));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getSexStr(int type) {
        String result = "保密";
        switch (type) {
            case 1:
                result = "男";
                break;
            case 2:
                result = "女";
                break;
        }
        return result;
    }
}
