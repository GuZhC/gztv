package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.CommunityAdapter;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.fragment.CommonityRecordFragment;
import com.dfsx.ganzcms.app.fragment.CommunityPubFileFragment;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.model.Level;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.lzcms.liveroom.view.LiveServiceSharePopupwindow;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.dfsx.procamera.fragment.AcvityCameraTabFragment;
import com.dfsx.selectedmedia.MyVideoThumbLoader;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heyang on  2018/1/10
 */
public class CommunityRecycleUpFragment extends Fragment implements IButtonClickListenr, PullToRefreshBase.OnRefreshListener2<RecyclerView>, NewsDatailHelper.ICommendDialogLbtnlister {
    private static final String fileName = "CommunityRecycleUpFragment";
    protected static final int BAR_TEXT_SIZE_SP = 14;
    protected static final int ITEM_MIN_SPACE_DP = 22;
    private CommunityAdapter adapter;
    private ImageView leftbtn, cWriteBtn, cRecordBtn, cCancelBtn, mtoTopBtn;
    private android.view.animation.Animation animation;
    private TopicListManager dataRequester;
    Subscription mUserStatutasnscription = null, commendUpdateSubscription;  //   用户退出消息监测
    private int offset = 1;
    private long mClounType = -1, userId = -1;
    TopicalApi mTopicalApi = null;
    boolean isRefresh = false;
    Subscription mSubscription = null;
    MyVideoThumbLoader myVideoThumbLoader;
    View rootView, bottomOperView;
    CommuntyDatailHelper _comnityHelper = null;
    List<String> totalTags;
    Activity act;
    private IGetPraistmp mIGetPraistmp = null;
    private PullToRefreshRecyclerView _refreshLayout;
    private EmptyView emptyView;
    private HorizontalScrollView mHorizontalScrollView;
    private LinearLayout mLinearLayout;
    ArrayList<ScrollItem.ScrollItemEx> itemList;
    private ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    protected int mScreenWidth;
    private Map<String, Boolean> mselectTags;
    protected IsLoginCheck isLoginCheck;


