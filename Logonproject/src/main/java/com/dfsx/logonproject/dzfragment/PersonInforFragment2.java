package com.dfsx.logonproject.dzfragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import cn.qqtheme.framework.WheelPicker.entity.City;
import cn.qqtheme.framework.WheelPicker.entity.County;
import cn.qqtheme.framework.WheelPicker.entity.Province;
import cn.qqtheme.framework.WheelPicker.picker.AddressPicker;
import cn.qqtheme.framework.WheelPicker.picker.OptionPicker;
import cn.qqtheme.framework.init.AddressInitTask;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.*;
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
import com.dfsx.logonproject.fragment.*;
import com.dfsx.logonproject.model.ChangedUserInfo;
import com.dfsx.logonproject.view.TimeBottomPopupwindow;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by heyang on  2016/10/27
 * 修改部分  wenxiaolong
 */
public class PersonInforFragment2 extends BaseResultFragment implements View.OnClickListener {
    public static String PUBLIC_PATH = Environment.getExternalStorageDirectory() + "/DCIM/Camera/";   //  保存的拍摄的照片
    private LinearLayout mPasswordlayout, mMaillayout, mBirthdaylayout;
    private LinearLayout mNickNameayout, mHeadImgLayout, mHeadSignLayout, personAreaLayout;
    private LinearLayout personAddresLayout, personSexLayout, thirdLayout;
    private CircleButton mUserImag;
    private TextView mNicknameTx, mAreaTx;
    private ImageView vefifiedImg;
    private View bacnBtn, _passWordItemView;
    private Button mLogoutBtn;
    private AccountApi mUserApi;
    private ProgressDialog progressDialog;
    private ChangedUserInfo changedUserInfo;
    private String imagpath, relativeUrl;
    private TextView mSingnTx, topBarRight, _telePhoneTxt, mAddressTxt, emainTxtHit;
    private RadioGroup serGroup;
    private LinearLayout thirdIconContainer;
    private TextView birthdayTxt;
    private String strProvice, strCity, strRehin, straddress;
    private ImageView weixinImg, weiboImg, qqImg;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (getActivity() == null)
                return false;
            if (message.what == 3) {
                createThirdIcons();
                getMythird();
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myperson_info2, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changedUserInfo = new ChangedUserInfo();
        mUserApi = new AccountApi(getActivity());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在提交");
        initView(view);
        initUserInfo();
        getMythird();
    }

