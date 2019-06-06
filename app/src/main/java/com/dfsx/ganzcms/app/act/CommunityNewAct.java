package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.MyFragmentPagerAdapter;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.fragment.*;
import com.dfsx.ganzcms.app.model.Attachment;
import com.dfsx.ganzcms.app.model.ReplyEntry;
import com.dfsx.ganzcms.app.model.ScrollItem;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.*;
import com.dfsx.ganzcms.app.view.zoom.ZoomHeaderCoordinatorLayout;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.google.gson.Gson;
import com.taobao.accs.utl.UT;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by heyang on 2016/11/24  使用
 */

public class CommunityNewAct extends AbsVideoActivity implements PullToRefreshRecyclerView.PullRecyclerHelper
        , View.OnClickListener, IButtonClickListenr<ReplyEntry> {
    protected static final int BAR_TEXT_SIZE_SP = 15;
    protected static final int ITEM_MIN_SPACE_DP = 8;
    private EditText mEditTxtContent;
    private ImageButton mReportBtn;
    private View mBackBtn;
    private WebView mWebView;
    private long mIndex = -1;
    private int attonflag = -1;
    private final int NETWORK_BUSY = 13;
    private final int LOAD_COMPLATE = 10;
    private CustomeProgressDialog progressDialog;
    private String mtitle, connectUrl;
    GestureDetector mGestureDetector;
    //网络请求连接
    private String postGetUrl, mContent;
    private DisplayMetrics dm;
    private static final int HTMLWEB_TYPE = 1;
    private static final int VIDEOENABLEWEB_TYPE = 0;
    private int meWebTYPE = 0, item_width;
    ImageView mAttenttx, mPlayVide0btn, mPosterImagView;
    private long mAver_id = -1;
    int nScreenheight = 0, nPixWidth;
    private ViewGroup mAnchor;
    View mHeandRalayout, mTailRalayout, mWebLayout, mVideoLayout, mImageViewContainer, mShareBtn;
    private android.view.animation.Animation animation;
    ImageButton mReLoadBtn;
    TextView mSourceTxtvw, mBottomtitle;
    String mPosterPath = "", mVideoPath, mInfo;
    RelativeLayout mloadFailurelay, mBottomRelatlout, mMasklayout;
    boolean isGetDateflag = false;
    boolean isComparehtml = false;
    private View rootView, porLayout, mHeaderCommendLay, tailView;
    private FrameLayout fullScreenContainer;
    ReplyListManager replyListManager;
    TextView mAuthorNameTx;
    TopicalApi mTopicalApi = null;
    LinearLayout mBodyImgs;
    ImageView mHeadImgView;
    boolean isFavel = false;
    EditText mReplyContent;
    CommunitActSharePopwindow sharePopupwindow;
    private ImageView mFavorityBtn, mBottomAdImg, mfvelImag;
    TopicalEntry gEnties = null;
    ReportPopupWindow reportPopWindow = null;
    long mPraiseNumber, mStrmpNumber;
    IGetPraistmp mIGetPraistmp = null;
    private IsLoginCheck mloginCheck;
    private CommuntyDatailHelper mCommuntyDatailHelper;
    //    private Subscription commendUpdateSubscription;
    private ImageView _userLeverImage, mPariseBtn, mStrmpBtn;
    private TextView mPrausAimal, mStrampAnmal;
    private int pageIndex = 1;
    private boolean isPraiseFlag = false;
    private boolean isStrampflag = false;
    private TabGrouplayout tagGroups;
    private HorizontalScrollView mHorizontalScrollView;
    private ViewPager pager;
    private LinearLayout mLinearLayout;
    private int endPosition, beginPosition, currentFragmentIndex, startScrollPosition;
    private boolean isEnd;
    private int oldSelectedPosition;
    private ArrayList<ScrollItem> itemList;
    protected ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    protected ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private Context mContext;
    private int headerOffSetSize;
    private long totalCommeds = 0;
    private long totalpraise = 0;
    private boolean isScrollTop = false;
    private boolean isCommedflag = true;
    private boolean isVideoType = false;
    private CmmunityCommendFragment comendFrag;
    private CmmunityPraiseFragment praiseFrag;
    private Subscription commendUpdateSubscription;   //提交评论


    private View indicatorBarContainer;
    private CoordinatorLayout zoomHeaderCoordinatorLayout;
    private AppBarLayout barLayout;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (CommunityNewAct.this == null)
                return false;
            if (message.what == NETWORK_BUSY) {
                if (CommunityNewAct.this != null) {
                    UtilHelp.getConfirmDialog(mContext, "网络繁忙，是否重新加载数据....?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LoadDataFormURl();
                        }
                    }).show();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
            return false;
        }
    });

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //do something...
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                if (mEditTxtContent != null) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditTxtContent.getWindowToken(), 0);
                }
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showNetError() {
        isGetDateflag = false;
        if (mContext != null) {
            UtilHelp.getConfirmDialog(mContext, "网络繁忙，是否重新加载数据....?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    LoadDataFormURl();
                }
            }).show();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    int topHeight = 0;

    private void showWebviewData(TopicalEntry entities) {
        if (mContext != null) {
            if (!isComparehtml) {
                Account user = App.getInstance().getUser();
                if (user != null && user.getUser().getId() != entities.getAuthor_id()) {
                    setAttteonNewStatus(attonflag, mAttenttx, false);
                }
                isGetDateflag = false;
                if (entities.getAttitude() == 1) {
                    isPraiseFlag = true;
                } else if (entities.getAttitude() == 2) {
                    isStrampflag = true;
                } else {
                    isPraiseFlag = false;
                    isStrampflag = false;
                }
                mSourceTxtvw.setText(UtilHelp.getTimeFormatText("HH:mm yyyy/MM/dd", entities.getPost_time() * 1000));
                mAuthorNameTx.setText(entities.getAuthor_nickname());
                setFavStatus(isFavel, false);
                if (entities.getAttitude() == 1) {
                    mPariseBtn.setImageResource(R.drawable.community_praise_btn_sel);
                } else if (entities.getAttitude() == 2) {
                    mStrmpBtn.setImageResource(R.drawable.community_stramp_btn_sel);
                }
                mPraiseNumber = entities.getLike_count();
                mStrmpNumber = entities.getDislike_count();
                String praiseList = entities.getPraiseList();
                getCommuntiyVideoPath(entities);
//                if (mVideoPath != null && !("").equals(mVideoPath)) {
//                    mVideoLayout.setVisibility(View.VISIBLE);
//                    mPlayVide0btn.setVisibility(View.VISIBLE);
//                    if (entities.getAttachmentInfos() != null &&
//                            entities.getAttachmentInfos().size() > 0)
//                        mPosterPath = entities.getAttachmentInfos().get(0).getThumbnail_url();
//                    Util.LoadThumebImage(mPosterImagView, mPosterPath, null);
////                    List<Attachment> dlist = entities.getAttachmentInfos();
////                    if (!(dlist == null || dlist.isEmpty()))
////                        mCommuntyDatailHelper.createVideoContainer((LinearLayout) mPosterImagView, dlist.get(0));
//                } else {
//                    mVideoLayout.setVisibility(View.GONE);
//                }
                UserLevelManager.getInstance().showLevelImage(mContext, entities.getUser_level_id(), _userLeverImage);
                UtilHelp.LoadImageErrorUrl(mHeadImgView, entities.getAuthor_avatar_url(), null, R.drawable.icon_defalut_no_login_logo);
                mHeadImgView.setTag(R.id.cirbutton_user_id, mAver_id);
                if (mWebView != null) {
                    loadDataFromText();
                }
//                if (!TextUtils.isEmpty(entities.getUrls()) && mBodyImgs.getChildCount() == 0) {
                if (!isVideoType && (!(entities.getAttachmentInfos() == null ||
                        entities.getAttachmentInfos().isEmpty()))) {
                    String arr[] = entities.getUrls().split(",");
//                    setMulitpImage(mBodyImgs, arr, entities.getUrls());
                    mCommuntyDatailHelper.setMulitpImage(mBodyImgs, entities.getAttachmentInfos(), entities.getUrls());
                }
                mCommuntyDatailHelper.initTabGroupLayout(tagGroups, entities.getTags());
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                barLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        topHeight = v.getHeight();
                        Log.e("CMD", "topHeight====" + topHeight);
                    }
                });

                int top = barLayout.getTotalScrollRange();
                Log.e("CMD", "top====" + top + Util.dp2px(mContext, 35));
                topHeight += Util.dp2px(mContext, 35);

