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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
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
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.fragment.CmyReplyPageFragment;
import com.dfsx.ganzcms.app.fragment.CommendPubFragment;
import com.dfsx.ganzcms.app.fragment.ReportPopupWindow;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.MoreTextView;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.thirdloginandshare.share.*;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by heyang on 2016/11/24  使用
 */

public class CommunityAct extends AbsVideoActivity implements PullToRefreshBase.OnRefreshListener2<ListView>
        , View.OnClickListener, IButtonClickListenr<ReplyEntry> {
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
    private int meWebTYPE = 0;
    ImageView mAttenttx, mPosterImagView, mPlayVide0btn;
    private long mAver_id = -1;
    int nScreenheight = 0, nPixWidth;
    private ViewGroup mAnchor;
    View mHeandRalayout, mTailRalayout, mWebLayout, mVideoLayout;
    private android.view.animation.Animation animation;
    ImageButton mReLoadBtn;
    TextView mSourceTxtvw, mBottomtitle;
    String mPosterPath = "", mVideoPath, mInfo;
    RelativeLayout mloadFailurelay, mBottomRelatlout, mMasklayout;
    boolean isGetDateflag = false;
    PullToRefreshListView mPullRefreshList;
    ReplayAdapter mReplayAdapter = null;
    ListView mListView;
    boolean isComparehtml = false;
    private View rootView, porLayout, headLayout, mHeaderCommendLay, commnedNoplayLayout, tailView;
    private FrameLayout fullScreenContainer;
    ReplyListManager replyListManager;
    TextView mAuthorNameTx, mfvelTx, mCommendNumberTx, mBrowerNumberTx;
    TopicalApi mTopicalApi = null;
    LinearLayout mBodyImgs;
    CircleButton mHeadImgView;
    boolean isFavel = false;
    EditText mReplyContent;
    SharePopupwindow sharePopupwindow;
    private ImageView mShareBtn, mFavorityBtn, mBottomAdImg, mfvelImag;
    TopicalEntry gEnties = null;
    ReportPopupWindow reportPopWindow = null;
    TextView mPariseBtn, mStrmpBtn;
    long mPraiseNumber, mStrmpNumber;
    IGetPraistmp mIGetPraistmp = null;
    private IsLoginCheck mloginCheck;
    private CommuntyDatailHelper mCommuntyDatailHelper;
    private Subscription commendUpdateSubscription;
    private TextView _topTitletxt;
    private MoreTextView _mulinePrsiseTxt;
    private ImageView _userLeverImage;
    private int pageIndex = 1;
    private boolean isPraiseFlag = false;
    private boolean isStrampflag = false;
    private boolean  isVideoType=false;


    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (CommunityAct.this == null)
                return false;
            if (message.what == NETWORK_BUSY) {
                if (CommunityAct.this != null) {
                    UtilHelp.getConfirmDialog(CommunityAct.this, "网络繁忙，是否重新加载数据....?", new DialogInterface.OnClickListener() {
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
                    InputMethodManager imm = (InputMethodManager) CommunityAct.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        if (CommunityAct.this != null) {
            UtilHelp.getConfirmDialog(CommunityAct.this, "网络繁忙，是否重新加载数据....?", new DialogInterface.OnClickListener() {
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

    private void showWebviewData(TopicalEntry entities) {
        if (CommunityAct.this != null) {
            if (!isComparehtml) {
                Account user = App.getInstance().getUser();
                if (user != null && user.getUser().getId() != entities.getAuthor_id()) {
                    setAttteonNewStatus(attonflag, mAttenttx, false);
                } else {
//                    mAttenttx.setVisibility(View.GONE);
                }
                mListView.setVisibility(View.VISIBLE);
                isGetDateflag = false;
                if (entities.getAttitude() == 1) {
                    isPraiseFlag = true;
                } else if (entities.getAttitude() == 2) {
                    isStrampflag = true;
                } else {
                    isPraiseFlag = false;
                    isStrampflag = false;
                }
                mloadFailurelay.setVisibility(View.GONE);
                mBrowerNumberTx.setText(entities.getView_count() + "阅读");
                _topTitletxt.setText(entities.getView_count() + "次浏览");
                mSourceTxtvw.setText(UtilHelp.getTimeFormatText("HH:mm yyyy/MM/dd", entities.getPost_time() * 1000));
                mAuthorNameTx.setText(entities.getAuthor_nickname());
                mCommendNumberTx.setText("评论" + entities.getReply_count());
//                mVideoPath = entities.getVideoUrl();
                setFavStatus(isFavel, false);
                mPraiseNumber = entities.getLike_count();
                mStrmpNumber = entities.getDislike_count();
                mPariseBtn.setText(mPraiseNumber + "");
                mStrmpBtn.setText(mStrmpNumber + "");
                String praiseList = entities.getPraiseList();
                if (praiseList != null && !TextUtils.isEmpty(praiseList)) {
                    _mulinePrsiseTxt.setText(praiseList);
                } else {
                    _mulinePrsiseTxt.setVisibility(View.GONE);
                }
//                setAttteonNewStatus(attonflag, mAttenttx, false);
//                if (mVideoPath != null && !("").equals(mVideoPath)) {
//                    mVideoLayout.setVisibility(View.VISIBLE);
//                    mPlayVide0btn.setVisibility(View.VISIBLE);
//                    if (entities.getAttachmentInfos() != null &&
//                            entities.getAttachmentInfos().size() > 0)
//                        mPosterPath = entities.getAttachmentInfos().get(0).getThumbnail_url();
//                    Util.LoadThumebImage(mPosterImagView, mPosterPath, null);
//                } else {
//                    mVideoLayout.setVisibility(View.GONE);
//                }
                getCommuntiyVideoPath(entities);
                UserLevelManager.getInstance().showLevelImage(CommunityAct.this, entities.getUser_level_id(), _userLeverImage);
//                Util.LoadThumebImage(mHeadImgView, entities.getAver_url(), null);
                UtilHelp.LoadImageErrorUrl(mHeadImgView, entities.getAuthor_avatar_url(), null, R.drawable.icon_defalut_no_login_logo);
                mHeadImgView.setTag(R.id.cirbutton_user_id, mAver_id);
//                connectUrl = entities.getHtmlContent();
                if (mWebView != null) {
                    loadDataFromText();
                }
                if (!isVideoType && (!(entities.getAttachmentInfos() == null ||
                        entities.getAttachmentInfos().isEmpty()))) {
                    String arr[] = entities.getUrls().split(",");
                    setMulitpImage(mBodyImgs, arr, entities.getUrls());
//                    mCommuntyDatailHelper.setMulitpImage(mBodyImgs, entities.getAttachmentInfos(), entities.getUrls());
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
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

    public View initTailVIew() {
        View tailView = getLayoutInflater().from(this).inflate(R.layout.cms_tail_bar, null);
        return tailView;
    }

    private void InitCustomVideoView() {
        Activity act = CommunityAct.this;
        mAnchor = (ViewGroup) act.findViewById(R.id.videoSurfaceContainer);
        ViewGroup.LayoutParams lp = mAnchor.getLayoutParams();
//        lp.height = nScreenheight;
        lp.height = Util.dp2px(this, 204);
        mAnchor.setLayoutParams(lp);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        replyListManager.start(mIndex, 1, false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        tailView.setVisibility(View.GONE);
        pageIndex++;
        replyListManager.start(mIndex, pageIndex, true);
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
                DefaultFragmentActivity.start(CommunityAct.this, CommendPubFragment.class.getName(), intent);
                break;
            case IButtonClickType.ITEM_CLICK:
                if (entry.getSub_reply_count() == 0) {
                    ToastUtils.toastNoCommendFunction(CommunityAct.this);
                    return;
                }
                intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_PARAM, entry.getId());
                intent.putExtra("itemId", mIndex);
                intent.putExtra("obeject", entry);
                intent.putExtra("praiseNumer", entry.getLike_count());
//                intent.putExtra("thumb", entry.getMthumImage());
                DefaultFragmentActivity.start(CommunityAct.this, CmyReplyPageFragment.class.getName(), intent);
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
                    ToastUtils.toastPraiseMsgFunction(CommunityAct.this);
                    replyListManager.start(mIndex, pageIndex, false);
                }
            }

            @Override
            public void onSuccess(boolean isAppend, Object data) {

            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                ToastUtils.toastApiexceFunction(CommunityAct.this, e);
            }
        });
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
        rootView = getLayoutInflater().inflate(R.layout.commonity_detail_activity, null);
        setContentView(rootView);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIndex = bundle.getLong("tid");
            attonflag = bundle.getInt("attion");
            mAver_id = bundle.getLong("aver_id");
            mtitle = bundle.getString("title");
//            mVideoPath = bundle.getString("videoUrl");
        }
        mCommuntyDatailHelper = new CommuntyDatailHelper(this);
//        mIGetPraistmp = IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_COMMUNNITY);
        mIGetPraistmp = mCommuntyDatailHelper.getmIGetPraistmp();
        animation = AnimationUtils.loadAnimation(CommunityAct.this, R.anim.nn);
        reportPopWindow = new ReportPopupWindow(CommunityAct.this);
        mReplayAdapter = new ReplayAdapter(this);
        mReplayAdapter.setCallback(this);
//        mTopicalApi = new TopicalApi(CommunityAct.this);
        mTopicalApi = mCommuntyDatailHelper.getmTopicalApi();
//        mloginCheck = new IsLoginCheck(this);
        mloginCheck = mCommuntyDatailHelper.getMloginCheck();
        //创建数据的管理器
        replyListManager = new ReplyListManager(CommunityAct.this, 3 + "", mIndex);
        replyListManager.setCallback(new DataRequest.DataCallback<ArrayList<ReplyEntry>>() {
            @Override
            public void onSuccess(final boolean isAppend, ArrayList<ReplyEntry> data) {
                if (mPullRefreshList != null)
                    mPullRefreshList.onRefreshComplete();
                if (data == null) {
                    if (!isAppend)
                        tailView.setVisibility(View.GONE);
                    if (!mReplayAdapter.isHas() && isAppend)
                        tailView.setVisibility(View.GONE);
                    return;
                } else {
                    if (data.isEmpty() && !isAppend) {
                        tailView.setVisibility(View.GONE);
                    } else {
                        tailView.setVisibility(View.VISIBLE);
                    }
                }
                Observable.from(data)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<ReplyEntry, ReplyEntry>() {
                            @Override
                            public ReplyEntry call(ReplyEntry topicalEntry) {
                                try {
                                    String respone = mTopicalApi.getAtthmentById(topicalEntry.getAttmentId());
                                    if (!TextUtils.isEmpty(respone.toString().trim())) {
                                        JSONObject json = JsonCreater.jsonParseString(respone);
                                        Gson gson = new Gson();
                                        JSONArray arr = json.optJSONArray("result");
                                        if (arr != null && arr.length() > 0) {
                                            for (int i = 0; i < arr.length(); i++) {
                                                try {
                                                    JSONObject obj = (JSONObject) arr.get(i);
                                                    Attachment cp = gson.fromJson(obj.toString(),
                                                            Attachment.class);
                                                    topicalEntry.setMthumImage(cp.getUrl());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                } catch (ApiException e) {
                                    e.printStackTrace();
                                }
                                return topicalEntry;
                            }
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<ReplyEntry>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<ReplyEntry> data) {
                                if (data != null) {
                                    mReplayAdapter.update((ArrayList<ReplyEntry>) data, isAppend);
                                    commnedNoplayLayout.setVisibility(View.GONE);
                                }
                            }
                        });

            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                if (mPullRefreshList != null)
                    mPullRefreshList.onRefreshComplete();
                mCommendNumberTx.setText("评论0");
                Log.e("MyDocxFragment", "get data fail");
            }
        });
        dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) CommunityAct.this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        nPixWidth = dm.widthPixels;
        nScreenheight = ((dm.widthPixels - 20) * 3) / 4;
        initView();
        initData();
        initRegister();
    }

    public void initView() {
        _topTitletxt = (TextView) findViewById(R.id.cmy_top_title);
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
                    InputMethodManager imm = (InputMethodManager) CommunityAct.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditTxtContent.getWindowToken(), 0);
                }
                finish();
            }
        });
        mPullRefreshList = (PullToRefreshListView) findViewById(R.id.relati_list);
//        mPullRefreshList.setAdapter(adapter);
        mPullRefreshList.setAdapter(mReplayAdapter);
        mPullRefreshList.setOnRefreshListener(this);
        mPullRefreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mListView = (ListView) mPullRefreshList.getRefreshableView();
        headLayout = LayoutInflater.from(CommunityAct.this).inflate(R.layout.news_conmmuity_detail_header, null);
        mListView.addHeaderView(headLayout);
        tailView = initTailVIew();
        mListView.addFooterView(tailView);
        mListView.setSelectionFromTop(0, 1);
//        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                IntentUtil.goReport(view.getContext());
//                return false;
//            }
//        });
        //        mPullRefreshList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                IntentUtil.goReport(view.getContext());
//                return false;
//            }
//        });
        _userLeverImage = (ImageView) headLayout.findViewById(R.id.cmy_user_level);
        _mulinePrsiseTxt = (MoreTextView) headLayout.findViewById(R.id.mulite_line_txt);
        mPariseBtn = (TextView) headLayout.findViewById(R.id.cvideo_praise_tx);
        mPariseBtn.setOnClickListener(this);
        mStrmpBtn = (TextView) headLayout.findViewById(R.id.cvideo_stamp_tx);
        mStrmpBtn.setOnClickListener(this);
        mHeadImgView = (CircleButton) headLayout.findViewById(R.id.head_img);
        mBottomtitle = (TextView) headLayout.findViewById(R.id.bottom_ad_title_tx);
        mBottomAdImg = (ImageView) headLayout.findViewById(R.id.bottom_ad_img);
        mBottomRelatlout = (RelativeLayout) headLayout.findViewById(R.id.bottom_relation_layout);
        mShareBtn = (ImageView) findViewById(R.id.share_lay_btn);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWnd();
            }
        });
        commnedNoplayLayout = (View) headLayout.findViewById(R.id.comnend_noplay_layout);
        mMasklayout = (RelativeLayout) headLayout.findViewById(R.id.masg_layout);
        mloadFailurelay = (RelativeLayout) findViewById(R.id.load__news_fail_layout);
        mReLoadBtn = (ImageButton) findViewById(R.id.reload_news_btn);
        mReLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadDataFormURl();
            }
        });
        mSourceTxtvw = (TextView) headLayout.findViewById(R.id.common_time);
        mAttenttx = (ImageView) headLayout.findViewById(R.id.common_guanzhu_tx);
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
        mPosterImagView = (ImageView) headLayout.findViewById(R.id.psoter_imagveo);
        mEditTxtContent = (EditText) findViewById(R.id.socitynews_comment_edit);
        mHeaderCommendLay = (View) findViewById(R.id.communityd_head_comengd_lay);
        mHeaderCommendLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.putExtra("id", mIndex);
                DefaultFragmentActivity.start(CommunityAct.this, CommendPubFragment.class.getName(), mIndex);
            }
        });
        mReplyContent = (EditText) findViewById(R.id.commentEdit_replay_edt);
        mFavorityBtn = (ImageView) findViewById(R.id.commnitu_isfav_img);
        mFavorityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavritory(!isFavel);
            }
        });
        mPosterPath = "";
        mPlayVide0btn = (ImageView) headLayout.findViewById(R.id.player_imagveo);
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
        mfvelTx = (TextView) findViewById(R.id.commnitu_isfav_tx);
        mCommendNumberTx = (TextView) headLayout.findViewById(R.id.communtiry_count_tx);
        mBrowerNumberTx = (TextView) headLayout.findViewById(R.id.communtiry_brower_count_tx);
        mBodyImgs = (LinearLayout) headLayout.findViewById(R.id.commomtiy_imgs);
        mAuthorNameTx = (TextView) headLayout.findViewById(R.id.replay_user_name);
        mWebView = (WebView) headLayout.findViewById(R.id.webView_content);
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mHeandRalayout = (View) findViewById(R.id.new_detail_video_return_layout);
        mTailRalayout = (View) findViewById(R.id.news_detail_video_bottom);
        mWebLayout = (View) findViewById(R.id.news_detail_web);
        mVideoLayout = (View) findViewById(R.id.news_detail_centernss);
