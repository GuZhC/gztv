package com.dfsx.ganzcms.app.act;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.model.ContentCmsInfoEntry;
import com.dfsx.ganzcms.app.model.SocirtyNewsChannel;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.HtmlEntities;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareUtil;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * Created by heyang on 2015-03-26.
 */

public class NewsDetailVideoActivity extends AbsVideoActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, View.OnTouchListener,
        GestureDetector.OnGestureListener, View.OnClickListener {
    private final String NewsDetailVideoActivity = "NewsDetailVideoActivity";

    private Context context;
    private TextView mTxtShowCommendNumber;
    private TextView mTxtShowTitle;
    private EditText mEditTxtContent;
    private ImageButton mBackBtn;
    private WebView mWebView;
    private ImageButton mShareBtn;
    private ImageButton mFavorityBtn;
    private TextView mCommentBtn;
    private ProgressBar mProcessBar;
    private boolean isComplate;
    private boolean bIsExit;
    private int mIndex = -1;
    private final int NETWORK_BUSY = 13;
    private final int GETURL_FROMNET = 7;
    private final int LOAD_COMPLATE = 10;
    private final int NO_LOGON = 17;
    private final int TOAST_MSG = 22;
    private final int UPDATETHUMB_UP_MSG = 0x000078;   //更新点赞数
    private final int OBTAIN_SINGLEAD_MSG = 0x000034;   //更新点赞数
    private final int OBTAIN_ADWAREVIDEO_MSG = 0x000036;   //更新点赞数
    private final int OBTAIN_PAUSEIMGEO_MSG = 0x000038;   //更新点赞数
    private CustomeProgressDialog progressDialog;
    private String urlPath;
    private String connectUrl;
    private String mthumb;
    private String mtitle;
    private Point prev;

    GestureDetector mGestureDetector;
    private int verticalMinDistance = 100;
    private int minVelocity = 100;
    //网络请求连接
    private String postGetUrl;
    int mColumnTy;
    private ViewPager mViewPager;
    //    private Account mUser;
    private String mStrresult;
    private boolean isComemndComplate = true;   //评论是否提交完成
    private DisplayMetrics dm;
    private boolean isSuccess;
    private int commendNumber;

    private static final int HTMLWEB_TYPE = 1;
    private static final int VIDEOENABLEWEB_TYPE = 0;
    private static final int UPDATE_THUMB_NUMBER = 0x000067;
    private static final int GET_DATE_FAILURE = 0x000047;   //获取网页数据失败
    private int meWebTYPE = 0;
    RelativeLayout container;
    TextView mTitleTxtvw;

    private VideoEnabledWebChromeClient webChromeClient;
    private RelativeLayout mBottomLayout;
    private RelativeLayout mTopLayout;
    String mContent;
    ContentCmsApi mContentCmsApi = null;

    int nScreenheight = 0;
    //    private  VlcVideoView mVlcView;
//    private  CustomVideoView mCustomView;
//    private  BaseVideoView mVideoView;
//    private  BaseVideoController mediaController;
    private ViewGroup mAnchor;
    private boolean mIsUseVlc = false;
    private boolean mVlcSurface = false;
    private ImageView mPosterImagView;
    ImageView mPlayVide0btn;

    RelativeLayout mHeandRalayout;
    RelativeLayout mTailRalayout;
    RelativeLayout mTitleLayout;
    RelativeLayout mWebLayout;
    RelativeLayout mVideoLayout;
    TextView mThumbupNumbertv;
    private android.view.animation.Animation animation;
    TextView mThumbShoutv;
    ImageButton mThunmbUpBtn;
    boolean isObzavr = false;
    ImageView mAppclientbtn;
    ImageView mAppWeiboBtn;
    String appSites = "http://www.wsrtv.com.cn/sites/default/files/wsrtv-app_0_0.png,http://www.wsrtv.com.cn/sites/default/files/wsrtv-weixin_0.png";

    TextView mSourceTxtvw;
    String mPosterPath;
    String mVideoPath;
    String mSource;
    String mInfo;
    RelativeLayout mloadFailurelay;
    ImageButton mReLoadBtn;
    RelativeLayout mMasklayout;
    boolean isGetDateflag = false;
    PullToRefreshListView mPullRefreshList;
    ListAdapter adapter = null;
    TextView mBottomtitle;
    ImageView mBottomAdImg;
    RelativeLayout mBottomRelatlout;
    String[] videoUrls = {""};
    String[] durations = {"0"};
    String[] mPauseImags = {""};

    private Context mContext;
    ListView mListView;
    int nFlags;
    //    HtmlDbApi mHtmlApi = null;
    boolean isComparehtml = false;
    String mHtmlContent;

    private View porLayout;
    private FrameLayout fullScreenContainer;
    private FrameLayout heardView;
    private View headLayout;

    private NewsDatailHelper newsDatailHelper;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (NewsDetailVideoActivity.this == null)
                return false;
            if (message.what == NETWORK_BUSY) {
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
                if (NewsDetailVideoActivity.this != null) {
                    AlertDialog adig = new AlertDialog.Builder(NewsDetailVideoActivity.this).setTitle("提示").setMessage("网络繁忙，是否重新加载数据.....？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            LoadDataFormURl();
                            ;
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        }
                    }).create();
                    adig.show();
                }
            }
            if (message.what == LOAD_COMPLATE) {
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
                if (NewsDetailVideoActivity.this != null) {
//                    mMasklayout.setVisibility(View.VISIBLE);
                }
            }
            if (message.what == UPDATETHUMB_UP_MSG) {
                int number = (Integer) message.obj;
                if (number > 0) {
                    mThumbupNumbertv.setVisibility(View.VISIBLE);
                }
                mThumbupNumbertv.setText(number + "");
            }
            if (message.what == UPDATE_THUMB_NUMBER) {
                isObzavr = false;
                int num = Integer.parseInt(mThumbupNumbertv.getText().toString().trim());
                int nCount = (Integer) message.obj;
                if (num != nCount) {
                    mThumbupNumbertv.setVisibility(View.VISIBLE);
                    mThumbShoutv.setVisibility(View.VISIBLE);
                    mThumbShoutv.startAnimation(animation);
                    mThumbupNumbertv.setText(nCount + "");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mThumbShoutv.setVisibility(View.GONE);
                        }
                    }, 1000);
                }
            }
            if (message.what == NO_LOGON) {
                if (NewsDetailVideoActivity.this != null) {
                    AlertDialog adig = new AlertDialog.Builder(NewsDetailVideoActivity.this).setTitle("提示").setMessage("未登录，是否现在登录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            Intent intent = new Intent();
//                            intent.setClass(NewsDetailVideoActivity.this, LoginActivity.class);
//                            startActivity(intent);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        }
                    }).create();
                    adig.show();
                }
            }
            if (message.what == TOAST_MSG) {
                if (NewsDetailVideoActivity.this != null) {
                    isComemndComplate = true;
                    mEditTxtContent.setText("");
                    Toast.makeText(NewsDetailVideoActivity.this, message.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                    if (isSuccess) {
                        isSuccess = false;
                        mEditTxtContent.setHint("你来说几句");
                        commendNumber++;
                        Intent intent = getIntent();
                        intent.putExtra("cid", commendNumber);
                        Bundle bn = intent.getExtras();
                        int nn = bn.getInt("commenNumber");
                        bn.putInt("commenNumber", commendNumber);
//                    intent.putExtra("numId",commendNumber);
                        setResult(-1, intent);
                        mTxtShowCommendNumber.setText(commendNumber + "跟帖");
                    }
                }
            }
            if (message.what == GET_DATE_FAILURE) {
                if (NewsDetailVideoActivity.this != null) {
                    isGetDateflag = false;
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    mloadFailurelay.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
            }
            if (message.what == OBTAIN_SINGLEAD_MSG) {
                String[] mp = message.obj.toString().trim().split(",");
                mBottomtitle.setText(mp[1].toString());
//                UtilHelp.LoadImageFormUrl(mBottomAdImg, mp[0].toString().trim(), null);
            } else if (message.what == OBTAIN_ADWAREVIDEO_MSG) {
                if (message.obj != null) {
                    adsInfo af = (adsInfo) message.obj;
                    videoUrls = af.urls.toString().trim().split(",");
                    durations = af.durations.toString().trim().split(",");
                }
            } else if (message.what == OBTAIN_PAUSEIMGEO_MSG) {
                if (message.obj.toString().trim() != null) {
                    mPauseImags = message.obj.toString().trim().split(",");
                }
            }
            return false;
        }
    });

    public static JSONObject getJsonObject(String key, Object value) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (key != "" && value != "")
                jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void WriteFleThread(final String filename, final String context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    FileUtils.saveToSDCard(filename, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //do something...
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
//                if(mCustomView.isPlaying())
//                {
//                    mCustomView.stopPlayback();
//                }
//                if(nFlags==Intent.FLAG_ACTIVITY_NEW_TASK)
//                {
//                    Intent  intent=new  Intent();
//                    intent.setClass(NewsDetailVideoActivity.this,ColumnsActivity.class);
//                    startActivity(intent);
//                }
                InputMethodManager imm = (InputMethodManager) NewsDetailVideoActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTxtContent.getWindowToken(), 0);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showNetError() {
        isGetDateflag = false;
        if (NewsDetailVideoActivity.this != null) {
            AlertDialog adig = new AlertDialog.Builder(NewsDetailVideoActivity.this).setTitle("提示").setMessage("网络繁忙，是否重新加载数据.....？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    LoadDataFormURl();
                    ;
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

    private void showWebviewData(ContentCmsInfoEntry webData) {
        if (NewsDetailVideoActivity.this != null) {
            if (!isComparehtml) {
                mListView.setVisibility(View.VISIBLE);
                //     getThumbNumber();
                isGetDateflag = false;
                mloadFailurelay.setVisibility(View.GONE);
                mTitleTxtvw.setText(webData.getTitle());
//                mSourceTxtvw.setText(webData.getSource());
                mSourceTxtvw.setText(UtilHelp.getTimeString("yyyy-MM-dd", webData.getPublish_time()));
                mVideoPath = webData.getUrl();
                if (!("").equals(mVideoPath) && mVideoPath != null) {
                    mVideoLayout.setVisibility(View.VISIBLE);
                    mPlayVide0btn.setVisibility(View.VISIBLE);
                    Util.LoadThumebImage(mPosterImagView, mPosterPath, null);
                } else {
                    mVideoLayout.setVisibility(View.GONE);
                }
                connectUrl = getHtmlWeb(webData.getBody());
//                if (bundel.getParcelableArrayList("relatiNodes") != null) {
////                        String[] list = bundel.getString("relatiNodes").toString().trim().split(",");
//                    ArrayList<SocirtyNewsChannel> list = bundel.getParcelableArrayList("relatiNodes");
//                    if (list != null && list.size() > 0) {
////                            mBottomLayout.setVisibility(View.VISIBLE);
//                        adapter.update(list, false);
//                    }
//                } else {
//                    mBottomLayout.setVisibility(View.GONE);
//                }
                if (mWebView != null) {
                    loadDataFromText();
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    }

    private void InitCustomVideoView() {
        Activity act = NewsDetailVideoActivity.this;
        mContext = act;
//        act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        act.getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        mCustomView = (CustomVideoView) act.findViewById(R.id.live_detail_video_player);
//        mVlcView = (VlcVideoView) act.findViewById(R.id.Vlc_live_detail__video_player);
        mAnchor = (ViewGroup) act.findViewById(R.id.videoSurfaceContainer);
//        mCustomView.setVideoViewStateListener(this);
//        mCustomView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_BACK)
//                {
//                    Intent  intent=new Intent();
//                    intent.setClass(NewsDetailVideoActivity.this,NewsDetailVideoActivity.class);
//                    startActivity(intent);
//                }
//                return false;
//            }
//        });

        ViewGroup.LayoutParams lp = mAnchor.getLayoutParams();
        lp.height = nScreenheight;
        mAnchor.setLayoutParams(lp);

//        if (mIsUseVlc && (App.getInstance().getLibVLC() != null)) {
//            mVlcView.InitMediaPlayer();
//            mVlcView.InitVideoView();
//            mVideoView = mVlcView;
//            mCustomView.setVisibility(View.GONE);
//            mVideoView.setVisibility(View.VISIBLE);
//            mediaController = new VlcMediaController(mContext);
//            mediaController.setAnchorView(mAnchor);
//            mediaController.setMediaPlayer((BaseVideoController.MediaPlayerControl) mVideoView);
//            mVideoView.setMediaController(mediaController);
//            mVideoView.setDefaultMediaController(mediaController);
//            mVideoView.requestFocus();
//            mVlcSurface = true;
//        } else {
//            mVideoView = mCustomView;
////            mVlcView.setVisibility(View.GONE);
//            mVideoView.setVisibility(View.VISIBLE);
//            mCustomView.InitMediaPlayer();
//            mediaController = new CustomMediaController(act);
//            //mVideoView.InitMediaPlayer();
//            mediaController.setAnchorView(mAnchor);
//            mediaController.setMediaPlayer((BaseVideoController.MediaPlayerControl) mVideoView);
//            mVideoView.setMediaController(mediaController);
//            mVideoView.setDefaultMediaController(mediaController);
//            mVideoView.requestFocus();
//            final String cpuType = CpuInfos.getCpuString();
//            final String vlcUrl = "http://www.dfsxcms.cn:8001/files/" + cpuType + ".zip";
//            // final  String vlclibStorePath = "/data/data/" + mContext.getPackageName() +"/" + cpuType;
//
//            View.OnClickListener downLoadListener = new View.OnClickListener() {
//                public void onClick(View v) {
////                    InstallVlcDependencies.checkForDialog((FragmentActivity) mContext, vlcUrl);
////                     SwitchToVlcVideView();
//                }
//
//            };
//            ((CustomMediaController) mediaController).setOnDownLoadListener(downLoadListener);
//        }
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
        newsDatailHelper = new NewsDatailHelper(context);
        setContentView(R.layout.news_detail_video_activity);
        animation = AnimationUtils.loadAnimation(NewsDetailVideoActivity.this, R.anim.tbhumb_aimal);
        porLayout = findViewById(R.id.poriant_layout);
        heardView = (FrameLayout) findViewById(R.id.heard_layout);
        fullScreenContainer = (FrameLayout) findViewById(R.id.full_screen_video_container);
        mTxtShowTitle = (TextView) findViewById(R.id.newsDetail_title);
        mTxtShowCommendNumber = (TextView) findViewById(R.id.news_list_item_show);
        //       mTxtShowCommendNumber.setVisibility(4);
        mBackBtn = (ImageButton) findViewById(R.id.news_image_news_back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                bIsExit = true;
//                if(nFlags==Intent.FLAG_ACTIVITY_NEW_TASK)
//                {
//                    Intent  intent=new  Intent();
//                    intent.setClass(NewsDetailVideoActivity.this,ColumnsActivity.class);
//                    startActivity(intent);
//                }
                InputMethodManager imm = (InputMethodManager) NewsDetailVideoActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTxtContent.getWindowToken(), 0);
                finish();
            }
        });
        mPullRefreshList = (PullToRefreshListView) findViewById(R.id.relati_list);
        adapter = new ListAdapter(NewsDetailVideoActivity.this);
        mPullRefreshList.setAdapter(adapter);
        mPullRefreshList.setOnRefreshListener(this);
        mPullRefreshList.setMode(PullToRefreshBase.Mode.DISABLED);
        mListView = (ListView) mPullRefreshList.getRefreshableView();
//        mListView.addHeaderView(LayoutInflater.from(NewsDetailVideoActivity.this).inflate(R.layout.news_newsdetail_header, null));
        headLayout = LayoutInflater.from(NewsDetailVideoActivity.this).inflate(R.layout.news_newsdetail_header, null);
//        heardView.addView(headLayout);
        mListView.addHeaderView(headLayout);
        mBottomtitle = (TextView) headLayout.findViewById(R.id.bottom_ad_title_tx);
        mBottomAdImg = (ImageView) headLayout.findViewById(R.id.bottom_ad_img);
        mBottomRelatlout = (RelativeLayout) headLayout.findViewById(R.id.bottom_relation_layout);
        mShareBtn = (ImageButton) findViewById(R.id.socitiynews_comment_share);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareContent info = new ShareContent();
                info.title = mtitle;
                info.type = ShareContent.UrlType.WebPage;
                String server = App.getInstance().getBaseUrl();
                info.thumb = mthumb;
                info.url = server;
                info.disc = mInfo;
                share(info);
            }
        });
        mBottomLayout = (RelativeLayout) headLayout.findViewById(R.id.bottom_relation_layout);
        mMasklayout = (RelativeLayout) headLayout.findViewById(R.id.masg_layout);
        mloadFailurelay = (RelativeLayout) findViewById(R.id.load__news_fail_layout);
        mReLoadBtn = (ImageButton) findViewById(R.id.reload_news_btn);
        mReLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadDataFormURl();
            }
        });
        mAppclientbtn = (ImageView) headLayout.findViewById(R.id.app_client_btn);
        mAppclientbtn.setOnClickListener(this);
        mAppWeiboBtn = (ImageView) headLayout.findViewById(R.id.app_wibo_btn);
        mAppWeiboBtn.setOnClickListener(this);
        mSourceTxtvw = (TextView) headLayout.findViewById(R.id.newsDetail_source);
        mTitleTxtvw = (TextView) headLayout.findViewById(R.id.newsDetailss_title);
        mPosterImagView = (ImageView) headLayout.findViewById(R.id.psoter_imagveo);
        mEditTxtContent = (EditText) findViewById(R.id.socitynews_comment_edit);
        //    mEditTxtContent.setVisibility(4);
        mCommentBtn = (TextView) findViewById(R.id.socitiynews_comment_collect);
        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilHelp.COMMANDE_IS_OPEN) {
                    String txt = mEditTxtContent.getText().toString().trim();
                    if (isComemndComplate && !txt.equals("")) {
                        isSuccess = false;
                        isComemndComplate = false;
                        postCommendContent();
                    }
                } else {
                    Toast.makeText(view.getContext(), R.string.commend_show_text, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mFavorityBtn = (ImageButton) findViewById(R.id.socitiynews_comment_favioty);
        mFavorityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavritory();
            }
        });
        mPosterPath = "";
        mVideoPath = "";
        mPlayVide0btn = (ImageView) headLayout.findViewById(R.id.player_imagveo);
        mPlayVide0btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!("").equals(mVideoPath) && mVideoPath != null) {
                    try {
//                        int index = App.getInstance().getVideoIndex();
//                        if (index >= videoUrls.length) {
//                            index = 0;
//                        }
                        Uri uril = Uri.parse(mVideoPath);
//                        List<Uri> playList = new ArrayList<Uri>();
//                        playList.add(uril);
////                    Uri adsuril = Uri.parse("http://www.wsrtv.com.cn/sites/default/files/jim_parsons_plays_flight_attendant_intel.mp4");
//                        if (!("").equals(videoUrls[index].toString().trim())) {
//                            Uri adsuril = Uri.parse(videoUrls[index]);
//                            playList.add(adsuril);
//                        }
//                        String ds = durations[index].trim();
//                        int adDuration = Integer.parseInt(ds);
//                        mCustomView.setDuration(adDuration);
//                        mCustomView.setVideoURI(playList, 0, false);
                        videoPlayer.start(mVideoPath);
                        videoPlayer.setCompletionListener(new VideoPlayView.CompletionListener() {
                            @Override
                            public void completion(IMediaPlayer mp) {

                            }
                        });
//                        if (mPauseImags.length > 0) {
//                            if (!("").equals(mPauseImags[0]))
//                                mCustomView.setAdvertisePics(mPauseImags);
//                        }
//                        App.getInstance().setVideoIndex(index + 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //  mCustomView.setVideoURI(uril);
                    //mCustomView.startPlayBack();
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
        mThumbShoutv = (TextView) findViewById(R.id.thumbup_number_agin_txt);
        mThumbupNumbertv = (TextView) findViewById(R.id.thumbup_number_txt);
        mThunmbUpBtn = (ImageButton) findViewById(R.id.socitiynews_thumbup_btn);
        mThunmbUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mThumbShoutv.setVisibility(View.VISIBLE);
//                mThumbShoutv.startAnimation(animation);
//                int  num=Integer.parseInt(mThumbupNumbertv.getText().toString().trim());
//                num++;
//                mThumbupNumbertv.setText(num+"");
                if (isObzavr) return;
                isObzavr = true;
                postCommintthumbNumber();
            }
        });
