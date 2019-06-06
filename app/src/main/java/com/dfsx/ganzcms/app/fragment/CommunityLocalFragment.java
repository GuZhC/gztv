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
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.CommunityAdapter;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.model.Attachment;
import com.dfsx.ganzcms.app.model.ColumnEntry;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.util.Richtext;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.TabGrouplayout;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.dfsx.selectedmedia.MyVideoThumbLoader;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2015/10/10.  社区
 */
public class CommunityLocalFragment extends Fragment implements IButtonClickListenr, NewsDatailHelper.ICommendDialogLbtnlister {
    private static final String fileName = "CommunityLocalFragment";
    private PullToRefreshRecyclerView pullListview;
    private CommunityAdapter adapter;
    private ImageView leftbtn, cWriteBtn, cRecordBtn, cCancelBtn, mtoTopBtn;
    private RelativeLayout mLoadFailLayout;
    private android.view.animation.Animation animation;
    private TopicListManager dataRequester;
    Subscription mUserStatutasnscription = null;  //   用户退出消息监测
    private int offset = 1;
    private long mClounType = -1, userId = -1;
    TopicalApi mTopicalApi = null;
    boolean isRefresh = false;
    Subscription mSubscription = null;
    MyVideoThumbLoader myVideoThumbLoader;
    private int nScreenWidth, nScreenHeight;
    View rootView, bottomOperView, portraitLayout;
    CommuntyDatailHelper _comnityHelper = null;
    List<String> totalTags;
    private FrameLayout fullScreenLayout;
    Activity act;
    private IGetPraistmp mIGetPraistmp = null;

//    public Handler myHander = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message message) {
//            if (getActivity() != null) return false;
//            return false;
//        }
//    });

    public static CommunityLocalFragment newInstance(ColumnEntry entry) {
        Bundle bundle = new Bundle();
        bundle.putLong("type", entry.getId());
        bundle.putSerializable("tags", (Serializable) entry.getTags());
        CommunityLocalFragment fragment = new CommunityLocalFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CommunityLocalFragment newInstance(long cId, long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong("type", cId);
        bundle.putLong("uId", uid);
        CommunityLocalFragment fragment = new CommunityLocalFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        _comnityHelper=new CommuntyDatailHelper(getActivity());
//        mTopicalApi = _comnityHelper.getmTopicalApi();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.community, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        act = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mClounType = bundle.getLong("type");
            userId = bundle.getLong("uId");
            totalTags = (List<String>) bundle.getSerializable("tags");
        }
        _comnityHelper = new CommuntyDatailHelper(getContext());
        mTopicalApi = _comnityHelper.getmTopicalApi();
        mIGetPraistmp = _comnityHelper.getmIGetPraistmp();
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.add_score_anim);
        portraitLayout = (View) view.findViewById(R.id.port_layout);
        fullScreenLayout = (FrameLayout) view.findViewById(R.id.full_screen_layout);
        mLoadFailLayout = (RelativeLayout) view.findViewById(R.id.load_fail_layout);
        ImageButton ratry = (ImageButton) view.findViewById(R.id.reload_btn);
        ratry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setInInit(false);
                CommunityLocalFragment.this.onResume();
            }
        });
        nScreenWidth = UtilHelp.getScreenWidth(getActivity());
        nScreenHeight = UtilHelp.getScreenHeight(getActivity());
        //获取屏幕的宽度，每行3个Button，间隙为10共300，除4为每个控件的宽度