//        mGestureDetector = new GestureDetector(CommunityAct.this, (GestureDetector.OnGestureListener) this);
        LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.newsdtauel_smain);
//        relativeLayout.setOnTouchListener(this);   //设置  mGestureDetector  Ontouche
        relativeLayout.setLongClickable(true);
        InitCustomVideoView();
        initWebView();
    }

    public void initRegister() {
        commendUpdateSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_COMNITY_COMNEND_OK)) {
                            if (mReplayAdapter != null) {
                                replyListManager.start(mIndex, 1, false);
                            }
                        }
                    }
                });

    }

    public void initData() {
        LoadDataFormURl();
        if (mReplayAdapter != null) {
            replyListManager.start(mIndex, 1, false);
        }
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
            Toast.makeText(CommunityAct.this, msg, Toast.LENGTH_SHORT).show();
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
//            UtilHelp.LoadImageFormUrl(mPosterImagView, mPosterPath, null);
        }
//        if (mReplayAdapter != null) {
//            replyListManager.start(mIndex, 1, false);
//        }
    }

    public void LoadDataFormURl() {
        if (!isGetDateflag) {
            isGetDateflag = true;
            mloadFailurelay.setVisibility(View.GONE);
            if (!isComparehtml)
                progressDialog = CustomeProgressDialog.show(CommunityAct.this, "正在加载中...");
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
//                    "style=\"white-space: pre-line;\"" +
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

    public LinearLayout createHorvertyLay(int i, boolean isSigle) {
        LinearLayout mLayout = new LinearLayout(CommunityAct.this);
//                    mLayout.setWeightSum(1.0f);
        mLayout.setOrientation(LinearLayout.HORIZONTAL);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
        LinearLayout.LayoutParams params = null;
        if (!isSigle) {
            params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        } else
            params = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
//                    params.leftMargin = ((int) (5.0F * dm.density));
//                    params.rightMargin = ((int) (5.0F * dm.density));
        if (i >= 1)
            params.topMargin = Util.dp2px(CommunityAct.this, 8);
        mLayout.setLayoutParams(params);
        return mLayout;
    }

    public ImageView createPixImag(int pos, String path, final String urls, boolean isSingle, int length) {
        ImageView bg = new ImageView(CommunityAct.this);
        LinearLayout.LayoutParams lp1 = null;
        int subPidex = -1;
        if (path != null) {
            subPidex = path.lastIndexOf("?");
            if (subPidex != -1) {
                path = path.substring(0, subPidex);
            }
        }
//        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(nItmeWidh, (int) (nItmeWidh * 3 / 4), 1.0f);
        //控件距离其右侧控件的距离，此处为60
        if (!isSingle) {
            if (length == 4 || length == 2) {
                int nItmeWidh = (nPixWidth - 60) / 2;
//                lp1 = new LinearLayout.LayoutParams(nItmeWidh, (int) (nItmeWidh * 3 / 4), 1.0f);
                lp1 = new LinearLayout.LayoutParams(nItmeWidh, nItmeWidh, 1.0f);
                int dpWidth = Util.dp2px(CommunityAct.this, nItmeWidh);
                int dpHeight = Util.dp2px(CommunityAct.this, nItmeWidh);
                path += "?w=" + dpWidth + "&h=" + dpHeight + "&s=1";
            } else {
                lp1 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                lp1.width = Util.dp2px(CommunityAct.this, 110);
                lp1.height = Util.dp2px(CommunityAct.this, 110);
                path += "?w=" + lp1.width + "&h=" + lp1.height + "&s=1";
            }
            lp1.setMargins(0, 0, 10, 0);
            bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            lp1 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            bg.setAdjustViewBounds(true);
//            lp1.width = Util.dp2px(CommunityAct.this, 197);
//            lp1.height = Util.dp2px(CommunityAct.this, 222);
//            path += "?w=" + lp1.width +"&h=" + lp1.height+ "&s=2";
        }
//        bg.setScaleType(ImageView.ScaleType.FIT_XY);
        bg.setLayoutParams(lp1);
//        Util.LoadThumebImage(bg, path, null);

        //解决加载gif图片慢的问题
//        Glide.with(CommunityAct.this).load(path).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(bg);

        LoadImageWithCache(path, bg);

//        Glide.with(CommunityAct.this)
//                .load(path)
////                .override(width, height)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//图片缓存策略,这个一般必须有
//                .centerCrop()//对图片进行裁剪
//                .placeholder(R.drawable.glide_default_image)//加载图片之前显示的图片
//                .error(R.drawable.glide_default_image)//加载图片失败的时候显示的默认图
//                .crossFade()//让图片显示的时候带有淡出的效果
//                .into(bg);

        bg.setTag(R.id.commny_bg_position, pos);
        bg.setTag(R.id.commny_bg_urls, urls);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag(R.id.commny_bg_position);
                String imgs = (String) view.getTag(R.id.commny_bg_urls);
                OpenImageUtils.openImage(CommunityAct.this, imgs, position);
            }
        });
        return bg;
    }


    public void LoadImageWithCache(String filePath, ImageView images) {
        if (filePath == null || TextUtils.isEmpty(filePath)) return;
        boolean isGif = false;
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        if (TextUtils.equals(suffix.toString().toLowerCase().trim(), "gif")) {
            isGif = true;
        }
        if (isGif) {
//            GlideImgManager.getInstance().
//                    showGif(context, images, filePath);
            Glide.with(CommunityAct.this)
                    .load(filePath)
                    .placeholder(R.color.white)
                    .error(R.color.white)
                    .into(images);
        } else {
            Glide.with(CommunityAct.this)
                    .load(filePath)
                    .placeholder(R.color.white)
                    .error(R.color.white)
                    .crossFade()
                    .into(images);
        }
    }

    public void setMulitpImage(LinearLayout mainVew, String[] dlist, String imgs) {
        if (mainVew != null) mainVew.removeAllViews();
        int line = 2;
        int count = 3;
        int pos = 0;
        boolean siSingle = false;
        boolean flag = true;
        if (dlist.length == 4 || dlist.length == 2) {
            count = 2;
        } else if (dlist.length < 4) {
            line = 1;
            if (dlist.length == 1) siSingle = true;
        } else if (dlist.length > 4) {
            line = 3;
        }
        for (int i = 0; i < line; i++) {
            LinearLayout mHorView = createHorvertyLay(i, siSingle);
            for (int k = 0; k < count; k++) {
                if (pos < dlist.length) {
//                    if (siSingle)
//                        flag = dlist.get(pos).getWidth() >dlist.get(0).getHeight()? false : true;
                    ImageView bg = createPixImag(pos, dlist[pos].toString(), imgs, siSingle, dlist.length);
                    mHorView.addView(bg);
                    pos++;
                }
            }
            mainVew.addView(mHorView);
        }
    }