//                ViewGroup.LayoutParams lp = barLayout.getLayoutParams();
//                lp.height = top + Util.dp2px(mContext, 45);
//                barLayout.setLayoutParams(lp);

//                android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams) barLayout.getLayoutParams()).getBehavior();
//                behavior.onNestedPreScroll(zoomHeaderCoordinatorLayout, barLayout, mLinearLayout, 0, topHeight, new int[]{0, 0});
                if (isScrollTop)
                    resetPosition();
            }
        }
    }

    /**
     * 获取视频地址
     */
    public void getCommuntiyVideoPath(TopicalEntry topicalEntry) {
        if (topicalEntry == null) return;
        if (!(topicalEntry.getAttachmentInfos() == null ||
                topicalEntry.getAttachmentInfos().isEmpty() ||
                topicalEntry.getAttachmentInfos().size() > 1)) {
            Attachment att = topicalEntry.getAttachmentInfos().get(0);
            if (att != null) {
                if (att.getType() == 2) {
                    isVideoType = true;
                    mVideoPath = att.getUrl();
                } else {
                    String name = att.getUrl();
                    if (!TextUtils.isEmpty(name)) {
                        String pix = FileUtil.getSuffix(name.toLowerCase().toString());
                        if (TextUtils.equals(pix, ".mp4") ||
                                TextUtils.equals(pix, ".flv") ||
                                TextUtils.equals(pix, ".m3u8")) {
                            isVideoType = true;
                            mVideoPath = att.getUrl();
                        }
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(mVideoPath)) {
            mVideoLayout.setVisibility(View.VISIBLE);
            mPlayVide0btn.setVisibility(View.VISIBLE);
            if (topicalEntry.getAttachmentInfos() != null &&
                    topicalEntry.getAttachmentInfos().size() > 0)
                mPosterPath = topicalEntry.getAttachmentInfos().get(0).getThumbnail_url();
            Util.LoadThumebImage(mPosterImagView, mPosterPath, null);
        } else {
            mVideoLayout.setVisibility(View.GONE);
        }
    }

    public void resetPosition() {
        myHander.postDelayed(new Runnable() {
            @Override
            public void run() {
                android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams) barLayout.getLayoutParams()).getBehavior();
                behavior.onNestedPreScroll(zoomHeaderCoordinatorLayout, barLayout, mLinearLayout, 0, topHeight, new int[]{0, 0});
            }
        }, 1000);
    }

    public View initTailVIew() {
        View tailView = getLayoutInflater().from(this).inflate(R.layout.cms_tail_bar, null);
        return tailView;
    }

    private void InitCustomVideoView() {
        Activity act = CommunityNewAct.this;
        mAnchor = (ViewGroup) act.findViewById(R.id.videoSurfaceContainer);
        ViewGroup.LayoutParams lp = mAnchor.getLayoutParams();
//        lp.height = nScreenheight;
        lp.height = Util.dp2px(this, 204);
        mAnchor.setLayoutParams(lp);
    }

    @Override
    public void onLbtClick(int type, IButtonClickData<ReplyEntry> data) {
        ReplyEntry entry = data.getObject();
        if (entry == null) return;
        Intent intent = new Intent();
        switch (type) {
            case IButtonClickType.COMMEND_CLICK:
                intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_PARAM, mIndex);
                intent.putExtra("tid", entry.getId());
                DefaultFragmentActivity.start(mContext, CommendPubFragment.class.getName(), intent);
//                startActivityForResult(mContext, CommendPubFragment.class.getName(), intent);
                break;
            case IButtonClickType.ITEM_CLICK:
                if (entry.getSub_reply_count() == 0) {
                    ToastUtils.toastNoCommendFunction(mContext);
                    return;
                }
                intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_PARAM, entry.getId());
                intent.putExtra("itemId", mIndex);
                intent.putExtra("obeject", entry);
                intent.putExtra("praiseNumer", entry.getLike_count());
//                intent.putExtra("thumb", entry.getMthumImage());
                DefaultFragmentActivity.start(mContext, CmyReplyPageFragment.class.getName(), intent);
                break;
            case IButtonClickType.PRAISE_CLICK:
                onPraiseBtn(data.getTag(), entry.getId());
                break;
        }
    }

    public void onPraiseBtn(View v, long comendId) {
        mCommuntyDatailHelper.praiseforTopicCommend(v, comendId, new DataRequest.DataCallbackTag() {
            @Override
            public void onSuccess(Object object, boolean isAppend, Object data) {
                if ((boolean) data) {
                    ToastUtils.toastPraiseMsgFunction(mContext);
                    //   replyListManager.start(mIndex, pageIndex, false);
                }
            }

            @Override
            public void onSuccess(boolean isAppend, Object data) {

            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                ToastUtils.toastApiexceFunction(mContext, e);
            }
        });
    }

    @Override
    public boolean isReadyForPullEnd() {
        boolean is = Math.abs(headerOffSetSize) >= barLayout.getHeight() - indicatorBarContainer.getHeight();
        return is;
    }

    @Override
    public boolean isReadyForPullStart() {
        return headerOffSetSize == 0;
    }

    // js通信接口
    public class JavascriptInterface {
        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        //for api17+
        @android.webkit.JavascriptInterface
        public void openImage(String object, int position) {
            OpenImageUtils.openImage(context, object, position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_community_scoll_custom, null);
        setContentView(rootView);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIndex = bundle.getLong("tid");
            attonflag = bundle.getInt("attion");
            mAver_id = bundle.getLong("aver_id");
            mtitle = bundle.getString("title");
//            mVideoPath = bundle.getString("videoUrl");
            totalCommeds = bundle.getLong("commeds_count");
            totalpraise = bundle.getLong("priase_count");
            isScrollTop = bundle.getBoolean("scoll_top", false);
            isCommedflag = bundle.getBoolean("isCommedflag", true);
        }
        mContext = this;
        mCommuntyDatailHelper = new CommuntyDatailHelper(this);
        mIGetPraistmp = mCommuntyDatailHelper.getmIGetPraistmp();
        animation = AnimationUtils.loadAnimation(mContext, R.anim.nn);
        reportPopWindow = new ReportPopupWindow(CommunityNewAct.this);
        mTopicalApi = mCommuntyDatailHelper.getmTopicalApi();
        mloginCheck = mCommuntyDatailHelper.getMloginCheck();
        dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        nPixWidth = dm.widthPixels;
        nScreenheight = ((dm.widthPixels - 20) * 3) / 4;
        int textLength = Util.dp2px(this, 2 * BAR_TEXT_SIZE_SP);//按每项为4字计算
        item_width = textLength + 2 * Util.dp2px(this, ITEM_MIN_SPACE_DP);
        initView();
        initData();
        initRegister();
        initAction();
    }

    public void initRegister() {
        commendUpdateSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_COMNITY_COMNEND_OK)) {
                            if (comendFrag != null && mContext != null) {
                                updateCommendUmber();
                                comendFrag.resrashData();
                            }
                        }
                    }
                });
    }

    public void initView() {
        porLayout = findViewById(R.id.poriant_layout);
        fullScreenContainer = (FrameLayout) findViewById(R.id.full_screen_video_container);
        mBackBtn = (View) findViewById(R.id.news_image_news_back_btn);
        mReportBtn = (ImageButton) findViewById(R.id.news_report_btn);
        mReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtil.goReport(view.getContext());
            }
        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditTxtContent != null) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditTxtContent.getWindowToken(), 0);
                }
                finish();
            }
        });
        tailView = initTailVIew();