    public static CommunityRecycleUpFragment newInstance(ColumnEntry entry) {
        Bundle bundle = new Bundle();
        bundle.putLong("type", entry.getId());
        bundle.putSerializable("tags", (Serializable) entry.getTags());
        CommunityRecycleUpFragment fragment = new CommunityRecycleUpFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CommunityRecycleUpFragment newInstance(long cId, long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong("type", cId);
        bundle.putLong("uId", uid);
        CommunityRecycleUpFragment fragment = new CommunityRecycleUpFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.community_recycle_custom, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        act = getActivity();
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            mClounType = bundle.getLong("type");
            userId = bundle.getLong("uId");
            totalTags = (List<String>) bundle.getSerializable("tags");
        }
        mScreenWidth = UtilHelp.getScreenWidth(getContext());
        _comnityHelper = new CommuntyDatailHelper(getContext());
        mTopicalApi = _comnityHelper.getmTopicalApi();
        mIGetPraistmp = _comnityHelper.getmIGetPraistmp();
        isLoginCheck = _comnityHelper.getMloginCheck();
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.add_score_anim);
        dataRequester = new TopicListManager(getActivity(), userId + "", mClounType);
        myVideoThumbLoader = new MyVideoThumbLoader();
        dataRequester.setCallback(
                new DataRequest.DataCallback<ArrayList<TopicalEntry>>() {
                    @Override
                    public void onSuccess(final boolean isAppend, final ArrayList<TopicalEntry> topicalies) {
                        if (topicalies == null || topicalies.isEmpty()) {
                            if (_refreshLayout != null)
                                _refreshLayout.onRefreshComplete();
                            if (totalTags != null && !isAppend) {
                                adapter.update((ArrayList<TopicalEntry>) topicalies, isAppend);
                            }
                            return;
                        }
                        Observable.just(topicalies)
                                .subscribeOn(Schedulers.io())
                                .concatMap(new Func1<ArrayList<TopicalEntry>, Observable<Bundle>>() {
                                    @Override
                                    public Observable<Bundle> call(final ArrayList<TopicalEntry> topicalies) {
                                        Observable.OnSubscribe<Bundle> subscribe = new Observable.OnSubscribe<Bundle>() {
                                            @Override
                                            public void call(final Subscriber<? super Bundle> subscriber) {
                                                ArrayList<Long> topList = new ArrayList<>();
                                                for (TopicalEntry itemInfo : topicalies) {
                                                    topList.add(itemInfo.getId());
                                                }
                                                ArrayList<Long> userList = new ArrayList<>();
                                                for (TopicalEntry itemInfo : topicalies) {
                                                    if (itemInfo == null) continue;
                                                    boolean flag = checkIsExist(userList, itemInfo.getAuthor_id());
                                                    if (!flag)
                                                        userList.add(itemInfo.getAuthor_id());
                                                }
                                                Map<Long, Boolean> favMap = mTopicalApi.getFavorityFlagsByIds(topList.toArray(new Long[0]));
                                                Map<Long, List<Attachment>> attahMap = mTopicalApi.getCmyAttachmensByIds(topicalies);
                                                Map<Long, Level> levelMap = mTopicalApi.getUserLvelByIds(topList.toArray(new Long[0]));
                                                Map<Long, Boolean> AttionMap = mTopicalApi.getAttionUserFlagsByIds(userList.toArray(new Long[0]));
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("list", topicalies);
                                                bundle.putSerializable("favlist", (Serializable) favMap);
                                                bundle.putSerializable("levellist", (Serializable) levelMap);
                                                bundle.putSerializable("attionlist", (Serializable) AttionMap);
                                                bundle.putSerializable("attments", (Serializable) attahMap);
                                                subscriber.onNext(bundle);
                                            }
                                        };
                                        return Observable.create(subscribe);
                                    }
                                })
                                .map(new Func1<Bundle, ArrayList<TopicalEntry>>() {
                                    @Override
                                    public ArrayList<TopicalEntry> call(Bundle bundle) {
                                        boolean ishome = false;
                                        if (mClounType == -1) ishome = true;
                                        Map<Long, Boolean> favMap = (Map<Long, Boolean>) bundle.getSerializable("favlist");
                                        Map<Long, Boolean> attionMap = (Map<Long, Boolean>) bundle.getSerializable("attionlist");
                                        Map<Long, Level> userLvelMap = (Map<Long, Level>) bundle.getSerializable("levellist");
                                        Map<Long, List<Attachment>> attmentsMap = (Map<Long, List<Attachment>>) bundle.getSerializable("attments");
                                        ArrayList<TopicalEntry> sp = (ArrayList<TopicalEntry>) bundle.getSerializable("list");
                                        if (!(sp == null || sp.isEmpty())) {
                                            for (int i = 0; i < sp.size(); i++) {
                                                TopicalEntry tag = sp.get(i);
                                                tag.setHome(ishome);
                                                if (App.getInstance().getUser() == null) {
                                                    tag.setRelationRole(3);   // 显示关注
                                                } else {
                                                    if (!(attionMap == null || attionMap.isEmpty())) {
                                                        boolean flag = attionMap.get(tag.getAuthor_id());
                                                        if (flag) {
                                                            //  显示已关注
//                                                            tag.setRelationRole(-1);
                                                            tag.setRelationRole(1);
                                                        } else {
                                                            //顯示 加关注
                                                            tag.setRelationRole(0);
                                                        }
                                                    } else {
                                                        tag.setRelationRole(0);
                                                    }
                                                }
                                                if (!(userLvelMap == null || userLvelMap.isEmpty())) {
                                                    Level level = userLvelMap.get(tag.getAuthor_id());
                                                    if (level != null) {
                                                        tag.setUser_level_img(level.getIconUrl());
                                                    }
                                                }
                                                if (!(favMap == null || favMap.isEmpty())) {
                                                    Boolean isfav = favMap.get(tag.getId());
                                                    tag.setIsFavl(isfav);
                                                }
                                                List<Attachment> lists=null;
                                                if (!(attmentsMap == null || attmentsMap.isEmpty())) {
                                                    lists = attmentsMap.get(tag.getId());
                                                    if (!(lists == null || lists.isEmpty()))
                                                        tag.setAttachmentss(lists);
                                                }
                                                tag.setType(mTopicalApi.getQuanziType(lists));
                                            }
                                        }
                                        return sp;     //  from   一个一个的推,. just; 推整个集合
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<ArrayList<TopicalEntry>>() {
                                    @Override
                                    public void onCompleted() {
                                        _refreshLayout.onRefreshComplete();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        _refreshLayout.onRefreshComplete();
                                    }

                                    @Override
                                    public void onNext(ArrayList<TopicalEntry> data) {
                                        if (data != null && !data.isEmpty()) {
                                            if (!isAppend)
                                                FileUtil.saveFileByAccountId(act, fileName + userId, mClounType + "", data);
                                            adapter.update((ArrayList<TopicalEntry>) data, isAppend);
                                        } else {
                                            adapter.update((ArrayList<TopicalEntry>) data, isAppend);
                                        }
                                        _refreshLayout.onRefreshComplete();
                                    }
                                });
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        if (_refreshLayout != null)
                            _refreshLayout.onRefreshComplete();
                    }
                });
        _refreshLayout = (PullToRefreshRecyclerView) view.findViewById(R.id.disclosure_scroll_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _refreshLayout.getRefreshableView().setLayoutManager(layoutManager);
        _refreshLayout.setMode(PullToRefreshBase.Mode.BOTH);
        _refreshLayout.setOnRefreshListener(this);

        adapter = new CommunityAdapter(getActivity());
        _refreshLayout.getRefreshableView().setAdapter(adapter);

        if (getParentFragment() != null && getParentFragment() instanceof PullToRefreshRecyclerView.PullRecyclerHelper)
            _refreshLayout.setPullRecyclerHelper((PullToRefreshRecyclerView.PullRecyclerHelper) getParentFragment());

        adapter.set_item_back(this);
        if (savedInstanceState != null) {
            adapter.init(savedInstanceState);
        }

        //测试
//        if (totalTags == null)
//            totalTags = new ArrayList<>();
//        for (int i = 0; i < 8; i++) {
//            ColumnEntry.ExtraTagsBean bean = new ColumnEntry.ExtraTagsBean();
//            bean.setName("天下" + i);
//            bean.setTag_property(1);
//            bean.setPicture_url("https://www3.autoimg.cn/newsdfs/g24/M07/97/8B/400x300_0_autohomecar__wKgHIVpv4oWAfLBYAADGNHYPKyg856.jpg");
//            totalTags.add(bean);
//        }

//        if (mClounType != -1 && totalTags != null)
//            setHeader(_refreshLayout.getRefreshableView());

        leftbtn = (ImageView) view.findViewById(R.id.boalios_btn);
        cWriteBtn = (ImageView) view.findViewById(R.id.cnew_btn);
        cRecordBtn = (ImageView) view.findViewById(R.id.crecord_btn);
        cCancelBtn = (ImageView) view.findViewById(R.id.ccancel_btn);
        bottomOperView = (View) view.findViewById(R.id.commnutiy_oper_views);
        mtoTopBtn = (ImageView) view.findViewById(R.id.top_btn);
        createFloatButton();
        initRegister();
        initData();
        rootView = view;
    }

    public boolean checkIsExist(ArrayList<Long> dlist, long tagId) {
        boolean isExist = false;
        if (dlist == null || dlist.isEmpty()) return isExist;
        for (int i = 0; i < dlist.size(); i++) {
            if (dlist.get(i) == tagId) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

//    private void setHeader(RecyclerView view) {
//        if (totalTags == null || totalTags.isEmpty()) return;
//        View header = LayoutInflater.from(getActivity()).inflate(R.layout.cummunity_top_tag_layout, view, false);
//        adapter.setHeaderView(header);
//        mHorizontalScrollView = (HorizontalScrollView) header.findViewById(R.id.hsv_view);
//        mLinearLayout = (LinearLayout) header.findViewById(R.id.hsv_content);
//    }

    protected void setEmptyLayout() {
        emptyView = new EmptyView(act);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        emptyView.setLayoutParams(p);
        View emptyLayout = LayoutInflater.from(act).
                inflate(R.layout.no_my_live_layout, null);

        emptyView.setLoadOverView(emptyLayout);
        emptyView.loading();


//        adapter.setEmptyView(emptyView);
    }

    public void initData() {
        List<TopicalEntry> dlsit = (List<TopicalEntry>) FileUtil.getFileByAccountId(getContext(), fileName + userId, mClounType + "");
        if (dlsit != null && dlsit.size() > 0) {
            adapter.update((ArrayList<TopicalEntry>) dlsit, false);
            adapter.setInInit(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && !adapter.isInInit()) {
            dataRequester.start(mClounType, false, 1);
        }
        if (isRefresh) {
            isRefresh = false;
            dataRequester.start(mClounType, false, offset);
        }
    }

    private void initRegister() {
        if (mSubscription == null) {
            mSubscription = RxBus.getInstance().
                    toObserverable(Intent.class).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Intent>() {
                        @Override
                        public void call(Intent intent) {
                            String ation = intent.getAction();
                            int a = 0;
                            if (intent.getAction().equals(IntentUtil.ACTION_UPDTA_ATTEION_OK)
                                    || intent.getAction().equals(IntentUtil.UPDATE_FAVIRITY_MSG)
                                    ) {
//                                dataRequester.start(mClounType, false, offset);
//                                isRefresh = true;
                                if (getActivity() != null)
                                    resrshData();
                            }
                        }
                    });
        }

        mUserStatutasnscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_LOGIN_OUT)) {
                            if (getActivity() != null)
                                dataRequester.start(mClounType, false, 1);
                        } else if (intent.getAction().equals(IntentUtil.ACTION_LOGIN_OK)) {
                            if (getActivity() != null)
                                dataRequester.start(mClounType, false, 1);
                        } else if (intent.getAction().equals(IntentUtil.ACTION_COMNITY_COMNEND_OK)) {
                            if (getActivity() != null)
                                resrshData();
                        }
                    }
                });

        commendUpdateSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_COMNITY_COMNEND_OK)) {
                            resrshData();
                        }
                    }
                });

    }

    private void createFloatButton() {
        mtoTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= 8) {
//                    _refreshLayout.getRefreshRecycleView().smoothScrollToPosition(0);
                } else {
//                    pullListview.getRefreshableView().setSelection(0);
                }
            }
        });
        mtoTopBtn.setVisibility(View.INVISIBLE);
        leftbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                bottomOperView.setVisibility(View.VISIBLE);
                leftbtn.setVisibility(View.VISIBLE);
            }
        });

        cWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                WhiteTopBarActivity.startAct(getActivity(), CommunityFileFragment.class.getName(), "新建话题", "发布");
                if (!isLoginCheck.checkLogin()) return;
                if (!App.getInstance().checkIsVerity()) {
                    return;
                }
                Intent inten = new Intent();
                inten.putExtra("id", mClounType);
                inten.putExtra("type", 0);
                inten.putExtra("tags", (Serializable) totalTags);
                DefaultFragmentActivity.start(getActivity(), CommunityPubFileFragment.class.getName(), inten);
            }
        });

        cRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoginCheck.checkLogin()) return;
                if (!App.getInstance().checkIsVerity()) {
                    return;
                }
                openCamaer();
            }
        });

        cCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomOperView.setVisibility(View.GONE);
                leftbtn.setVisibility(View.VISIBLE);
            }
        });
    }

    // 设置打开录像的权限
    public void openCamaer() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent inten = new Intent();
                inten.putExtra("id", mClounType);
                inten.putExtra("type", 1);
                inten.putExtra("tags", (Serializable) totalTags);
                inten.putExtra("isCommunity", true);