//    @Override
//    public void onBackPressed() {
//        int  a=0;
    // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
//        if(meWebTYPE==VIDEOENABLEWEB_TYPE)
//        {
//        if (!webChromeClient.onBackPressed()) {
//            if (mWebView.canGoBack()) {
//                mWebView.goBack();
//            } else {
    // Standard back button implementation (for example this could close the app)
//                super.onBackPressed();
//            }
//        }
//        }
//    }

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
        mWebView.resumeTimers();
        mWebView.loadDataWithBaseURL("file:///android_asset/", connectUrl, "text/html", "utf-8", null);
//        Log.e("TAG", "html == " + connectUrl);
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

//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        return mGestureDetector.onTouchEvent(motionEvent);
////        return false;
//    }

    public void setFavStatus(boolean flag, boolean isShowMsg) {
        String msg = "收藏成功";
        if (flag) {
            mfvelImag.setImageResource(R.drawable.communtiy_item_fal_sel);
        } else {
            msg = "取消收藏成功";
            mfvelImag.setImageResource(R.drawable.cvidoe_favirty_normal);
        }
        if (isShowMsg) {
            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_MSG));
            Toast.makeText(CommunityAct.this, msg, Toast.LENGTH_SHORT).show();
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
                e.printStackTrace();
                ToastUtils.toastApiexceFunction(CommunityAct.this, e);
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
//        boolean flag = mIGetPraistmp.isPriseFlag(mIndex);
//        if (flag) {
//            Toast.makeText(CommunityAct.this, "已经点赞过了", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (isPraiseFlag) {
            mTopicalApi.cancelPariseToptic(mIndex, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
                    if ((boolean) data) {
                        isPraiseFlag = false;
                        if (mPraiseNumber >= 0) {
                            mPraiseNumber--;
                        } else
                            mPraiseNumber = 0;
                        mPariseBtn.startAnimation(animation);
                        mPariseBtn.setText("-1");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                mPariseBtn.setText(mPraiseNumber + "");
                                ToastUtils.toastMsgFunction(CommunityAct.this, "取消点赞");
                            }
                        }, 50);