//        mScrollView=(ScrollView)findViewById(R.id.news_scrollView);
        mWebView = (WebView) headLayout.findViewById(R.id.webView_content);
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mHeandRalayout = (RelativeLayout) findViewById(R.id.new_detail_video_return_layout);
        mTailRalayout = (RelativeLayout) findViewById(R.id.news_detail_video_bottom);
        mTitleLayout = (RelativeLayout) findViewById(R.id.new_detail_title_layout);
        mWebLayout = (RelativeLayout) findViewById(R.id.news_detail_web);
        mVideoLayout = (RelativeLayout) findViewById(R.id.news_detail_centernss);
//        container = (RelativeLayout)findViewById(R.id.news_detail_centernss);
        mGestureDetector = new GestureDetector(NewsDetailVideoActivity.this, (GestureDetector.OnGestureListener) this);
        LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.newsdtauel_smain);
        relativeLayout.setOnTouchListener(this);
        relativeLayout.setLongClickable(true);
        initView();
        bIsExit = false;
        dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) NewsDetailVideoActivity.this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        nScreenheight = ((dm.widthPixels - 20) * 3) / 4;
        nFlags = getIntent().getFlags();
        Bundle bundle = getIntent().getExtras();
        mIndex = bundle.getInt("index");
        mColumnTy = bundle.getInt("cloumnType");
        postGetUrl = bundle.getString("get_url");
        mPosterPath = bundle.getString("posterPath");
