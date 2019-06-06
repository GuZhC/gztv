package com.dfsx.ganzcms.app.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.act.ReportActivity;
import com.dfsx.ganzcms.app.view.zoom.IPullZoom;
import com.dfsx.ganzcms.app.view.zoom.ZoomHeaderCoordinatorLayout;
import com.dfsx.logonproject.act.LoginActivity;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.business.MyDataManager;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.model.ScrollItem;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.util.ConcernChanegeInfo;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.fivehundredpx.android.blur.BlurringView;
import com.google.gson.Gson;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

/*
 *  Created  by heyang  2017-3-12
 */

public class PersonHomeFragment extends Fragment implements IPullZoom, View.OnClickListener, PullToRefreshRecyclerView.PullRecyclerHelper {
    private static final int BAR_TEXT_SIZE_SP = 15;
    private static final int ITEM_MIN_SPACE_DP = 20;
    private final static int GET_USERIMG = 49;
    private CircleButton mUserImage;
    private TextView mPeronname;
    private TextView mTxtSignature;
    private TextView mTxtPersonid;
    private int endPosition;
    private int beginPosition;
    private int currentFragmentIndex;
    private int startScrollPosition;
    private boolean isEnd;
    private int oldSelectedPosition;
    //    TwoRelyView myAttention, myFans;
    ImageButton mBackBtn;
    ImageButton mShareBtn;
    boolean bLogon = false;
    private int item_width;
    MainTabActivity activity;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    private Subscription updateInfoScription;
    private Subscription concernSubscription;
    private HorizontalScrollView mHorizontalScrollView;
    private ViewPager pager;
    private LinearLayout mLinearLayout;
    private View mImageViewContainer;
    private MyDataManager dataManager;
    private long userId = -1;
    TopicalApi mTopicalApi = null;
    boolean isAttionAuthor = false;
    SharePopupwindow sharePopupwindow;
    View rootView;
    TextView mTxtAttion;
    ImageView mImgTopBackground, mPerAddAttionBtn, userLevelImg;
    BlurringView mImagTopSerground;
    int scrrenWidth;
    TextView storeNumberTx, myFollowNumberTx, myFansNumberTx;
    View storeNumberView, myFollowNumberView, myFansNumberView;
    private ZoomHeaderCoordinatorLayout zoomHeaderCoordinatorLayout;
    private AppBarLayout barLayout;
    private int headerOffSetSize;
    private View _barTopFolatView;
    private ImageView reportBtn;

    private Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == GET_USERIMG) {
                bLogon = true;
//                mBottomExistlay.setVisibility(View.VISIBLE);
                String imgpth = message.obj.toString().trim();
                if (imgpth != null && !TextUtils.isEmpty(imgpth)) {
                    UtilHelp.LoadImageErrorUrl(mUserImage, imgpth, null, R.drawable.icon_defalut_no_login_logo);
                } else {
                    mUserImage.setImageResource(R.drawable.icon_defalut_no_login_logo);
                }
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataManager = new MyDataManager(getActivity());
//        rootView = inflater.inflate(R.layout.user_home_custom, container, false);
        rootView = inflater.inflate(R.layout.activity_user_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userId = bundle.getLong("id");
        }
        zoomHeaderCoordinatorLayout = (ZoomHeaderCoordinatorLayout) view.findViewById(R.id.main_content);
        barLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        _barTopFolatView = (View) view.findViewById(R.id.bar_top_float_vew);
        mBackBtn = (ImageButton) view.findViewById(R.id.slider_fg_back);
        mBackBtn.setOnClickListener(this);
        mShareBtn = (ImageButton) view.findViewById(R.id.slider_fg_share);
        mShareBtn.setOnClickListener(this);
        mUserImage = (CircleButton) view.findViewById(R.id.slider_person_img);
        mUserImage.setOnClickListener(this);
        mPeronname = (TextView) view.findViewById(R.id.slider_person_name);
        mTxtSignature = (TextView) view.findViewById(R.id.slider_person_mark);
        mTxtPersonid = (TextView) view.findViewById(R.id.slider_persons_id);
//        myFans = (TwoRelyView) view.findViewById(R.id.my_home_fans);
//        myAttention = (TwoRelyView) view.findViewById(R.id.my_home_attnetin);
        mHorizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.hsv_view);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.hsv_content);
        int textLength = Util.dp2px(getActivity(), 2 * BAR_TEXT_SIZE_SP);//按每项为4字计算
        if (item_width < textLength) {
            item_width = textLength + 2 * Util.dp2px(getActivity(), ITEM_MIN_SPACE_DP);
        }
        mImageViewContainer = view.findViewById(R.id.scroll_bottom_view);
        mImageViewContainer.getLayoutParams().width = item_width;
        pager = (ViewPager) view.findViewById(R.id.pager);