//                        mIGetPraistmp.updateValuse(mIndex, true, false, false);
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    ToastUtils.toastApiexceFunction(CommunityAct.this, e);
                }
            });
        } else {
            mTopicalApi.pariseToptic(mIndex, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
                    isPraiseFlag = true;
                    mPraiseNumber++;
                    mPariseBtn.startAnimation(animation);
                    mPariseBtn.setText("+1");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mPariseBtn.setText(mPraiseNumber + "");
                            ToastUtils.toastMsgFunction(CommunityAct.this, "点赞成功");
                        }
                    }, 50);
                    mIGetPraistmp.updateValuse(mIndex, true, false, false);
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                    ToastUtils.toastApiexceFunction(CommunityAct.this, e);
                }
            });
        }

    }

    public void strampButton() {
        if (!mloginCheck.checkLogin()) return;
//        boolean flag = mIGetPraistmp.isStrmpFlag(mIndex);
//        if (flag) {
//            Toast.makeText(CommunityAct.this, "已经踩过了", Toast.LENGTH_SHORT).show();
//            return;
//        }
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
                        mStrmpBtn.startAnimation(animation);
                        mStrmpBtn.setText("-1");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                mStrmpBtn.setText(mStrmpNumber + "");
                                ToastUtils.toastMsgFunction(CommunityAct.this, "取消点踩");
                            }
                        }, 50);