//                DefaultFragmentActivity.start(getActivity(), CommonityRecordFragment.class.getName(), inten);
                DefaultFragmentActivity.start(getActivity(), AcvityCameraTabFragment.class.getName(), inten);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA)
                .check();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            adapter.saveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null)
            mSubscription.unsubscribe();
        if (mUserStatutasnscription != null)
            mUserStatutasnscription.unsubscribe();
        if (commendUpdateSubscription != null)
            commendUpdateSubscription.unsubscribe();
    }

    public void pullDown() {
        dataRequester.start(mClounType, false, 1);
    }

    public void pullUp() {
        offset++;
        dataRequester.start(mClounType, true, offset);
    }

    public void setAttentionUser(final ImageView attbtn, final long uId, final int role) {
        if (!_comnityHelper.getMloginCheck().checkLogin()) return;
//        int isAttion = mIGetPraistmp.isAttionUuser(uId, role);
        mTopicalApi.attentionAuthor(uId, role, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                if ((boolean) data) {
                    boolean flag = role == 0 ? true : false;
//                    mIGetPraistmp.updateAttionUuser(uId, role);
                    String msg = "关注成功";
                    if (!flag) {
                        msg = "取消关注";
                    }
                    ToastUtils.toastMsgFunction(act, msg);
//                    _comnityHelper.setAttteonStatus(flag, attbtn);
                    adapter.setItemAttion(uId, role == 0 ? 1 : 0);
//                    RXBusUtil.sendConcernChangeMessage(flag, 1);/
                    RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_DEF_OK));
                } else {
                    ToastUtils.toastMsgFunction(getActivity(), "关注失败");
                }
