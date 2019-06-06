package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.fragment.AnswerInfoFragment;
import com.dfsx.ganzcms.app.fragment.CommendPubFragment;
import com.dfsx.ganzcms.app.fragment.ReportPopupWindow;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by heyang on 2016/11/24  使用
 */

public class QuestionInfoAct extends AbsVideoActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, View.OnTouchListener,
        GestureDetector.OnGestureListener, View.OnClickListener {
    private static final String TAG = "QuestionInfoAct";
    private Context context;
    //网络请求连接
    private VideoEnabledWebChromeClient webChromeClient;
    String mSource;
    String author;
    long mReplayCount = 0;
    View mGuanzhuView, mAskView, mAnwerView;
    private EditText mEditTxtContent;
    private WebView mWebView;
    private long mIndex = -1;
    private final int NETWORK_BUSY = 13;
    private final int LOAD_COMPLATE = 10;
    private CustomeProgressDialog progressDialog;
    private String mtitle, connectUrl, mContent;
    GestureDetector mGestureDetector;
    //网络请求连接
    private DisplayMetrics dm;
    private int meWebTYPE = 0;
    ImageView mPosterImagView, mPlayVide0btn;
    int nScreenheight = 0, nPixWidth;
    private ViewGroup mAnchor;
    View mTailRalayout, mWebLayout, mVideoLayout;
    private android.view.animation.Animation animation;
    ImageButton mReLoadBtn, mBackBtn;
    String mPosterPath, mVideoPath, mInfo, imgs;
    RelativeLayout mloadFailurelay;
    boolean isGetDateflag = false;
    PullToRefreshListView mPullRefreshList;
    ReplayAdapter mReplayAdapter = null;
    ListView mListView;
    boolean isComparehtml = false;
    private View rootView, porLayout, headLayout, commnedNoplayLayout;
    private FrameLayout fullScreenContainer;
    ReplyListManager replyListManager;
    TextView mQuestTitle;
    TopicalApi mTopicalApi = null;
    LinearLayout mBodyImgs;
    boolean isFavel = false;
    EditText mReplyContent;
    PopupWindow mMorePopupWindow;
    int mShowMorePopupWindowWidth, mShowMorePopupWindowHeight;
    SharePopupwindow sharePopupwindow;
    TopicalEntry gEnties = null;
    ReportPopupWindow reportPopWindow = null;
    private IsLoginCheck mloginCheck;
    private CommuntyDatailHelper mCommuntyDatailHelper;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (QuestionInfoAct.this == null)
                return false;
            if (message.what == NETWORK_BUSY) {
                UtilHelp.getConfirmDialog(QuestionInfoAct.this, "网络繁忙，是否重新加载数据....?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoadDataFormURl();
                    }
                }).show();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
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
                InputMethodManager imm = (InputMethodManager) QuestionInfoAct.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTxtContent.getWindowToken(), 0);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showNetError() {
        isGetDateflag = false;
        if (QuestionInfoAct.this != null) {
            AlertDialog adig = new AlertDialog.Builder(QuestionInfoAct.this).setTitle("提示").setMessage("网络繁忙，是否重新加载数据.....？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    LoadDataFormURl();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            }).create();
            adig.show();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void showWebviewData(TopicalEntry entities) {
        if (QuestionInfoAct.this != null) {
            if (!isComparehtml) {
                mListView.setVisibility(View.VISIBLE);
                isGetDateflag = false;
                mloadFailurelay.setVisibility(View.GONE);
//                mCommendNumberTx.setText("评论" + entities.getReply_count());
                mQuestTitle.setText(entities.getTitle());
                if (!("").equals(mVideoPath) && mVideoPath != null) {
                    mVideoLayout.setVisibility(View.VISIBLE);
                    mPlayVide0btn.setVisibility(View.VISIBLE);
                    Util.LoadThumebImage(mPosterImagView, mPosterPath, null);
                } else {
                    mVideoLayout.setVisibility(View.GONE);
                }
                if (mWebView != null) {
                    loadDataFromText();
                }
                List<Attachment> list = entities.getAttachmentInfos();
                if (list != null && !list.isEmpty() && mBodyImgs.getChildCount() == 0) {
                    mCommuntyDatailHelper.setMulitpImage(mBodyImgs, list, imgs);
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    }

    private void InitCustomVideoView() {
        Activity act = QuestionInfoAct.this;
        mAnchor = (ViewGroup) act.findViewById(R.id.videoSurfaceContainer);
        ViewGroup.LayoutParams lp = mAnchor.getLayoutParams();
        lp.height = nScreenheight;
        mAnchor.setLayoutParams(lp);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

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
        context = this;
        rootView = getLayoutInflater().inflate(R.layout.question_info_activity, null);
        setContentView(rootView);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIndex = bundle.getLong("tid");
            mtitle = bundle.getString("title");
            imgs = bundle.getString("imgs");
            mVideoPath = bundle.getString("videoUrl");
        }
        mCommuntyDatailHelper = new CommuntyDatailHelper(this);
        animation = AnimationUtils.loadAnimation(QuestionInfoAct.this, R.anim.nn);
        reportPopWindow = new ReportPopupWindow(QuestionInfoAct.this);
        mReplayAdapter = new ReplayAdapter();
        mTopicalApi = new TopicalApi(QuestionInfoAct.this);
        mloginCheck = new IsLoginCheck(this);
        //创建数据的管理器
        replyListManager = new ReplyListManager(QuestionInfoAct.this, 3 + "", mIndex);
        replyListManager.setCallback(new DataRequest.DataCallback<ArrayList<ReplyEntry>>() {
            @Override
            public void onSuccess(final boolean isAppend, ArrayList<ReplyEntry> data) {
                if (mPullRefreshList != null)
                    mPullRefreshList.onRefreshComplete();
                if (data == null) return;
                Observable.from(data)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<ReplyEntry, ReplyEntry>() {
                            @Override
                            public ReplyEntry call(ReplyEntry topicalEntry) {
                                try {
                                    String respone = mTopicalApi.getSyncAtthmentById(topicalEntry.getAttachments());
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
                                if (mReplayAdapter != null && data != null) {
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
//                mCommendNumberTx.setText("评论0");
                Log.e("MyDocxFragment", "get data fail");
            }
        });
        dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) QuestionInfoAct.this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        nPixWidth = dm.widthPixels;
        nScreenheight = ((dm.widthPixels - 20) * 3) / 4;
        initView();
        initData();
    }

    public void initView() {
        porLayout = findViewById(R.id.poriant_layout);
        fullScreenContainer = (FrameLayout) findViewById(R.id.full_screen_video_container);
        mBackBtn = (ImageButton) findViewById(R.id.news_image_news_back_btn);
//        mReportBtn = (ImageButton) findViewById(R.id.news_report_btn);
//        mReportBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                IntentUtil.goReport(view.getContext());  //跳往登陆界面
//            }
//        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditTxtContent != null) {
                    InputMethodManager imm = (InputMethodManager) QuestionInfoAct.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditTxtContent.getWindowToken(), 0);
                }
                finish();
            }
        });
        mPullRefreshList = (PullToRefreshListView) findViewById(R.id.relati_list);
        mPullRefreshList.setAdapter(mReplayAdapter);
        mPullRefreshList.setOnRefreshListener(this);
        mPullRefreshList.setMode(PullToRefreshBase.Mode.DISABLED);
        mListView = (ListView) mPullRefreshList.getRefreshableView();
        headLayout = LayoutInflater.from(QuestionInfoAct.this).inflate(R.layout.question_info_header, null);
        mListView.addHeaderView(headLayout);
        commnedNoplayLayout = (View) headLayout.findViewById(R.id.comnend_noplay_layout);
        mloadFailurelay = (RelativeLayout) findViewById(R.id.load__news_fail_layout);
        mReLoadBtn = (ImageButton) findViewById(R.id.reload_news_btn);
        mReLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadDataFormURl();
            }
        });
//        mAttenttx = (ImageView) headLayout.findViewById(R.id.common_guanzhu_tx);
//        mAttenttx.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_click));
//                mTopicalApi.attentionAuthor(mAver_id, attonflag, new DataRequest.DataCallback() {
//                    @Override
//                    public void onSuccess(boolean isAppend, Object data) {
//                        Observable.just((Boolean) data).
//                                subscribeOn(Schedulers.io()).
//                                observeOn(Schedulers.io()).
//                                map(new Func1<Boolean, Integer>() {
//                                    @Override
//                                    public Integer call(Boolean aBoolean) {
//                                        int res = mTopicalApi.isAttentionOther(mAver_id);
//                                        return res;
//                                    }
//                                })
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new Observer<Integer>() {
//                                               @Override
//                                               public void onCompleted() {
//                                                   RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
//                                                   boolean flag = attonflag == 1 ? true : false;
//                                                   RXBusUtil.sendConcernChangeMessage(flag, 1);
//                                               }
//
//                                               @Override
//                                               public void onError(Throwable e) {
//
//                                               }
//
//                                               @Override
//                                               public void onNext(Integer result) {
//                                                   RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
//                                                   attonflag = result;
////                                                   setAttteonNewStatus(attonflag, mAttenttx, true);
//                                               }
//                                           }
//                                );
//                    }
//
//                    @Override
//                    public void onFail(ApiException e) {
//                        showNetError();
//                    }
//                });
//            }
//        });
        mPosterImagView = (ImageView) headLayout.findViewById(R.id.psoter_imagveo);
        mEditTxtContent = (EditText) findViewById(R.id.socitynews_comment_edit);
        mReplyContent = (EditText) findViewById(R.id.commentEdit_replay_edt);
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
        mQuestTitle = (TextView) headLayout.findViewById(R.id.question_title);
        mBodyImgs = (LinearLayout) headLayout.findViewById(R.id.commomtiy_imgs);
        mWebView = (WebView) headLayout.findViewById(R.id.webView_content);
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mTailRalayout = (View) findViewById(R.id.comnend_noplay_layout);
        mWebLayout = (View) findViewById(R.id.news_detail_web);
        mVideoLayout = (View) findViewById(R.id.news_detail_centernss);
        mGestureDetector = new GestureDetector(QuestionInfoAct.this, (GestureDetector.OnGestureListener) this);
        LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.newsdtauel_smain);
        relativeLayout.setOnTouchListener(this);   //设置  mGestureDetector  Ontouche
        relativeLayout.setLongClickable(true);
        mGuanzhuView = (View) headLayout.findViewById(R.id.qinfo_attetn_view);
        mGuanzhuView.setOnClickListener(this);
        mAskView = (View) headLayout.findViewById(R.id.qinfo_attetn_view);
        mAskView.setOnClickListener(this);
        mAnwerView = (View) headLayout.findViewById(R.id.qinfo_attetn_view);
        mAnwerView.setOnClickListener(this);
        InitCustomVideoView();
        initWebView();
    }

    public void initData() {
        LoadDataFormURl();
        if (mReplayAdapter != null) {
            replyListManager.start(mIndex, 1, false);
        }
    }

    public void setAttteonNewStatus(int falg, ImageView view) {
        if (falg == 1) {
            view.setImageResource(R.drawable.commuity_att_selected);
        } else {
            view.setImageResource(R.drawable.commuity_att_normal);
        }
    }

    private void share(ShareContent shareContent) {
        ShareUtil.share(context, shareContent);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!("").equals(mPosterPath) && !("").equals(mVideoPath)) {
            mVideoLayout.setVisibility(View.VISIBLE);
            mPlayVide0btn.setVisibility(View.VISIBLE);
            mPosterImagView.setVisibility(View.VISIBLE);
            UtilHelp.LoadImageFormUrl(mPosterImagView, mPosterPath, null);
        }
        if (mReplayAdapter != null) {
            replyListManager.start(mIndex, 1, false);
        }
    }

    public void LoadDataFormURl() {
        if (!isGetDateflag) {
            isGetDateflag = true;
            mloadFailurelay.setVisibility(View.GONE);
            if (!isComparehtml)
                progressDialog = CustomeProgressDialog.show(QuestionInfoAct.this, "正在加载中...");
            getDataMainThread();
        }
    }

    private void showMore(View moreBtnView) {
        if (mMorePopupWindow == null) {
            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = li.inflate(R.layout.layout_more, null, false);
            mMorePopupWindow = new PopupWindow(content, WRAP_CONTENT,
                    WRAP_CONTENT);
            mMorePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mMorePopupWindow.setOutsideTouchable(true);
            mMorePopupWindow.setTouchable(true);
            content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mShowMorePopupWindowWidth = UtilHelp.getScreenWidth(this);
            mShowMorePopupWindowHeight = content.getMeasuredHeight();
            View parent = mMorePopupWindow.getContentView();
            mReplyContent = (EditText) parent.findViewById(R.id.commentEdit_replay_edt);
//            TextView comment = (TextView) parent.findViewById(R.id.comment);
// 点赞的监听器
//            comment.setOnClickListener(this);
        }
        if (mMorePopupWindow.isShowing()) {
            mMorePopupWindow.dismiss();
        } else {
            int heightMoreBtnView = moreBtnView.getHeight();
            mMorePopupWindow.showAsDropDown(moreBtnView, mShowMorePopupWindowWidth,
                    120, Gravity.BOTTOM);
            onFocusChange(true, mReplyContent);
        }
    }

    /*
     * 显示或隐藏输入法
     */
    private void onFocusChange(boolean hasFocus, final EditText edt) {
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(INPUT_METHOD_SERVICE);
                if (isFocus) {
                    // 显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    // 隐藏输入法
                    imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    public void getDataMainThread() {
        String url = App.getInstance().getmSession().getCommunityServerUrl() +
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
                    public void onSuccess(final boolean isAppend, final TopicalEntry data) {
                        if (data == null) return;
                        Observable.just(data).
                                subscribeOn(Schedulers.io()).
                                observeOn(Schedulers.io()).
                                flatMap(new Func1<TopicalEntry, Observable<TopicalEntry>>() {
                                    @Override
                                    public Observable<TopicalEntry> call(TopicalEntry entry) {
                                        TopicalEntry p = mTopicalApi.getTopicTopicalInfo(entry);
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
                                                   gEnties = att;
                                                   isFavel = att.isFavl();
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
            mReplayCount = jsonObject.optLong("reply_count");
            author = jsonObject.optString("author_name");
            long time = jsonObject.optLong("post_time");
            if (time > 0) {
                mSource = Util.timestampToString(time * 1000, "yyyy-MM-dd");
            } else {
                mSource = "";
            }
            String txtWeb = "<html>\n" +
                    "<meta name=\"viewport\" content=\"width=device-width\"/>\n" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"main.css\" />\n" +
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
        LinearLayout mLayout = new LinearLayout(QuestionInfoAct.this);
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
            params.topMargin = Util.dp2px(QuestionInfoAct.this, 8);
        mLayout.setLayoutParams(params);
        return mLayout;
    }


    @Override
    public void onBackPressed() {
//        int  a=0;
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
//        if(meWebTYPE==VIDEOENABLEWEB_TYPE)
//        {
        if (!webChromeClient.onBackPressed()) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                // Standard back button implementation (for example this could close the app)
                super.onBackPressed();
            }
        }
//        }
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

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//                    cleareWebView();
//                    if (nFlags == Intent.FLAG_ACTIVITY_NEW_TASK) {
//                        Intent intent = new Intent();
//                        intent.setClass(NewsDetailVideoActivity.this, ColumnsActivity.class);
//                        startActivity(intent);
//                    }
                    finish();
                }
                return false;
            }
        });

        webSettings.setJavaScriptEnabled(true);

        //这方法可以让你的页面适应手机屏幕的分辨率，完整的显示在屏幕上，可以放大缩小(推荐使用)。
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);

        // 添加js交互接口类，并起别名 imagelistner
        mWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");