//        nItmeWidh = (nScreenWidth - 60) / 3;
        //创建数据的管理器
        dataRequester = new TopicListManager(getActivity(), userId + "", mClounType);
        myVideoThumbLoader = new MyVideoThumbLoader();
        dataRequester.setCallback(
                new DataRequest.DataCallback<ArrayList<TopicalEntry>>() {
                    @Override
                    public void onSuccess(final boolean isAppend, ArrayList<TopicalEntry> topicalies) {
                        if (pullListview != null)
                            pullListview.onRefreshComplete();
                        if (topicalies == null || topicalies.isEmpty()) {
                            return;
                        }
                        Observable.from(topicalies)
                                .subscribeOn(Schedulers.io())
                                .map(new Func1<TopicalEntry, TopicalEntry>() {
                                    @Override
                                    public TopicalEntry call(TopicalEntry topicalEntry) {
                                        TopicalEntry tag = mTopicalApi.getTopicTopicalInfo(topicalEntry);
                                        return tag;
                                    }
                                })
                                .toList()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<List<TopicalEntry>>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onNext(List<TopicalEntry> data) {
                                        if (data != null && !data.isEmpty()) {
                                            if (!isAppend)
                                                FileUtil.saveFileByAccountId(act, fileName, mClounType + "", data);
                                            adapter.update((ArrayList<TopicalEntry>) data, isAppend);
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        if (pullListview != null)
                            pullListview.onRefreshComplete();
                    }
                });
        pullListview = (PullToRefreshRecyclerView) view.findViewById(R.id.disclosure_scroll_layout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pullListview.getRefreshableView().setLayoutManager(layoutManager);

        pullListview.setMode(PullToRefreshBase.Mode.BOTH);
        adapter = new CommunityAdapter(getActivity());
//        pullListview.setOnRefreshListener(this);
        pullListview.getRefreshableView().setAdapter(adapter);
        adapter.set_item_back(this);
//        pullListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                _comnityHelper.goDetail();
//        });

        if (getParentFragment() != null && getParentFragment() instanceof PullToRefreshRecyclerView.PullRecyclerHelper)
            pullListview.setPullRecyclerHelper((PullToRefreshRecyclerView.PullRecyclerHelper) getParentFragment());

        if (savedInstanceState != null) {
            adapter.init(savedInstanceState);
        }
        pullListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pullDown();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pullUp();
            }
        });
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

    public void initData() {
        List<TopicalEntry> dlsit = (List<TopicalEntry>) FileUtil.getFileByAccountId(getContext(), fileName, mClounType + "");
        if (dlsit != null && dlsit.size() > 0)
            adapter.update((ArrayList<TopicalEntry>) dlsit, false);
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
                                isRefresh = true;
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
                        }
                    }
                });

    }

    private void createFloatButton() {
        mtoTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= 8) {
                    pullListview.getRefreshableView().smoothScrollToPosition(0);
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
                Intent inten = new Intent();
                inten.putExtra("id", mClounType);
                inten.putExtra("type", 0);
                inten.putExtra("tags", (Serializable) totalTags);
                DefaultFragmentActivity.start(getActivity(), CommunityFileFragment.class.getName(), inten);
            }
        });

        cRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                DefaultFragmentActivity.start(getActivity(), CommonityRecordFragment.class.getName(), inten);
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
    }

    public void pullDown() {
        dataRequester.start(mClounType, false, 1);
    }

    public void pullUp() {
        offset++;
        dataRequester.start(mClounType, true, offset);
    }

    @Override
    public void onLbtClick(int tyep, IButtonClickData data) {
        IButtonClickData<TopicalEntry> mydata = data;
        final TopicalEntry entry = mydata.getObject();
        final TextView anmal = (TextView) mydata.getTag();
        switch (tyep) {
            case IButtonClickType.VISTI_CLICK:
                long userId = entry.getAuthor_id();
                WhiteTopBarActivity.startAct(getActivity(), VisitRcordFragment.class.getName(), "到访记录", "", userId);
                break;
            case IButtonClickType.ATTION_CLICK:
                setAttentionUser(entry.getAuthor_id(), entry.getRelationRole());
                break;
            case IButtonClickType.ITEM_CLICK:
                _comnityHelper.gotoComunnityInfo(entry);
                break;
            case IButtonClickType.PRAISE_CLICK:
                if (entry.getAttitude() == 1) {
                    //cance
                    mTopicalApi.cancelPariseToptic(entry.getId(), new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            if (!(boolean) data) return;
                            if (anmal != null) {
                                anmal.setVisibility(View.VISIBLE);
                                anmal.startAnimation(animation);
                                anmal.setText("-1");
                            }
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (anmal != null) anmal.setVisibility(View.GONE);
                                    //选中
                                    dataRequester.start(mClounType, false, offset);
                                }
                            }, 50);
                            _comnityHelper.getmIGetPraistmp().updateValuse(entry.getId(), false, false, false);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            e.printStackTrace();
                            LSUtils.toastMsgFunction(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()));
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
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (anmal != null) anmal.setVisibility(View.GONE);
                                    //选中
                                    dataRequester.start(mClounType, false, offset);
                                }
                            }, 50);
                            _comnityHelper.getmIGetPraistmp().updateValuse(entry.getId(), true, false, false);
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
                _comnityHelper.showCommendDialog(rootView, entry.getId(),-1, this);
                break;
            case IButtonClickType.SHARE_CLICK: {
                String url = App.getInstance().getCommuityShareUrl() + entry.getId();
                String thumb = "";
                if (entry.getAttachmentInfos() != null && entry.getAttachmentInfos().size() > 0)
                    thumb = entry.getAttachmentInfos().get(0).getThumbnail_url();
                ShareContent shareContent = _comnityHelper.ObtainShareContent(entry.getId(), entry.getContent(),
                        url, thumb, false);
                _comnityHelper.shareNewUiWnd(rootView, shareContent);
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
                _comnityHelper.addFavritory(entry.isFavl(), entry.getId(), new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        if ((boolean) data) {
                            String msg = "收藏成功";
                            if (isfal)
                                msg = "取消收藏";
                            LSUtils.toastMsgFunction(getActivity(), msg);
                            dataRequester.start(mClounType, false, offset);
                            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_MSG));
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        LSUtils.toastMsgFunction(getActivity(), JsonCreater.getErrorMsgFromApi(e.toString()));
                    }
                });
            }
            break;
        }
    }

    @Override
    public boolean onParams(long id,long ref_id, String context) {
        _comnityHelper.writeCommend(id, -1, context, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                long id = (Long) data;
                if (id != -1) {
                    LSUtils.toastMsgFunction(getActivity(), "评论发表成功");
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

    class ViewHolder {
        public TopicalEntry item;
        public int pos;
        public CircleButton userImg;
        public TextView usename;
        public ImageView userLevel;
        public Richtext title;
        public TextView time;
        public TextView commenNumber;
        public TextView readdNumber;
        public TextView cloumnTx;

        //hey  2017-7-3
        public ImageView commendPubBtn;
        public ImageView shareBtn;
        public ImageView favirtyBtn;

        //2017-7/24
        public ImageView visitBtn;
        public TabGrouplayout tabGroup;
        public LinearLayout userGroup;
        public TextView vistNumberTx;
    }

    class ImageViewHolder extends ViewHolder {
        public LinearLayout imgs;
        public ImageView realtRoleTx;

        public ImageView praiseBtn;
        public TextView animalBtn;
    }

    public class ListAdapter extends BaseAdapter {
        private final String STATE_LIST = "ListqqDisclsureAreaAdapter.mlist";
        private ArrayList<TopicalEntry> items = new ArrayList<TopicalEntry>();
        private LayoutInflater inflater;
        public boolean bInit;

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            View imgeView = null;
            {
                ImageViewHolder viewHolder = null;
                if (view == null) {
                    imgeView = inflater.inflate(R.layout.disclosure_item, parent, false);
                    viewHolder = new ImageViewHolder();
                    viewHolder.usename = (TextView) imgeView.findViewById(R.id.replay_user_name);
                    viewHolder.userImg = (CircleButton) imgeView.findViewById(R.id.head_img);
                    viewHolder.userLevel = (ImageView) imgeView.findViewById(R.id.cmy_user_level);
                    viewHolder.title = (Richtext) imgeView.findViewById(R.id.disclosure_list_title);
                    viewHolder.realtRoleTx = (ImageView) imgeView.findViewById(R.id.common_guanzhu_tx);
                    viewHolder.praiseBtn = (ImageView) imgeView.findViewById(R.id.comnity_item_praise);
                    viewHolder.animalBtn = (TextView) imgeView.findViewById(R.id.comnity_item_praise_animal);
                    viewHolder.time = (TextView) imgeView.findViewById(R.id.common_time);
                    viewHolder.imgs = (LinearLayout) imgeView.findViewById(R.id.disclosure_list_iamgelayout);
                    viewHolder.cloumnTx = (TextView) imgeView.findViewById(R.id.disclosure_column_tx);
                    viewHolder.commenNumber = (TextView) imgeView.findViewById(R.id.disclosure_iamg_comend);
                    viewHolder.tabGroup = (TabGrouplayout) imgeView.findViewById(R.id.communt_img_taglay);
                    viewHolder.userGroup = (LinearLayout) imgeView.findViewById(R.id.userGrouplay);
                    viewHolder.vistNumberTx = (TextView) imgeView.findViewById(R.id.community_vist_number);
                    viewHolder.shareBtn = (ImageView) imgeView.findViewById(R.id.comnity_item_share);
                    viewHolder.favirtyBtn = (ImageView) imgeView.findViewById(R.id.comnity_item_favority);
                    viewHolder.visitBtn = (ImageView) imgeView.findViewById(R.id.cumonuity_vist_bnt);
                    viewHolder.vistNumberTx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            long id = (long) view.getTag(R.id.cirbutton_user_id);
                            WhiteTopBarActivity.startAct(getActivity(), VisitRcordFragment.class.getName(), "到访记录", "", id);
                        }
                    });
                    viewHolder.commendPubBtn = (ImageView) imgeView.findViewById(R.id.comnity_item_commend);
                    viewHolder.commendPubBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageViewHolder holder = (ImageViewHolder) view.getTag();
                            if (holder == null) return;
//                            showCommendWnd(view, holder.item.getId());
                        }
                    });
                    viewHolder.shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageViewHolder holder = (ImageViewHolder) view.getTag();
                            if (holder == null) return;
                            String thumb = "";
                            if (holder.item.getAttachmentInfos() != null && holder.item.getAttachmentInfos().size() > 0)
                                thumb = holder.item.getAttachmentInfos().get(0).getUrl();