//                Observable.just((Boolean) data).
//                        subscribeOn(Schedulers.io()).
//                        observeOn(Schedulers.io()).
//                        map(new Func1<Boolean, Integer>() {
//                            @Override
//                            public Integer call(Boolean aBoolean) {
////                                int res = mTopicalApi.isAttentionOther(uId);
//                                return 0;
//                            }
//                        })
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<Integer>() {
//                                       @Override
//                                       public void onCompleted() {
////                                           RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
//                                           boolean flag = role == 0 ? true : false;
//                                           if (flag) {
//                                               ToastUtils.toastMsgFunction(act, "关注成功");
//                                           } else {
//                                               ToastUtils.toastMsgFunction(act, "取消关注");
//                                           }
//                                           RXBusUtil.sendConcernChangeMessage(flag, 1);
//                                       }
//
//                                       @Override
//                                       public void onError(Throwable e) {
//                                           e.printStackTrace();
//                                       }
//
//                                       @Override
//                                       public void onNext(Integer result) {
//                                           RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
////                                           int flag = role == 0 ? 1 : 0;
////                                           _comnityHelper.setAttteonMarkStatus(flag, attbtn);
////                                           dataRequester.start(mClounType, false, 1);
//                                       }
//                                   }
//                        );
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                ToastUtils.toastApiexceFunction(getActivity(), e);
            }
        });
    }

    @Override
    public void onLbtClick(int tyep, IButtonClickData data) {
        IButtonClickData<TopicalEntry> mydata = data;
        final TopicalEntry entry = mydata.getObject();
        switch (tyep) {
            case IButtonClickType.VISTI_CLICK:
//                long userId = entry.getId();
//                WhiteTopBarActivity.startAct(getActivity(), PraiseListFragment.class.getName(), "点赞列表", "", userId);
                _comnityHelper.gotoComunnityInfo(entry, true, false);
                break;
            case IButtonClickType.ATTION_CLICK:
                final ImageView attBtn = (ImageView) mydata.getTag();
                setAttentionUser(attBtn, entry.getAuthor_id(), entry.getRelationRole());
                break;
            case IButtonClickType.ITEM_CLICK:
                _comnityHelper.gotoComunnityInfo(entry);
                break;
            case IButtonClickType.PRAISE_CLICK:
                final TextView anmal = (TextView) mydata.getTag();
                if (entry.getAttitude() == 1) {
                    //已点赞
                    mTopicalApi.cancelPariseToptic(entry.getId(), new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            if (!(boolean) data) return;
                            if (anmal != null) {
                                anmal.setVisibility(View.VISIBLE);
                                anmal.startAnimation(animation);
                                anmal.setText("-1");
                            }
                            ToastUtils.toastMsgFunction(act, "取消点赞");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (anmal != null) anmal.setVisibility(View.GONE);
                                }
                            }, 50);
                            adapter.setItemPraise(entry.getId(), false);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            e.printStackTrace();
                            ToastUtils.toastApiexceFunction(getActivity(), e);
                        }
                    });
                } else {
                    _comnityHelper.praiseLbtCLick(entry.getId(), anmal, new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            if (!(boolean) data) return;
                            if (anmal != null) {
                                anmal.setVisibility(View.VISIBLE);
                                anmal.startAnimation(animation);
                                anmal.setText("+1");
                            }
                            ToastUtils.toastMsgFunction(act, "点赞成功");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (anmal != null) anmal.setVisibility(View.GONE);
                                }
                            }, 50);
                            adapter.setItemPraise(entry.getId(), true);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            e.printStackTrace();
                            LSUtils.toastMsgFunction(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()));
                        }
                    });
                }
                break;
            case IButtonClickType.COMMEND_CLICK:
                if (!isLoginCheck.checkLogin()) return;
                if (!App.getInstance().checkIsVerity()) {
                    return;
                }
                _comnityHelper.showCommendDialog(rootView, entry.getId(), -1, this);
                break;
            case IButtonClickType.SHARE_CLICK: {
                String url = App.getInstance().getCommuityShareUrl() + entry.getId();
                String thumb = "";
                if (entry.getAttachmentInfos() != null && entry.getAttachmentInfos().size() > 0)
                    thumb = entry.getAttachmentInfos().get(0).getThumbnail_url();
                String content = entry.getContent();
                if (!TextUtils.isEmpty(content)) {
                    if (content.length() > 28)
                        content = content.substring(0, 28);
                }
                ShareContent shareContent = _comnityHelper.ObtainShareContent(entry.getId(), content,
                        url, thumb, false);
                shareNewUiWnd(shareContent);
//                _comnityHelper.shareNewUiWnd(rootView, shareContent);
            }
            break;
            case IButtonClickType.QUERY_COMMEND: {
                _comnityHelper.gotoComunnityInfo(entry, true, true);
//                String url = App.getInstance().getCommuityShareUrl() + entry.getId();
            }
            break;
            case IButtonClickType.DEL_CLICK: {
                mTopicalApi.delTopticById(entry.getId(), new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        LSUtils.toastMsgFunction(getActivity(), "圈子删除成功");
                        dataRequester.start(mClounType, false, offset);
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        LSUtils.toastMsgFunction(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()));
                    }
                });
            }
            break;
            case IButtonClickType.FARVITY_CLICK: {
                final boolean isfal = entry.isFavl();
                final ImageView favotyBtn = (ImageView) mydata.getTag();
                _comnityHelper.addFavritory(entry.isFavl(), entry.getId(), new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        if ((boolean) data) {
                            boolean blag = isfal ? false : true;
//                            if (favotyBtn != null)
//                                _comnityHelper.setFavStatus(favotyBtn, blag, true);
//                            if (favotyBtn == null) return;
                            String msg = "收藏成功";
                            if (blag) {
                                favotyBtn.setImageResource(R.drawable.communtiy_item_fal_sel);
                            } else {
                                msg = "取消收藏";
                                favotyBtn.setImageResource(R.drawable.communtiy_item_falnoral);
                            }
                            adapter.setItemFavority(entry.getId(), blag);
//                            String msg = "收藏成功";
//                            if (blag) {
//                            } else {
//                                msg = "取消收藏";
//                            }
                            ToastUtils.toastMsgFunction(getActivity(), msg);
//                            dataRequester.start(mClounType, false, offset);
                            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_DEF_MSG));
                        } else {
                            ToastUtils.toastMsgFunction(getActivity(), "收藏失败");
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        ToastUtils.toastApiexceFunction(getActivity(), e);
                    }
                });
            }
            break;
        }
    }

    LiveServiceSharePopupwindow shareNewPopupwindow = null;

    public void shareNewUiWnd(ShareContent shareContent) {
        if (shareNewPopupwindow == null) {
            shareNewPopupwindow = new LiveServiceSharePopupwindow(getActivity(), shareContent);
            shareNewPopupwindow.setShareItemClickListener2(new LiveServiceSharePopupwindow.OnShareItemClickListener2() {
                @Override
                public void onShareItemClick(SharePlatform platform, ShareContent shareContent) {
                    if (platform == platform.QQ) {
                        onSharePlatfrom(SharePlatform.QQ, shareContent);
                    } else if (platform == platform.WeiBo) {
                        onSharePlatfrom(SharePlatform.WeiBo, shareContent);
                    } else if (platform == platform.Wechat) {
                        onSharePlatfrom(SharePlatform.Wechat, shareContent);
                    } else if (platform == platform.Wechat_FRIENDS) {
                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS, shareContent);
                    }
                }
            });
        } else
            shareNewPopupwindow.setShareContent(shareContent);
        shareNewPopupwindow.show(rootView);
    }

    public void onSharePlatfrom(SharePlatform platform, ShareContent content) {
        content.type = ShareContent.UrlType.WebPage;
        content.url = App.getInstance().getCommuityShareUrl() + content.getId();
        AbsShare share = ShareFactory.createShare(getActivity(), platform);
        share.share(content);
    }

    @Override
    public boolean onParams(final long id, long ref_id, final String context) {
        if (TextUtils.isEmpty(context)) {
            ToastUtils.toastNoContentCommendFunction(getContext());
            return false;
        }
        _comnityHelper.writeCommend(id, -1, context, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                long ids = (Long) data;
                if (ids != -1) {
                    LSUtils.toastMsgFunction(getActivity(), "评论发表成功");
                    adapter.setItemCommend(id, context);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                LSUtils.toastMsgFunction(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()));
            }
        });
        return true;
    }

    public void resrshData() {
        if (!(mselectTags == null || mselectTags.isEmpty())) {
            dataRequester.start(mClounType, false, 1);
        } else {
            dataRequester.start(mClounType, false, 1);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        pullDown();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        pullUp();
    }
}