//        webSettings.setDisplayZoomControls(false);

//        mScrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return mGestureDetector.onTouchEvent(motionEvent);
//            }
//        });

        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });

        //2016-1-28  heyag
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });

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
        mWebView.loadDataWithBaseURL("file:///android_asset/", connectUrl, "text/html", "utf-8", null);
//        Log.e("TAG", "html == " + connectUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) return false;
        try {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            //限制必须得划过屏幕的1/4才能算划过
            float x_limit = dm.widthPixels / 4;
            float y_limit = dm.heightPixels / 4;
            float x_abs = Math.abs(x);
            float y_abs = Math.abs(y);
            if (x_abs >= y_abs) {
                //gesture left or right
                if (x > x_limit || x < -x_limit) {
                    if (x > 0) {
//                        if (nFlags == Intent.FLAG_ACTIVITY_NEW_TASK) {
//                            Intent intent = new Intent();
//                            intent.setClass(NewsDetailVideoActivity.this, ColumnsActivity.class);
//                            startActivity(intent);
//                        }
                        finish();
                        //right
                        //doResult(GESTURE_RIGHT);
                    } else if (x <= 0) {
//                        Intent intent = new Intent();
//                        Bundle bundle = new Bundle();
//                        bundle.putInt("commentsId", mIndex);
//                        intent.putExtras(bundle);
//                        intent.setClass(NewsDetailVideoActivity.this, CommentsActivity.class);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                        //left
//                        //doResult(GESTURE_LEFT);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
//            Toast.makeText(this, "向左手势"
//                    , Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putInt("commentsId", mIndex);
//            intent.putExtras(bundle);
//            intent.setClass(NewsDetailActivity.this, CommentsActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//        }
//
//        else if(e2.getX() - e1.getX() > verticalMinDistance && Math.abs (velocityX) > minVelocity)
//        {
//             NewsDetailActivity.this.finish();
//            Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
//        }
        return false;

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return mGestureDetector.onTouchEvent(motionEvent);
//        return false;
    }

    @Override
    public void onClick(View view) {
        if (view == mGuanzhuView) {
            WhiteTopBarActivity.startAct(QuestionInfoAct.this, AnswerInfoFragment.class.getName(), "一共30个回答");
        } else if (view == mAskView) {

        } else if (view == mAnwerView) {

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
        if (imgs != null) {
            String arr[] = imgs.split(",");
            if (arr != null && arr.length > 0) {
                content.thumb = arr[0];
            }
        }
        content.type = ShareContent.UrlType.WebPage;
        content.url = App.getInstance().getCommuityShareUrl() + mIndex;
        AbsShare share = ShareFactory.createShare(QuestionInfoAct.this, platform);
        share.share(content);
    }

    public class ReplayAdapter extends BaseAdapter {
        private ArrayList<ReplyEntry> list;

        public ReplayAdapter() {
        }

        public ReplayAdapter(ArrayList<ReplyEntry> list) {
            this.list = list;
        }

        public void update(ArrayList<ReplyEntry> data, boolean isAdd) {
            if (list == null || !isAdd) {
                list = data;
            } else {
                list.addAll(data);
            }

            notifyDataSetChanged();
        }

        public ArrayList<ReplyEntry> getData() {
            return list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BaseViewHodler hodler = BaseViewHodler.get(convertView, parent,
                    R.layout.community_replay_item, position);

            setViewData(hodler, position);
            return hodler.getConvertView();
        }

        public void setViewData(BaseViewHodler hodler, int position) {
            CircleButton logo = hodler.getView(R.id.replay_user_logo);
            TextView nameText = hodler.getView(R.id.replay_user_name);
            TextView titleText = hodler.getView(R.id.replay_title_value);
            TextView timeText = hodler.getView(R.id.disclosure_list_time);
            TextView numberText = hodler.getView(R.id.replay_count_text);
            ImageView thumb = hodler.getView(R.id.replay_thumb);
            ImageButton replypub = hodler.getView(R.id.disclosure_replay_btn);
            replypub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    commentLinear.setVisibility(View.VISIBLE);
                    long tId = (long) view.getTag(R.id.tag_replay_cid);
                    Intent intent = new Intent();
                    intent.putExtra("id", mIndex);
                    intent.putExtra("tid", tId);
                    DefaultFragmentActivity.start(QuestionInfoAct.this, CommendPubFragment.class.getName(), intent);
//                    showMore(view);
                    //    onFocusChange(true);
                }
            });
            LinearLayout extendlay = (LinearLayout) hodler.getView(R.id.replya_exlist_layout);
//            setCountText(itemCountText, position);
            ReplyEntry info = list.get(position);
            GlideImgManager.getInstance().showImg(context, logo, info.getAuthor_avatar_url());
            nameText.setText(info.getAuthor_name());
            titleText.setText(info.getContent());
            replypub.setTag(R.id.tag_replay_cid, info.getId());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//制定日期的显示格式
            String time = sdf.format(new Date(info.getPost_time() * 1000));
            timeText.setText(time);
            if (info.getMthumImage() != null && !TextUtils.isEmpty(info.getMthumImage())) {
                thumb.setVisibility(View.VISIBLE);
                String path = info.getMthumImage();
                path += "?w=" + 196 + "&h=" + 263 + "&s=2";
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
            List<ReplyEntry.RefRepliesBean> reppls = info.getRef_replies();
            if (reppls != null && !reppls.isEmpty()) {
                if (extendlay.getChildCount() > 0) extendlay.removeAllViews();
                extendlay.setVisibility(View.VISIBLE);
                for (ReplyEntry.RefRepliesBean bean : reppls) {
                    View view = createChildReplay(bean.getAuthor_name(), info.getAuthor_name(), bean.getContent());
                    extendlay.addView(view);
                }
            }
//            numberText.setText(info.getre + "回复");
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

    public View createChildReplay(String replyNickName, String commentNickName, String replyContentStr) {
        LinearLayout view = new LinearLayout(this);
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        view.setLayoutParams(lp);
        TextView txt = new TextView(this);
        txt.setTextSize(12);
        txt.setMaxLines(2);
        txt.setEllipsize(TextUtils.TruncateAt.END);
        SpannableString ss = null;
        if (replyNickName != null && !TextUtils.isEmpty(commentNickName)) {
//            if (!TextUtils.equals(replyNickName, commentNickName)) {
            ss = new SpannableString(replyNickName + "回复" + commentNickName + ":" + replyContentStr);
//            } else {
//                ss = new SpannableString(replyNickName + "回复" + ":" + replyContentStr);
//            }
        } else {
//            if (!TextUtils.equals(replyNickName, commentNickName)) {
            ss = new SpannableString(replyNickName + "评论" + commentNickName + ":" + replyContentStr);
//            } else {
//                ss = new SpannableString(replyNickName + "评论" + ":" + replyContentStr);
//            }
        }
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#7888a9")), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//        ss.setSpan(new ForegroundColorSpan(Color.BLUE), 0, replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new ForegroundColorSpan(Color.BLUE), replyNickName.length() + 2, replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txt.setText(ss);
        view.addView(txt);
        return view;
    }


}