//                            shareNewUiWnd(holder.item.getId(), holder.item.getContent(), thumb);
                        }
                    });
                    viewHolder.praiseBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageViewHolder holder = (ImageViewHolder) view.getTag();
                            if (holder == null) return;
//                            pariseButton(holder.item.getId(), holder.animalBtn);
                        }
                    });
                    viewHolder.favirtyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            ImageViewHolder holder = (ImageViewHolder) view.getTag();
                            if (holder == null) return;
                            if (!_comnityHelper.getMloginCheck().checkLogin()) return;
                            final boolean bflag = holder.item.isFavl();
                            _comnityHelper.addFavritory(!bflag, holder.item.getId(), new DataRequest.DataCallback() {
                                @Override
                                public void onSuccess(boolean isAppend, Object data) {
                                    if ((boolean) data) {
                                        _comnityHelper.setFavStatus((ImageView) view, !bflag, true);
                                        dataRequester.start(mClounType, false, 1);
                                        RxBus.getInstance().post(IntentUtil.UPDATE_FAVIRITY_MSG);
                                    }
                                }

                                @Override
                                public void onFail(ApiException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });
                    viewHolder.item = items.get(position);
                    imgeView.setTag(viewHolder);
                    viewHolder.realtRoleTx.setTag(viewHolder);
                    viewHolder.commendPubBtn.setTag(viewHolder);
                    viewHolder.shareBtn.setTag(viewHolder);
                    viewHolder.favirtyBtn.setTag(viewHolder);
                    viewHolder.praiseBtn.setTag(viewHolder);
                    viewHolder.imgs.setTag(viewHolder);
                    view = imgeView;
                } else {
                    viewHolder = (ImageViewHolder) view.getTag();
                }
                Account user = App.getInstance().getUser();
                viewHolder.item = items.get(position);
                viewHolder.pos = position;
                UtilHelp.LoadImageErrorUrl(viewHolder.userImg, viewHolder.item.getAuthor_avatar_url(), null, R.drawable.icon_defalut_no_login_logo);
                viewHolder.userImg.setTag(R.id.cirbutton_user_id, viewHolder.item.getAuthor_id());
                UserLevelManager.getInstance().showLevelImage(getContext(), viewHolder.item.getUser_level_id(), viewHolder.userLevel);
                viewHolder.usename.setText(viewHolder.item.getAuthor_nickname());
                if (viewHolder.item.getContent() != null)
                    viewHolder.title.setText(viewHolder.item.getContent());
                viewHolder.time.setText(UtilHelp.getTimeFormatText("HH:mm yyyy/MM/dd", viewHolder.item.getPost_time() * 1000));
                int role = viewHolder.item.getRelationRole();
                _comnityHelper.setAttteonTextStatus(role, viewHolder.realtRoleTx);
                viewHolder.realtRoleTx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
                        ImageViewHolder holder = (ImageViewHolder) view.getTag();
