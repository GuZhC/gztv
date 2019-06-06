package com.dfsx.shop.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.view.banner.BannerDataHelper;
import com.dfsx.core.common.view.banner.BannerItem;
import com.dfsx.core.common.view.banner.SimpleImageBanner;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.shop.R;
import com.dfsx.shop.busniness.CreditShopApi;
import com.dfsx.shop.model.ShopEntryInfo;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by heyang on 2017/7/24
 */
public class ShopInfoFragment extends Fragment {
    private static final String TAG = "ShopInfoFragment";
    public static final int RESULT_OK = -1;
    private final int NETWORK_BUSY = 54;
    private int mScreenWidth;
    private final String STATEText_LIST = "ImagesTextArrayr.mlist";
    protected Activity act;
    //定时任务
    private RelativeLayout mRelativeLayoutFail;
    private SimpleImageBanner topBanner;
    private int destheight = 250;
    int topHeight;
    private CreditShopApi _creditShopAPI;
    private Button mExchangeBtn;
    private long _shopId;
    private TextView _shopNameTxt;
    private TextView _shopPriceTxt;
    private TextView _shopBuyPeopleTxt;
    private TextView _shopRemainTxt;
    private TextView _shopMessageTxt;
    private ShopEntryInfo _shopEntryInfo;


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

    public static ShopInfoFragment newInstance(long type) {
        Bundle bundle = new Bundle();
        bundle.putLong("type", type);
        ShopInfoFragment fragment = new ShopInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_shop_info_custom, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (act instanceof WhiteTopBarActivity) {
            ((WhiteTopBarActivity) act).getTopBarRightText().
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            WhiteTopBarActivity.startAct(getActivity(), ExchangeRcordFragment.class.getName(), "兑换记录");
                        }
                    });
        }
        act = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _shopId = bundle.getLong(WhiteTopBarActivity.KEY_PARAM);
        }
        topHeight = Util.dp2px(getActivity(), 29);
        _creditShopAPI = new CreditShopApi(getActivity());
        initView(view);
//        getLooperImageData();
        getShopMessage();
    }

    public void initView(View view) {
        mExchangeBtn = (Button) view.findViewById(R.id.shop_exchange_btn);
        mExchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("obj", _shopEntryInfo);
                WhiteTopBarActivity.startAct(getActivity(), ComfireExShopFragment.class.getName(), "确认兑换", "", bundle);
            }
        });
        _shopNameTxt = (TextView) view.findViewById(R.id.shop_name_txt);
        _shopPriceTxt = (TextView) view.findViewById(R.id.shop_price_txt);
        _shopBuyPeopleTxt = (TextView) view.findViewById(R.id.shop_buypeople_txt);
        _shopRemainTxt = (TextView) view.findViewById(R.id.shop_reamain_txt);
        _shopMessageTxt = (TextView) view.findViewById(R.id.shop_meassge_content);
        topBanner = (SimpleImageBanner) view.findViewById(R.id.shop_simple_img_banner);
//        if (mBshowViewPager) {
//        list.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.banner_layout, null));
//        topBanner = (SimpleImageBanner) list.findViewById(R.id.simple_img_banner);
        destheight = Util.dp2px(getActivity(), 169);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topBanner.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(mScreenWidth, destheight);
        } else {
            params.width = mScreenWidth;
            params.height = destheight;
        }
        topBanner.setLayoutParams(params);
        topBanner.setDelay(4);
        topBanner.setTitleShow(false);
        topBanner.setPeriod(4);
//        topBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
//            @Override
//            public void onItemClick(int position) {
//                if (looperDataList != null && position >= 0 &&
//                        position < looperDataList.size()) {
//                }
//            }
//        });
    }

    private void getShopMessage() {
        _creditShopAPI.getShopInfo(_shopId, new DataRequest.DataCallback<ShopEntryInfo>() {
            @Override
            public void onSuccess(boolean isAppend, ShopEntryInfo data) {
                if (data != null) {
                    _shopEntryInfo = data;
                    _shopNameTxt.setText(data.getName());
                    _shopPriceTxt.setText(data.getPrice() + "");
                    String strFormat = String.format("%d", data.getExchange_count()) + "人兑换";
                    _shopBuyPeopleTxt.setText(strFormat);
                    strFormat = String.format("%d", data.getExchange_count()) + "剩余";
                    _shopRemainTxt.setText(strFormat);
                    _shopMessageTxt.setText("\b\b\b\b"+data.getDetail());
                    if (data.getImages() != null && data.getImages().size() > 0) {
                        topBanner.setSource(bannerDataHelper.getBannerItems(data.getImages())).startScroll();
                    }
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

//    private void getLooperImageData() {
//        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/";
//        url += 30 + "/contents?";
//        DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().
//                setUrl(url).setToken(null).build();
//        if (looperDataRequester == null) {
//            looperDataRequester = new HeadlineListManager(getActivity(), 1 + "", mCLoumnType, true);
//        }
//        looperDataRequester.getData(params, false).setCallback
//                (new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
//                    @Override
//                    public void onSuccess(boolean isAppend, ArrayList<ContentCmsEntry> data) {
//                        looperDataList = data;
//                        if (data != null && data.size() > 0 && topBanner != null) {
//                            if (data.size() > 4) {
//                                int count = data.size();
//                                while (count > 4) {
//                                    count--;
//                                    data.remove(count);
//                                    count = data.size();
//                                }
//                            }
//                            topBanner.setSource(bannerDataHelper.getBannerItems(data)).startScroll();
//                        }
//                    }
//
//                    @Override
//                    public void onFail(ApiException e) {
//
//                    }
//                });
//    }

    private BannerDataHelper bannerDataHelper = new BannerDataHelper<ShopEntryInfo.ImagesBean>() {
        @Override
        public BannerItem changeToBannerItem(ShopEntryInfo.ImagesBean data) {
            BannerItem item = new BannerItem();
            item.imgUrl = data.getUrl();
            item.title = "";
            return item;
        }
    };

}
