package com.dfsx.ganzcms.app.act;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.MessageData;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.business.LanguageUtil;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.ListViewAdapter;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.fragment.*;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.TabItem;
import com.dfsx.ganzcms.app.push.NotificationMessageStartManager;
import com.dfsx.ganzcms.app.view.LiveVideoPopupWindow;
import com.dfsx.lzcms.liveroom.fragment.BaseAndroidWebFragment;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.dfsx.videoijkplayer.util.NetworkUtil;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.loveplusplus.update.UpdateChecker;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by heynag on 2016/10/10.
 */
public class MainTabActivity extends AbsVideoScreenSwitchActivity implements ComuncityPostWordObserver.ICommPostObserInter {
    private static final String TAG = "MainTabActivity";
    private static final String TAG_NEWS_FRAG = "com.dfsx.lscms.app.act.MainTabActivity_news";
    private static final String TAG_TV_FRAG = "com.dfsx.lscms.app.act.MainTabActivity_TV";
    private static final String TAG_LIVE_FRAG = "ccom.dfsx.lscms.app.act.MainTabActivity_live";
    private static final String TAG_TOPIC_FRAG = "com.dfsx.lscms.app.act.MainTabActivity_topic";
    private static final String TAG_ME_FRAG = "com.dfsx.lscms.app.act.MainTabActivity_me";
    private Activity context;
    private RadioGroup tabBar;
    private Fragment newsFrag;
    private Fragment tvFrag;
    private Fragment topicFrag;
    private Fragment liveFrag;
    private Fragment meFrag;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private View portraintLayout;
    private FrameLayout fullScreenLayout;
    private View topLeftView;
    private View topRightView;
    private ImageView mMiddleAddBtn;
    private int currentShowId = -1;
    private Handler handler = new Handler();
    private TextView mTopUpStatus;
    private RadioButton _bottomTopLast;
    private TextView _bottomRedTip;
    protected SystemBarTintManager systemBarTintManager;
    public FrameLayout mFlFullVideo;
    private ShortVideoFragment mShortVideoFragment = null;
    private boolean isWifi;

    public FrameLayout getmFlFullVideo() {
        return mFlFullVideo;
    }

    public FrameLayout getActivityContainer() {
        return activityContainer;
    }

    protected FrameLayout activityContainer;
    private View rootView, topView;
    private LiveVideoPopupWindow popupWindow;
    SearchWnd dialogWnd = null;
    private int statusBarColor;
    private NewsDatailHelper newsDatailHelper;
    private Subscription loginSub;
    private boolean isPostAppOpen = false;
    //未读消息管理
    private PushMessageHelper.NoReadPushMessageChangeListener noReadPushMessageChangeListener;
    private Subscription loginOkSubscription;
    Drawable topSelectDrawable;
    Drawable topMorDrawable;
    View topbarLeftImage;

    private FileUploadProgress fileUploadProgress;


    private Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //当activity销毁重建的时候取消保存的fragments
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        super.onCreate(savedInstanceState);
        int type = NetworkUtil.getConnectivityStatus(this);
        isWifi = type != NetworkUtil.TYPE_WIFI ? false : true;
        UpdateChecker.checkForDialog(MainTabActivity.this, App.getInstance().getPotrtServerUrl() + "/public/apps");
        systemBarTintManager = Util.applyKitKatTranslucency(this,
                getStatusBarColor());
        rootView = getLayoutInflater().inflate(R.layout.act_main_tab, null);
        setContentView(rootView);
        LanguageUtil.switchLanguage(this, "zh_CN");
        //        setContentView(R.layout.act_main_tab);
        context = this;
        newsDatailHelper = new NewsDatailHelper(this);
        newsDatailHelper.clearWebDDataCache();
        initView();
        initAction();
        registerAction();
        initData();
        initDrawLyout();
        initPop();
        initUploadFileData();
        NotificationMessageStartManager.getInstance().setMainTabActivityIsLive(true);
        NotificationMessageStartManager.getInstance().startMessageAct(this);
        WebUrlStartManager.getInstance().startWebUrlAct(context);
        //注册广播接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentUtil.ACTION_UPFILE_PROGRESS_MSG);
        registerReceiver(mReceiver, filter);
        //上传打开APP的任务
        postAppOpenTask();