    private void initUserInfo() {
        if (!CoreApp.getInstance().isOpenVerityTele())
            emainTxtHit.setText("更改邮箱");
        Account user = CoreApp.getInstance().getUser();
        if (!(user == null || user.getUser() == null)) {
            if (!TextUtils.isEmpty(user.getUser().getAvatar_url())) {
                Util.LoadImageErrorUrl(mUserImag, user.getUser().getAvatar_url(), null, R.drawable.icon_defalut_no_login_logo);
            } else {
                mUserImag.setImageResource(R.drawable.icon_defalut_no_login_logo);
            }
            strProvice = user.getUser().getProvince() == null ? "" : user.getUser().getProvince();
            strCity = user.getUser().getCity() == null ? "" : user.getUser().getCity();
            strRehin = user.getUser().getRegion() == null ? "" : user.getUser().getRegion();
            straddress = user.getUser().getDetail_address() == null ? "" : user.getUser().getDetail_address();
            mNicknameTx.setText(user.getUser().getNickname());
            setSexButton(user.getUser().getSex());
//            mSexTx.setText(getSexStr(user.getUser().getSex()));
            String area = user.getUser().getCity();
            if (!TextUtils.isEmpty(area))
                mAreaTx.setText(area);
            String sign = user.getUser().getSignature();
            if (!TextUtils.isEmpty(sign))
                mSingnTx.setText(sign);

            String stffomat = strProvice + strCity + strRehin + straddress;
            mAddressTxt.setText(stffomat);
//            String birdthdaye = Util.getTimeString("yyyy-MM-dd", user.getUser().getBirthday() + 86400);
            String birdthdaye = Util.getTimeString("yyyy-MM-dd", user.getUser().getBirthday());
            birthdayTxt.setText(birdthdaye);
//            mEmailTx.setText(user.getUser().getEmail());
            if (CoreApp.getInstance().isOpenVerityTele()) {
                String telephone = user.getUser().getPhone_number();
                if (!TextUtils.isEmpty(telephone)) {
                    try {
                        StringBuilder sb = new StringBuilder(telephone);
                        //  取中间四位
                        sb.replace(3, 7, "****");
                        // 取后四位
                        sb.substring(7, 11);
                        _telePhoneTxt.setText(sb.toString());
                        _telePhoneTxt.setTag(telephone);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    _telePhoneTxt.setText(telephone);
            } else {
                String email = user.getUser().getEmail();
                if (!TextUtils.isEmpty(email))
                    _telePhoneTxt.setText(email);
            }
            if (user.getUser().is_verified())
                vefifiedImg.setVisibility(View.VISIBLE);

            //个人资料里面区分登录的类型，如果是第三方登录就隐藏修改密码的选项
//            if (user.loginInfo != null &&
//                    user.loginInfo.loginType == LoginParams.LOGIN_TYPE_THIRD_USER) {
//                _passWordItemView.setVisibility(View.GONE);
//            } else {
//                _passWordItemView.setVisibility(View.VISIBLE);
//            }
//            createThirdIcons();
        }
//        if (getActivity() instanceof WhiteTopBarActivity) {
//            WhiteTopBarActivity activity1 = (WhiteTopBarActivity) getActivity();
//            topBarRight = activity1.getTopBarRightText();
//            topBarRight.setTextColor(Color.BLUE);
//            topBarRight.setOnClickListener(this);
//        }
    }

    public void getMythird() {
        Observable.just("").
                subscribeOn(Schedulers.io()).
                observeOn(Schedulers.io()).
                map(new Func1<String, String>() {
                    @Override
                    public String call(String id) {
                        List<Account.AuthorThrid> authlist = null;
                        try {
                            authlist = mUserApi.getMyThirdInfo();
                            Account user = CoreApp.getInstance().getUser();
                            if (user != null) {
                                user.setAthorList(authlist);
                            }
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                        return "";
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(String s) {
                                   createThirdIcons();
                               }
                           }
                );
    }

    public void setSexButton(int sex) {
        if (sex == 0) {
            serGroup.check(R.id.bar_nosex_btn);
        } else if (sex == 1) {
            serGroup.check(R.id.bar_man_btn);
        } else
            serGroup.check(R.id.bar_woman_btn);
    }

    public void createThirdIcons() {
        Log.e("TAG", "createThirdIcons() =====================");
        weixinImg.setVisibility(View.GONE);
        weiboImg.setVisibility(View.GONE);
        qqImg.setVisibility(View.GONE);
//        if (thirdIconContainer != null)
//            thirdIconContainer.removeAllViews();
        Account account = CoreApp.getInstance().getUser();
        List<Account.AuthorThrid> list = account.getAthorList();
        if (!(list == null || list.isEmpty())) {
            for (Account.AuthorThrid third : list) {
//                ImageView imageView = new ImageView(getActivity());
//                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(Util.dp2px(getActivity(), 27),
//                        Util.dp2px(getActivity(), 27));
//                imageView.setPadding(0, 0, Util.dp2px(getActivity(), 5), 0);
//                imageView.setLayoutParams(lp);
                if (third.getType() == 1) {
                    // weixin
                    weixinImg.setVisibility(View.VISIBLE);
//                    imageView.setImageResource(R.drawable.person_wexij_samll);
                } else if (third.getType() == 2) {
                    // weibo
                    weiboImg.setVisibility(View.VISIBLE);
//                    imageView.setImageResource(R.drawable.person_weibo_samll);
                } else {
                    qqImg.setVisibility(View.VISIBLE);
//                    imageView.setImageResource(R.drawable.person_qq_samll);
                }
//                thirdIconContainer.addView(imageView);
            }
        }
    }


    private void initView(View view) {
        weixinImg = (ImageView) view.findViewById(R.id.per_weixin_icon);
        weiboImg = (ImageView) view.findViewById(R.id.per_weibo_icon);
        qqImg = (ImageView) view.findViewById(R.id.per_qq_icon);
        ;
        mUserImag = (CircleButton) view.findViewById(R.id.person_img_img);
        mUserImag.setOnClickListener(this);
        mNicknameTx = (TextView) view.findViewById(R.id.person_nick_tx);
//        mSexTx = (TextView) view.findViewById(R.id.person_sex_tx);
        mAreaTx = (TextView) view.findViewById(R.id.person_area_tx);
//        mEmailTx = (TextView) view.findViewById(R.id.person_mail_tx);
        bacnBtn = (View) view.findViewById(R.id.back_finish_per);
        topBarRight = (TextView) view.findViewById(R.id.right_text);
        bacnBtn.setOnClickListener(this);
        topBarRight.setOnClickListener(this);
        emainTxtHit = (TextView) view.findViewById(R.id.telephone_btn);
        mSingnTx = (TextView) view.findViewById(R.id.person_sign_tx);
        _telePhoneTxt = (TextView) view.findViewById(R.id.person_telephone_tx);
        mAddressTxt = (TextView) view.findViewById(R.id.person_add_tx);
        birthdayTxt = (TextView) view.findViewById(R.id.birthday_txt);
        mLogoutBtn = (Button) view.findViewById(R.id.person_logout_tx);
        vefifiedImg = (ImageView) view.findViewById(R.id.accreditation_img);
        personSexLayout = (LinearLayout) view.findViewById(R.id.person_sex_layout);
        thirdLayout = (LinearLayout) view.findViewById(R.id.person_thrid_layout);
        thirdIconContainer = (LinearLayout) view.findViewById(R.id.third_container);
        personAreaLayout = (LinearLayout) view.findViewById(R.id.person_area_layout);
        personAddresLayout = (LinearLayout) view.findViewById(R.id.person_addres_layout);
        mPasswordlayout = (LinearLayout) view.findViewById(R.id.person_pwd_layout);
        mMaillayout = (LinearLayout) view.findViewById(R.id.person_mail_layout);
        mBirthdaylayout = (LinearLayout) view.findViewById(R.id.person_birthday_layout);
        mNickNameayout = (LinearLayout) view.findViewById(R.id.person_name_layout);
        mHeadImgLayout = (LinearLayout) view.findViewById(R.id.person_head_layout);
        mHeadSignLayout = (LinearLayout) view.findViewById(R.id.person_sign_layout);
        _passWordItemView = view.findViewById(R.id.person_password_item);
        serGroup = (RadioGroup) view.findViewById(R.id.person_sex_group);
        mHeadSignLayout.setOnClickListener(this);
        mNickNameayout.setOnClickListener(this);
        mHeadImgLayout.setOnClickListener(this);
        mPasswordlayout.setOnClickListener(this);
        mMaillayout.setOnClickListener(this);
        mBirthdaylayout.setOnClickListener(this);
        mLogoutBtn.setOnClickListener(this);
        personAreaLayout.setOnClickListener(this);
        personAddresLayout.setOnClickListener(this);
        personSexLayout.setOnClickListener(this);
        thirdLayout.setOnClickListener(this);
        serGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int sex = 0;
                if (checkedId == R.id.bar_man_btn) {
                    sex = 1;
                } else if (checkedId == R.id.bar_woman_btn)
                    sex = 2;
                if (changedUserInfo != null)
                    changedUserInfo.setSex(sex);
            }
        });
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
                    JsonCreater.checkThrowError(jbonj);
                } catch (Exception e) {
                    subscriber.onError(e);
                    return;
                }
                subscriber.onCompleted();
                try {
                    JSONObject jObject = JsonHelper.httpGetJson(CoreApp.getInstance().getPotrtServerUrl() +
                            "/public/users/current", CoreApp.getInstance().getUser().getToken());
                    JsonCreater.checkThrowError(jObject);
//                    System.out.println(jObject.toString() + "  新的资料 jObject");
                    updateUserInfo(jObject);
                } catch (Exception e) {
                    subscriber.onError(e);
                    return;
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
        user.setDetail_address(jsonObject.optString("detail_address"));
        user.setBirthday(jsonObject.optLong("birthday"));
        user.setAvatar_id(jsonObject.optInt("avatar_id"));
        user.setAvatar_url(jsonObject.optString("avatar_url"));
        AccountApi.saveAccount(CoreApp.getInstance().getUser());
        //发出通知让 MYFragment更新用户信息
        RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OK));
    }