//        mListView.addFooterView(tailView);
//        mListView.setSelectionFromTop(0, 1);
        tagGroups = (TabGrouplayout) findViewById(R.id.cmy_group_taglay);
        _userLeverImage = (ImageView) findViewById(R.id.cmy_user_level);
        mPariseBtn = (ImageView) findViewById(R.id.communitys_praise_btn);
        mPrausAimal = (TextView) findViewById(R.id.communitys_praise_animal);
        mPariseBtn.setOnClickListener(this);
        mStrmpBtn = (ImageView) findViewById(R.id.cumonuitys_stramp_bnt);
        mStrampAnmal = (TextView) findViewById(R.id.communitys_stramp_animal);
        mStrmpBtn.setOnClickListener(this);
        mHeadImgView = (ImageView) findViewById(R.id.head_img);
        mBottomtitle = (TextView) findViewById(R.id.bottom_ad_title_tx);
        mBottomAdImg = (ImageView) findViewById(R.id.bottom_ad_img);
        mBottomRelatlout = (RelativeLayout) findViewById(R.id.bottom_relation_layout);
        mShareBtn = (View) findViewById(R.id.share_lay_btn);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWnd();
            }
        });
        mMasklayout = (RelativeLayout) findViewById(R.id.masg_layout);
//        mloadFailurelay = (RelativeLayout) findViewById(R.id.load__news_fail_layout);
//        mReLoadBtn = (ImageButton) findViewById(R.id.reload_news_btn);
//        mReLoadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LoadDataFormURl();
//            }
//        });
        mSourceTxtvw = (TextView) findViewById(R.id.common_time);
        mAttenttx = (ImageView) findViewById(R.id.common_guanzhu_tx);
        mAttenttx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_click));
                mTopicalApi.attentionAuthor(mAver_id, attonflag, new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        Observable.just((Boolean) data).
                                subscribeOn(Schedulers.io()).
                                observeOn(Schedulers.io()).
                                map(new Func1<Boolean, Integer>() {
                                    @Override
                                    public Integer call(Boolean aBoolean) {
                                        int res = mTopicalApi.isAttentionOther(mAver_id);
                                        return res;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Integer>() {
                                               @Override
                                               public void onCompleted() {
                                                   RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
                                                   boolean flag = attonflag == 1 ? true : false;
                                                   RXBusUtil.sendConcernChangeMessage(flag, 1);
                                               }

                                               @Override
                                               public void onError(Throwable e) {

                                               }

                                               @Override
                                               public void onNext(Integer result) {
                                                   RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
                                                   attonflag = result;
                                                   setAttteonNewStatus(attonflag, mAttenttx, true);
                                               }
                                           }
                                );
                    }

                    @Override
                    public void onFail(ApiException e) {
                        showNetError();
                    }
                });
            }
        });
        mPosterImagView = (ImageView) findViewById(R.id.psoter_imagveo);
        mEditTxtContent = (EditText) findViewById(R.id.socitynews_comment_edit);
        mHeaderCommendLay = (View) findViewById(R.id.communityd_head_comengd_lay);
        mHeaderCommendLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.putExtra("id", mIndex);
                DefaultFragmentActivity.start(mContext, CommendPubFragment.class.getName(), mIndex);
            }
        });
        mReplyContent = (EditText) findViewById(R.id.commentEdit_replay_edt);
        mFavorityBtn = (ImageView) findViewById(R.id.commnitu_isfav_img);
        View parent = (View) mFavorityBtn.getParent();
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavritory(!isFavel);
            }
        });
        mPosterPath = "";
        mPlayVide0btn = (ImageView) findViewById(R.id.player_imagveo);
        mPlayVide0btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!("").equals(mVideoPath) && mVideoPath != null) {
                    try {
                        videoPlayer.start(mVideoPath);
                        videoPlayer.setCompletionListener(new VideoPlayView.CompletionListener() {
                            @Override
                            public void completion(IMediaPlayer mp) {
                                mPlayVide0btn.setVisibility(View.VISIBLE);
                                mPosterImagView.setVisibility(View.VISIBLE);
                            }
                        });
                        videoPlayer.setErrorListener(new VideoPlayView.OnErrorListener() {
                            @Override
                            public void errorListener(IMediaPlayer mp) {
                                mPlayVide0btn.setVisibility(View.VISIBLE);
                                mPosterImagView.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPlayVide0btn.setVisibility(View.GONE);
                    mPosterImagView.setVisibility(View.GONE);
                } else {
                    mVideoLayout.setVisibility(View.GONE);
                }
            }
        });
        if (android.os.Build.VERSION.SDK_INT <= 16) {
            meWebTYPE = HTMLWEB_TYPE;
        } else {
            meWebTYPE = VIDEOENABLEWEB_TYPE;
        }
        mfvelImag = (ImageView) findViewById(R.id.commnitu_isfav_img);
        mBodyImgs = (LinearLayout) findViewById(R.id.commomtiy_imgs);
        mAuthorNameTx = (TextView) findViewById(R.id.replay_user_name);
        mWebView = (WebView) findViewById(R.id.webView_content);
        mWebView.setEnabled(false);
        mWebView.setFocusable(false);
        mWebView.setFocusableInTouchMode(false);
//        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mHeandRalayout = (View) findViewById(R.id.new_detail_video_return_layout);
        mTailRalayout = (View) findViewById(R.id.news_detail_video_bottom);
        mWebLayout = (View) findViewById(R.id.news_detail_web);
        mVideoLayout = (View) findViewById(R.id.news_detail_centernss);
//        tabBar = (RadioGroup) findViewById(R.id.tab_oper_group);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv_view);
        mLinearLayout = (LinearLayout) findViewById(R.id.hsv_content);
        mImageViewContainer = findViewById(R.id.scroll_bottom_view);
        pager = (ViewPager) findViewById(R.id.pager);
//        mGestureDetector = new GestureDetector(CommunityAct.this, (GestureDetector.OnGestureListener) this);
//        LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.newsdtauel_smain);
//        relativeLayout.setOnTouchListener(this);   //设置  mGestureDetector  Ontouche
//        relativeLayout.setLongClickable(true);
        InitCustomVideoView();
        initWebView();

        barLayout = (AppBarLayout) findViewById(R.id.appbar);
        zoomHeaderCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        indicatorBarContainer = findViewById(R.id.bar_top_float_vew);

        barLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                headerOffSetSize = verticalOffset;
                Log.e("CMD", "verticalOffset====" + verticalOffset);
            }
        });

