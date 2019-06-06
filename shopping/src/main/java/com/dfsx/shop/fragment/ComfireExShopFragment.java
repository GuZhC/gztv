package com.dfsx.shop.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.shop.R;
import com.dfsx.shop.busniness.CreditShopApi;
import com.dfsx.shop.model.ExchangeShop;
import com.dfsx.shop.model.ShopEntryInfo;
import rx.android.schedulers.AndroidSchedulers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by heyang on 2017/7/24
 */

public class ComfireExShopFragment extends Fragment {
    private static final String TAG = "ComfireExShopFragment";
    public static final int RESULT_OK = -1;
    private ListView list;
    private final int NETWORK_BUSY = 54;
    private ImageButton mLoadRetryBtn;
    private long mCLoumnType = -1;
//    private HeadlineListManager dataRequester;
//    private HeadlineListManager looperDataRequester;
//    private ContentCmsApi mContentCmsApi = null;
    //定时任务
    private RelativeLayout mRelativeLayoutFail;
//    private ArrayList<ContentCmsEntry> looperDataList;
    Button mComfireBtn;
    ImageView mShopNumPlus, mShopNumAdd;
    TextView mShopNumberTx;
    private int number = 1;
    private CreditShopApi _creditShopApi;
    private ImageView _shopImage;
    private TextView _shopName;
    private TextView _remianTxt;
    private TextView _totalPrice;
    private TextView _buyNumberTxt;
    private EditText _buyNameTxt;
    private EditText _buyTelephoneTxt;
    private EditText _buyAddressTxt;
    private ShopEntryInfo _shopInfo;
    private double totalPrice;
    private EditText _exchangeDescribe;
    private int _stockNumber;
    private boolean isDefaulUser = false;
    private String _defaultTelphone = "";


    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == NETWORK_BUSY) {
                if (getActivity() != null) {
                } else {
                    mRelativeLayoutFail.setVisibility(View.VISIBLE);
                }
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_comfire_shop_ex_custom, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _shopInfo = (ShopEntryInfo) bundle.getSerializable("obj");
        }
        _creditShopApi = new CreditShopApi(getActivity());
        initView(view);
        iniData();
    }

    public void initView(View view) {
        mComfireBtn = (Button) view.findViewById(R.id.comshop_exchange_btn);
        mComfireBtn.setOnClickListener(onClickListener);
        mShopNumPlus = (ImageView) view.findViewById(R.id.c_shop_plus);
        mShopNumPlus.setOnClickListener(onClickListener);
        mShopNumAdd = (ImageView) view.findViewById(R.id.c_shop_add);
        mShopNumAdd.setOnClickListener(onClickListener);
        mShopNumberTx = (TextView) view.findViewById(R.id.c_shop_number);
        _shopImage = (ImageView) view.findViewById(R.id.c_shop_image);
        _shopName = (TextView) view.findViewById(R.id.c_shop_name);
        _remianTxt = (TextView) view.findViewById(R.id.c_shop_reamin);
        _totalPrice = (TextView) view.findViewById(R.id.c_shop_remain_interal);
        _buyNumberTxt = (TextView) view.findViewById(R.id.c_shop_number);
        _buyNameTxt = (EditText) view.findViewById(R.id.c_shop_username);
        _buyTelephoneTxt = (EditText) view.findViewById(R.id.c_shop_phone);
        _buyAddressTxt = (EditText) view.findViewById(R.id.c_shop_address);
        _exchangeDescribe = (EditText) view.findViewById(R.id.shop_exchange_describe);
    }

    public void iniData() {
        initContact();
        if (_shopInfo != null) {
            String thumb = "";
            if (_shopInfo.getImages() != null && _shopInfo.getImages().size() > 0)
                thumb = _shopInfo.getImages().get(0).getUrl();
            Util.LoadThumebImage(_shopImage, thumb, null);
            _shopName.setText(_shopInfo.getName());
            _totalPrice.setText(_shopInfo.getPrice() + "");
            _stockNumber = _shopInfo.getStock() - 1;
            String strFormat = String.format("%d", _stockNumber) + "剩余";
            _remianTxt.setText(strFormat);
        }
    }

    public void initContact() {
        _creditShopApi.getDefaultContact(new DataRequest.DataCallback<ExchangeShop.ContactBean>() {
            @Override
            public void onSuccess(boolean isAppend, ExchangeShop.ContactBean data) {
                if (data != null) {
                    _defaultTelphone = data.getPhone_number();
                    _buyNameTxt.setText(data.getName());
                    _buyTelephoneTxt.setText(data.getPhone_number());
                    _buyAddressTxt.setText(data.getAddress());
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mShopNumPlus) {
                plusShopNumber();
            } else if (view == mShopNumAdd) {
                addShopNumber();
            } else if (view == mComfireBtn) {
                String buyName = _buyNameTxt.getText().toString().trim();
                if (buyName == null || TextUtils.isEmpty(buyName)) {
                    ToastUtils.toastMsgFunction(getContext(), "兑换人姓名不能为空");
                    return;
                }
                String buyTelephone = _buyTelephoneTxt.getText().toString().trim();
                if (buyTelephone == null || TextUtils.isEmpty(buyTelephone)) {
                    ToastUtils.toastMsgFunction(getContext(), "兑换人电话不能为空");
                    return;
                }
                if (!checkTelePhone(buyTelephone)) {
                    ToastUtils.toastMsgFunction(getContext(), "兑换人电话号码输入有误");
                    return;
                }
                String buyAddress = _buyAddressTxt.getText().toString().trim();
                if (buyAddress == null || TextUtils.isEmpty(buyAddress)) {
                    ToastUtils.toastMsgFunction(getContext(), "兑换人地址不能为空");
                    return;
                }
                ExchangeShop exshop = new ExchangeShop();
                exshop.setCount(number);
                ExchangeShop.ContactBean bean = new ExchangeShop.ContactBean();
                if (!TextUtils.equals(_defaultTelphone, buyTelephone)) {
                    bean.setId(0);
                } else
                    bean.setId(1);
//                bean.setId(0);   //0: 新增  》0  修改
                bean.setName(buyName);
                bean.setPhone_number(buyTelephone);
                bean.setAddress(buyAddress);
                exshop.setContact(bean);
                _creditShopApi.exchangeShop(_shopInfo.getId(), exshop, new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        String result = (String) data;
                        if (result != null && !TextUtils.isEmpty(result)) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("obj", _shopInfo);
                            bundle.putString("exchange_code", result);
                            bundle.putDouble("takeScore", totalPrice);
                            bundle.putString("exchange_describe", _exchangeDescribe.getText().toString());
                            WhiteTopBarActivity.startAct(getActivity(), ExShopResultFragment.class.getName(), "兑换结果", "", bundle);
                        } else {
                            ToastUtils.toastMsgFunction(getActivity(), "兑换失败");
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        ToastUtils.toastMsgFunction(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()));
                    }
                });

            }
        }
    };

    public void addShopNumber() {
        getTotalPrice(true);
    }

    public void plusShopNumber() {
        getTotalPrice(false);
    }

    public void getTotalPrice(boolean isAdd) {
        if (isAdd) {
            if (_stockNumber == 0) {
                return;
            } else {
                number++;
            }
        } else {
            if (number > 1) {
                number--;
            } else {
                return;
            }
        }
        totalPrice = number * _shopInfo.getPrice();
        String strFormat = String.format("%.1f", totalPrice);
        mShopNumberTx.setText(number + "");
        _totalPrice.setText(strFormat);
        if (isAdd) {
            _stockNumber = _stockNumber - 1;
        } else {
            _stockNumber = _stockNumber + 1;
        }
        String str = String.format("%d", _stockNumber) + "剩余";
        _remianTxt.setText(str);
    }

    public boolean checkTelePhone(String telephone) {
        boolean isOk = false;
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(telephone);   //此处参数为String的字符串
        if (m.matches()) {
            isOk = true;
        }
        return isOk;
    }


}