        isOpenAdLink();
    }


    public void isOpenAdLink() {
        if (PictureManager.getInstance().isIsshowAd()) {
            String link = PictureManager.getInstance().getAdPath();
            if (!TextUtils.isEmpty(link)) {
                PictureManager.getInstance().setIsshowAd(false);
                Bundle bundles = new Bundle();
                bundles.putString(BaseAndroidWebFragment.PARAMS_URL, link);
                //                WhiteTopBarActivity.startAct(context, NewsWebVoteFragment.class.getName(), "", R.drawable.cvideo_share,
                //                        bundles);
                WhiteTopBarActivity.startAct(context, NewsWebVoteFragment.class.getName(), "", "",
                        bundles);
            }
        }
    }

    private void initUploadFileData() {
        fileUploadProgress = new FileUploadProgress(context);
        RelativeLayout progressContainer = (RelativeLayout) findViewById(R.id.act_main_root_layout);
        fileUploadProgress.setProgressContainer(progressContainer);
        ComuncityPostWordObserver oberver = new ComuncityPostWordObserver(fileUploadProgress);
//        oberver.setCallBack(this);
        PublishDataManager.getInstance().setPostWordObserver(oberver);
        PublishDataManager.getInstance().setUploadPercentListener(fileUploadProgress);
//        }
    }

    public void onResult(boolean isComplate) {
        if (topicFrag != null)
            ((CommunitMulityFragment) topicFrag).clearPubtnStatus();
    }

    public void registerAction() {
        loginOkSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_LOGIN_OK)) {
                            PushMessageHelper.getInstance().getNoReadMessageInfo(context, false, null);
                        }
                    }
                });

        noReadPushMessageChangeListener = new PushMessageHelper.NoReadPushMessageChangeListener() {
            @Override
            public void onNoReadMessage(PushMessageHelper.NoReadPushMessage noReadPushMessageInfo) {
                boolean ishasNoReadMessage = noReadPushMessageInfo != null;
                if (ishasNoReadMessage) {
                    _bottomRedTip.setVisibility(View.VISIBLE);
                } else
                    _bottomRedTip.setVisibility(View.GONE);
            }
        };
        PushMessageHelper.getInstance().addNoReadPushMessageChangeListener(noReadPushMessageChangeListener);
    }

    /**
     * 上传打开APP的任务
     */
    private void postAppOpenTask() {
        if (!isPostAppOpen && App.getInstance().getUser() != null &&
                App.getInstance().getUser().getUser() != null) {
            TaskManager.uploadOpenAppTask(context);
            isPostAppOpen = true;
        }
    }

    private void initView() {
        topbarLeftImage = (View) findViewById(R.id.top_bar_left_images);
        topSelectDrawable = getResources().getDrawable(R.drawable.icon_me);
        topSelectDrawable.setBounds(0, 0, topSelectDrawable.getMinimumWidth(), topSelectDrawable.getMinimumHeight());
        topMorDrawable = getResources().getDrawable(R.drawable.icon_me_gray);
        topMorDrawable.setBounds(0, 0, topMorDrawable.getMinimumWidth(), topMorDrawable.getMinimumHeight());
        _bottomTopLast = (RadioButton) findViewById(R.id.bottom_tab_last);
        _bottomRedTip = (TextView) findViewById(R.id.tip_number_tx);
        topView = (View) findViewById(R.id.main_top_vew);
        tabBar = (RadioGroup) findViewById(R.id.tab_radio_group);
        portraintLayout = findViewById(R.id.portrait_layout);
        fullScreenLayout = (FrameLayout) findViewById(R.id.full_screen_layout);
        mMiddleAddBtn = (ImageView) findViewById(R.id.bottom_add_news);
        mFlFullVideo = (FrameLayout) findViewById(R.id.fl_full_video);
        mMiddleAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.show(view);
            }
        });
        mMiddleAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                popupWindow.show(view);
                tabBar.check(R.id.rb_trade);
                switchTab(R.id.bottom_add_news);

            }
        });
        initTopbar();
        _bottomTopLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLastRadSelected(true);
            }
        });
    }

    public int getStatusBarColor() {
        return this.getResources().getColor(R.color.transparent);
    }

    @Override
    public void setContentView(int layoutResID) {
        View activityView = getLayoutInflater().inflate(layoutResID, null);
        setContentView(activityView);
    }

    //    @Override
    //    public void setContentView(View view) {
    //        activityContainer = new FrameLayout(this);
    //        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
    //                ViewGroup.LayoutParams.MATCH_PARENT);
    //        activityContainer.setLayoutParams(params);
    //        activityContainer.setPadding(0, systemBarTintManager.getConfig().getStatusBarHeight(), 0, 0);
    //        activityContainer.addView(view);
    //        super.setContentView(activityContainer);
    //    }

    /**
     * 接收Service发送的进度数据
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新进度
            final int progress = intent.getIntExtra("progress", 0);
            final String msg = intent.getStringExtra("errmsg");
            //            systemBarTintManager.setStatusBarTintEnabled(true);
            //            mTopUpStatus.setVisibility(View.VISIBLE);
            if (progress == 100) {
                //                mTopUpStatus.setVisibility(View.GONE);
                //                systemBarTintManager.setStatusBarTintEnabled(false);
                RxBus.getInstance().post(new MessageData<MessageData.upFileDate>(IntentUtil.RX_UPFILE_PROGRESS_MSG,
                        new MessageData.upFileDate(progress, msg)));

                myhandler.postDelayed(new Runnable() {
                    public void run() {
                        //execute the task
                        PictureManager.getInstance().setUploadResult(true, "");
                        RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPLOAD_COMPLATE_OK));
                    }
                }, 1000);

                //                PictureManager.getInstance().setUploadResult(true, "");
                //                RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPLOAD_COMPLATE_OK));
            } else if (progress == 0) {
                //                systemBarTintManager.setStatusBarTintEnabled(false);
                //                mTopUpStatus.setText("发送失败");
                PictureManager.getInstance().setUploadResult(false, msg);
                Toast.makeText(MainTabActivity.this, "发送失败: " + msg, Toast.LENGTH_SHORT).show();
                RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPLOAD_COMPLATE_OK));
            } else if (progress == -1 || progress == -2) {
                // 发送中间失败
                PictureManager.getInstance().setUploadResult(false, msg);
                RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPLOAD_COMPLATE_OK));
            } else {
                myhandler.postDelayed(new Runnable() {
                    public void run() {
                        //execute the task
                        RxBus.getInstance().post(new MessageData<MessageData.upFileDate>(IntentUtil.RX_UPFILE_PROGRESS_MSG,
                                new MessageData.upFileDate(progress, msg)));
                    }
                }, 100);

                //                RxBus.getInstance().post(new MessageData<MessageData.upFileDate>(IntentUtil.RX_UPFILE_PROGRESS_MSG,
                //                        new MessageData.upFileDate(progress, msg)));
                //
            }
        }
    };

    private void initTopbar() {
        topbarLeftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageUtil.switchLanguage(context, "tbt");
                DefaultFragmentActivity.start(context, MulityColumnFragment.class.getName());
            }
        });
        mTopUpStatus = (TextView) findViewById(R.id.head_up_status);
        topRightView = findViewById(R.id.top_rigth_btn);
        //
        //        topLeftView.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                mDrawerLayout.openDrawer(Gravity.LEFT);
        //            }
        //        });
        topRightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_click));
                dialogWnd = new SearchWnd(MainTabActivity.this);
                dialogWnd.setListViewAdapter(new ListViewAdapter(context));
                dialogWnd.setOnSearchClickListener(new ContentSearchImpl());
                dialogWnd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (parent instanceof ListView) {
                            ListView searchListView = (ListView) parent;
                            int pos = position - searchListView.getHeaderViewsCount();
                            ListViewAdapter searchAdapter = (ListViewAdapter) dialogWnd.getListViewAdapter();
                            ContentCmsEntry entry = (ContentCmsEntry) searchAdapter.getItem(pos);
                            if (entry != null) {
                                newsDatailHelper.goDetail(entry);
                            }
                        }
                    }
                });
                dialogWnd.showDialog();
            }
        });
    }

    public void initPop() {
        popupWindow = new LiveVideoPopupWindow(context);
        popupWindow.setOnPopViewClickListener(new LiveVideoPopupWindow.
                OnPopViewClickListener() {
            @Override
            public void onStartLiveClick() {
                gotoLivRecord();
            }

            @Override
            public void onUploadVideoClick() {
                Intent intent = new Intent(MainTabActivity.this, VideoRecordAct.class);
                startActivity(intent);
            }

            @Override
            public void onStartYuGaoLiveClick() {
                ArrayList<String> titleList = new ArrayList<String>();
                titleList.add("预告直播");
                titleList.add("预告列表");
                WhiteTopBarSwitchActivity.start(context,
                        YuGaoFragment.class.getName(), titleList, 0, "");
            }

            @Override
            public void onServiceLiveClick() {
                WhiteTopBarActivity.startAct(context,
                        MyLiveServiceListFragment.class.getName(), "活动列表");
            }

            @Override
            public void onCancleClick() {

            }
        });

    }

    public void gotoLivRecord() {
        new TedPermission(MainTabActivity.this).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(MainTabActivity.this, PrepareLiveActivity.class);
                startActivity(intent);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(MainTabActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(MainTabActivity.this.getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA)
                .check();
    }


    public void initDrawLyout() {
    }

    public SystemBarTintManager getSystemBarTintManager() {
        return systemBarTintManager;
    }

    public void setLastRadSelected(boolean isSelect) {
        if (isSelect) {
            tabBar.check(R.id.bottom_tab_me);
            _bottomTopLast.setCompoundDrawablesWithIntrinsicBounds(null, topSelectDrawable, null, null);
            _bottomTopLast.setTextColor(0xff363636);
        } else {
            //            Drawable rightDrawable = getResources().getDrawable(R.drawable.icon_me_gray);
            //            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            //            _bottomTopLast.setCompoundDrawables(null, null, rightDrawable, null);
            _bottomTopLast.setCompoundDrawablesWithIntrinsicBounds(null, topMorDrawable, null, null);
            _bottomTopLast.setTextColor(0xff959595);
        }
    }

    private void initData() {
        PushMessageHelper.getInstance().getNoReadMessageInfo(context, false, null);   //胡群殴未读 城东消息

        tabBar.check(R.id.bottom_tab_news);
        loginSub = RxBus.getInstance().toObserverable(Intent.class)
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Intent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Intent intent) {
                        if (intent != null &&
                                TextUtils.equals(IntentUtil.ACTION_LOGIN_OK, intent.getAction())) {
                            postAppOpenTask();
                        }
                    }
                });
    }

    private void initAction() {
        tabBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switchTab(checkedId);
            }
        });
    }

    public void switchTab(int checkedId) {
        if (checkedId != R.id.bottom_tab_me) setLastRadSelected(false);
//        if (checkedId == mMiddleAddBtn.getId()) return;
        if (currentShowId == checkedId) return;
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        reSetFragment(manager);
        hideTab(transaction);
        currentShowId = checkedId;
        if (currentShowId != R.id.bottom_add_news && mShortVideoFragment != null) {
            mShortVideoFragment.stopPVideo();
            mShortVideoFragment.stopBannerScroll();
        }
        switch (checkedId) {
            case R.id.bottom_tab_news:
                if (newsFrag == null) {
                    newsFrag = ImportNewsFragment.newInstance();
                    transaction.add(R.id.container, newsFrag, TAG_NEWS_FRAG);
                } else {
                    transaction.show(newsFrag);
                }
                RxBus.getInstance().post(new TabItem(TabItem.BOTTOM_TAB_NEWS));
                break;
            case R.id.bottom_tab_tv:
                if (tvFrag == null) {
//                            tvFrag = new HeadScrollFragment();
                    tvFrag = new TvRadioFragment();
                    transaction.add(R.id.container, tvFrag, TAG_TV_FRAG);
                } else {
                    transaction.show(tvFrag);
                }
                RxBus.getInstance().post(new TabItem(TabItem.BOTTOM_TAB_TV));
                break;
            case R.id.bottom_tab_cmy:
                if (topicFrag == null) {
//                            topicFrag = new CommunityTabFragment();
//                            int count = App.getInstance().getmSession().getTopicCount();
//                            if (count > 3) {
                    topicFrag = new CommunitMulityFragment();
//                            } else {
//                                topicFrag = new CommunityTabFragment();
//                            }
                    transaction.add(R.id.container, topicFrag, TAG_TOPIC_FRAG);
                } else {
                    transaction.show(topicFrag);
                }
                RxBus.getInstance().post(new TabItem(TabItem.BOTTOM_TAB_TOPIC));
                break;
            case R.id.bottom_add_news:
                if (liveFrag == null) {
//                            liveFrag = new LiveTabFragment();
//                    liveFrag = new LiveWebLinkFragment();
                    liveFrag = new ShortVideoFragment();
                    mShortVideoFragment = (ShortVideoFragment) liveFrag;
                    transaction.add(R.id.container, liveFrag, TAG_LIVE_FRAG);
                } else {
                    transaction.show(liveFrag);
                    if (isWifi) {
                        mShortVideoFragment.playVideo();
                    }
                    mShortVideoFragment.startBannerScroll();
                }
                RxBus.getInstance().post(new TabItem(TabItem.BOTTOM_TAB_LIVE));
                break;
            case R.id.bottom_tab_me:
                if (meFrag == null) {
                    meFrag = new MyThridInfoFragment();
                    transaction.add(R.id.container, meFrag, TAG_ME_FRAG);
                } else {
                    transaction.show(meFrag);
                }
                RxBus.getInstance().post(new TabItem(TabItem.BOTTOM_TAB_ME));
                break;
        }
        transaction.commit();
        if (checkedId != R.id.bottom_tab_tv) {
            RxBus.getInstance().post(new Intent(IntentUtil.ACTION_SCROLL_ITEM_OK).putExtra("pos", 4));
        }

        if (checkedId == R.id.bottom_tab_news) {
            topbarLeftImage.setVisibility(View.VISIBLE);
        } else
            topbarLeftImage.setVisibility(View.GONE);
        //                if (checkedId == R.id.bottom_tab_live) {
        //                    RxBus.getInstance().post(true);
        //                } else {
        //                    RxBus.getInstance().post(false);
        //                    videoPlayer.release();
        //                }
    }

    public void changeStateBarColor(int color) {
        if (statusBarColor != color) {
            if (color == 0) {//设置为默认的颜色
                systemBarTintManager = Util.applyKitKatTranslucency(this,
                        getStatusBarColor());
            } else {
                systemBarTintManager = Util.applyKitKatTranslucency(this,
                        color);
            }
        }
        statusBarColor = color;
    }

    private void reSetFragment(FragmentManager manager) {
        newsFrag = manager.findFragmentByTag(TAG_NEWS_FRAG);
        liveFrag = manager.findFragmentByTag(TAG_LIVE_FRAG);
        tvFrag = manager.findFragmentByTag(TAG_TV_FRAG);
        topicFrag = manager.findFragmentByTag(TAG_TOPIC_FRAG);
        meFrag = manager.findFragmentByTag(TAG_ME_FRAG);
    }

    private void hideTab(FragmentTransaction transaction) {
        if (newsFrag != null) {
            transaction.hide(newsFrag);
        }
        if (tvFrag != null) {
            transaction.hide(tvFrag);
        }
        if (liveFrag != null) {
            transaction.hide(liveFrag);
        }
        if (topicFrag != null) {
            transaction.hide(topicFrag);
        }
        if (meFrag != null) {
            transaction.hide(meFrag);
        }
    }

    @Override
    public void addVideoPlayerToContainer(VideoAdwarePlayView videoPlayer) {
        if (systemBarTintManager != null) {
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setNavigationBarTintEnabled(true);
        }
        RxBus.getInstance().post(videoPlayer);
        portraintLayout.setVisibility(View.VISIBLE);
        fullScreenLayout.setVisibility(View.GONE);
    }

    @Override
    public void addVideoPlayerToFullScreenContainer(VideoAdwarePlayView videoPlayer) {
        if (systemBarTintManager != null) {
            systemBarTintManager.setStatusBarTintEnabled(false);
            systemBarTintManager.setNavigationBarTintEnabled(false);
        }
        //        portraintLayout.setVisibility(View.GONE);
        //        fullScreenLayout.setVisibility(View.VISIBLE);
        //        if (fullScreenLayout != null) {
        //            if (!(fullScreenLayout.getChildAt(0) instanceof VideoPlayView)) {
        //                fullScreenLayout.addView(videoPlayer, 0);
        //            }
        //        }
        super.addVideoPlayerToFullScreenContainer(videoPlayer);
    }

    public VideoAdwarePlayView getVideoPlayer() {
        return videoPlayer;
    }

    private int backPressedCount;

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (isShortVideoFragmetVideoFull()){
                mShortVideoFragment.onBackPressed();
                return;
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        //双击两次退出
        if (backPressedCount < 1) {
            backPressedCount++;
            Toast.makeText(context, "再按一次退出", Toast.LENGTH_SHORT).show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedCount = 0;
                }
            }, 5000);
            return;
        } else {
            backPressedCount = 0;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //        if (currentShowId != R.id.bottom_tab_live) {
        //            return;
        //        }
        if (isShortVideoFragmetVideoFull()){
            if (mShortVideoFragment != null) {
                mShortVideoFragment.onActivityConfigurationChanged(newConfig, mFlFullVideo);
            }
        }else {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (activityContainer != null && systemBarTintManager != null) {
                    activityContainer.setPadding(0, systemBarTintManager.getConfig().getStatusBarHeight(), 0, 0);
                }
            } else {
                if (activityContainer != null) {
                    activityContainer.setPadding(0, 0, 0, 0);
                }
            }
        }
    }
    @Override
    protected boolean isShortVideoFragmetVideoFull() {
        if (mShortVideoFragment != null) {
            return mShortVideoFragment.isPaly();
        }else {
            return false;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loginOkSubscription != null) {
            loginOkSubscription.unsubscribe();
        }
        if (noReadPushMessageChangeListener != null) {
            PushMessageHelper.getInstance().removeNoReadPushMessageChangeListener(noReadPushMessageChangeListener);
        }
        NotificationMessageStartManager.getInstance().setMainTabActivityIsLive(false);
        // 注销广播
        unregisterReceiver(mReceiver);
        if (loginSub != null) {
            loginSub.unsubscribe();
        }

        if (Util.isOnMainThread() && !this.isFinishing()) {
            Glide.with(this).pauseRequests();
        }
    }

}