//        barLayout.sc

        //必需继承FragmentActivity,嵌套fragment只需要这行代码
//        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new ImportNewsFragment()).commitAllowingStateLoss();

    }

    private void initAction() {
        if (itemList == null) {
            itemList = new ArrayList<>();
        } else {
            itemList.clear();
        }
        mLinearLayout.removeAllViews();
        ArrayList<ScrollItem> itemList = new ArrayList<ScrollItem>();
        comendFrag = CmmunityCommendFragment.newInstance(mIndex);
        praiseFrag = CmmunityPraiseFragment.newInstance(mIndex);
        ScrollItem item = new ScrollItem("评论 " + totalCommeds, comendFrag);
        itemList.add(item);
        item = new ScrollItem("点赞 " + totalpraise, praiseFrag);
        itemList.add(item);
        fragments.clear();
        itemWidthList.clear();
        for (int i = 0; i < itemList.size(); i++) {
            ScrollItem scrollItem = (ScrollItem) itemList.get(i);
            RelativeLayout layout = new RelativeLayout(mContext);
            TextView titletxt = new TextView(mContext);
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(scrollItem.getItemTitle());
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            titletxt.setGravity(Gravity.CENTER);
            titletxt.setTextColor(0xff7a7a7a);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(titletxt, params);
            int textLength = Util.dp2px(mContext, scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP);
            int itemWidth = 2 * Util.dp2px(mContext, ITEM_MIN_SPACE_DP) + textLength;
//            int itemWidth = mScreenWidth / itemList.size();
            mLinearLayout.addView(layout, itemWidth, Util.dp2px(mContext, 32));
            itemWidthList.add(itemWidth);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    pager.setCurrentItem(pos);
                    setSelectedTextColor(pos);
                }
            });
            layout.setTag(i);
            fragments.add(scrollItem.getFragment());
        }
        try {
            int leftDistance = mLinearLayout.getChildAt(0).getLeft();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageViewContainer.getLayoutParams();
            params.leftMargin = leftDistance + Util.dp2px(mContext, 15);
            mImageViewContainer.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(fragmentPagerAdapter);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
        currentFragmentIndex = 0;
        if (!isCommedflag) {
            currentFragmentIndex = 1;
        }
        pager.setCurrentItem(currentFragmentIndex);
        setSelectedTextColor(currentFragmentIndex);
    }

    public void initData() {
        LoadDataFormURl();
    }

    public void setAttteonNewStatus(int falg, ImageView view, boolean iShow) {
        String msg = "关注成功";
        if (falg == 1) {
            view.setImageResource(R.drawable.commuity_att_selected);
        } else {
            msg = "已取消关注";
            view.setImageResource(R.drawable.commuity_att_normal);
        }
        if (iShow)
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addVideoPlayerToContainer(VideoPlayView videoPlayer) {
        if (mAnchor != null) {
            if (!(mAnchor.getChildAt(0) instanceof VideoPlayView)) {
                mAnchor.addView(videoPlayer, 0);
            }
        }
        porLayout.setVisibility(View.VISIBLE);
        fullScreenContainer.setVisibility(View.GONE);
    }

    @Override
    public void addVideoPlayerToFullScreenContainer(VideoPlayView videoPlayer) {
        if (fullScreenContainer != null) {
            if (fullScreenContainer.getChildAt(0) == null ||
                    !(fullScreenContainer.getChildAt(0)
                            instanceof VideoPlayView)) {
                fullScreenContainer.addView(videoPlayer, 0);
            }
        }
        porLayout.setVisibility(View.GONE);
        fullScreenContainer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!("").equals(mPosterPath) && !("").equals(mVideoPath) && mVideoPath != null) {
            mVideoLayout.setVisibility(View.VISIBLE);
            mPlayVide0btn.setVisibility(View.VISIBLE);
            mPosterImagView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(mPosterPath)
                    .placeholder(R.color.transparent)
                    .error(R.drawable.glide_default_image)
                    .crossFade()
                    .into(mPosterImagView);
//            if (gEnties != null) {
//                List<Attachment> att = gEnties.getAttachmentInfos();
//                if (!(att == null || att.isEmpty()))
//                    mCommuntyDatailHelper.createVideoContainer((LinearLayout) mPosterImagView, att.get(0));
//            }
        }
    }

    public void LoadDataFormURl() {
        if (!isGetDateflag) {
            isGetDateflag = true;
//            mloadFailurelay.setVisibility(View.GONE);
            if (!isComparehtml)
                progressDialog = CustomeProgressDialog.show(mContext, "正在加载中...");
            getDataMainThread();
        }
    }

    public void getDataMainThread() {
        String url = postGetUrl != null ? postGetUrl :
                App.getInstance().getmSession().getCommunityServerUrl() +
                        "/public/threads/" + Long.toString(mIndex);
        DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().
                setUrl(url).setToken(App.getInstance().getCurrentToken()).build();

        new DataFileCacheManager<TopicalEntry>(this, "commnity_new" + mIndex + "_txt") {
            @Override
            public TopicalEntry jsonToBean(JSONObject jsonObject) {
                return pareseJOSM(jsonObject);
            }
        }.getData(params, false)
                .setCallback(new DataRequest.DataCallback<TopicalEntry>() {
                    @Override
                    public void onSuccess(final boolean isAppend, TopicalEntry data) {
                        if (data == null) return;
                        Observable.just(data).
                                subscribeOn(Schedulers.io()).
                                observeOn(Schedulers.io()).
                                flatMap(new Func1<TopicalEntry, Observable<TopicalEntry>>() {
                                    @Override
                                    public Observable<TopicalEntry> call(TopicalEntry entry) {
                                        TopicalEntry p = mTopicalApi.getTopicTopicalInfo(entry);
                                        String praiseList = mTopicalApi.getPraiseNumberList(entry.getId());
                                        p.setPraiseList(praiseList);
                                        return Observable.just(p);
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<TopicalEntry>() {
                                               @Override
                                               public void onCompleted() {
                                               }

                                               @Override
                                               public void onError(Throwable e) {

                                               }

                                               @Override
                                               public void onNext(TopicalEntry att) {
                                                   attonflag = att.getRelationRole();
                                                   mAver_id = att.getAuthor_id();
                                                   mtitle = att.getContent();
                                                   mPosterPath = att.getVideoThumb();
                                                   isFavel = att.isFavl();
                                                   gEnties = att;
                                                   showWebviewData(att);
                                               }
                                           }
                                );
                        if (progressDialog != null && progressDialog.isShowing()) {
                            gEnties = null;
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        showNetError();
                    }
                });
    }

    // heyang  2016/11/2
    private TopicalEntry pareseJOSM(JSONObject jsonObject) {
        TopicalEntry entry = null;
        if (jsonObject == null || jsonObject.toString().isEmpty()) {
            return entry;
        }
        try {
//            mReplayCount = jsonObject.optLong("reply_count");
//            mBrower = jsonObject.optLong("view_count");
//            mheadImgfile = jsonObject.optString("author_avatar_url");
//            author = jsonObject.optString("author_name");
//            long time = jsonObject.optLong("post_time");
//            if (time > 0) {
//                mSource = Util.timestampToString(time * 1000, "yyyy-MM-dd");
//            } else {
//                mSource = "";
//            }
            String txtWeb = "<html>\n" +
                    "<meta name=\"viewport\" content=\"width=device-width\"/>\n" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"main.css\" />\n" +
//                    "<style>body{font-size:36px; }</style>\n" +
                    "<style>body{white-space: pre-line; }</style>\n" +
                    "<script language=\"javascript\">\n" +
                    "    function imgResize() {\n" +
                    "        var imgs = document.getElementsByTagName(\"img\");\n" +
                    "        var array = new Array();\n" +
                    "        for (var j = 0; j < imgs.length; j++) {\n" +
                    "         array[j] = imgs[j].attributes[\'src\'].value;\n" +
                    "          }\n" +
                    "        for (var i = 0; i < imgs.length; i++) {\n" +
                    "            imgs[i].pos = i;\n" +
                    "            imgs[i].onclick=function()" +
                    "            {\n" +
                    "              var pos = this.pos;\n" +
                    "window.imagelistner.openImage(array.join(\",\"),pos);\n" +
                    "            }\n" +
//                       "if (imgs[i].width==0 || window.innerWidth < imgs[i].width){" +
//                       "            imgs[i].style.width = \"100%\";\n" +
//                       "            imgs[i].style.height = \"auto\";}\n" +
//                       "            var p = imgs[i].parentElement;" +
//                       "            if(p != null)  p.style.textIndent = \"0\";" +
                    "        }\n" +
                    "    }\n" +
                    "    </script>\n" +
                    "<body>";

            if (!jsonObject.isNull("content")) {
//                    txtWeb += "<p style=\"word-break:break-all;padding:5px\">";
                txtWeb += "<font style=\"text-align:justify;font-size:15px;line-height:150%;text-indent:2em;\">";
//                    txtWeb += "<font style=\"line-height:180%;font-size:17px\">";
                try {
                    mContent = jsonObject.getString("content");
//                                String link = "\"http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files";
//                                String link = "\"http://" + mApi.getServerHost()  + "/sites/default/files";
                    String link = "\"" + App.getInstance().getBaseUrl() + "/sites/default/files";
                    mContent = mContent.replace("\"/sites/default/files", link);
                    txtWeb += mContent;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                txtWeb += "</font>";
//                    txtWeb += "</p>";
            }
            mInfo = "";
            txtWeb += "</body></html>";
            /**
             * 调用saveToSDCard方法保存文件到SD卡
             */
            connectUrl = txtWeb;
            entry = new Gson().fromJson(jsonObject.toString(), TopicalEntry.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entry;
    }

    public void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);
//        setZoomControlGone(mWebView);

        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
//            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }

        webSettings.setJavaScriptEnabled(true);

        //这方法可以让你的页面适应手机屏幕的分辨率，完整的显示在屏幕上，可以放大缩小(推荐使用)。
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);

        // 添加js交互接口类，并起别名 imagelistner
        mWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");

//        webSettings.setDisplayZoomControls(false);

//        mWebView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return mGestureDetector.onTouchEvent(motionEvent);
//            }
//        });

        //2016-1-28  heyag
//        mListView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return mGestureDetector.onTouchEvent(motionEvent);
//            }
//        });

        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }
//                if (!App.getInstance().getmSession().isRead((int) mIndex)) {
//                    App.getInstance().getmSession().signReadStatus(mIndex);
//                }
//                Toast.makeText(NewsDetailActivity.this, "网络加载完",
//                        Toast.LENGTH_SHORT).show();
                // view.loadUrl("javascript:var imgs = document.getElementsByTagName(\"img\");for(var i = 0; i<imgs.length; i++){imgs[i].style.width = \"100%\";imgs[i].style.height=\"auto\";}");
                view.loadUrl("javascript:imgResize()");
                myHander.sendEmptyMessage(LOAD_COMPLATE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String message = consoleMessage.message() + " -- line " + consoleMessage.lineNumber();
                switch (consoleMessage.messageLevel()) {
                    case ERROR:
                        Log.e("JSTag", message);

                        break;
                    default:
                        Log.i("JSTag", message);

                        break;
                }
                return true;
            }
        });

    }

    public void loadDataFromText() {
        if (mWebView == null) return;
        if (TextUtils.isEmpty(connectUrl)) {
            mWebView.setVisibility(View.GONE);
        } else {
            mWebView.resumeTimers();
            mWebView.loadDataWithBaseURL("file:///android_asset/", connectUrl, "text/html", "utf-8", null);
//        Log.e("TAG", "html == " + connectUrl);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (commendUpdateSubscription != null)
            commendUpdateSubscription.unsubscribe();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        cleareWebView();
    }

    public void cleareWebView() {
        mWebView.removeAllViews();
        final ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(mWebView);
        }
        mWebView.destroy();
        mWebView = null;
    }

    public void setFavStatus(boolean flag, boolean isShowMsg) {
        String msg = "收藏成功";
        if (flag) {
            mfvelImag.setImageResource(R.drawable.cvidoe_favirty_sel);
        } else {
            msg = "取消收藏成功";
            mfvelImag.setImageResource(R.drawable.cvidoe_favirty_normal);
        }
        if (isShowMsg) {
            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_MSG));
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void addFavritory(final boolean bfalg) {
        if (!mloginCheck.checkLogin()) return;
        mTopicalApi.farityToptic(mIndex, bfalg, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                if ((boolean) data) {
                    isFavel = !isFavel;
                    setFavStatus(bfalg, true);
                }
            }

            @Override
            public void onFail(ApiException e) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mPariseBtn) {
            pariseButton();
        } else if (view == mStrmpBtn) {
            strampButton();
        }
    }

    public void pariseButton() {
        if (!mloginCheck.checkLogin()) return;
        if (isPraiseFlag) {
            mTopicalApi.cancelPariseToptic(mIndex, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
                    if ((boolean) data) {
                        isPraiseFlag = false;
//                        if (mPraiseNumber >= 0) {
//                            mPraiseNumber--;
//                        } else
//                            mPraiseNumber = 0;
//                        mPrausAimal.setVisibility(View.VISIBLE);
                        mPrausAimal.startAnimation(animation);
                        mPrausAimal.setText("-1");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
//                                mPrausAimal.setVisibility(View.GONE);
                                ColumnBasicListManager.getInstance().setRershFlag(true);
                                ColumnBasicListManager.getInstance().setCmyId(mIndex);
                                ColumnBasicListManager.getInstance().setPraiseflag(false);
                                mPariseBtn.setImageResource(R.drawable.community_praise_btn_noraml);
//                                mPariseBtn.setText(mPraiseNumber + "");
                                ToastUtils.toastMsgFunction(mContext, "取消点赞");
                                if (praiseFrag != null && mContext!=null)
                                {
                                    praiseFrag.resrashData();
                                    updatePraiseUmber(false);
                                }
                            }
                        }, 50);
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    ToastUtils.toastApiexceFunction(mContext, e);
                }
            });
        } else {
            mTopicalApi.pariseToptic(mIndex, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
                    if ((boolean) data) {
                        isPraiseFlag = true;
//                        mPraiseNumber++;
//                        mPrausAimal.setVisibility(View.VISIBLE);
                        mPrausAimal.startAnimation(animation);
                        mPrausAimal.setText("+1");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
//                                mPariseBtn.setText(mPraiseNumber + "");
//                                mPrausAimal.setVisibility(View.GONE);
                                ColumnBasicListManager.getInstance().setRershFlag(true);
                                ColumnBasicListManager.getInstance().setCmyId(mIndex);
                                ColumnBasicListManager.getInstance().setPraiseflag(true);
                                mPariseBtn.setImageResource(R.drawable.community_praise_btn_sel);
                                ToastUtils.toastMsgFunction(mContext, "点赞成功");
                                if (praiseFrag != null && mContext != null)
                                {
                                    praiseFrag.resrashData();
                                    updatePraiseUmber(true);
                                }

                            }
                        }, 50);
                        //      mIGetPraistmp.updateValuse(mIndex, true, false, false);
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                    ToastUtils.toastApiexceFunction(mContext, e);
                }
            });
        }
    }

    public void updatePraiseUmber(boolean isadd) {
        if (isadd) {
            totalpraise++;
        } else {
            totalpraise = totalpraise >= 1 ? --totalpraise : 0;
        }
        if (mLinearLayout != null) {
            RelativeLayout lay = (RelativeLayout) mLinearLayout.getChildAt(1);
            TextView txt = (TextView) lay.getChildAt(0);
            txt.setText("点赞" + totalpraise);
        }
    }

    public void updateCommendUmber() {
        totalCommeds++;
        if (mLinearLayout != null) {
            RelativeLayout lay = (RelativeLayout) mLinearLayout.getChildAt(0);
            TextView txt = (TextView) lay.getChildAt(0);
            txt.setText("评论" + totalCommeds);
        }
    }

    public void strampButton() {
        if (!mloginCheck.checkLogin()) return;
        if (isStrampflag) {
            mTopicalApi.cancelPariseToptic(mIndex, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
                    if ((boolean) data) {
                        isStrampflag = false;
                        if (mStrmpNumber >= 0) {
                            mStrmpNumber--;
                        } else
                            mStrmpNumber = 0;
                        mStrampAnmal.startAnimation(animation);
                        mStrampAnmal.setText("-1");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
//                                mStrmpBtn.setText(mStrmpNumber + "");
                                mStrmpBtn.setImageResource(R.drawable.community_stramp_btn_noraml);
                                ToastUtils.toastMsgFunction(mContext, "取消点踩");
                            }
                        }, 50);
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    ToastUtils.toastApiexceFunction(mContext, e);
                }
            });
        } else {
            mTopicalApi.strampToptic(mIndex, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
                    isStrampflag = true;
//                    mStrmpNumber++;
                    mStrampAnmal.startAnimation(animation);
                    mStrampAnmal.setText("+1");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
//                            mStrmpBtn.setText(mStrmpNumber + "");
                            mStrmpBtn.setImageResource(R.drawable.community_stramp_btn_sel);
                            ToastUtils.toastMsgFunction(mContext, "点踩成功");
                        }
                    }, 50);
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, JsonCreater.getErrorMsgFromApi(e.toString()), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void shareWnd() {
        if (sharePopupwindow == null) {
            sharePopupwindow = new CommunitActSharePopwindow(this);
            sharePopupwindow.setOnShareClickListener(new CommunitActSharePopwindow.OnShareClickListener() {
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

    public int getItemLeftXPosition(int pos) {
        int xPos = 0;
        for (int i = 0; i < pos && i < itemWidthList.size(); i++) {
            xPos += itemWidthList.get(i);
        }
        return xPos;
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(final int position) {
            int dx = (itemWidthList.get(position) - item_width) / 2;
            int toPostion = getItemLeftXPosition(position) + dx - 24;//position * item_width;
            Animation animation = new TranslateAnimation(endPosition, toPostion, 0, 0);

            int leftDistance = mLinearLayout.getChildAt(0).getLeft();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageViewContainer.getLayoutParams();
            params.leftMargin = leftDistance + Util.dp2px(mContext, 10);
            mImageViewContainer.setLayoutParams(params);
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

    private void setSelectedTextColor(int selectedPosition) {
        if (mLinearLayout == null) return;
        TextView oldSelectedText = (TextView) mLinearLayout.findViewWithTag(oldSelectedPosition).
                findViewById(R.id.scroll_item_text_id);
        TextView selectedText = (TextView) mLinearLayout.findViewWithTag(selectedPosition).
                findViewById(R.id.scroll_item_text_id);
        oldSelectedText.setTextColor(0xff7a7a7a);
        selectedText.setTextColor(mContext.getResources().getColor(R.color.public_purple_bkg));
        oldSelectedPosition = selectedPosition;
    }

    public void onSharePlatfrom(SharePlatform platform) {
        if (gEnties == null) return;
        ShareContent content = new ShareContent();
        String title = mtitle;
        if (!TextUtils.isEmpty(title)) {
            if (title.length() > 28) {
                title = title.substring(0, 28);
            }
        }
        content.title = title;
        if (gEnties != null && gEnties.getAttachmentInfos() != null
                && gEnties.getAttachmentInfos().size() > 0) {
            Attachment att = gEnties.getAttachmentInfos().get(0);
            if (att != null) {
                String thumb = att.getUrl();
                if (att.getType() == 2) {
                    thumb = att.getThumbnail_url();
                }
                if (thumb != null && !TextUtils.isEmpty(thumb)) {
                    int width = Util.dp2px(this, 32);   //dp
                    int height = Util.dp2px(this, 32);
                    thumb += "?w=" + width + "&h=" + height + "&s=1";
                }
                content.thumb = thumb;
            }
        }
        content.type = ShareContent.UrlType.WebPage;
        content.url = App.getInstance().getCommuityShareUrl() + mIndex;
        AbsShare share = ShareFactory.createShare(mContext, platform);
        share.share(content);
    }


}