//                        setAttentionUser(holder.item.getAuthor_id(), holder.item.getRelationRole(), holder.realtRoleTx);
                    }
                });
                _comnityHelper.setFavStatus(viewHolder.favirtyBtn, viewHolder.item.isFavl(), false);
                viewHolder.cloumnTx.setText(viewHolder.item.getColumn_name());
                viewHolder.commenNumber.setText(viewHolder.item.getReply_count() + "");
//                viewHolder.purseNumber.setText(viewHolder.item.purerNum + "");
                viewHolder.readdNumber.setText(viewHolder.item.getView_count() + "");
                int nCount = viewHolder.imgs.getChildCount();
                List<Attachment> dlist = viewHolder.item.getAttachmentInfos();
                if (dlist != null && !dlist.isEmpty()) {
                    viewHolder.imgs.removeAllViews();
                    viewHolder.imgs.setVisibility(View.VISIBLE);
                    String urls = "";
                    if (dlist != null && !dlist.isEmpty()) {
                        for (int i = 0; i < dlist.size(); i++) {
                            urls += dlist.get(i).getUrl() + "?w=" + nScreenWidth + "&h=" + nScreenHeight + "&s<=1";
                            if (i < dlist.size() - 1)
                                urls += ",";
                        }
                        if (viewHolder.item.getType() == 1) {
                            _comnityHelper.setMulitpImage(viewHolder.imgs, dlist, urls);
                        } else {
                            _comnityHelper.createVideoContainer(viewHolder.imgs, dlist.get(0));
                        }
                    }
                } else {
                    viewHolder.imgs.removeAllViews();
                    viewHolder.imgs.setVisibility(View.GONE);
                }
                List<TopicalEntry.PraiseBean> visit = viewHolder.item.getPraiseBeanList();
                if (visit != null) {
                    viewHolder.vistNumberTx.setTag(R.id.cirbutton_user_id, viewHolder.item.getId());
                    viewHolder.vistNumberTx.setText(visit.size() + "");
                } else {
                    View vparent = (View) viewHolder.vistNumberTx.getParent();
                    vparent.setVisibility(View.GONE);
                }
                _comnityHelper.initTabGroupLayout(viewHolder.tabGroup, viewHolder.item.getTags());
                _comnityHelper.initUserGroupLayout(viewHolder.userGroup, visit);
            }
            return view;
        }
    }

    public void setAttentionUser(final long uId, final int role) {
        if (!_comnityHelper.getMloginCheck().checkLogin()) return;
        mTopicalApi.attentionAuthor(uId, role, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                Observable.just((Boolean) data).
                        subscribeOn(Schedulers.io()).
                        observeOn(Schedulers.io()).
                        map(new Func1<Boolean, Integer>() {
                            @Override
                            public Integer call(Boolean aBoolean) {
//                                int res = mTopicalApi.isAttentionOther(uId);
                                return 0;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Integer>() {
                                       @Override
                                       public void onCompleted() {
                                           RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
                                           boolean flag = role == 0 ? true : false;
                                           RXBusUtil.sendConcernChangeMessage(flag, 1);
                                       }

                                       @Override
                                       public void onError(Throwable e) {
                                           e.printStackTrace();
                                       }

                                       @Override
                                       public void onNext(Integer result) {
//                                           setAttteonTextStatus(result, view);
                                           dataRequester.start(mClounType, false, 1);
                                       }
                                   }
                        );
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

}