//                        mIGetPraistmp.updateValuse(mIndex, true, false, false);
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    ToastUtils.toastApiexceFunction(CommunityAct.this, e);
                }
            });
        } else {
            mTopicalApi.strampToptic(mIndex, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
                    isStrampflag = true;
                    mStrmpNumber++;
                    mStrmpBtn.startAnimation(animation);
                    mStrmpBtn.setText("+1");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mStrmpBtn.setText(mStrmpNumber + "");
                            ToastUtils.toastMsgFunction(CommunityAct.this, "点踩成功");
                        }
                    }, 50);
//                    mIGetPraistmp.updateValuse(mIndex, false, true, false);
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                    Toast.makeText(CommunityAct.this, JsonCreater.getErrorMsgFromApi(e.toString()), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void shareWnd() {
        if (sharePopupwindow == null) {
            sharePopupwindow = new SharePopupwindow(this);
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
        if (gEnties == null) return;
        ShareContent content = new ShareContent();
        content.title = mtitle;
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
        AbsShare share = ShareFactory.createShare(CommunityAct.this, platform);
        share.share(content);
    }

    public class ReplayAdapter extends BaseListViewAdapter<ReplyEntry> {
        public ReplayAdapter(Context context) {
            super(context);
        }

        private IButtonClickListenr callback;

        public void setCallback(IButtonClickListenr callback) {
            this.callback = callback;
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.nc_commend_replay_item;
        }

        @Override
        public int getCount() {
            return list == null ? 1 : list.size() == 0 ? 1 : list.size();
        }

        public boolean isHas() {
            boolean flag = false;
            if (list != null && !list.isEmpty())
                flag = true;
            return flag;
        }

        public void update(ArrayList<ReplyEntry> data, boolean bAddTail) {
            if (data != null && !data.isEmpty()) {
                boolean noData = false;
                if (bAddTail)
                    /*
                    if(items.size() >= data.size()
                            && items.get(items.size() - data.size()).id == data.get(0).id)*/
                    if (list.size() >= data.size()
                            && list.get(list.size() - 1).getId() == data.get(data.size() - 1).getId())
                        noData = true;
                    else
                        list.addAll(data);
                else {
                    if (list != null) {
                        if (/*items.size() == data.size() && */
                                list.size() > 0 &&
                                        list.get(0).getId() == data.get(0).getId())
                            noData = false;
                    }
                    if (!noData) {
                        list = data;
                    }
                }
//                bInit = true;
                if (!noData)
                    notifyDataSetChanged();
            }
        }

        @Override
        public void setItemViewData(final BaseViewHodler hodler, final int position) {
            View bodyView = hodler.getView(R.id.replay_item_hor);
            CircleButton logo = hodler.getView(R.id.replay_user_logo);
            TextView nameText = hodler.getView(R.id.replay_user_name);
            TextView titleText = hodler.getView(R.id.replay_title_value);
            TextView timeText = hodler.getView(R.id.replay_time_value);
            TextView numberText = hodler.getView(R.id.replay_count_text);
            TextView praiseText = hodler.getView(R.id.comemndg_praise_txt);
            ImageView thumb = hodler.getView(R.id.replay_thumb);
            ImageView praiseBtn = hodler.getView(R.id.replay_praise_child_btn);
            ImageView replypub = hodler.getView(R.id.disclosure_replay_btn);
            replypub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    long tId = (long) view.getTag(R.id.tag_replay_cid);
//                    Intent intent = new Intent();
//                    intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_PARAM, mIndex);
//                    intent.putExtra("tid", tId);
//                    DefaultFragmentActivity.start(CommunityAct.this, CommendPubFragment.class.getName(), intent);
                    ReplyEntry object = (ReplyEntry) view.getTag(R.id.tag_replay_cid);
                    if (callback != null) {
                        callback.onLbtClick(IButtonClickType.COMMEND_CLICK, new IButtonClickData(view, object));
                    }
                }
            });
            praiseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReplyEntry object = (ReplyEntry) v.getTag(R.id.tag_replay_cid);
                    if (callback != null) {
                        callback.onLbtClick(IButtonClickType.PRAISE_CLICK, new IButtonClickData(v, object));
                    }
                }
            });
            LinearLayout extendlay = (LinearLayout) hodler.getView(R.id.replya_exlist_layout);