//        mtitle = bundle.getString("tilte");
//       mthumb = bundle.getString("thumb");
//        this.setTitle(mtitle);
        commendNumber = bundle.getInt("commenNumber");
        mTxtShowCommendNumber.setText(commendNumber + "跟帖");
        mTxtShowCommendNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (mCustomView != null && mCustomView.isPlaying()) {
//                    mCustomView.stopPlayback();
//                }
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putInt("commentsId", mIndex);
//                intent.putExtras(bundle);
//                intent.setClass(NewsDetailVideoActivity.this, CommentsActivity.class);
//                startActivity(intent);
            }
        });
//        mApi = (AppApiImpl) mApp.getInstance().getApi();
//        Account account = mApi.getAccountApi().getCurrentAccount();
//        if (account == null) {
//            mEditTxtContent.setHint("你尚未登录");
//        }
        mContentCmsApi = new ContentCmsApi(this);
        InitCustomVideoView();
//        progressDialog = CustomeProgressDialog.show(NewsDetailVideoActivity.this, "正在加载中...");
        /*mHtmlApi = App.getInstance().getmHtmlApi();
        if (!mHtmlApi.isHasInfors(mIndex + "")) {
//            LoadDataFormURl();
        } else {
            HtmlEntities entry = mHtmlApi.querytFileName(mIndex + "");
            if (entry != null) {
                // 获取sd卡目录
                String path = FileUtils.Html_public_dir + entry.getFilename();
                if (FileUtils.isExistDirFile(entry.getFilename())) {
                    isComparehtml = true;
                    showWebContext(entry);
                } else {
//                    LoadDataFormURl();
                }
            }
        }*/
        LoadDataFormURl();
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

    public void showWebContext(HtmlEntities entry) {
        mListView.setVisibility(View.VISIBLE);
        isGetDateflag = false;
        mloadFailurelay.setVisibility(View.GONE);
        mTitleTxtvw.setText(entry.getTitle());
        mSourceTxtvw.setText(entry.getSoruceTx());
        mVideoPath = entry.getVideoUrl();
        if (!("").equals(mVideoPath) && mVideoPath != null) {
            mVideoLayout.setVisibility(View.VISIBLE);
            mPlayVide0btn.setVisibility(View.VISIBLE);
            Util.LoadThumebImage(mPosterImagView, entry.getVideoThumb(), null);
        } else {
            mVideoLayout.setVisibility(View.GONE);
        }
//        // 获取sd卡目录
//        String path = FileUtils.Html_public_dir + entry.getFilename();
//        FileUtils.readFile(path);
//        mHtmlContent = FileUtils.readFile(path);
//            if (bundel.getParcelableArrayList("relatiNodes") != null) {
////                        String[] list = bundel.getString("relatiNodes").toString().trim().split(",");
//                ArrayList<SocirtyNewsChannel> list = bundel.getParcelableArrayList("relatiNodes");
//                if (list != null && list.size() > 0) {
////                            mBottomLayout.setVisibility(View.VISIBLE);
//                    adapter.update(list, false);
//                }
//            } else {
        mBottomLayout.setVisibility(View.GONE);
//            }
        if (mWebView != null) {
            connectUrl = mHtmlContent;
            loadDataFromText();
        }
    }

    private void goToLogin() {
        if (NewsDetailVideoActivity.this != null) {
            AlertDialog adig = new AlertDialog.Builder(NewsDetailVideoActivity.this).setTitle("提示").setMessage("未登录，是否现在登录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent intent = new Intent();
//                    intent.setClass(NewsDetailVideoActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            }).create();
            adig.show();
        }
    }

    private void handleSuccess(String toastText) {
        if (NewsDetailVideoActivity.this != null) {
            isComemndComplate = true;
            mEditTxtContent.setText("");
            Toast.makeText(NewsDetailVideoActivity.this, toastText,
                    Toast.LENGTH_SHORT).show();
            if (isSuccess) {
                isSuccess = false;
                mEditTxtContent.setHint("你来说几句");
                commendNumber++;
                Intent intent = getIntent();
                intent.putExtra("cid", commendNumber);
                Bundle bn = intent.getExtras();
                int nn = bn.getInt("commenNumber");
                bn.putInt("commenNumber", commendNumber);
//                    intent.putExtra("numId",commendNumber);
                setResult(-1, intent);
                mTxtShowCommendNumber.setText(commendNumber + "跟帖");
            }
        }
    }

    public void postCommendContent() {
        JSONObject obj = getJsonObject("", "");
        try {
            JSONObject arrObj = getJsonObject("", "");
            JSONArray arrays = new JSONArray();
            JSONObject item = getJsonObject("value", mEditTxtContent.getText());
            arrays.put(item);
            arrObj.put("und", arrays);
            obj.put("comment_body", arrObj);
            obj.put("nid", mIndex);
            String ss = obj.toString();
            Log.e("commend", ss);
            String url = "http://www.dfsxcms.cn:8080/services/comment";
            Account mUser = App.getInstance().getUser();
            if (mUser == null) {
                goToLogin();
                return;
            }
            newsDatailHelper.postCommendContent(item, new DataRequest.DataCallback() {
                @Override
                public void onSuccess(boolean isAppend, Object data) {
                    isSuccess = true;
                    handleSuccess("评论提交成功！！！");
                }

                @Override
                public void onFail(ApiException e) {
                    isSuccess = false;
                    Toast.makeText(context, "评论提交失败", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postCommintthumbNumber() {
        JSONObject obj = UtilHelp.getJsonObject("", "");
        try {
            obj.put("type", "node");
            obj.put("id", mIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            newsDatailHelper.postCommitThumbNumber(obj, new DataRequest.DataCallback<Integer>() {
                @Override
                public void onSuccess(boolean isAppend, Integer data) {
                    if (data != -1) {
                        isObzavr = false;
                        int num = Integer.parseInt(mThumbupNumbertv.getText().toString().trim());
                        int nCount = data;
                        if (num != nCount) {
                            mThumbupNumbertv.setVisibility(View.VISIBLE);
                            mThumbShoutv.setVisibility(View.VISIBLE);
                            mThumbShoutv.startAnimation(animation);
                            mThumbupNumbertv.setText(nCount + "");
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    mThumbShoutv.setVisibility(View.GONE);
                                }
                            }, 1000);
                        }
                    } else {
                        isObzavr = false;
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    isObzavr = false;
                    Toast.makeText(context, "postCommintthumbNumber fail",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            isObzavr = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putInt("commennum",commendNumber);
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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        loadDataFromText();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void LoadDataFormURl() {
        if (!isGetDateflag) {
            isGetDateflag = true;
            mloadFailurelay.setVisibility(View.GONE);
            if (!isComparehtml)
                progressDialog = CustomeProgressDialog.show(NewsDetailVideoActivity.this, "正在加载中...");
            getDataMainThread();
        }
    }

    public void getSigleAdware() {
//        InputStream inputStream = null;
//        try {
//            inputStream = mApi.httpGet(mApi.makeUrl("services/app_ad_singlenews.json"));
//            JSONObject jsonObject = null;
//            jsonObject = mApi.jsonParse(inputStream);
//            if (jsonObject != null) {
//                JSONArray result = jsonObject.getJSONArray("result");
//                String str = null;
//                if (result != null && result.length() > 0) {
//                    JSONObject obj = (JSONObject) result.get(0);
//                    str=UtilHelp.getImagePath(obj.getString("field_ad_app"));
//                    str +=  "," + obj.getString("node_title");
//                    Message  msg=myHander.obtainMessage(OBTAIN_SINGLEAD_MSG);
//                    msg.obj=str;
//                    myHander.sendMessage(msg);
//                }
//            }
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void getPauseImage() {
//        InputStream inputStream = null;
//        try {
//            inputStream = mApi.httpGet(mApi.makeUrl("services/app_ad_pic_pause.json"));
//            JSONObject jsonObject = null;
//            jsonObject = mApi.jsonParse(inputStream);
//            if (jsonObject != null) {
//                JSONArray result = jsonObject.getJSONArray("result");
//                String str = null;
//                if (result != null && result.length() > 0) {
//                    str="";
//                    for(int i=0;i<result.length();i++)
//                    {
//                        JSONObject obj = (JSONObject) result.get(i);
//                        JSONArray  imgs=obj.getJSONArray("field_ad_pic_pause");
//                        str+=UtilHelp.getImagePath((String) imgs.get(0).toString().trim());
//                        str+=",";
//                    }
//                    Message  msg=myHander.obtainMessage(OBTAIN_PAUSEIMGEO_MSG);
//                    msg.obj=str;
//                    myHander.sendMessage(msg);
//                }
//            }
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void getAdwarVideo() {
//        InputStream inputStream = null;
//        try {
//            inputStream = mApi.httpGet(mApi.makeUrl("services/app_ad_video_before.json"));
//            JSONObject jsonObject = null;
//            jsonObject = mApi.jsonParse(inputStream);
//            if (jsonObject != null) {
//                JSONArray result = jsonObject.getJSONArray("result");
//                String str = null;
//                String durStr= "";
//                if (result != null && result.length() > 0) {
//                    str="";
//                    for(int i=0;i<result.length(); i++) {
//                        JSONObject obj = (JSONObject) result.get(i);
//                        str+=obj.getString("field_ad_video_before");
//                        str +=",";
//                        durStr+=obj.getString("field_ad_video_time");
//                        durStr +=",";
//                    }
//                    Message  msg=myHander.obtainMessage(OBTAIN_ADWAREVIDEO_MSG);
//                    adsInfo af = new adsInfo();
//                    af.urls = str;
//                    af.durations = durStr;
//                    msg.obj=af;
//                    myHander.sendMessage(msg);
//                }
//            }
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void getDataMainThread() {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() +
                "/public/contents/" + Long.toString(mIndex);
        DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().setRequestType(DataReuqestType.GET).
                setUrl(url).setToken(App.getInstance().getCurrentToken()).build();
        new DataFileCacheManager<ContentCmsInfoEntry>(this, App.getInstance().getPackageName() + mIndex + "_txt") {
            @Override
            public ContentCmsInfoEntry jsonToBean(JSONObject jsonObject) {
                ContentCmsInfoEntry entry = null;
                try {
                    if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                        entry = new Gson().fromJson(jsonObject.toString(), ContentCmsInfoEntry.class);
//                        JSONObject exObj=entry.getExtension();
                        JSONObject exObj = jsonObject.optJSONObject("extension");
                        if (exObj != null) {
                            if (entry.getShowType() == 2) {
                                if (!exObj.isNull("versions")) {
                                    JSONArray arr = exObj.optJSONArray("versions");
                                    if (arr != null && arr.length() > 0) {
                                        String path = "";
                                        for (int i = 0; i < arr.length(); i++) {
                                            JSONObject pl = (JSONObject) arr.get(i);
                                            path = pl.optString("url");
                                            String opp = path.substring(path.lastIndexOf(".") + 1, path.length());
                                            if (TextUtils.equals(".mp4", opp)) break;
                                        }
                                        entry.setUrl(path);
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return entry;
            }
        }.getData(params, false)
                .setCallback(new DataRequest.DataCallback<ContentCmsInfoEntry>() {
                    @Override
                    public void onSuccess(boolean isAppend, ContentCmsInfoEntry data) {
                        if (data != null) {
                            showWebviewData(data);
                        } else {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        showNetError();
                    }
                });
    }

    public String getHtmlWeb(String body) {
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
                "        }\n" +
                "    }\n" +
                "    </script>\n" +
                "<body>";


//                    txtWeb += "<p style=\"word-break:break-all;padding:5px\">";
        txtWeb += "<p style=\"text-align:justify;font-size:17px;line-height:180%;text-indent:2em;\">";
//                    txtWeb += "<font style=\"line-height:180%;font-size:17px\">";
        txtWeb += body;
//        txtWeb += "</font>";
        txtWeb += "</p>";
        txtWeb += "</body></html>";
        return txtWeb;
    }

    private HtmlEntities pareseData(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        try {
            ArrayList<String> uls = new ArrayList<String>();
            // maybe array or object.
            if (!jsonObject.isNull("field_news_video_app_mp4")) {
                JSONObject videoObj = jsonObject.optJSONObject("field_news_video_app_mp4");
                if (videoObj != null) {
                    JSONArray videoArry = videoObj.getJSONArray("und");
                    if (videoArry != null) {
                        for (int i = 0; i < videoArry.length(); i++) {
                            JSONObject item = (JSONObject) videoArry.get(i);
                            String fid = item.getString("value");
                            if (!("").equals(fid.toString().trim()) && !("null").equals(fid.toString().trim())) {
//                            String values = item.getString("uri").toLowerCase();
                                uls.add(fid);
                            }
                        }
                    }
                }
            }
            if (!jsonObject.isNull("field_news_video_app") && uls.isEmpty()) {
                JSONObject videoObj = jsonObject.optJSONObject("field_news_video_app");
                if (videoObj != null) {
                    JSONArray videoArry = videoObj.getJSONArray("und");
                    if (videoArry != null) {
                        for (int i = 0; i < videoArry.length(); i++) {
                            JSONObject item = (JSONObject) videoArry.get(i);
                            String fid = item.getString("value");
                            if (!("").equals(fid.toString().trim()) && !("null").equals(fid.toString().trim())) {
//                            String values = item.getString("uri").toLowerCase();
                                uls.add(fid);
                            }
                        }
                    }
                }
            }
            if (uls.isEmpty()) {
                JSONObject videoObj = jsonObject.optJSONObject("field_news_video_app");
                if (videoObj != null) {
                    JSONArray videoArry = videoObj.getJSONArray("und");
                    if (videoArry != null) {
                        for (int i = 0; i < videoArry.length(); i++) {
                            JSONObject item = (JSONObject) videoArry.get(i);
                            String fid = item.getString("value");
                            if (!("").equals(fid.toString().trim()) && !("null").equals(fid.toString().trim())) {
//                            String values = item.getString("uri").toLowerCase();
                                uls.add(fid);
                            }
                        }
                    }
                }
            }

            if (mtitle == null || ("").equals(mtitle)) {
                mtitle = jsonObject.optString("title");
            }
            if (mtitle == null || ("").equals(mtitle)) {
                mtitle = jsonObject.optString("node_title");
            }
            long time = jsonObject.optLong("created");
            if (time > 0) {
                mSource = Util.timestampToString(time * 1000, "yyyy-MM-dd");
            } else {
                mSource = "";
            }
            urlPath = jsonObject.optString("path");
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

//                txtWeb+="<h3>"+mtitle+"</h3>";

            if (!uls.isEmpty()) {
                mVideoPath = uls.get(0);
//                    JSONObject videoThumb = jsonObject.optJSONObject("field_news_video_thumb_app");
//                    String thumb= "http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files/";
//                    if(videoThumb != null) {
//                        JSONArray videoArry = videoThumb.getJSONArray("und");
//                        if (videoArry != null) {
//                            for (int i = 0; i < videoArry.length(); i++) {
//                                JSONObject item = (JSONObject) videoArry.get(i);
//                                thumb=thumb+ item.getString("filename");
//                            }
//                        }
//                    }
//                    mPosterPath=thumb;
                //txtWeb += "<video src=\"" + path + "\" poster=\""+thumb+"\" controls=\"controls\" width=\"100%\" height=\"auto\">";
//                    txtWeb += "<video onclick=playVideo(\"" + path + "\") poster=\""+thumb+"\" controls=\"controls\" width=\"100%\" height=\"auto\">";
//                    txtWeb += "</video>";

//                    if(meWebTYPE==HTMLWEB_TYPE)
//                    {
////                        txtWeb += "<video onclick=playVideo(\"" + path + "\") poster=\""+thumb+"\" controls=\"controls\" width=\"100%\" height=\"auto\">";
//                        txtWeb += "<video onclick=playVideo(\"" + path + "\") poster=\""+thumb+"\" controls=\"controls\" width=\"100%\" height="+(int)(nScreenheight/dm.density)+">";
//                    }else
//                    {
//                        txtWeb += "<video src=\"" + path + "\" poster=\""+thumb+"\" controls=\"controls\" width=\"100%\" height="+(int)(nScreenheight/dm.density)+">";
//                    }
//                    txtWeb += "</video>";
            }

            if (!jsonObject.isNull("field_news_thumb")) {
                JSONObject thumbObj = jsonObject.optJSONObject("field_news_video_thumb_app");
                if (thumbObj == null)
                    thumbObj = jsonObject.optJSONObject("field_news_thumb");
                if (thumbObj != null) {
                    JSONArray arry = thumbObj.getJSONArray("und");
                    if (arry != null) {
                        if (arry.length() > 0) {
//                            mthumb = "http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files/";
                            mthumb = App.getInstance().getBaseUrl() + "/sites/default/files/";    //heyang   2016-7-18
                            JSONObject obj = (JSONObject) arry.get(0);
                            String url = obj.getString("uri");
                            url = url.replace("//", "/");
                            int index = url.indexOf("/");
//                               String  name=obj.getString("filename");
                            String name = url.substring(index + 1, url.length());
//                            url = url.replace("//", "//");
                            mthumb += name;
//                            mthumb = URLEncoder.encode(mthumb, "UTF-8");
//                            txtWeb += "<img  src=\"" + mthumb + "\"  width=\"100%\" height=\"auto\">";
                            mPosterPath = mthumb;
                        }
                    }
                }
            }
            //针对广告
            if (mthumb == null || ("").equals(mthumb)) {
                if (!jsonObject.isNull("field_front_ad_app")) {
                    JSONObject thumbObj = jsonObject.optJSONObject("field_front_ad_app");
//                       if(thumbObj == null)
//                           thumbObj = jsonObject.optJSONObject("field_news_thumb");
                    if (thumbObj != null) {
                        JSONArray arry = thumbObj.getJSONArray("und");
                        if (arry != null) {
                            if (arry.length() > 0) {
//                                mthumb = "http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files/";
                                mthumb = App.getInstance().getBaseUrl() + "/sites/default/files/";
                                JSONObject obj = (JSONObject) arry.get(0);
                                String url = obj.getString("uri");
                                url = url.replace("//", "/");
                                int index = url.lastIndexOf("/");
//                               String  name=obj.getString("filename");
                                String name = url.substring(index + 1, url.length());
                                mthumb += URLEncoder.encode(name, "UTF-8");
                                txtWeb += "<img  src=\"" + mthumb + "\"  width=\"100%\" height=\"auto\">";
//                                   mPosterPath=mthumb;
                            }
                        }
                    }
                }
            }

            if (!jsonObject.isNull("body")) {
//                    txtWeb += "<p style=\"word-break:break-all;padding:5px\">";
                txtWeb += "<p style=\"text-align:justify;font-size:17px;line-height:180%;text-indent:2em;\">";
//                    txtWeb += "<font style=\"line-height:180%;font-size:17px\">";
                try {
                    JSONObject markbody = jsonObject.optJSONObject("body");
                    if (markbody != null) {
                        JSONArray result = markbody.getJSONArray("und");
                        if (result != null) {
                            mContent = "";
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                mContent = item.getString("value");
//                                String link = "\"http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files";
//                                String link = "\"http://" + mApi.getServerHost()  + "/sites/default/files";
                                String link = "\"" + App.getInstance().getBaseUrl() + "/sites/default/files";
                                mContent = mContent.replace("\"/sites/default/files", link);
                                txtWeb += mContent;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                txtWeb += "</font>";
//                    txtWeb += "</p>";
            }
            /*if(!jsonObject.isNull("field_news_photos")) {
                JSONObject newsInfo = jsonObject.optJSONObject("field_news_photos");
                String newsInfoStr = newsInfo.toString();
                newsInfoStr = newsInfoStr.replace("[", "");
                newsInfoStr = newsInfoStr.replace("]", "");
                newsInfoStr = newsInfoStr.replace("{", "");
                newsInfoStr = newsInfoStr.replace("}", "");

                txtWeb += "<p style=\"text-align:justify;font-size:17px;line-height:180%;text-indent:2em;\">";
                txtWeb += newsInfoStr;
            }*/
            mInfo = "";
            if (!jsonObject.isNull("field_news_intro")) {
                JSONObject info = jsonObject.optJSONObject("field_news_intro");
                if (info != null) {
                    JSONArray result = info.getJSONArray("und");
                    if (result != null) {
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject item = (JSONObject) result.get(i);
                            mInfo += item.getString("value");
                        }
                    }
                }
            }

            String relatiSt = null;
            if (!jsonObject.isNull("field_zhuanti_relation_add")) {
                JSONObject info = jsonObject.optJSONObject("endpoints");
                if (info != null) {
                    JSONArray result = info.getJSONArray("und");
                    if (result != null) {
                        for (int i = 1; i < result.length(); i++) {
                            JSONObject item = (JSONObject) result.get(i);
                            relatiSt += item.getLong("entity_id");
                            relatiSt += ",";
                        }
                    }
                }
            }

            ArrayList<SocirtyNewsChannel> adList = null;
//                    if (!jsonObject.isNull("field_news_relation")) {
//                        JSONObject rt = jsonObject.optJSONObject("field_news_relation");
//                        if (rt != null) {
//                            JSONArray result = rt.optJSONArray("und");
////                String ponitString = null;
//                            if (result != null) {
//                                adList = new ArrayList<SocirtyNewsEntity.SocirtyNewsChannel>();
////                    ponitString = "";
//                                for (int i = 0; i < result.length(); i++) {
//                                    JSONObject endPoints = (JSONObject) result.get(i);
//                                    JSONObject p = (JSONObject) endPoints.getJSONObject("endpoints");
//                                    JSONArray ponits = p.getJSONArray("und");
//                                    if (ponits != null) {
//                                        for (int k = 1; k < ponits.length(); k++) {
//                                            JSONObject item = (JSONObject) ponits.get(k);
////                                ponitString += item.getInt("entity_id");
//                                            int id = Integer.parseInt(item.getString("entity_id"));
//                                            if (id == mIndex) continue;
//                                            SocirtyNewsChannel ch = getInfoById(id);
//                                            if (ch.typeId != 0) continue;
//                                            ;
//                                            adList.add(ch);
////                                ponitString += ",";
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
            txtWeb += "</body></html>";
            /**
             * 调用saveToSDCard方法保存文件到SD卡
             */
            String str_file = "html_id%" + mIndex;
            WriteFleThread(str_file, txtWeb);
            HtmlEntities entry = new HtmlEntities();
            entry.setColumnId(mIndex + "");
            entry.setTitle(mtitle);
            entry.setSoruceTx(mSource);
            entry.setVideoUrl(mVideoPath);
            entry.setVideoThumb(mPosterPath);
            entry.setFilename(str_file);
            entry.setHtmlContent(txtWeb);
            return entry;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getData() {

//        InputStream inputStream = null;
//        try {
////            Log.e("NewsDetailActivity http URL  ","http://www.dfsxcms.cn:8080/services/node/" + Long.toString(mIndex)+".json");
//            inputStream = mApi.httpGet(mApi.makeUrl("services/node/" + Long.toString(mIndex) + ".json"));
//        } catch (ApiException e) {
//            e.printStackTrace();
//            myHander.sendEmptyMessage(GET_DATE_FAILURE);
//            return;
//        }
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = mApi.jsonParse(inputStream);
//        } catch (ApiException e) {
//            e.printStackTrace();
//            myHander.sendEmptyMessage(GET_DATE_FAILURE);
//            return;
//        }
//        try {
//            ArrayList<String> uls = new ArrayList<String>();
//            // maybe array or object.
//            if (!jsonObject.isNull("field_news_video_app_mp4")) {
//                JSONObject videoObj = jsonObject.optJSONObject("field_news_video_app_mp4");
//                if (videoObj != null) {
//                    JSONArray videoArry = videoObj.getJSONArray("und");
//                    if (videoArry != null) {
//                        for (int i = 0; i < videoArry.length(); i++) {
//                            JSONObject item = (JSONObject) videoArry.get(i);
//                            String fid = item.getString("value");
//                            if (!("").equals(fid.toString().trim()) && !("null").equals(fid.toString().trim())) {
////                            String values = item.getString("uri").toLowerCase();
//                                uls.add(fid);
//                            }
//                        }
//                    }
//                }
//            }
//            if (!jsonObject.isNull("field_news_video_app") && uls.isEmpty()) {
//                JSONObject videoObj = jsonObject.optJSONObject("field_news_video_app");
//                if (videoObj != null) {
//                    JSONArray videoArry = videoObj.getJSONArray("und");
//                    if (videoArry != null) {
//                        for (int i = 0; i < videoArry.length(); i++) {
//                            JSONObject item = (JSONObject) videoArry.get(i);
//                            String fid = item.getString("value");
//                            if (!("").equals(fid.toString().trim()) && !("null").equals(fid.toString().trim())) {
////                            String values = item.getString("uri").toLowerCase();
//                                uls.add(fid);
//                            }
//                        }
//                    }
//                }
//            }
//            if (uls.isEmpty()) {
//                JSONObject videoObj = jsonObject.optJSONObject("field_news_video_app");
//                if (videoObj != null) {
//                    JSONArray videoArry = videoObj.getJSONArray("und");
//                    if (videoArry != null) {
//                        for (int i = 0; i < videoArry.length(); i++) {
//                            JSONObject item = (JSONObject) videoArry.get(i);
//                            String fid = item.getString("value");
//                            if (!("").equals(fid.toString().trim()) && !("null").equals(fid.toString().trim())) {
////                            String values = item.getString("uri").toLowerCase();
//                                uls.add(fid);
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (mtitle == null || ("").equals(mtitle)) {
//                mtitle = jsonObject.getString("title");
//            }
//            long time = jsonObject.getLong("created");
//            mSource = Util.timestampToString(time * 1000, "yyyy-MM-dd");
//            urlPath = jsonObject.getString("path");
//            String txtWeb = "<html>\n" +
//                    "<meta name=\"viewport\" content=\"width=device-width\"/>\n" +
//                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"main.css\" />\n" +
//                    "<script language=\"javascript\">\n" +
//                    "    function imgResize() {\n" +
//                    "        var imgs = document.getElementsByTagName(\"img\");\n" +
//                    "        var array = new Array();\n" +
//                    "        for (var j = 0; j < imgs.length; j++) {\n" +
//                    "         array[j] = imgs[j].attributes[\'src\'].value;\n" +
//                    "          }\n" +
//                    "        for (var i = 0; i < imgs.length; i++) {\n" +
//                    "            imgs[i].pos = i;\n" +
//                    "            imgs[i].onclick=function()" +
//                    "            {\n" +
//                    "              var pos = this.pos;\n" +
//                    "window.imagelistner.openImage(array.join(\",\"),pos);\n" +
//                    "            }\n" +
////                       "if (imgs[i].width==0 || window.innerWidth < imgs[i].width){" +
////                       "            imgs[i].style.width = \"100%\";\n" +
////                       "            imgs[i].style.height = \"auto\";}\n" +
////                       "            var p = imgs[i].parentElement;" +
////                       "            if(p != null)  p.style.textIndent = \"0\";" +
//                    "        }\n" +
//                    "    }\n" +
//
//
//                    "    </script>\n" +
//                    "<body>";
//
////                txtWeb+="<h3>"+mtitle+"</h3>";
//
//            if (!uls.isEmpty()) {
//                mVideoPath = uls.get(0);
////                    JSONObject videoThumb = jsonObject.optJSONObject("field_news_video_thumb_app");
////                    String thumb= "http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files/";
////                    if(videoThumb != null) {
////                        JSONArray videoArry = videoThumb.getJSONArray("und");
////                        if (videoArry != null) {
////                            for (int i = 0; i < videoArry.length(); i++) {
////                                JSONObject item = (JSONObject) videoArry.get(i);
////                                thumb=thumb+ item.getString("filename");
////                            }
////                        }
////                    }
////                    mPosterPath=thumb;
//                //txtWeb += "<video src=\"" + path + "\" poster=\""+thumb+"\" controls=\"controls\" width=\"100%\" height=\"auto\">";
////                    txtWeb += "<video onclick=playVideo(\"" + path + "\") poster=\""+thumb+"\" controls=\"controls\" width=\"100%\" height=\"auto\">";
////                    txtWeb += "</video>";
//
////                    if(meWebTYPE==HTMLWEB_TYPE)
////                    {
//////                        txtWeb += "<video onclick=playVideo(\"" + path + "\") poster=\""+thumb+"\" controls=\"controls\" width=\"100%\" height=\"auto\">";
////                        txtWeb += "<video onclick=playVideo(\"" + path + "\") poster=\""+thumb+"\" controls=\"controls\" width=\"100%\" height="+(int)(nScreenheight/dm.density)+">";
////                    }else
////                    {
////                        txtWeb += "<video src=\"" + path + "\" poster=\""+thumb+"\" controls=\"controls\" width=\"100%\" height="+(int)(nScreenheight/dm.density)+">";
////                    }
////                    txtWeb += "</video>";
//            }
//
//            if (!jsonObject.isNull("field_news_thumb")) {
//                JSONObject thumbObj = jsonObject.optJSONObject("field_news_video_thumb_app");
//                if (thumbObj == null)
//                    thumbObj = jsonObject.optJSONObject("field_news_thumb");
//                if (thumbObj != null) {
//                    JSONArray arry = thumbObj.getJSONArray("und");
//                    if (arry != null) {
//                        if (arry.length() > 0) {
////                            mthumb = "http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files/";
//                            mthumb = mApi.getBaseUrl() + "/sites/default/files/";    //heyang   2016-7-18
//                            JSONObject obj = (JSONObject) arry.get(0);
//                            String url = obj.getString("uri");
//                            url = url.replace("//", "/");
//                            int index = url.indexOf("/");
////                               String  name=obj.getString("filename");
//                            String name = url.substring(index + 1, url.length());
////                            url = url.replace("//", "//");
//                            mthumb += name;
////                            mthumb = URLEncoder.encode(mthumb, "UTF-8");
////                            txtWeb += "<img  src=\"" + mthumb + "\"  width=\"100%\" height=\"auto\">";
//                            mPosterPath = mthumb;
//                        }
//                    }
//                }
//            }
//            //针对广告
//            if (mthumb == null || ("").equals(mthumb)) {
//                if (!jsonObject.isNull("field_front_ad_app")) {
//                    JSONObject thumbObj = jsonObject.optJSONObject("field_front_ad_app");
////                       if(thumbObj == null)
////                           thumbObj = jsonObject.optJSONObject("field_news_thumb");
//                    if (thumbObj != null) {
//                        JSONArray arry = thumbObj.getJSONArray("und");
//                        if (arry != null) {
//                            if (arry.length() > 0) {
////                                mthumb = "http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files/";
//                                mthumb = mApi.getBaseUrl() + "/sites/default/files/";
//                                JSONObject obj = (JSONObject) arry.get(0);
//                                String url = obj.getString("uri");
//                                url = url.replace("//", "/");
//                                int index = url.lastIndexOf("/");
////                               String  name=obj.getString("filename");
//                                String name = url.substring(index + 1, url.length());
//                                mthumb += URLEncoder.encode(name, "UTF-8");
//                                txtWeb += "<img  src=\"" + mthumb + "\"  width=\"100%\" height=\"auto\">";
////                                   mPosterPath=mthumb;
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (!jsonObject.isNull("body")) {
////                    txtWeb += "<p style=\"word-break:break-all;padding:5px\">";
//                txtWeb += "<p style=\"text-align:justify;font-size:17px;line-height:180%;text-indent:2em;\">";
////                    txtWeb += "<font style=\"line-height:180%;font-size:17px\">";
//                try {
//                    JSONObject markbody = jsonObject.optJSONObject("body");
//                    if (markbody != null) {
//                        JSONArray result = markbody.getJSONArray("und");
//                        if (result != null) {
//                            mContent = "";
//                            for (int i = 0; i < result.length(); i++) {
//                                JSONObject item = (JSONObject) result.get(i);
//                                mContent = item.getString("value");
////                                String link = "\"http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files";
////                                String link = "\"http://" + mApi.getServerHost()  + "/sites/default/files";
//                                String link = "\"" + mApi.getBaseUrl() + "/sites/default/files";
//                                mContent = mContent.replace("\"/sites/default/files", link);
//                                txtWeb += mContent;
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                txtWeb += "</font>";
////                    txtWeb += "</p>";
//            }
//
//            mInfo = "";
//            if (!jsonObject.isNull("field_news_intro")) {
//                JSONObject info = jsonObject.optJSONObject("field_news_intro");
//                if (info != null) {
//                    JSONArray result = info.getJSONArray("und");
//                    if (result != null) {
//                        for (int i = 0; i < result.length(); i++) {
//                            JSONObject item = (JSONObject) result.get(i);
//                            mInfo += item.getString("value");
//                        }
//                    }
//                }
//            }
//
//            String relatiSt = null;
//            if (!jsonObject.isNull("field_zhuanti_relation_add")) {
//                JSONObject info = jsonObject.optJSONObject("endpoints");
//                if (info != null) {
//                    JSONArray result = info.getJSONArray("und");
//                    if (result != null) {
//                        for (int i = 1; i < result.length(); i++) {
//                            JSONObject item = (JSONObject) result.get(i);
//                            relatiSt += item.getLong("entity_id");
//                            relatiSt += ",";
//                        }
//                    }
//                }
//            }
//
//            ArrayList<SocirtyNewsChannel> adList = null;
//            if (!jsonObject.isNull("field_news_relation")) {
//                JSONObject rt = jsonObject.optJSONObject("field_news_relation");
//                if (rt != null) {
//                    JSONArray result = rt.optJSONArray("und");
////                String ponitString = null;
//                    if (result != null) {
//                        adList = new ArrayList<SocirtyNewsChannel>();
////                    ponitString = "";
//                        for (int i = 0; i < result.length(); i++) {
//                            JSONObject endPoints = (JSONObject) result.get(i);
//                            JSONObject p = (JSONObject) endPoints.getJSONObject("endpoints");
//                            JSONArray ponits = p.getJSONArray("und");
//                            if (ponits != null) {
//                                for (int k = 1; k < ponits.length(); k++) {
//                                    JSONObject item = (JSONObject) ponits.get(k);
////                                ponitString += item.getInt("entity_id");
//                                    int id = Integer.parseInt(item.getString("entity_id"));
//                                    if (id == mIndex) continue;
//                                    SocirtyNewsChannel ch = getInfoById(id);
//                                    if (ch.typeId != 0) continue;
//                                    ;
//                                    adList.add(ch);
////                                ponitString += ",";
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            txtWeb += "</body></html>";
//            Message msg = myHander.obtainMessage(GETURL_FROMNET);
//            msg.what = GETURL_FROMNET;
//            Bundle bun = new Bundle();
//            bun.putString("webStr", txtWeb.toString().trim());
//            bun.putParcelableArrayList("relatiNodes", adList);
//            msg.obj = bun;
//            myHander.sendMessage(msg);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            myHander.sendEmptyMessage(NETWORK_BUSY);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

//    public SocirtyNewsChannel getInfoById(int id) {
//        InputStream inputStream = null;
//        try {
//            inputStream = mApi.httpGet(mApi.makeUrl("services/node/" + id + ".json"));
//        } catch (ApiException e) {
//            e.printStackTrace();
////            myHander.sendEmptyMessage(OBATIN_SPECILTOP_FAILED);
//            return null;
//        }
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = mApi.jsonParse(inputStream);
//            if (jsonObject == null) return null;
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
//        SocirtyNewsChannel snews = new SocirtyNewsChannel();
//        try {
//            snews.id = jsonObject.getInt("nid");
//            snews.newsTitle = jsonObject.getString("title");
//            long dleng = jsonObject.getLong("created");
//            snews.newsTime = UtilHelp.GetSimpleDate(dleng);
//            String type = jsonObject.getString("type");
//            if (("special_topic").equals(type)) {
//                snews.typeId = 4;
//            } else {
//                snews.typeId = 0;
//            }
//            String mthumb = "";
//            if (!jsonObject.isNull("field_news_thumb")) {
//                JSONObject thumbObj = jsonObject.optJSONObject("field_news_video_thumb_app");
//                if (thumbObj == null)
//                    thumbObj = jsonObject.optJSONObject("field_news_thumb");
//                if (thumbObj != null) {
//                    JSONArray arry = thumbObj.getJSONArray("und");
//                    if (arry != null) {
//                        if (arry.length() > 0) {
////                            mthumb= "http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files/";
//                            mthumb = mApi.getBaseUrl() + "/sites/default/files/";
//                            JSONObject obj = (JSONObject) arry.get(0);
//                            String name = obj.getString("filename");
//                            mthumb += URLEncoder.encode(name, "UTF-8");
//                        }
//                    }
//                }
//            }
//
//            if (!jsonObject.isNull("field_video_thumb")) {
//                JSONObject thumbObj = jsonObject.optJSONObject("field_video_thumb");
//                if (thumbObj != null) {
//                    JSONArray arry = thumbObj.getJSONArray("und");
//                    if (arry != null) {
//                        if (arry.length() > 0) {
////                            mthumb= "http://" + mApi.getServerHost() + ":" + String.valueOf(mApi.getServerPort()) + "/sites/default/files/";
//                            mthumb = mApi.getBaseUrl() + "/sites/default/files/";
//                            JSONObject obj = (JSONObject) arry.get(0);
//                            String filename = obj.getString("filename");
//                            mthumb += URLEncoder.encode(filename, "UTF-8");
//                        }
//                    }
//                }
//            }
//            snews.newsThumb = mthumb;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return snews;
//    }

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

    public void initView() {
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
//                if (!App.getInstance().getmSession().isRead(mIndex)) {
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


    public void setZoomControlGone(View view) {
        Class classType;
        Field field;
        try {
            classType = WebView.class;
            field = classType.getDeclaredField("mZoomButtonsController");
            field.setAccessible(true);
            ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);
            mZoomButtonsController.getZoomControls().setVisibility(View.GONE);
            try {
                field.set(view, mZoomButtonsController);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void getThumbNumber() {
        JSONObject obj = UtilHelp.getJsonObject("", "");
        try {
            obj.put("type", "node");
            obj.put("id", mIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        newsDatailHelper.getThumbNumber(obj, new DataRequest.DataCallback<Integer>() {
            @Override
            public void onSuccess(boolean isAppend, Integer data) {
                if (data != -1) {
                    int number = data;
                    if (number > 0) {
                        mThumbupNumbertv.setVisibility(View.VISIBLE);
                    }
                    mThumbupNumbertv.setText(number + "");
                }
            }

            @Override
            public void onFail(ApiException e) {

            }
        });
    }

    public void loadDataFromText() {
        //   str="http://www.selake.com/Travel/";
//        mWebView.loadUrl(urlPath);
//        mWebView.getSettings().setJavaScriptEnabled(true);
        //  mWebView.loadDataWithBaseURL(urlPath,connectUrl , "text/html", "utf-8", null);
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
//        final ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
//        if (viewGroup != null)
//        {
//            viewGroup.removeView(mWebView);
//        }
//        mWebView.removeAllViews();
//        mWebView.destroy();
//        mWebView=null;
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

    //    @Override
    public void ScreenChangeNotify(boolean isFullScreen) {
        if (isFullScreen) {
            ViewGroup.LayoutParams lp = mAnchor.getLayoutParams();
            lp.height = dm.widthPixels;
            mAnchor.setLayoutParams(lp);
            mHeandRalayout.setVisibility(View.GONE);
            mTailRalayout.setVisibility(View.GONE);
            mTitleLayout.setVisibility(View.GONE);
            mWebLayout.setVisibility(View.GONE);
//            mediaController = mVideoView.getMediaPlayerController();
//            mediaController.setOnForwardListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ThirdPartManager.Basic_Info info = getBasicInfo();
//                    ThirdPartManager.getInstance().Forward(NewsDetailVideoActivity.this, info);
//
//                }
//            });
//
//            mediaController.setOnLikeListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String url = "/services/like_and_dislike/like.json";
//                    String id = String.valueOf(mIndex);
//                    String type = "node";
//                    Handler handler = myHander;
//                    ThumbUp.ActionInfo info = ThumbUp.PackActionInfo(url,
//                            id, type, NewsDetailVideoActivity.this);
//                    ThumbUp.TumbAction(info);
//
//                }
//            });
        } else {

            ViewGroup.LayoutParams lp = mAnchor.getLayoutParams();
            lp.height = nScreenheight;
            mAnchor.setLayoutParams(lp);
            mHeandRalayout.setVisibility(View.VISIBLE);
            mTailRalayout.setVisibility(View.VISIBLE);
            mTitleLayout.setVisibility(View.VISIBLE);
            mWebLayout.setVisibility(View.VISIBLE);

        }
    }

    class adsInfo {
        String durations;
        String urls;
    }

    public void addFavritory() {
        Account mUser = App.getInstance().getUser();
        if (mUser == null) {
            goToLogin();
            return;
        }
//
        //判断是否备受擦鞥
        JSONObject obj = UtilHelp.getJsonObject("", "");
        try {
            obj.put("flag_name", "favorites");
            obj.put("entity_id", mIndex);
            obj.put("uid", mUser.getUser().getId());
            newsDatailHelper.postIsAddedFavorite(obj, new DataRequest.DataCallback<Boolean>() {
                @Override
                public void onSuccess(boolean isAppend, Boolean data) {
                    if (data) {
                        handleSuccess("该文章已收藏！！！！");
                    } else {
                        //收藏
                        JSONObject objs = UtilHelp.getJsonObject("", "");
                        try {
                            objs.put("flag_name", "favorites");
                            objs.put("entity_id", mIndex);
                            objs.put("action", "flag");
                            objs.put("uid", App.getInstance().getUser().getUser().getId());

                            newsDatailHelper.postAddFavorite(objs, new DataRequest.DataCallback<Boolean>() {
                                @Override
                                public void onSuccess(boolean isAppend, Boolean data) {
                                    if (data) {
                                        handleSuccess("文章收藏成功！！！！");
                                    }
                                }

                                @Override
                                public void onFail(ApiException e) {
                                    Toast.makeText(context, "postAddFavorite fail", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    Toast.makeText(context, "postIsAddedFavorite fail", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View view) {
        if (view == mAppclientbtn) {
            zoomErweima(0);
        } else if (view == mAppWeiboBtn) {
            zoomErweima(1);
        }
    }

    public void zoomErweima(int postion) {
//        Intent intent = new Intent();
//        intent.putExtra(ShowWebImageActivity.IMAGE_URLS, appSites);
//        intent.putExtra(ShowWebImageActivity.POSITION, postion);
//        intent.setClass(NewsDetailVideoActivity.this, ShowWebImageActivity.class);
//        startActivity(intent);
    }

    protected class ListAdapter extends BaseAdapter implements View.OnClickListener {

        private final String STATE_LIST = "ListAdapter.mlist";
        private LayoutInflater mLayoutInflater = null;
        private ArrayList<SocirtyNewsChannel> mList = new ArrayList<SocirtyNewsChannel>();
        boolean mbInit = false;

        public ListAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
            mbInit = false;

        }

        public void DelItem(int id) {
            if (id < mList.size())
                mList.remove(id);
            notifyDataSetChanged();
        }

        public void SetInitStatus(boolean flag) {
            mbInit = flag;
        }

        public void init(Bundle savedInstanceState) {
            ArrayList<SocirtyNewsChannel> sList;
            sList = savedInstanceState.getParcelableArrayList(STATE_LIST);
            if (sList != null) {
                mList = sList;
                notifyDataSetChanged();
                mbInit = true;
            }
        }

        public void saveInstanceState(Bundle outState) {
            outState.putParcelableArrayList(STATE_LIST, mList);
        }

        public boolean isInited() {
            return mbInit;
        }

        public long getMinId() {
            return mList.isEmpty() ? -1 : mList.get(mList.size() - 1).id;
        }

        public long getMaxId() {
            return mList.isEmpty() ? -1 : mList.get(0).id;
        }

        public void update(ArrayList<SocirtyNewsChannel> data, boolean bAddTail) {

            if (bAddTail)
                mList.addAll(data);
            else
                mList = data;

            mbInit = true;
            notifyDataSetChanged();
        }

        public void getChannel(SocirtyNewsChannel chel) {
            if (!mList.isEmpty()) {
                for (int i = 0; i < mList.size(); i++) {
                    if (chel.id == mList.get(i).id) {
                        mList.get(i).newsTime = chel.newsTime;
                        mList.get(i).newsTitle = chel.newsTitle;
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return 0; // mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mList.get(position).id;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
//            ViewHolder viewHolder = null;
//            if (view == null) {
//                view = mLayoutInflater.inflate(R.layout.newsdetail_item_list, null);
//                view.setOnClickListener(this);
//                viewHolder = new ViewHolder();
//                viewHolder.titleTextView = (TextView) view.findViewById(R.id.followed_list_item_title);
//                viewHolder.createTimeTextView = (TextView) view.findViewById(R.id.followed_list_item_source);
//                viewHolder.numberTextView = (TextView) view.findViewById(R.id.followed_list_item_number);
//                viewHolder.item = mList.get(position);
//                view.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }
//            viewHolder.titleTextView.setText(viewHolder.item.newsTitle);
////            viewHolder.numberTextView.setText(viewHolder.item.newsTime);
////            viewHolder.createTimeTextView.setText(UtilHelp.GetSimpleDate(viewHolder.item.newsTime));
//            viewHolder.createTimeTextView.setText((viewHolder.item.newsTime));
//            return view;
            return null;
        }

        @Override
        public void onClick(View view) {
//            Intent intent = new Intent();
//            Bundle bundle = new Bundle();
//            ViewHolder vmHolder = (ViewHolder) view.getTag();
//            bundle.putInt("pos", vmHolder.pos);
//            bundle.putInt("index", (int) vmHolder.item.id);
////                bundle.putInt("cloumnType", mCloumnType);
//            bundle.putInt("commenNumber", vmHolder.item.commengNumber);
//            int typeId = (int) vmHolder.item.typeId;
//            if (typeId == 0 || typeId == 2) {
//                intent.setClass(view.getContext(), NewsDetailVideoActivity.class);
//                intent.putExtras(bundle);
//                startActivityForResult(intent, typeId);
//            }
        }

    }

//    protected static class ViewHolder {
//        public SocirtyNewsChannel item;
//        public int pos;
//        public TextView titleTextView;
//        public TextView createTimeTextView;
//        public TextView numberTextView;
//    }


//    protected class LoadTaskInfo extends AsyncTask<String, String, SocirtyNewsChannel> {
//        private long mBaseId;
//        boolean mbNext;
//        boolean mbAddTail = false;
//
//        LoadTaskInfo(long baseId) {
//            mBaseId = baseId;
//        }
//
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected SocirtyNewsChannel doInBackground(String... strings) {
//            SocirtyNewsChannel channel = new SocirtyNewsChannel();
//            InputStream inputStream = null;
//            try {
//                inputStream = mApi.httpGet(mApi.makeUrl("services/node/" + mBaseId + ".json"));
//                JSONObject jsonObject = null;
//                jsonObject = mApi.jsonParse(inputStream);
//                if (jsonObject != null) {
//                    String type = jsonObject.getString("type");
//                    channel.id = jsonObject.getInt("nid");
//                    channel.newsTitle = jsonObject.getString("title");
//                    long time = jsonObject.getLong("created");
//                    channel.newsTime = UtilHelp.GetSimpleDate(time);
//                }
//            } catch (ApiException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return channel;
//        }
//
//        public void onPostExecute(SocirtyNewsChannel chs) {
//            //Log.d(TAG, "get chs " + chs.size());
////            if (isResumed()) {
//            adapter.getChannel(chs);
//            if (mPullRefreshList != null)
//                mPullRefreshList.onRefreshComplete();
//        }
//    }

}
