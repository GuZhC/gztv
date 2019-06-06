package com.dfsx.shop.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.view.banner.BannerItem;
import com.dfsx.core.common.view.banner.BaseBanner;
import com.dfsx.core.common.view.banner.SimpleImageBanner;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.shop.R;
import com.dfsx.shop.adapter.ShopGridAdapter;
import com.dfsx.shop.fragment.ExchangeRcordFragment;
import com.dfsx.shop.fragment.ShopInfoFragment;
import com.dfsx.shop.model.ShopEntry;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by heyang on 2017/7/26
 */
public class CreditShopFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private static final String TAG = "CreditShopFragment";
    public static final int RESULT_OK = -1;
    private PullToRefreshListView pullListview;
    private ListView list;
    private final int NETWORK_BUSY = 54;
    private int mScreenWidth;
    private ImageButton mLoadRetryBtn;
    private boolean mBshowViewPager = false;
    private long mCLoumnType = -1;
    //    private HeadlineListManager dataRequester;
//    private HeadlineListManager looperDataRequester;
    private ShopGridAdapter adapter;
    protected Activity act;
    //定时任务
    private RelativeLayout mRelativeLayoutFail;
    private SimpleImageBanner topBanner;
    private int destheight = 250;
    //    private ArrayList<ContentCmsEntry> looperDataList;
    private int offset = 1;
    //    private EmptyView emptyView;
    private TextView mUpdateNumbertx;
    int topHeight;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == NETWORK_BUSY) {
                if (getActivity() != null) {
                    AlertDialog adig = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("网络繁忙，是否重新加载数据.....？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            onResume();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            mRelativeLayoutFail.setVisibility(View.VISIBLE);
                            pullListview.setVisibility(View.GONE);
                            list.setVisibility(View.GONE);
                        }
                    }).create();
                    adig.show();
                } else {
                    mRelativeLayoutFail.setVisibility(View.VISIBLE);
                    pullListview.setVisibility(View.GONE);
                    list.setVisibility(View.GONE);
                }
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mBshowViewPager = false;
        }
        if (act instanceof WhiteTopBarActivity) {
            ((WhiteTopBarActivity) act).getTopBarRightText().
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            WhiteTopBarActivity.startAct(getActivity(), ExchangeRcordFragment.class.getName(), "兑换记录");
///**/                            WhiteTopBarActivity.startAct(getActivity(), ExShopResultFragment.class.getName(), "兑换结果");
                        }
                    });
        }
        topHeight = Util.dp2px(getActivity(), 29);
        View view = inflater.inflate(R.layout.frag_shop_list_custom, container, false);
//        mUpdateNumbertx = (TextView) view.findViewById(R.id.header_update_count);
        mRelativeLayoutFail = (RelativeLayout) view.findViewById(R.id.load_fail_layout);
        mLoadRetryBtn = (ImageButton) view.findViewById(R.id.reload_btn);
        mLoadRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResume();
            }
        });
        pullListview = (PullToRefreshListView) view.findViewById(R.id.news_scroll_layout);
        pullListview.setOnRefreshListener(this);
        pullListview.setMode(PullToRefreshBase.Mode.BOTH);
        list = ((ListView) pullListview.getRefreshableView());
        adapter = new ShopGridAdapter(getActivity());
        list.setAdapter(adapter);
        ArrayList<ShopEntry> dlist = new ArrayList<ShopEntry>();