//        mViewBottom = (View) view.findViewById(R.id.bottom_bootm_layout);
//        mTxtAttion = (TextView) view.findViewById(R.id.slider_attion_tx);
//        mImagAttion = (ImageView) view.findViewById(R.id.slider_attion_btn);
        mPerAddAttionBtn = (ImageView) view.findViewById(R.id.per_home_addattrion_btn);
        mPerAddAttionBtn.setOnClickListener(this);
//        mViewBottom.setOnClickListener(this);
        userLevelImg = (ImageView) view.findViewById(R.id.cmy_user_level);
        mTopicalApi = new TopicalApi(getContext());
        mImgTopBackground = (ImageView) view.findViewById(R.id.top_bangrond_view);
        mImagTopSerground = (BlurringView) view.findViewById(R.id.room_home_filter);
        mImagTopSerground.setBlurredView(mImgTopBackground);
        scrrenWidth = UtilHelp.getScreenWidth(getActivity());
        storeNumberTx = (TextView) view.findViewById(R.id.user_store_num_text);
        myFollowNumberTx = (TextView) view.findViewById(R.id.user_concern_num_text);
        myFansNumberTx = (TextView) view.findViewById(R.id.user_fans_num_text);
        storeNumberView = (View) view.findViewById(R.id.user_store_view);
        myFollowNumberView = (View) view.findViewById(R.id.user_concern_view);
        myFansNumberView = (View) view.findViewById(R.id.user_fans_view);
        reportBtn = (ImageView) view.findViewById(R.id.cmy_user_report);
        reportBtn.setOnClickListener(this);
        storeNumberView.setOnClickListener(this);
        myFollowNumberView.setOnClickListener(this);
        myFansNumberView.setOnClickListener(this);
//        zoomHeaderCoordinatorLayout.setPullZoom(barLayout,
//                PixelUtil.dp2px(getActivity(), 360),
//                PixelUtil.dp2px(getActivity(), 500), this);

        barLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                headerOffSetSize = verticalOffset;
            }
        });

        if (userId != -1) {
            Account accout = App.getInstance().getUser();
            if (accout != null &&
                    userId == accout.getUser().getId()) {
                storeNumberView.setVisibility(View.VISIBLE);
            }
            getUserInfo(userId);
            initRegister();
//        iniData();
            initAction();
        }