//            setCountText(itemCountText, position);
            if (list == null || list.isEmpty()) {
                bodyView.setVisibility(View.GONE);
                return;
            } else {
                bodyView.setVisibility(View.VISIBLE);
            }
            ReplyEntry info = list.get(position);
            if (info == null) return;
            Util.LoadImageErrorUrl(logo, info.getAuthor_avatar_url(), null, R.drawable.user_default_commend);
            praiseText.setText(StringUtil.getNumKString(info.getLike_count()));

            bodyView.setTag(R.id.tag_replay_cid, info);
            praiseBtn.setTag(R.id.tag_replay_cid, info);
            bodyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ReplyEntry entry = (ReplyEntry) v.getTag(R.id.tag_replay_item_cid);
//                    Intent intent = new Intent();
//                    intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_PARAM, entry.getId());
//                    intent.putExtra("tid", mIndex);
//                    intent.putExtra("obeject", entry);
//                    intent.putExtra("thumb", entry.getMthumImage());
//                    if (entry.getSub_reply_count() > 0)
//                        DefaultFragmentActivity.start(context, CmyReplyPageFragment.class.getName(), intent);
                    ReplyEntry object = (ReplyEntry) v.getTag(R.id.tag_replay_cid);
                    if (callback != null) {
                        callback.onLbtClick(IButtonClickType.ITEM_CLICK, new IButtonClickData(v, object));
                    }
                }
            });
            nameText.setText(info.getAuthor_nickname());
            String content = info.getContent();
            titleText.setText(content);
            replypub.setTag(R.id.tag_replay_cid, info);
            timeText.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", info.getPost_time() * 1000));
            if (info.getMthumImage() != null && !TextUtils.isEmpty(info.getMthumImage())) {
                thumb.setVisibility(View.VISIBLE);
                String path = info.getMthumImage();
//                path += "?w=" + 196 + "&h=" + 263 + "&s=2";
                path += "?w=" + 120 + "&h=" + 90 + "&s=1";
                Util.LoadThumebImage(thumb, info.getMthumImage(), null);
                thumb.setTag(R.id.tag_replay_thumb, info.getMthumImage());
            } else {
                thumb.setVisibility(View.GONE);
            }
            thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = (String) view.getTag(R.id.tag_replay_thumb);
                    if (path != null && !TextUtils.isEmpty(path)) {
                        OpenImageUtils.openImage(view.getContext(), path, 0);
                    }
                }
            });