//        for (int i = 0; i < 6; i++) {
//            ShopEntry shop = new ShopEntry();
//            shop.setShopName("苹果iphon4热卖了");
//            shop.setPeoplebuy(234);
//            shop.setMoney(34);
//            shop.setThumb("http://www3.autoimg.cn/newsdfs/g17/M13/05/37/120x90_0_autohomecar__wKgH51lsGRqAJonaAAhgkf8Ciko608.jpg");
//            dlist.add(shop);
//        }
        adapter.update(dlist, false);
        adapter.setOnGridItemClickListener(new BaseGridListAdapter.OnGridItemClickListener() {
            @Override
            public void onGridItemClick(int linePosition, int column) {
                int realPosition = linePosition * adapter.getColumnCount() + column;
                ArrayList<ShopEntry> dlist = (ArrayList<ShopEntry>) adapter.getData();
                if (dlist != null && realPosition >= 0 && realPosition < dlist.size()) {
                    ShopEntry shop = (ShopEntry) dlist.get(realPosition);
                    WhiteTopBarActivity.startAct(getActivity(), ShopInfoFragment.class.getName(), "积分商城", "", shop.getId());
                }
            }
        });
        if (mBshowViewPager) {
            list.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.banner_layout, null));
            topBanner = (SimpleImageBanner) list.findViewById(R.id.simple_img_banner);
            destheight = Util.dp2px(getActivity(), 169);
            AbsListView.LayoutParams params = (AbsListView.LayoutParams) topBanner.getLayoutParams();
            if (params == null) {
                params = new AbsListView.LayoutParams(mScreenWidth, destheight);
            } else {
                params.width = mScreenWidth;
                params.height = destheight;
            }
            topBanner.setLayoutParams(params);
            topBanner.setDelay(4);
            topBanner.setTitleShow(false);
            topBanner.setBarColor(0);
            topBanner.setGravity(Gravity.CENTER);
            topBanner.setPeriod(4);
            topBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
                @Override
                public void onItemClick(int position) {
//                    if (looperDataList != null && position >= 0 &&
//                            position < looperDataList.size()) {
//                        goDetail(null, looperDataList.get(position), -1);
//                    }
                }
            });
//            getLooperImageData();
        }
        return view;
    }

    private DataFileCacheManager<ArrayList<ShopEntry>> dataRequester = new
            DataFileCacheManager<ArrayList<ShopEntry>>
                    (CoreApp.getInstance().getApplicationContext(),
                            1112 + "", CoreApp.getInstance().getPackageName() + "creaditShopFragment.txt") {
                @Override
                public ArrayList<ShopEntry> jsonToBean(JSONObject jsonObject) {
                    ArrayList<ShopEntry> socityNewsAarry = null;
                    try {
                        JSONArray result = jsonObject.getJSONArray("data");
                        if (result != null) {
                            socityNewsAarry = new ArrayList<ShopEntry>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                ShopEntry entry = new Gson().fromJson(item.toString(), ShopEntry.class);
                                socityNewsAarry.add(entry);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return socityNewsAarry;
                }
            };

    private void getData(int offset) {
        String url = CoreApp.getInstance().getShoppServerUrl() + "/public/commodities?";
        url += "page=" + offset + "&size=20&status=0";
        dataRequester.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(CoreApp.getInstance().getCurrentToken())
                .build(), offset > 1).setCallback(callback);
    }

    private DataRequest.DataCallback<ArrayList<ShopEntry>>
            callback = new DataRequest.DataCallback<ArrayList<ShopEntry>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<ShopEntry> data) {
            if (pullListview != null)
                pullListview.onRefreshComplete();
            if (adapter != null && data != null) {
                adapter.update(data, isAppend);
            }
            pullListview.onRefreshComplete();
        }

        @Override
        public void onFail(ApiException e) {
            e.printStackTrace();
            pullListview.onRefreshComplete();
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && !adapter.isInit()) {
            getData(offset);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData(1);
        if (mBshowViewPager) {
//            getLooperImageData();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        offset++;
        getData(offset);
    }

//    private void getLooperImageData() {
//        String url = CoreApp.getInstance().getShoppServerUrl() + "/public/columns/";
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
//                        e.printStackTrace();
//                    }
//                });
//    }

//    private BannerDataHelper bannerDataHelper = new BannerDataHelper<ContentCmsEntry>() {
//
//        @Override
//        public BannerItem changeToBannerItem(ContentCmsEntry data) {
//            BannerItem item = new BannerItem();
//            if (data.getThumbnail_urls() != null && data.getThumbnail_urls().size() > 0)
//                item.imgUrl = data.getThumbnail_urls().get(0);
//            item.title = data.getTitle();
//            return item;
//        }
//    };

}