//        initData();
    }

    public void initData() {
        setMyFallowTextNum();
        setMyFansTextNum();
        setMyStoreTextNum();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainTabActivity) {
            activity = (MainTabActivity) context;
        }
    }

    private void initRegister() {
        updateInfoScription = RxBus.getInstance().toObserverable(Intent.class).
                subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Intent>() {
            @Override
            public void call(Intent intent) {
//                if (intent != null &&
//                        UtilHelp.UPDATE_USER_INFO_MESSAGE.equals(
//                                intent.getAction()
//                        )) {
//                    initLoginStatus();
//                }
            }
        });

        concernSubscription = RxBus.getInstance()
                .toObserverable(ConcernChanegeInfo.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ConcernChanegeInfo>() {
                    @Override
                    public void call(ConcernChanegeInfo chanegeInfo) {
//                        if (chanegeInfo == null) {
//                            return;
//                        }
//                        String numStr = myAttention.getFirstView().getText().toString();
//                        int num = TextUtils.isDigitsOnly(numStr) ? Integer.valueOf(numStr) : 0;
//                        num = num + (chanegeInfo.isAdd() ? 1 : -1) * chanegeInfo.getChangeNum();
//                        if (num < 0) {
//                            num = 0;
//                        }
//                        myAttention.getFirstView().setText(num + "");
                    }
                });
    }

    private void initAction() {
        ArrayList<ScrollItem> itemList = new ArrayList<ScrollItem>();
        ScrollItem item = new ScrollItem("直播", UserLiveFragment.newInstance(userId));
        itemList.add(item);
//        item = new ScrollItem("圈子", CommunityLocalFragment.newInstance(-1, userId));
        item = new ScrollItem("圈子", CommunityRecycleUpFragment.newInstance(-1, userId));
        itemList.add(item);
//        item = new ScrollItem("视频 ", MineVideoFragment.newInstance(userId));
//        itemList.add(item);
        fragments.clear();
        itemWidthList.clear();
        for (int i = 0; i < itemList.size(); i++) {
            ScrollItem scrollItem = (ScrollItem) itemList.get(i);
            RelativeLayout layout = new RelativeLayout(getActivity());
            TextView titletxt = new TextView(getActivity());
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(scrollItem.getItemTitle());
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            titletxt.setGravity(Gravity.CENTER);
            if (isAdded())
                titletxt.setTextColor(getResources().getColor(R.color.perosn_home_midlle_font));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(titletxt, params);
            int textLength = Util.dp2px(getActivity(), scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP);
//            int itemWidth = 2 * Util.dp2px(getActivity(), ITEM_MIN_SPACE_DP) + textLength;
            int itemWidth = scrrenWidth / 2;
            mLinearLayout.addView(layout, itemWidth, Util.dp2px(getActivity(), 42));
            itemWidthList.add(itemWidth);
            layout.setOnClickListener(this);
            layout.setTag(i);
            fragments.add(scrollItem.getFragment());
        }

        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        pager.setAdapter(fragmentPagerAdapter);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
        pager.setCurrentItem(0);
        setSelectedTextColor(0);
    }

    public void initLoginInfo(Account.UserBean account) {
        if (account != null) {
            mPeronname.setText(account.getNickname());
            if (account.getSignature() != null)
                mTxtSignature.setText(account.getSignature());
            mTxtPersonid.setText("ID: " + account.getId());
            storeNumberTx.setText(account.getFavorite_count() + "");
            myFollowNumberTx.setText(account.getFollow_count() + "");
            myFansNumberTx.setText(account.getFan_count() + "");
            String headUrl = account.getAvatar_url();
            UserLevelManager.getInstance().showLevelImage(getContext(), account.getUser_level_id(), userLevelImg);
            if (!TextUtils.isEmpty(headUrl)) {
                Util.LoadImageErrorUrl(mUserImage, headUrl, null, R.drawable.icon_defalut_no_login_logo);
//                mUserImage.setBackgroundColor(getContext().getResources().
//                        getColor(R.color.transparent));
//                Util.LoadThumebImage(mImgTopBackground, headUrl, null);
                Glide.with(getActivity())
                        .load(headUrl)
                        .error(R.drawable.icon_defalut_no_login_logo)
                        .into(new GlideDrawableImageViewTarget(mImgTopBackground) {
                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                myHander.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mImagTopSerground.invalidate();
                                    }
                                }, 100);
                            }

                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                myHander.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mImagTopSerground.invalidate();
                                    }
                                }, 100);
                            }
                        });
            } else {
                //       mUserImage.setImageResource(getContext().getResources().
                //               getColor(R.color.transparent));
                mUserImage.setBackgroundResource(R.drawable.icon_defalut_no_login_logo);
            }
            boolean isFlag = true;
            if (App.getInstance().getUser() != null) {
                if (account.getId() == App.getInstance().getUser().getUser().getId()) {
                    isFlag = false;
                } else {
                    Observable.just(account.getId())
                            .subscribeOn(Schedulers.io())
                            .map(new Func1<Long, Boolean>() {
                                @Override
                                public Boolean call(Long id) {
                                    if (App.getInstance().getUser() != null) {
                                        if (id != App.getInstance().getUser().getUser().getId()) {
                                            int res = mTopicalApi.isAttentionOther(id);
                                            isAttionAuthor = res == 1 ? true : false;
                                        }
                                    }
                                    return isAttionAuthor;
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Boolean>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(Boolean falg) {
                                    setAttteonTextStatus(isAttionAuthor);
                                }
                            });
                }
            }
//            mTxtAttion.setVisibility(isFlag ? View.VISIBLE : View.GONE);
//            mPerAddAttionBtn.setVisibility(isFlag ? View.VISIBLE : View.GONE);
        } else {
//            loginText.setVisibility(View.VISIBLE);
            mPeronname.setVisibility(View.GONE);
//            mUserImage.setImageResource(getContext().getResources().
//                    getColor(R.color.transparent));
            mUserImage.setBackgroundResource(R.drawable.icon_defalut_no_login_logo);
        }
    }

    public void iniData() {
        //  initLoginStatus();
        //  setMyFallowTextNum();
        //  setMyFansTextNum();
    }

    private void setMyFallowTextNum() {
        dataManager.getMyFollowNum(new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                myFollowNumberTx.setText(data + "");
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                myFollowNumberTx.setText("0");
            }
        });
    }

    private void setMyFansTextNum() {
        dataManager.getMyFansNum(new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                myFansNumberTx.setText(data + "");
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                myFansNumberTx.setText("0");
            }
        });
    }

    private void setMyStoreTextNum() {
        dataManager.getMyStoreNum(new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                storeNumberTx.setText(data + "");
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                storeNumberTx.setText("0");
            }
        });
    }

    public void shareWnd() {
        if (sharePopupwindow == null) {
            sharePopupwindow = new SharePopupwindow(getActivity());
            sharePopupwindow.setOnShareClickListener(new SharePopupwindow.OnShareClickListener() {
                @Override
                public void onShareClick(View v) {
                    int vId = v.getId();
                    if (vId == com.dfsx.lzcms.liveroom.R.id.share_qq) {
                        onSharePlatfrom(SharePlatform.QQ);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wb) {
                        onSharePlatfrom(SharePlatform.WeiBo);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wx) {
                        onSharePlatfrom(SharePlatform.Wechat);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wxfriends) {
                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS);
                    }
                }
            });
        }
        sharePopupwindow.show(rootView);
    }

    public void onSharePlatfrom(SharePlatform platform) {
        ShareContent content = new ShareContent();
        content.title = mPeronname.getText().toString() + "的主页";
        content.disc = "";
//        if (mCotentInfoeny.getThumbnail_urls() != null && mCotentInfoeny.getThumbnail_urls().size() > 0)
//            content.thumb = mCotentInfoeny.getThumbnail_urls().get(0);
        content.type = ShareContent.UrlType.WebPage;
        content.url = App.getInstance().getmSession().getBaseMobileWebUrl() + "/user/home/" + userId;
        AbsShare share = ShareFactory.createShare(getActivity(), platform);
        share.share(content);
    }

    public void getUserInfo(long uid) {
        String url = App.getInstance().getPotrtServerUrl() + "/public/users/" + uid;
        new DataRequest<Account.UserBean>(getActivity()) {
            @Override
            public Account.UserBean jsonToBean(JSONObject json) {
                Account.UserBean act = null;
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    act = new Gson().fromJson(json.toString(), Account.UserBean.class);
                }
                return act;
            }
        }.getData(new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setToken(App.getInstance().getCurrentToken()).
                build(), false).
                setCallback(new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        Account.UserBean account = (Account.UserBean) data;
                        if (account != null) {
                            initLoginInfo(account);
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void setAttteonTextStatus(boolean falg) {
        if (falg) {
//            mImagAttion.setVisibility(View.INVISIBLE);
//            mTxtAttion.setText("√已关注");
            mPerAddAttionBtn.setVisibility(View.GONE);
        } else {
//            mImagAttion.setVisibility(View.VISIBLE);
//            mTxtAttion.setText("加关注");
            mPerAddAttionBtn.setVisibility(View.VISIBLE);
        }
    }

//    public void upFansUpdate(boolean flag) {
//        String str = myFans.getFirstView().getText().toString().trim();
//        if (!TextUtils.isEmpty(str) && str != null) {
//            Long number = Long.parseLong(str);
//            if (flag) {
//                number++;
//            } else {
//                number--;
//            }
//            myFans.getFirstView().setText(number + "");
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateInfoScription != null) {
            updateInfoScription.unsubscribe();
        }
        if (concernSubscription != null)
            concernSubscription.unsubscribe();
    }

    public void AddAttionAutor() {
        if (isUserNull()) return;
        if (userId == -1) return;
//        int fouc = isAttionAuthor ? 0 : 1;
//        isAttionAuthor =0;
        mTopicalApi.attentionAuthor(userId, 0, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                Observable.just((Boolean) data).
                        subscribeOn(Schedulers.io()).
                        observeOn(Schedulers.io()).
                        map(new Func1<Boolean, Integer>() {
                            @Override
                            public Integer call(Boolean aBoolean) {
                                int res = mTopicalApi.isAttentionOther(userId);
                                return res;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Integer>() {
                                       @Override
                                       public void onCompleted() {
                                       }

                                       @Override
                                       public void onError(Throwable e) {

                                       }

                                       @Override
                                       public void onNext(Integer result) {
                                           RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
//                                           isAttionAuthor = result == 1 ? true : false;
                                           isAttionAuthor = !isAttionAuthor;
                                           RXBusUtil.sendConcernChangeMessage(isAttionAuthor, 1);
//                                           upFansUpdate(isAttionAuthor);
                                           setAttteonTextStatus(isAttionAuthor);
                                           ToastUtils.toastMsgFunction(getActivity(), "关注成功");
                                       }
                                   }
                        );
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                //   showNetError();
                ToastUtils.toastApiexceFunction(getActivity(),e);
            }
        });
    }

    @Override
    public boolean isReadyForPullEnd() {
        boolean is = Math.abs(headerOffSetSize) >= barLayout.getHeight() - _barTopFolatView.getHeight();
        return is;
    }

    @Override
    public boolean isReadyForPullStart() {
        return headerOffSetSize == 0;
    }

    @Override
    public void onPullZooming(int newScrollValue) {

    }

    @Override
    public void onPullZoomEnd() {

    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> fragments;
        private FragmentManager fm;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Object obj = super.instantiateItem(container, position);
            return obj;
        }
    }

    public int getItemLeftXPosition(int pos) {
        int xPos = 0;
        for (int i = 0; i < pos && i < itemWidthList.size(); i++) {
            xPos += itemWidthList.get(i);
        }
        return xPos;
    }

    private void setSelectedTextColor(int selectedPosition) {
        TextView oldSelectedText = (TextView) mLinearLayout.findViewWithTag(oldSelectedPosition).
                findViewById(R.id.scroll_item_text_id);
        TextView selectedText = (TextView) mLinearLayout.findViewWithTag(selectedPosition).
                findViewById(R.id.scroll_item_text_id);
        oldSelectedText.setTextColor(getResources().getColor(R.color.perosn_home_midlle_font));
        selectedText.setTextColor(getResources().getColor(R.color.perosn_home_midlle_sele_font));
        ((RelativeLayout) oldSelectedText.getParent()).setBackgroundColor(getActivity().getResources().getColor(R.color.perosn_home_banground));
        ((RelativeLayout) selectedText.getParent()).setBackgroundColor(getActivity().getResources().getColor(R.color.public_purple_bkg));
        oldSelectedPosition = selectedPosition;
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(final int position) {
            int dx = (itemWidthList.get(position) - item_width) / 2;
            int toPostion = getItemLeftXPosition(position) + dx;//position * item_width;
            Animation animation = new TranslateAnimation(endPosition, toPostion, 0, 0);

            beginPosition = toPostion; //position * item_width;

            currentFragmentIndex = position;
            setSelectedTextColor(position);
            if (animation != null) {
                animation.setFillAfter(true);
                animation.setDuration(0);
                mImageViewContainer.startAnimation(animation);
                mHorizontalScrollView.smoothScrollTo(getItemLeftXPosition(currentFragmentIndex - 1), 0);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            if (!isEnd) {
                int startPox = startScrollPosition != 0 ? startScrollPosition :
                        getItemLeftXPosition(currentFragmentIndex);
                if (currentFragmentIndex == position) {
                    endPosition = startPox +
                            (int) (itemWidthList.get(currentFragmentIndex) * positionOffset);
                }
                if (currentFragmentIndex == position + 1) {
                    endPosition = startPox -
                            (int) (itemWidthList.get(currentFragmentIndex) * (1 - positionOffset));
                }

                Animation mAnimation = new TranslateAnimation(beginPosition, endPosition, 0, 0);
                mAnimation.setFillAfter(true);
                mAnimation.setDuration(0);
                mImageViewContainer.startAnimation(mAnimation);
                mHorizontalScrollView.invalidate();
                beginPosition = endPosition;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                isEnd = false;
                startScrollPosition = beginPosition;
            } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                isEnd = true;
                int dx = (itemWidthList.get(currentFragmentIndex) - item_width) / 2;
                beginPosition = getItemLeftXPosition(currentFragmentIndex) + dx;
                if (pager.getCurrentItem() == currentFragmentIndex) {
                    mImageViewContainer.clearAnimation();
                    Animation animation = null;
                    animation = new TranslateAnimation(endPosition, beginPosition, 0, 0);
                    animation.setFillAfter(true);
                    animation.setDuration(1);
                    mImageViewContainer.startAnimation(animation);
                    mHorizontalScrollView.invalidate();
                    endPosition = beginPosition;
                }
            }
        }
    }

    @Override
    public void onClick(final View view) {
        final View v = view;
        android.view.animation.Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.image_click);
        animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                if (v == mUserImage) {
//                    if (App.getInstance().getUser() != null) {
//                        int useId = (int) App.getInstance().getUser().id;
//                        Bundle bundle = new Bundle();
//                        bundle.putInt("userId", useId);
//                        intent.putExtras(bundle);
//                        intent.setClass(getActivity(), IndividualActivity.class);
////                        startActivity(intent);
//                    } else {
//                        intent.setClass(getActivity(), LoginActivity.class);
////                        startActivity(intent);
//                    }
                    //               startActivity(intent);
                } else if (v == mBackBtn) {
                    getActivity().finish();
                } else if (v == mShareBtn) {
                    shareWnd();
                } else if (v == mPerAddAttionBtn) {
                    //add attion
                    AddAttionAutor();
                } else if (v == storeNumberView) {
                    WhiteTopBarActivity.startAct(getActivity(), MyfavorityFragment.class.getName(), "收藏", " ");
                } else if (v == myFollowNumberView) {
                    WhiteTopBarActivity.startAct(getActivity(), MyAttentionFragment.class.getName(), "他关注的", "", userId);
                } else if (v == myFansNumberView) {
                    WhiteTopBarActivity.startAct(getActivity(), MyFansFragment.class.getName(), "他的粉丝", "", userId);
                } else if (v == reportBtn) {
                    Intent intent = new Intent(getActivity(), ReportActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    if (v instanceof RelativeLayout) {
                        if (v.getTag() != null) {
                            int postion = (int) v.getTag();
                            if (postion != -1) {
                                pager.setCurrentItem(postion);
                                setSelectedTextColor(postion);
                            }
                        }
                    }
                }
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {

            }
        });
        v.startAnimation(animation);
    }

    public boolean isUserNull() {
        boolean isLogo = false;
        if (App.getInstance().getUser() == null) {
            isLogo = true;
            goToLogin();
        }
        return isLogo;
    }

    private void goToLogin() {
        if (getActivity() != null) {
            AlertDialog adig = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("未登录，是否现在登录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            }).create();
            adig.show();
        }
    }
}
