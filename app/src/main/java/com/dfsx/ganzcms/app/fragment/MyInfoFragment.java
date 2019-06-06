package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.*;
import com.dfsx.ganzcms.app.adapter.MyInfoListAdapter;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.model.DayTask;
import com.dfsx.ganzcms.app.model.ITabUserView;
import com.dfsx.ganzcms.app.model.UserLivePlatformInfo;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.view.LiveVideoPopupWindow;
import com.dfsx.ganzcms.app.view.QianDaoPopwindow;
import com.dfsx.logonproject.act.WelActivity;
import com.dfsx.logonproject.dzfragment.PersonInforFragment2;
import com.dfsx.logonproject.fragment.PersonInforFragment;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.model.Level;
import com.dfsx.lzcms.liveroom.util.ConcernChanegeInfo;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.ecloud.pulltozoomview.PullToZoomListViewEx;
import com.fivehundredpx.android.blur.BlurringView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/6/16.
 * 版本二替换之前的MyFragment
 */
public class MyInfoFragment extends Fragment {

    private Activity context;
    private MyInfoListAdapter adapter;
    private FrameLayout loginViewContainer;
    private PullToZoomListViewEx zoomListView;
    private ListView listView;
    private View noLoginView;
    private View loginedView;
    private ImageView bkgImageView;
    private BlurringView bkgBlurringView;
    private CircleButton userLogoView;
    private View userPersonInfoView;//我的作品
    private View userRenzhengView;
    private View userSettingsView;
    private TextView userNameView;
    private TextView userIdView;
    private CheckedTextView userQiandaoView;
    private View loginedInfoView;
    private View shopView;
    /**
     * 达人榜
     */
    private View talentPersonTopList;
    private View startLiveView;

    private View storeView;
    private View concernView;
    private View fansView;

    private TextView storeNumView;
    private TextView concernNumView;
    private TextView fansNumView;

    private Subscription loginOkSubscription;
    private Subscription favritySubscription;
    private Subscription concernSubscription;
    private PushMessageHelper.NoReadPushMessageChangeListener noReadPushMessageChangeListener;
    private boolean isLogin;

    private MyDataManager dataManager;
    private UserEditWordPermissionHelper editWordPermissionHelper;

    private ArrayList<ITabUserView> tabList;
    private Handler handler = new Handler();

    private IGradeResource gradeResourceHelper;

    private TabLabel messageLabel;
    private TabLabel taskLabel;
    private TabLabel gradeLabel;
    private TabLabel editWordLabel;

    private LiveVideoPopupWindow livePopwidow;

    /**
     * 是否显示开始直播的的控件
     */
    private boolean isShowStartLiveBtn;

    /**
     * 是否显示投稿的控件
     */
    private boolean isShowEditWordBtn;

    private ArrayList<Long> editWordColumnIdList;

    private FileUploadProgress fileUploadProgress;

    private QianDaoPopwindow qianDaoPopwindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        return inflater.inflate(R.layout.frag_my_info_layout, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataManager = new MyDataManager(getActivity());
        editWordPermissionHelper = new UserEditWordPermissionHelper(context);
        gradeResourceHelper = new GradeResourceImpl();
        initView(view);
        initLoginTopView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setListAdapter(zoomListView.getPullRootView());
        initAction();
        initRegister();
        initUploadFileData();
        setLoginView();
        updateUserInfo();
    }

    private void initView(View v) {
        zoomListView = (PullToZoomListViewEx) v.findViewById(R.id.pull_to_zoom_view);
        listView = zoomListView.getPullRootView();
    }

