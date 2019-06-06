package com.dfsx.ganzcms.app.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.view.banner.BannerDataHelper;
import com.dfsx.core.common.view.banner.BannerItem;
import com.dfsx.core.common.view.banner.BaseBanner;
import com.dfsx.core.common.view.banner.SimpleImageBanner;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.view.ImagTopView;
import com.dfsx.ganzcms.app.view.QianDaoPopwindow;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.ListViewAdapter;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.*;

/**
 * Created by heyang on 2015/4/1.
 */
public class HeadLineFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private static final String TAG = "HeadLineFragment";
    private static final int SLIDER_PAGE_MAX_NUMBER = 5;
    public static final int RESULT_OK = -1;
    private PullToRefreshListView pullListview;
    protected ListView list;
    private int mScreenWidth;
    private ImageButton mLoadRetryBtn;
    private boolean mBshowViewPager = false;
    private HeadlineListManager dataRequester, looperDataRequester, dynamicRequset,emengyDataRequester;
    protected ListViewAdapter adapter;
    protected NewsDatailHelper newsDatailHelper;
    private final static int defaultIndex = 0;
    private SimpleImageBanner topBanner;
    private int destheight = 250, offset = 1;
    private ArrayList<ContentCmsEntry> looperDataList;
    private Map<Long, ContentCmsEntry> mFirstPageMaps;
    boolean isScrollbean = false;
    private Subscription tabItemSubscription;
    private TextView mUpdateNumbertx;
    Animation shakeIn = null, shakeOut = null;
    long mCLoumnType = -1, mSliderId = -1, dynamicId = 0;
    String columnCode;
    private ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    ArrayList<ScrollItem.ScrollItemEx> itemList;
    private HorizontalScrollView mHorizontalScrollView;
    private LinearLayout mLinearLayout;
    Context _Context;
    ContentCmsApi _contentCmsApi;
    private View rootView;
    QianDaoPopwindow _qiaoDaoPup;
    private boolean _ishowQucikentry = false;

    private Map<Integer, AdsEntry> adRowMap;
    private LinearLayout emergencyContainer;   //应急广播
    private ViewFlipper flipperText;    //滚动文字
    private boolean showEmergency = false;   //是否显示应急广播

    public static HeadLineFragment newInstance(long id, String type, long slideId) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("type", type);
        bundle.putLong("slideId", slideId);
        HeadLineFragment fragment = new HeadLineFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static HeadLineFragment newInstance(long id, String type, long slideId, long dynaicId) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("type", type);
        bundle.putLong("slideId", slideId);
        bundle.putLong("dynamicId", dynaicId);
        HeadLineFragment fragment = new HeadLineFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        newsDatailHelper = new NewsDatailHelper(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tabItemSubscription != null)
            tabItemSubscription.unsubscribe();
        if (getVideoPlyer() != null) {
            getVideoPlyer().onDestroy();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news, container, false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mCLoumnType = bundle.getLong("id");
            columnCode = bundle.getString("type");
            mSliderId = bundle.getLong("slideId");
            dynamicId = bundle.getLong("dynamicId");
            if (mSliderId != -1 && mSliderId != 0) {
                mBshowViewPager = true;
            }
//            if (TextUtils.equals(columnCode, "home")) {
            if (dynamicId != 0) {
                _ishowQucikentry = true;
            }
        }

        long homeId = ColumnBasicListManager.getInstance().getHomeNewsId();
        if (homeId == mCLoumnType)
            showEmergency = true;

        rootView = view;
        _Context = getActivity();
        _contentCmsApi = newsDatailHelper.getmContentCmsApi();
        shakeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.head_view_top_in);
        shakeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.head_view_top_out);
        //创建数据的管理器
        dynamicRequset = new HeadlineListManager(getActivity(), dynamicId + "", dynamicId, "dynamicId");
        looperDataRequester = new HeadlineListManager(getActivity(), mSliderId + "", mSliderId, "slider");
        dataRequester = new HeadlineListManager(getActivity(), mCLoumnType + "", mCLoumnType, columnCode);
        dataRequester.setCallback(new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
            @Override
            public void onSuccess(final boolean isAppend, ArrayList<ContentCmsEntry> data) {
                if (pullListview != null)
                    pullListview.onRefreshComplete();
                if (!(data == null || data.isEmpty())) {
                    Observable.just(data)
                            .subscribeOn(Schedulers.io())
                            .flatMap(new Func1<ArrayList<ContentCmsEntry>, Observable<ContentCmsEntry>>() {
                                @Override
                                public Observable<ContentCmsEntry> call(ArrayList<ContentCmsEntry> topicalEntry) {
                                    int start = adapter.getCount();
                                    int size = topicalEntry.size();
                                    int row_start = start;
                                    int row_end = 20;
                                    if (isAppend) {
                                        row_end = start + size;
                                    } else {
                                        row_start = 0;
                                        row_end = size;
                                    }
                                    adRowMap = _contentCmsApi.getListAdsEntry(mCLoumnType, row_start, row_end);
                                    //获取广告
                                    return Observable.from(topicalEntry);
                                }
                            })
                            .map(new Func1<ContentCmsEntry, ContentCmsEntry>() {
                                @Override
                                public ContentCmsEntry call(ContentCmsEntry contentcms) {
                                    //特殊处理show 直播的情况
                                    if (contentcms != null && contentcms.getModeType() == 3) {
                                        if (contentcms.getShow_id() == -1 || contentcms.getShow_id() == 0) {
                                            ContentCmsInfoEntry info = _contentCmsApi.getEnteyFromJson(contentcms.getId());
                                            if (info != null) {
                                                contentcms.setShow_id(info.getLiveId());
                                            }
                                        }
                                        LiveInfo liveInfo = _contentCmsApi.getLiveInfoStatus(contentcms.getShow_id());
                                        contentcms.setLiveInfo(liveInfo);
                                    }
                                    if (contentcms != null && contentcms.getModeType() == 11) {
                                        ContentCmsInfoEntry info = _contentCmsApi.getEnteyFromJson(contentcms.getId());
                                        //特殊处理专题
                                        if (info != null) {
                                            contentcms.setType(info.getType());
                                            contentcms.setId(info.getId());
                                            contentcms.setTitle(info.getTitle());
                                            contentcms.setView_count(info.getView_count());
                                            contentcms.setPublish_time(info.getPublish_time());
                                            if (!(info.getThumbnail_urls() == null
                                                    || info.getThumbnail_urls().isEmpty())) {
                                                contentcms.setPoster_url(info.getThumbnail_urls().get(0));
                                            }
                                        }
                                    }
                                    return contentcms;
                                }
                            })
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<List<ContentCmsEntry>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(List<ContentCmsEntry> data) {
                                    updateList(isAppend, data);
                                    createRowAdware();
                                }
                            });
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                if (pullListview != null)
                    pullListview.onRefreshComplete();
            }
        });
        mUpdateNumbertx = (TextView) view.findViewById(R.id.header_update_count);
        mLoadRetryBtn = (ImageButton) view.findViewById(R.id.reload_btn);
        mLoadRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HeadLineFragment.this.onResume();
            }
        });
        pullListview = (PullToRefreshListView) view.findViewById(R.id.news_scroll_layout);
        pullListview.setOnRefreshListener(this);
        pullListview.setMode(PullToRefreshBase.Mode.BOTH);
        list = ((ListView) pullListview.getRefreshableView());
        list.setNestedScrollingEnabled(true);   //listview能够嵌套滚动
        adapter = new ListViewAdapter(this.getActivity());
        if (savedInstanceState != null) {
            adapter.init(savedInstanceState);
        }
        list.setAdapter(adapter);
        list.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.banner_recommend_layout, null));
        topBanner = (SimpleImageBanner) list.findViewById(R.id.simple_img_banner);
        mHorizontalScrollView = (HorizontalScrollView) list.findViewById(R.id.hsv_view);
        mLinearLayout = (LinearLayout) list.findViewById(R.id.hsv_content);
        emergencyContainer = (LinearLayout) list.findViewById(R.id.emergency_container);
        flipperText = (ViewFlipper) list.findViewById(R.id.flipper_text);
        if (mBshowViewPager) {
            destheight = Util.dp2px(getActivity(), 182);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topBanner.getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(mScreenWidth, destheight);
            } else {
                params.width = mScreenWidth;
                params.height = destheight;
            }
            topBanner.setLayoutParams(params);
            topBanner.setDelay(4);
            topBanner.setPeriod(4);
            topBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
                @Override
                public void onItemClick(int position) {
                    if (looperDataList != null && position >= 0 &&
                            position < looperDataList.size()) {
                        goDetail(looperDataList.get(position));
                    }
                }
            });
            getLooperImageData();
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListViewAdapter.ViewHolder vmHolder = (ListViewAdapter.ViewHolder) view.getTag();
                if (getVideoPlyer() != null && getVideoPlyer().getTag() != null) {
                    View child = (View) getVideoPlyer().getTag();
                    String ip = child.getParent().getParent().getParent().getClass().getName();
                    if (view == child.getParent().getParent().getParent().getParent()) {
                        if (getVideoPlyer().isPlay())
                            return;
                    }
                }
                adapter.stopPlay();
                if (vmHolder != null) {
                    gotoAct(view, vmHolder.item);
                }
            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:  //停止滚动
                        isScrollbean = false;
                        int lastVisiblePosition = pullListview.getRefreshableView().getLastVisiblePosition();
                        if (lastVisiblePosition == pullListview.getRefreshableView().getCount() - 1) {
                        }
                        if (pullListview.getRefreshableView().getFirstVisiblePosition() == 0) {
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:   //滚动时
                        isScrollbean = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:   //抬起手指，屏幕产生惯性滑动
                        isScrollbean = false;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //    if (isScrollbean)
                {
                    int selectedItem = adapter.getPlayVideoIndex();
                    if (selectedItem == -1) return;
                    boolean isStop = false;
                    int i = selectedItem + list.getHeaderViewsCount();
                    int childCount = list.getChildCount();
                    View selectedItemView = null;
                    if (i == firstVisibleItem) {
                        selectedItemView = list.getChildAt(0);
                        int viewTop = selectedItemView.getTop();
                        int height = selectedItemView.getHeight();
                        if (viewTop == list.getPaddingTop()) { //消失
                        } else if (height - Math.abs(viewTop) < height / 4) {
                            isStop = true;
                            adapter.setPlayVideoIndex(-1);
                        } else {
                        }
                        //          Log.e("TAG", "viewTop == " + viewTop);
                        //           Log.e("TAG", "viewHe == " + selectedItemView.getHeight());
                    } else if (i == firstVisibleItem + visibleItemCount - 1) {
                        int allCount = list.getChildCount();
                        selectedItemView = list.getChildAt(allCount - 1);
                        int viewTop = selectedItemView.getTop();
                        int viewHeight = selectedItemView.getHeight();
                        int height = list.getHeight();
                        //       Log.e("TAG", "viewTop == " + viewTop);
                        //       Log.e("TAG", "list height == " + list.getHeight());
                        if (viewTop == list.getHeight()) {

                        } else if (height - Math.abs(viewTop) < viewHeight / 4) {
                            isStop = true;
                            adapter.setPlayVideoIndex(-1);
                        } else {

                        }
                    }
                    if (isStop) stopVideo();
                }
            }
        });
        initAction();
        if (_ishowQucikentry)
            getDynamicData();
        if (showEmergency){ //获取应急广播
            getEmergencyData();
        }

        /**
         * 适配嵌套滚动
         */
        if (getParentFragment() != null && getParentFragment() instanceof PullToRefreshListView.PullRecyclerHelper)
            pullListview.setPullRecyclerHelper((PullToRefreshListView.PullRecyclerHelper) getParentFragment());

    }

    private void getEmergencyData() {
        ColumnCmsEntry emengyMsg = ColumnBasicListManager.getInstance().getYingjiColumn();
        if (emengyMsg != null && emergencyContainer != null){
            emergencyContainer.setVisibility(View.VISIBLE);
            emergencyContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DefaultFragmentActivity.start(getActivity(),EmergencyFragment.class.getName());
                }
            });
            if (emengyDataRequester == null)
                emengyDataRequester = new HeadlineListManager(getActivity(), emengyMsg.getId() + "", emengyMsg.getId(), "");
            emengyDataRequester.start(false, 1);
            emengyDataRequester.setCallback(new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
                @Override
                public void onSuccess(boolean isAppend, ArrayList<ContentCmsEntry> data) {
                    if (HeadLineFragment.this.getContext() != null &&
                            HeadLineFragment.this.getActivity() != null &&
                            !HeadLineFragment.this.getActivity().isFinishing()) {
                        if (!(data == null || data.isEmpty())) {
                            ArrayList<ContentCmsEntry> lists = new ArrayList<>();
                            int k = 0;
                            for (int i = 0; i <  data.size(); i++) {
                                if (!TextUtils.isEmpty(data.get(i).getEmergencyType()) && k < 5){
                                    lists.add(data.get(i));
                                    k++;
                                }
                            }
                            initFlipperText(lists);
                        }
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void initFlipperText(ArrayList<ContentCmsEntry> data) {
        //防止重复请求造成多此添加
        flipperText.removeAllViews();
        flipperText.clearAnimation();
        flipperText.setAutoStart(false);
        flipperText.stopFlipping();
        for (ContentCmsEntry entry : data){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_flipper_text,null);
            TextView textView = (TextView) view.findViewById(R.id.text_title);
            textView.setText(entry.getTitle());
            flipperText.addView(view);
        }
        flipperText.setInAnimation(getActivity(),R.anim.notice_in);
        flipperText.setOutAnimation(getActivity(),R.anim.notice_out);
        flipperText.setAutoStart(true);
        flipperText.setFlipInterval(2000);
        flipperText.startFlipping();
    }

    protected void createRowAdware() {
        if (adapter == null || adapter.getCount() == 0) return;
        if (!(adRowMap == null || adRowMap.isEmpty())) {
            for (Map.Entry<Integer, AdsEntry> entry : adRowMap.entrySet()) {
                int row = entry.getKey();
                AdsEntry adsEntry = entry.getValue();
                ContentCmsEntry cms = null;
                if (adsEntry != null) {
                    cms = newsDatailHelper.covertToContent(adsEntry);
                    if (cms != null)
                        adapter.insertAd(row, cms);
//                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void updateList(boolean isAppend, List<ContentCmsEntry> data) {
        boolean isUp = false;
        if (!isAppend && dataRequester.isShowUpdate()) {
            dataRequester.setIsShowUpdate(false);
            String msg = "最近没有更新";
            if (data != null && data.size() > 0) {
                Log.e(TAG, "获取条目数===:" + data.size());
                Log.e(TAG, "获取第一条目数===:" + data.get(0).getTitle().toString());
                Log.e(TAG, "获取最后一条目数===:" + data.get(data.size() - 1).getTitle().toString());
                int count = 0;
                if (mFirstPageMaps != null) {
                    for (ContentCmsEntry entry : data) {
                        ContentCmsEntry tag = mFirstPageMaps.get(entry.getId());
                        if (tag == null) count++;
                    }
                }
//                        if (!adapter.compareTO(data, isAppend)) {
                if (count > 0) {
//                    mUpdateNumbertx.setVisibility(View.VISIBLE);
                    msg = count + "条更新";
//                    isUp = true;
                }
            } else {
                Log.e(TAG, "没有获取条目数===:");
            }
            if (isUp) {
                mUpdateNumbertx.setVisibility(View.VISIBLE);
                mUpdateNumbertx.startAnimation(shakeIn);
                mUpdateNumbertx.setText(msg);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mUpdateNumbertx.startAnimation(shakeOut);
                        mUpdateNumbertx.setVisibility(View.GONE);
                    }
                }, 1000);
            }
        }

        if (!(data == null || data.isEmpty())) {
            adapter.update((ArrayList<ContentCmsEntry>) data, isAppend);
            if (isUp || mFirstPageMaps == null) {
                if (mFirstPageMaps == null) mFirstPageMaps = new HashMap<Long, ContentCmsEntry>();
                if (mFirstPageMaps.size() > 0) mFirstPageMaps.clear();
                for (ContentCmsEntry entry : data) {
                    mFirstPageMaps.put(entry.getId(), entry);
                }
                Log.e(TAG, "map 的大小 ==" + mFirstPageMaps.size());
            }
        }
    }

    public void gotoAct(View parent, ContentCmsEntry item) {
        if (parent != null) {
            TextView titleText = (TextView) parent.findViewById(R.id.news_list_item_title);
            if (titleText != null) {
                titleText.setTextColor(getActivity().getResources().getColor(R.color.news_item_title_forget_color));
            }
            IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_NEWS).updateValuse(item.getId(), false, false, true);
        }
        goDetail(item);
    }

    public void goDetail(ContentCmsEntry item) {
        newsDatailHelper.goDetail(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            adapter.saveInstanceState(outState);
        }
    }

    private void getDynamicData() {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + dynamicId + "/contents?";
        url += "&page=" + 1 + "";
        DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().
                setUrl(url).setToken(App.getInstance().getCurrentToken()).build();
        dynamicRequset.getData(params, false).setCallback
                (callback);
    }

    private DataRequest.DataCallback<ArrayList<ContentCmsEntry>>
            callback = new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
        @Override
        public void onSuccess(boolean isAppend, ArrayList<ContentCmsEntry> data) {
            if (data != null && !data.isEmpty()) {
                mHorizontalScrollView.setVisibility(View.VISIBLE);
                Observable.from(data)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<ContentCmsEntry, ContentCmsInfoEntry>() {
                            @Override
                            public ContentCmsInfoEntry call(ContentCmsEntry topicalEntry) {
                                ContentCmsInfoEntry tag = _contentCmsApi.getContentCmsInfo(topicalEntry.getId());
                                return tag;
                            }
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<ContentCmsInfoEntry>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<ContentCmsInfoEntry> data) {
                                if (data != null && !data.isEmpty()) {
                                    initOtherWays((ArrayList<ContentCmsInfoEntry>) data);
                                }
                            }
                        });
            } else {
                mHorizontalScrollView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFail(ApiException e) {
            e.printStackTrace();
        }
    };

    private void initOtherWays(ArrayList<ContentCmsInfoEntry> dlisf) {
        //        LinkedHashSet<ColumnCmsEntry> dlisf = ColumnBasicListManager.getInstance().findChildColListByName("推荐", "手机动态入口");
        if (itemList == null) {
            itemList = new ArrayList<>();
        } else {
            itemList.clear();
            mLinearLayout.removeAllViews();
        }
        if (dlisf == null || dlisf.isEmpty()) {
            mHorizontalScrollView.setVisibility(View.GONE);
            return;
        }
        fragments.clear();
        itemWidthList.clear();
        if (dlisf == null || dlisf.isEmpty()) return;
//        Collections.sort(dlisf, new Comparator() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                return new Integer(((ColumnCmsEntry) o1).getWeight()).compareTo(new Integer(((ColumnCmsEntry) o2).getWeight()));
//            }
//        });
//        Collections.reverse(dlisf);
        int count = dlisf.size();
        for (int i = 0; i < dlisf.size(); i++) {
            ContentCmsInfoEntry item = dlisf.get(i);
            ContentCmsInfoEntry.Quickentry quick = item.getQuickentry();
            if (quick != null && TextUtils.equals(quick.getType(), "contribute") &&
                    !ColumnBasicListManager.getInstance().isShowEditWordBtn()) {
                count -= 1;
                continue;
            }
        }
        if (count > 5) {
            createDynitacIcon(dlisf, count);
        } else {
            createNysicMinIcon(dlisf, count);
        }
        /**
         int count = dlisf.size();
         for (int i = 0; i < dlisf.size(); i++) {
         ContentCmsInfoEntry item = dlisf.get(i);
         ContentCmsInfoEntry.Quickentry quick = item.getQuickentry();
         if (quick != null && TextUtils.equals(quick.getType(), "contribute") &&
         !ColumnBasicListManager.getInstance().isShowEditWordBtn()) {
         count -= 1;
         continue;
         }
         }
         for (int i = 0; i < dlisf.size(); i++) {
         ContentCmsInfoEntry item = dlisf.get(i);
         ContentCmsInfoEntry.Quickentry quick = item.getQuickentry();
         if (quick != null && TextUtils.equals(quick.getType(), "contribute") &&
         !ColumnBasicListManager.getInstance().isShowEditWordBtn()) {
         //                count -= 1;
         continue;
         }
         RelativeLayout layout = new RelativeLayout(_Context);
         ImagTopView img = new ImagTopView(_Context);
         //            img.getFirstView().setBackgroundResource(R.drawable.qidao);
         String thumb = "";
         if (item.getThumbnail_urls() != null && item.getThumbnail_urls().size() > 0)
         thumb = item.getThumbnail_urls().get(0);
         Util.LoadThumebImage(img.getFirstView(), thumb, null);

         //            int itemSize = (mScreenWidth - Util.dp2px(getActivity(), 46)) / count;
         int itemSize = Util.dp2px(_Context, 50);

         ViewGroup.LayoutParams lp = img.getFirstView().getLayoutParams();
         //            lp.width = itemSize;
         lp.height = itemSize;//lp.height=LayoutParams.WRAP_CONTENT;
         img.getFirstView().setLayoutParams(lp);
         img.getFirstView().setAdjustViewBounds(true);
         //            img.getFirstView().setScaleType(ImageView.ScaleType.CENTER);
         img.getSecondView().setText(item.getTitle());
         img.getSecondView().setTextColor(R.color.black);
         img.getSecondView().setTextSize(12);
         RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
         ViewGroup.LayoutParams.MATCH_PARENT);
         //            layout.setPadding(0, 0, Util.dp2px(_Context, 13), 0);
         //            layout.addView(img, params);
         //            mLinearLayout.addView(layout);
         params.addRule(RelativeLayout.CENTER_IN_PARENT);
         layout.addView(img, params);
         int itemWidth = (mScreenWidth - Util.dp2px(getActivity(), 46)) / count;

         //            int itemWidth = (mScreenWidth - Util.dp2px(getActivity(), 46));
         mLinearLayout.addView(layout, itemWidth, Util.dp2px(_Context, 70));
         itemWidthList.add(itemSize);
         layout.setOnClickListener(_dynamicTaskItemlister);
         layout.setTag(item);
         //            fragments.add(scrollItem.getFragment());
         }   **/
    }

    public void createDynitacIcon(ArrayList<ContentCmsInfoEntry> dlisf, int count) {
        for (int i = 0; i < dlisf.size(); i++) {
            ContentCmsInfoEntry item = dlisf.get(i);
            ContentCmsInfoEntry.Quickentry quick = item.getQuickentry();
            if (quick != null && TextUtils.equals(quick.getType(), "contribute") &&
                    !ColumnBasicListManager.getInstance().isShowEditWordBtn()) {
                continue;
            }
            RelativeLayout layout = new RelativeLayout(_Context);
            String thumb = "";
            if (item.getThumbnail_urls() != null && item.getThumbnail_urls().size() > 0)
                thumb = item.getThumbnail_urls().get(0);
            ImagTopView img = createIcon(item.getTitle(), thumb);

            int itemWidth = (mScreenWidth - Util.dp2px(getActivity(), 46)) / count;
            //            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemWidth,
            //                    ViewGroup.LayoutParams.MATCH_PARENT);
            //            params.width=itemWidth;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(img, params);
            //            int itemWidth = (mScreenWidth - Util.dp2px(getActivity(), 46)) / count;

            int itemSize = Util.dp2px(_Context, 60);

            //            int itemWidth = (mScreenWidth - Util.dp2px(getActivity(), 46));
            mLinearLayout.addView(layout, itemSize, Util.dp2px(_Context, 70));
            //            mLinearLayout.addView(layout);
            //            itemWidthList.add(itemSize);
            layout.setOnClickListener(_dynamicTaskItemlister);
            layout.setTag(item);
        }
    }

    //   图标 有个 4个
    public void createNysicMinIcon(ArrayList<ContentCmsInfoEntry> dlisf, int count) {
        for (int i = 0; i < dlisf.size(); i++) {
            ContentCmsInfoEntry item = dlisf.get(i);
            ContentCmsInfoEntry.Quickentry quick = item.getQuickentry();
            if (quick != null && TextUtils.equals(quick.getType(), "contribute") &&
                    !ColumnBasicListManager.getInstance().isShowEditWordBtn()) {
                continue;
            }
            RelativeLayout layout = new RelativeLayout(_Context);
            String thumb = "";
            if (item.getThumbnail_urls() != null && item.getThumbnail_urls().size() > 0)
                thumb = item.getThumbnail_urls().get(0);
            ImagTopView img = createIcon(item.getTitle(), thumb);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            int itemWidth = (mScreenWidth - Util.dp2px(_Context, 46)) / count;
            params.width = itemWidth;
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(img, params);
            mLinearLayout.addView(layout, itemWidth, Util.dp2px(_Context, 70));
            //            itemWidthList.add(itemSize);
            layout.setOnClickListener(_dynamicTaskItemlister);
            layout.setTag(item);
        }
    }

    public ImagTopView createIcon(String title, String thumb) {
        ImagTopView img = new ImagTopView(_Context);
//        Util.LoadThumebImage(img.getFirstView(), thumb, null);
        Glide.with(getActivity())
                .load(thumb)
                .asBitmap()
                .error(R.drawable.glide_default_image)
                .into(img.getFirstView());
        int itemSize = Util.dp2px(_Context, 50);
        ViewGroup.LayoutParams lp = img.getFirstView().getLayoutParams();
        lp.height = itemSize;//lp.height=LayoutParams.WRAP_CONTENT;
        img.getFirstView().setLayoutParams(lp);
        img.getFirstView().setAdjustViewBounds(true);
        img.getSecondView().setText(title);
        img.getSecondView().setTextColor(R.color.black);
        img.getSecondView().setTextSize(12);
        itemWidthList.add(itemSize);
        return img;
    }

    View.OnClickListener _dynamicTaskItemlister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                ContentCmsInfoEntry entry = (ContentCmsInfoEntry) v.getTag();
                if (entry == null) return;
                if (TextUtils.equals(entry.getQuickentry().getType(), "sign")) {
                    if (_qiaoDaoPup == null)
                        _qiaoDaoPup = new QianDaoPopwindow(getActivity());
                    _qiaoDaoPup.autoQianDao(getActivity(), rootView);
                } else {
                    try {
                        newsDatailHelper.gotoDyniamictsk(entry);
                    } catch (ApiException e) {
                        e.printStackTrace();
                        ToastUtils.toastApiexceFunction(getActivity(), e);
                    }
                }
            }
        }
    };

    private void initAction() {
        tabItemSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_SCROLL_ITEM_OK)) {
                            int pos = intent.getIntExtra("pos", -1);
                            if (pos != -1) {
                                if (pos != defaultIndex) {
                                    adapter.stopPlay();
                                }
                            }
                        }
                    }
                });
    }

    public VideoAdwarePlayView getVideoPlyer() {
        if (getActivity() instanceof MainTabActivity) {
            return ((MainTabActivity) getActivity()).getVideoPlayer();
        }
        return null;
    }

    public void stopVideo() {
        if (getVideoPlyer() != null) {
            getVideoPlyer().stop();
            getVideoPlyer().release();
            removeVideoPlayer();
            if (getVideoPlyer().getTag() != null) {
                View tag = (View) getVideoPlyer().getTag();
                tag.setVisibility(View.VISIBLE);
            }
        }
    }

    private void removeVideoPlayer() {
        if (getVideoPlyer() != null) {
            ViewGroup view = (ViewGroup) getVideoPlyer().getParent();
            if (view != null) {
                view.removeView(getVideoPlyer());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && !adapter.isInited()) {
            dataRequester.start(false, false, offset);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        offset = 1;
        dataRequester.setIsShowUpdate(true);
        dataRequester.start(false, false, offset);
        if (mBshowViewPager) {
            getLooperImageData();
        }
        if (_ishowQucikentry)
            getDynamicData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        offset++;
        dataRequester.start(true, false, offset);
    }

    private void getLooperImageData() {
//        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/";
//        url += mSliderId + "/contents?";
//        DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().
//                setUrl(url).setToken(null).build();
        looperDataRequester.start(false, 1);
        looperDataRequester.setCallback
                (new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
                    @Override
                    public void onSuccess(boolean isAppend, ArrayList<ContentCmsEntry> data) {
                        looperDataList = data;
                        if (data != null && data.size() > 0 && topBanner != null) {
                            if (data.size() > SLIDER_PAGE_MAX_NUMBER) {
                                int count = data.size();
                                while (count > SLIDER_PAGE_MAX_NUMBER) {
                                    count--;
                                    data.remove(count);
                                    count = data.size();
                                }
                            }
                            topBanner.setVisibility(View.VISIBLE);
                            topBanner.setSource(bannerDataHelper.getBannerItems(data)).startScroll();
                        } else {
                            topBanner.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                    }
                });
    }

    private BannerDataHelper bannerDataHelper = new BannerDataHelper<ContentCmsEntry>() {
        @Override
        public BannerItem changeToBannerItem(ContentCmsEntry data) {
            BannerItem item = new BannerItem();
            String thumb = data.getPoster_url();
            if (thumb == null || TextUtils.isEmpty(thumb)) {
                if (data.getThumbnail_urls() != null && data.getThumbnail_urls().size() > 0)
                    thumb = data.getThumbnail_urls().get(0);
            }
            item.imgUrl = thumb;
            item.title = data.getTitle();
            return item;
        }
    };

}