//            List<ReplyEntry.RefRepliesBean> reppls = info.getRef_replies();
//            if (reppls != null && !reppls.isEmpty()) {
//                extendlay.setVisibility(View.VISIBLE);
//                if (extendlay.getChildCount() > 0) extendlay.removeAllViews();
//                extendlay.setVisibility(View.VISIBLE);
//                for (ReplyEntry.RefRepliesBean bean : reppls) {
//                    View view = mCommuntyDatailHelper.createSubReplay(bean.getAuthor_nickname(), bean.getContent());
//                    extendlay.addView(view);
//                }
//            } else {
//                extendlay.removeAllViews();
//            extendlay.setVisibility(View.GONE);
//            }
            long nSubCommendNum = info.getSub_reply_count();
            if (nSubCommendNum > 0) {
                numberText.setVisibility(View.VISIBLE);
                numberText.setText(nSubCommendNum + "回复");
//                numberText.setBackground(getResources().getDrawable(R.drawable.shape_news_commend_circle));
            } else {
                numberText.setVisibility(View.GONE);
                numberText.setText("回复");
//                numberText.setBackground(null);
            }
            View horItem = hodler.getView(R.id.replay_item_hor);
            horItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (reportPopWindow != null)
                        reportPopWindow.show(view);
                    return false;
                }
            });
        }
    }

}