    private void initAction() {
        userRenzhengView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteTopBarActivity.startAct(context, RenZhengFragment.class.getName(), "实名认证");
            }
        });
        userLogoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.gotoPersonHomeAct(getActivity(), App.getInstance().getUser().getUser().getId());
            }
        });

        noLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WelActivity.class);
                startActivity(intent);
                //                IntentUtil.goToLogin(getActivity());
            }
        });

        userPersonInfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                WhiteTopBarActivity.startAct(context, UserLiveFragment.class.getName(), "我的直播");
                IntentUtil.gotoPersonHomeAct(getActivity(), App.getInstance().getUser().getUser().getId());
            }
        });

        storeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                WhiteTopBarActivity.startAct(getActivity(), MyEnshrineFragment.class.getName(), "收藏","清除全部");
                WhiteTopBarActivity.startAct(getActivity(), MyfavorityFragment.class.getName(), "收藏");
            }
        });
        concernView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteTopBarActivity.startAct(getActivity(), MyAttentionFragment.class.getName(), "我关注的");
            }
        });

        fansView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteTopBarActivity.startAct(getActivity(), MyFansFragment.class.getName(), "我的粉丝");
            }
        });

        userSettingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    DefaultFragmentActivity.start(getActivity(), PersonInforFragment2.class.getName());
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "你还没有登录", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        shopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LSUtils.toastNoFunction(context);
                //                                                WhiteTopBarActivity.startAct(context,
                //                                                        CreditShopFragment.class.getName(), "积分商城", "兑换记录");
            }
        });

        talentPersonTopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //达人排行榜调转
                //                Intent intent = new Intent(getActivity(), TalentPersonAct.class);
                //                getActivity().startActivity(intent);

                WhiteTopBarActivity.startAct(context,
                        TalentPersonFragment.class.getName(), "达人榜", "");
            }
        });

        startLiveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                if (livePopwidow == null) {
                //                    initLivePop();
                //                }
                //                livePopwidow.show(v);
                gotoLivRecord();
            }
        });
        userQiandaoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isQiandao = userQiandaoView.isChecked();
                if (!isQiandao) {
                    showQiandaoWindow(v);
                    //                    userQiandaoView.setChecked(!isQiandao);
                    //                    qianDao();
                } else {
                    //                    WhiteTopBarActivity.startAct(context,
                    //                            QianDaoFragment.class.getName(), "每日签到");
                }
            }
        });
    }

    private void initLivePop() {
        livePopwidow = new LiveVideoPopupWindow(context);
        livePopwidow.setOnPopViewClickListener(new LiveVideoPopupWindow.
                OnPopViewClickListener() {
            @Override
            public void onStartLiveClick() {
                gotoLivRecord();
            }

            @Override
            public void onUploadVideoClick() {
                Intent intent = new Intent(context, VideoRecordAct.class);
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
        new TedPermission(context).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(context, PrepareLiveActivity.class);
                startActivity(intent);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(context, "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(context.getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA)
                .check();
    }

    private void qianDao() {
        dataManager.qianDao(new DataRequest.DataCallback<Boolean>() {
            @Override
            public void onSuccess(boolean isAppend, Boolean data) {
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                Toast.makeText(context, "签到失败", Toast.LENGTH_SHORT).show();
                userQiandaoView.setChecked(false);
            }
        });
    }


    private void showQiandaoWindow(View v) {
        if (qianDaoPopwindow == null) {
            qianDaoPopwindow = new QianDaoPopwindow(getActivity());
        }
        qianDaoPopwindow.show(v);
    }

    private void initRegister() {
        noReadPushMessageChangeListener = new PushMessageHelper.NoReadPushMessageChangeListener() {
            @Override
            public void onNoReadMessage(PushMessageHelper.NoReadPushMessage noReadPushMessageInfo) {
                if (messageLabel != null && adapter != null) {
                    boolean ishasNoReadMessage = noReadPushMessageInfo != null;
                    messageLabel.setShowPoint(ishasNoReadMessage);
                    if (ishasNoReadMessage) {
                        messageLabel.setContentText("您有" + noReadPushMessageInfo.allCount + "条未读消息");
                    } else {
                        messageLabel.setContentText("");
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        };
        PushMessageHelper.getInstance().addNoReadPushMessageChangeListener(noReadPushMessageChangeListener);
        loginOkSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_LOGIN_OK)) {
                            setLoginView();
                            updateUserInfo();
                            PushMessageHelper.getInstance().getNoReadMessageInfo(context, true, null);
                        } else if (TextUtils.equals(intent.getAction(), IntentUtil.ACTION_QIAN_DAO_SUCCESS)) {
                            userQiandaoView.setChecked(true);
                            updateUserInfo();
                        }
                    }
                });

        if (favritySubscription == null) {
            favritySubscription = RxBus.getInstance().
                    toObserverable(Intent.class).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Intent>() {
                        @Override
                        public void call(Intent intent) {
                            if (intent.getAction().equals(IntentUtil.UPDATE_FAVIRITY_MSG)
                                    || intent.getAction().equals(IntentUtil.UPDATE_FAVIRITY_DEF_MSG)) {
                                setMyStoreTextNum();
                            } else if (intent.getAction().equals(IntentUtil.ACTION_UPDTA_ATTEION_OK)
                                    || intent.getAction().equals(IntentUtil.ACTION_UPDTA_ATTEION_DEF_OK)) {
                                updateUserInfo();
                            }
                        }
                    });
        }
        concernSubscription = RxBus.getInstance()
                .toObserverable(ConcernChanegeInfo.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ConcernChanegeInfo>() {
                    @Override
                    public void call(ConcernChanegeInfo chanegeInfo) {
                        Account account = App.getInstance().getUser();
                        if (chanegeInfo == null || account == null || account.getUser() == null) {
                            return;
                        }

                        long num = account.getUser().getFollow_count();
                        num = num + (chanegeInfo.isAdd() ? 1 : -1) * chanegeInfo.getChangeNum();
                        if (num < 0) {
                            num = 0;
                        }
                        account.getUser().setFollow_count(num);
                        setUserConcernDataView(num);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initLoginTopView(View v) {
        zoomListView.setZoomView(LayoutInflater.from(context)
                .inflate(R.layout.frag_my_info_header, null));
        loginViewContainer = (FrameLayout) v.findViewById(R.id.login_view_container);

        noLoginView = LayoutInflater.from(context)
                .inflate(R.layout.no_login_layout_2, null);
        loginedView = LayoutInflater.from(context)
                .inflate(R.layout.my_logined_layout, null);

        loginedInfoView = loginedView.findViewById(R.id.logined_info_view);
        startLiveView = loginedView.findViewById(R.id.user_start_live);
        shopView = loginedView.findViewById(R.id.shop_view);
        talentPersonTopList = loginedView.findViewById(R.id.talent_top_person_view);
        bkgImageView = (ImageView) loginedView.findViewById(R.id.bkg_image_view);
        bkgBlurringView = (BlurringView) loginedView.findViewById(R.id.background_img_blur);
        bkgBlurringView.setBlurredView(bkgImageView);
        userLogoView = (CircleButton) loginedView.findViewById(R.id.user_logo_image_view);
        userPersonInfoView = loginedView.findViewById(R.id.user_person_info_image);
        userRenzhengView = loginedView.findViewById(R.id.user_renzheng_image);
        userSettingsView = loginedView.findViewById(R.id.user_settings_image);
        userNameView = (TextView) loginedView.findViewById(R.id.user_name_text);
        userIdView = (TextView) loginedView.findViewById(R.id.user_id_text);
        userQiandaoView = (CheckedTextView) loginedView.findViewById(R.id.user_qiandao_text);
        storeView = loginedView.findViewById(R.id.user_store_view);
        concernView = loginedView.findViewById(R.id.user_concern_view);
        fansView = loginedView.findViewById(R.id.user_fans_view);
        storeNumView = (TextView) loginedView.findViewById(R.id.user_store_num_text);
        concernNumView = (TextView) loginedView.findViewById(R.id.user_concern_num_text);
        fansNumView = (TextView) loginedView.findViewById(R.id.user_fans_num_text);
    }

    private void changeStatusBarColor(boolean isLogin) {
        if (context instanceof MainTabActivity) {
            ((MainTabActivity) context).changeStateBarColor(isLogin ?
                    getResources().getColor(R.color.black) : 0);
        }
    }

    /**
     * 为快速投稿的精度条做显示
     */
    private void initUploadFileData() {
        fileUploadProgress = new FileUploadProgress(context);
        if (getActivity() instanceof MainTabActivity) {
            MainTabActivity mainTabActivity = (MainTabActivity) getActivity();

            RelativeLayout progressContainer = (RelativeLayout) mainTabActivity.
                    findViewById(R.id.act_main_root_layout);
            fileUploadProgress.setProgressContainer(progressContainer);
            PostWordManager.getInstance().setPostWordObserver(new DefaultPostWordObserver(fileUploadProgress));
            PostWordManager.getInstance().setUploadPercentListener(fileUploadProgress);
        }
    }

    private void updateUserInfo() {
        UserInfoHelper.updateCurrentUserInfo(context, new DataRequest.DataCallback<Account>() {
            @Override
            public void onSuccess(boolean isAppend, Account data) {
                if (data != null && data.getUser() != null) {
                    setUserBaseInfoShow(data.getUser());
                    setUserGrade(data.getUser());
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
        updateTaskInfo();
    }

    private void setLoginView() {
        loginViewContainer.removeAllViews();
        isLogin = App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null;
        if (!isLogin) {
            loginViewContainer.addView(noLoginView);
            //没有登录也清除登录的信息
            isShowEditWordBtn = false;
            isShowStartLiveBtn = false;
            setEditWordLabel();
            if (gradeLabel != null) {
                gradeLabel.setContentImagePath("");
                adapter.notifyDataSetChanged();
            }
        } else {
            Account.UserBean user = App.getInstance().getUser().getUser();
            loginViewContainer.addView(loginedView);
            setUserBaseInfoShow(user);

            getStoreNum(user);
            setUserGrade(user);
            setQiandaoInfo();
            setStartLiveViewVisible();
            setLivePlatformInfo();
            setCMSPlatformInfo();
        }
        setHeaderViewSize(isLogin);
    }

    /**
     * 显示用户基础信息
     *
     * @param user
     */
    private void setUserBaseInfoShow(Account.UserBean user) {
        String logoUrl = user.getAvatar_url();
        setLoginedLogoImage(logoUrl);

        userNameView.setText(user.getNickname());
        userIdView.setText("ID: " + user.getId());
        setUserConcernDataView(user.getFollow_count());
        fansNumView.setText(StringUtil.getNumKString(user.getFan_count()));
        storeNumView.setText(StringUtil.getNumKString(user.getFavorite_count()));
    }

    private void setUserConcernDataView(long num) {
        concernNumView.setText(StringUtil.getNumKString(num));
    }

    private void getStoreNum(final Account.UserBean user) {
        dataManager.getMyStoreNum(new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                storeNumView.setText(StringUtil.getNumKString(data));
                if (user != null) {
                    user.setStore_count(data);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

    private void setStartLiveViewVisible() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                startLiveView.setVisibility(isShowStartLiveBtn ?
                        View.VISIBLE : View.GONE);
            }
        });
    }

    private void setEditWordLabel() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isShowEditWordBtn) {
                    if (editWordLabel == null) {
                        editWordLabel = new TabLabel("快速投稿", R.drawable.icon_start_edit_content);
                        adapter.getData().add(0, editWordLabel);
                        adapter.notifyDataSetChanged();
                    } else {
                        String text = adapter.getData().get(0).getTabViewText();
                        if (!TextUtils.equals("快速投稿", text)) {
                            adapter.getData().add(0, editWordLabel);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    if (editWordLabel != null) {
                        boolean is = adapter.getData().remove(editWordLabel);
                        if (is) {
                            adapter.notifyDataSetChanged();
                        }
                        editWordLabel = null;
                    }
                }
            }
        });

    }

    private void setCMSPlatformInfo() {
        editWordPermissionHelper.isEditWordPermission(new UserEditWordPermissionHelper.EditPermissionCallBack() {
            @Override
            public void callBack(boolean isPermission, ArrayList<Long> permissionColumnIds) {
                isShowEditWordBtn = isPermission;
                editWordColumnIdList = permissionColumnIds;
                setEditWordLabel();
            }
        });
    }

    private void setLivePlatformInfo() {
        dataManager.getMyLivePlatformInfo(new DataRequest.DataCallback<UserLivePlatformInfo>() {
            @Override
            public void onSuccess(boolean isAppend, UserLivePlatformInfo data) {
                isShowStartLiveBtn = data != null ? data.isCreateLivePermission()
                        : false;
                setStartLiveViewVisible();
                setHeaderViewSize(isLogin);
            }

            @Override
            public void onFail(ApiException e) {
                Log.e("TAG", "live platform info error == " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void setQiandaoInfo() {
        dataManager.isQianDao(new DataRequest.DataCallback<Boolean>() {
            @Override
            public void onSuccess(boolean isAppend, Boolean data) {
                userQiandaoView.setChecked(data);
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                userQiandaoView.setChecked(false);
            }
        });
    }

    private void setHeaderViewSize(boolean isLogin) {
        handler.post(new AbsRunnable<Boolean>(isLogin) {
            @Override
            public void runData(Boolean isLogin) {
                int width = context.getWindowManager().getDefaultDisplay().getWidth();
                int height = 0;
                if (isLogin) {
                    int liveHeight = isShowStartLiveBtn ?
                            PixelUtil.dp2px(context, 60)
                            : 0;
                    int statusBarHeight = getStatusBarHeight();

                    height = PixelUtil.dp2px(context, 185) + statusBarHeight +
                            PixelUtil.dp2px(context, 60) + PixelUtil.dp2px(context, 65)
                            + PixelUtil.dp2px(context, 8) * 2 + 2 + liveHeight;
                } else {
                    height = PixelUtil.dp2px(context, 265);
                }
                zoomListView.setHeaderViewSize(width, height);
            }
        });
    }

    private int getStatusBarHeight() {
        int height = 0;
        if (getActivity() != null && getActivity() instanceof MainTabActivity) {
            height = ((MainTabActivity) getActivity()).getSystemBarTintManager().
                    getConfig().getStatusBarHeight();
        }
        return height;
    }

    private void setLoginedLogoImage(String logoUrl) {
        if (TextUtils.isEmpty(logoUrl)) {
            bkgBlurringView.setVisibility(View.GONE);
            bkgImageView.setImageResource(R.color.black);
            userLogoView.setImageResource(R.drawable.icon_defalut_no_login_logo);
        } else {
            bkgBlurringView.setBlurredView(bkgImageView);
            bkgBlurringView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(logoUrl)
                    .error(R.drawable.icon_defalut_no_login_logo)
                    .into(new GlideDrawableImageViewTarget(bkgImageView) {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bkgBlurringView.invalidate();
                                }
                            }, 50);
                        }

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bkgBlurringView.invalidate();
                                }
                            }, 50);
                        }
                    });
            LSLiveUtils.showUserLogoImage(context, userLogoView, logoUrl);
        }
    }

    private void setUserGrade(final Account.UserBean user) {
        long gradeId = user.getUser_level_id();
        UserLevelManager.getInstance().findLevel(context, gradeId,
                new ICallBack<Level>() {
                    @Override
                    public void callBack(Level data) {
                        handler.post(new AbsRunnable<Level>(data) {
                            @Override
                            public void runData(Level data) {
                                String gradleImageUrl = data != null ? data.getIconUrl() :
                                        "";
                                if (gradeLabel != null) {
                                    gradeLabel.setContentImagePath(gradleImageUrl);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
    }

    public void setListAdapter(final ListView listView) {
        adapter = new MyInfoListAdapter(context);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int realPosition = position - listView.getHeaderViewsCount();
                if (tabList != null && realPosition >= 0 && realPosition < tabList.size()) {
                    TabLabel label = (TabLabel) tabList.get(realPosition);
                    goLabel(label);
                }
            }
        });

        tabList = new ArrayList<>();
        TabLabel label = null;
        if (isShowEditWordBtn) {
            editWordLabel = new TabLabel("快速投稿", R.drawable.icon_start_edit_content);
            tabList.add(editWordLabel);
        }
        gradeLabel = new TabLabel("个人等级", R.drawable.icon_person_grade);
        tabList.add(gradeLabel);
        taskLabel = new TabLabel("每日任务", R.drawable.icon_each_day_task);
        tabList.add(taskLabel);
        messageLabel = new TabLabel("我的消息", R.drawable.icon_my_message, true);
        tabList.add(messageLabel);
//        label = new TabLabel("分享名片", R.drawable.icon_my_person_share_card);
//        tabList.add(label);
        label = new TabLabel("意见反馈", R.drawable.icon_person_agree);
        tabList.add(label);
        label = new TabLabel("关于我们", R.drawable.icon_person_about_us);
        tabList.add(label);

        adapter.update(tabList, false);

        //获取未读消息
        PushMessageHelper.getInstance().getNoReadMessageInfo(context, true, new DataRequest.DataCallback<PushMessageHelper.NoReadPushMessage>() {
            @Override
            public void onSuccess(boolean isAppend, PushMessageHelper.NoReadPushMessage data) {
                if (messageLabel != null && adapter != null) {
                    boolean ishasNoReadMessage = data != null;
                    if (ishasNoReadMessage) {
                        messageLabel.setShowPoint(ishasNoReadMessage);
                        messageLabel.setContentText("您有" + data.allCount + "条未读消息");
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateTaskInfo() {
        if (isLogin) {
            dataManager.getMyDayTask(new DataRequest.DataCallback<List<DayTask>>() {
                @Override
                public void onSuccess(boolean isAppend, List<DayTask> data) {
                    if (data != null && !data.isEmpty()) {
                        int count = data.size();
                        int index = 0;
                        for (DayTask task : data) {
                            if (task.isTaskFinish()) {
                                index++;
                            }
                        }
                        setUserTaskDataView(index + "/" + count);
                    }
                }

                @Override
                public void onFail(ApiException e) {

                }
            });
        } else {
            setUserTaskDataView("");
        }
    }

    private void setUserTaskDataView(String text) {
        handler.post(new AbsRunnable<String>(text) {
            @Override
            public void runData(String data) {
                if (taskLabel != null) {
                    taskLabel.setContentText(data);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void goLabel(TabLabel label) {
        if (TextUtils.equals("快速投稿", label.text)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(EditWordsFragment.KEY_WORD_COLUMN_ID_LIST, editWordColumnIdList);
            WhiteTopBarActivity.startAct(context,
                    EditWordsFragment.class.getName(), "快速投稿", "确定", bundle);
        } else if (TextUtils.equals("个人等级", label.text)) {
            WhiteTopBarActivity.startAct(context,
                    LevelWebFragment.class.getName(), "个人等级");
        } else if (TextUtils.equals("每日任务", label.text)) {
            //            LSUtils.toastNoFunction(act);
            WhiteTopBarActivity.startAct(context,
                    DayTaskListFragment.class.getName(), "每日任务");
        } else if (TextUtils.equals("我的消息", label.text)) {
            if (isLogin) {
                WhiteTopBarActivity.startAct(context, MyMessageTypeFragment.class.getName(), "我的消息");
                //                ArrayList<String> logList = new ArrayList<>();
                //                logList.add("我关注的");
                //                logList.add("系统消息");
                //                DeepColorSwitchTopbarActivity.start(getActivity(), MyMessageFragment.class.getName(),
                //                        logList, R.drawable.icon_topbar_delete);
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "你还没有登录", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (TextUtils.equals("分享名片", label.text)) {
            if (isLogin) {
                DeepColorTopbarActivity.start(getActivity(), ShareCard.class.getName(), "分享名片", null);
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "你还没有登录", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (TextUtils.equals("意见反馈", label.text)) {
            //            LSUtils.toastNoFunction(act);

            //                        WhiteTopBarActivity.startAct(context,
            //                                DayTaskListFragment.class.getName(), "意见反馈",R.drawable.qiandao_image,null);

            DefaultFragmentActivity.start(context,
                    SuggestBackFragment.class.getName());

//            DefaultFragmentActivity.start(context,
//                    UsertBasicFragment2.class.getName());

            //            Intent intent = new Intent(context, HostPersonInfoActivity.class);
            //            startActivity(intent);
        } else if (TextUtils.equals("关于我们", label.text)) {
            WhiteTopBarActivity.startAct(context, AboutInfoFragment.class.getName(), "关于");
        }
    }


    private void setMyStoreTextNum() {
        dataManager.getMyStoreNum(new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                storeNumView.setText(data + "");
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                storeNumView.setText("0");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginOkSubscription != null) {
            loginOkSubscription.unsubscribe();
        }
        if (favritySubscription != null) {
            favritySubscription.unsubscribe();
        }
        if (concernSubscription != null) {
            concernSubscription.unsubscribe();
        }
        if (noReadPushMessageChangeListener != null) {
            PushMessageHelper.getInstance().removeNoReadPushMessageChangeListener(noReadPushMessageChangeListener);
        }

    }

    private void setMyFansTextNum() {
        dataManager.getMyFansNum(new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                fansNumView.setText(data + "");
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                fansNumView.setText("0");
            }
        });
    }

    private void setMyFallowTextNum() {
        dataManager.getMyFollowNum(new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                concernNumView.setText(data + "");
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                concernNumView.setText("0");
            }
        });
    }

    class TabLabel implements ITabUserView {

        private String text;
        private int imageRes;
        private boolean isShowPoint;

        private String contentImagePath;
        private String contentText;
        private boolean isShowDivideView;

        public TabLabel(String text, int res) {
            this.text = text;
            this.imageRes = res;
        }

        public TabLabel(String text, int res, boolean isShowDivideView) {
            this.text = text;
            this.imageRes = res;
            this.isShowDivideView = isShowDivideView;
        }

        @Override
        public String getTabViewText() {
            return text;
        }

        @Override
        public String getTabViewImageUrl() {
            return null;
        }

        @Override
        public int getTabViewImageRes() {
            return imageRes;
        }

        @Override
        public boolean isShowRightTopPoint() {
            return isShowPoint;
        }

        @Override
        public String getTabImageContent() {
            return contentImagePath;
        }

        @Override
        public String getTabTextContent() {
            return TextUtils.isEmpty(contentText) ? "" : contentText;
        }

        @Override
        public boolean isShowDivideView() {
            return isShowDivideView;
        }

        public boolean isShowPoint() {
            return isShowPoint;
        }

        public void setShowPoint(boolean showPoint) {
            isShowPoint = showPoint;
        }

        public String getContentImagePath() {
            return contentImagePath;
        }

        public void setContentImagePath(String contentImagePath) {
            this.contentImagePath = contentImagePath;
        }

        public String getContentText() {
            return contentText;
        }

        public void setContentText(String contentText) {
            this.contentText = contentText;
        }

        public void setShowDivideView(boolean showDivideView) {
            isShowDivideView = showDivideView;
        }
    }
}