//    public void onFocusChange(boolean hasFocus, final EditText edt) {
//        final boolean isFocus = hasFocus;
//        (new Handler()).postDelayed(new Runnable() {
//            public void run() {
//                InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
//                if (isFocus) {
//                    // 显示输入法
//                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                } else {
//                    // 隐藏输入法
//                    imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
//                }
//            }
//        }, 100);
//    }

    @Override
    public void onClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
        if (view == bacnBtn) {
            getActivity().finish();
        } else if (view == mPasswordlayout) {
            if (CoreApp.getInstance().isOpenVerityTele()) {
                String tele = (String) _telePhoneTxt.getTag();
                Bundle bundle = new Bundle();
                if (!TextUtils.isEmpty(tele)) {
                    bundle.putString("tele", tele);
                }
                WhiteTopBarActivity.startAct(getActivity(), ResetPassVeriFragment.class.getName(), "重置密码", "", bundle);
            } else {
                WhiteTopBarActivity.startAct(getActivity(), PassChangeFragment.class.getName(), "更改密码", "保存");
            }
        } else if (view == mBirthdaylayout) {
            TimeBottomPopupwindow timePopupwindow = new TimeBottomPopupwindow(getActivity(), new TimeBottomPopupwindow.DateChooseInterface() {
                @Override
                public void getDateTime(String start, String end) {
                    try {
                        long tiom = Util.stringToLong(start, "yyyy-MM-dd HH:mm:ss");
                        long tt = tiom / 1000;
                        changedUserInfo.setBirthday(tt + 86400);
//                        String stfr = Util.getTimeString("yyyy-MM-dd", tiom / 1000);
                        String stfr = Util.getTimeString("yyyy-MM-dd", tt);
                        birthdayTxt.setText(stfr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            timePopupwindow.show(view.getRootView());
        } else if (view == mMaillayout) {
            if (CoreApp.getInstance().isOpenVerityTele()) {
                boolean isVerifty = true;
                Account account = CoreApp.getInstance().getUser();
                if (!(account == null || account.getUser() == null)) {
                    if (!TextUtils.isEmpty(account.getUser().getPhone_number())) {
                        isVerifty = false;
                        frgStartWhiteTopBarActivityForResult(getActivity(), RegVerifyFragment.class.getName(), "更换手机号", "", 12);
                    }
                }
                if (isVerifty)
                    frgStartWhiteTopBarActivityForResult(getActivity(), RegVerifyFragment.class.getName(), "验证手机号", "", 12);
            } else {
                frgStartWhiteTopBarActivityForResult(getActivity(), MailChangeFragment.class.getName(), "更改邮箱", "保存", 1);
            }
        } else if (view == mNickNameayout) {
            frgStartWhiteTopBarActivityForResult(getActivity(), NickNameChangeFragment.class.getName(), "更改昵称", "保存", 2);
        } else if (view == mHeadSignLayout) {
            frgStartWhiteTopBarActivityForResult(getActivity(), SignChangeFragment.class.getName(), "个性签名", "保存", 4
                    , mSingnTx.getText().toString());
        } else if (view == thirdLayout) {
            frgStartWhiteTopBarActivityForResult(getActivity(), ThridChangeFragment.class.getName(), "第三方平台", "", 16);
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
//                    mSexTx.setText(option);
                    changedUserInfo.setSex(position);
                }
            });
            picker.show();
        } else if (view == personAddresLayout) {
//            frgStartWhiteTopBarActivityForResult(getActivity(), AddressChangeFragment.class.getName(), "编辑地址", "保存", 18);


            Intent intent = new Intent(getActivity(), WhiteTopBarActivity.class);
            intent.putExtra(WhiteTopBarActivity.KEY_FRAGMENT_NAME, AddressChangeFragment.class.getName());
            intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_TITLE, "编辑地址");
            intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_RIGHT_TEXT, "保存");

            if (changedUserInfo.getProvince() != null)
                strProvice = changedUserInfo.getProvince();
            if (changedUserInfo.getCity() != null)
                strCity = changedUserInfo.getCity();
            if (changedUserInfo.getRegion() != null)
                strRehin = changedUserInfo.getRegion();
            if (changedUserInfo.getDetail_address() != null)
                straddress = changedUserInfo.getDetail_address();

            String[] provice = {strProvice, strCity, strRehin};
            intent.putExtra("provice", provice);
            intent.putExtra("address", straddress);
            startActivityForResult(intent, 18);

            //            showCommendDialog(view, new ICommendDialogLbtnlister() {
//                @Override
//                public boolean onEdioteTex(String content) {
//                    if (TextUtils.isEmpty(content))
//                        return false;
//                    mAddressTxt.setText(content);
//                    changedUserInfo.setDetail_address(content);
//                    return true;
//                }
//            });
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
        } else if (view == mHeadImgLayout || view == mUserImag) {
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

    PopupWindow mCommendPopupWindow;
    Button mSendBtn;
    EditText mCommendEdt;

    public void showCommendDialog(final View moreBtnView, final ICommendDialogLbtnlister lister) {
        if (mCommendPopupWindow == null) {
            LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View content = li.inflate(R.layout.layout_commend_dialog, null, false);
            mCommendPopupWindow = new PopupWindow(content, MATCH_PARENT,
                    WRAP_CONTENT);
            mCommendPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mCommendPopupWindow.setFocusable(true);
            mCommendPopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            mCommendPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            View parent = mCommendPopupWindow.getContentView();
            mCommendEdt = (EditText) parent.findViewById(R.id.commentEdit_replay_edt);
            mSendBtn = (Button) parent.findViewById(R.id.dialog_comfirm_btn);
//            mCommendEdt.addTextChangedListener(new EditChangedLister(new EditChangedLister.EditeTextStatuimpl() {
//                @Override
//                public void onTextStatusCHanged(boolean isHas) {
//                    if (isHas) {
//                        mSendBtn.setImageResource(R.drawable.video_send_select);
//                    } else {
//                        mSendBtn.setImageResource(R.drawable.video_send_normal);
//                    }
//                }
//            }));
//            mSendBtn.setTag(resf_cid);
            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contnt = mCommendEdt.getText().toString().trim();
                    boolean flag = lister.onEdioteTex(contnt);
                    if (flag) {
                        onFocusChange(false, moreBtnView);
                        mCommendEdt.setText("");
                        mCommendPopupWindow.dismiss();
                    } else {
                        ToastUtils.toastMsgFunction(getActivity(), "地址不能为空");
                    }
                }
            });
        }
        if (mCommendPopupWindow.isShowing()) {
            mCommendPopupWindow.dismiss();
            mCommendEdt.setText("");
        } else {
            mCommendPopupWindow.showAtLocation(moreBtnView, Gravity.BOTTOM,
                    0, 0);
            onFocusChange(true, mCommendEdt);
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
        String newTelephone = (String) _telePhoneTxt.getTag();
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
            case 16: {
                if (data == null) return;
                str = data.getStringExtra("ischang");
                if (!TextUtils.isEmpty(str)) {
                    Log.e("TAG", "data.getStringExtra  sendEmptyMessage");
                    myHander.sendEmptyMessage(3);
                }
            }
            break;
            case 18:
                if (data == null) return;
                String[] provice = data.getStringArrayExtra("provice");
                String address = data.getStringExtra("address");
                String strformat = "";
                if (!(provice == null || provice.length == 0)) {
                    strformat = provice[0] + provice[1] + provice[2];
                    changedUserInfo.setProvince(provice[0]);
                    changedUserInfo.setCity(provice[1]);
                    changedUserInfo.setRegion(provice[2]);
                }
                strformat += address;
                mAddressTxt.setText(strformat);
                changedUserInfo.setDetail_address(address);
                break;
        }
        if (isUpdate) {
            topBarRight.setTextColor(getActivity().getResources().getColor(R.color.public_purple_bkg));
        } else {
            topBarRight.setTextColor(getActivity().getResources().getColor(R.color.button_enable_color));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    interface ICommendDialogLbtnlister {
        public boolean onEdioteTex(String content);
    }

}
