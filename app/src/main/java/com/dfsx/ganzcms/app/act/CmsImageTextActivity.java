package com.dfsx.ganzcms.app.act;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.webkit.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.processWnd.ProcessDialog;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.BuildConfig;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.NewsReplayListAdapter;
import com.dfsx.ganzcms.app.business.TaskManager;
import com.dfsx.ganzcms.app.business.WebMediaReplacelHelper;
import com.dfsx.ganzcms.app.business.WebUrlCache;
import com.dfsx.ganzcms.app.fragment.CommendPageFragment;
import com.dfsx.ganzcms.app.fragment.NewsWebVoteFragment;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsInfoEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.MoreTextView;
import com.dfsx.ganzcms.app.view.MyCollapseeView;
import com.dfsx.lzcms.liveroom.fragment.BaseAndroidWebFragment;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.searchlibaray.SearchUtil;
import com.dfsx.thirdloginandshare.ShareCallBackEvent;
import com.dfsx.videoijkplayer.NetChecker;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.google.zxing.qrcode.decoder.Decoder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.znq.zbarcode.CaptureActivity;
import com.znq.zbarcode.ResultsActivity;
import com.znq.zbarcode.utils.DecodeUtils;
import com.znq.zbarcode.utils.ProcessDataTask;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by heyang on 2017/12/5.
 */
public class CmsImageTextActivity extends AbsCmsActivity implements View.OnLongClickListener {
    private final static String TAG = "CmsImageTextActivity";
    private NewsReplayListAdapter mReplayAdapter = null;
    private EmptyView emptyView;
    private FrameLayout fullScreenContainer;
    private TextView videoPraiseTxt, videoStrampTxt, videoTitleTx,
            bottomViewTxt;
    private MyCollapseeView summaryTxt;
    private int pageoffset = 1;
    private ImageView favorityBtn, praiseMark, shareBtn, backBtn, attionbtn;
    private LinearLayout _videoRalationContainer;
    private View _commnedNoplayLayout, commendBtn;
    private ContentCmsInfoEntry _gEntry;
    private long mPraiseNumber, mStrmpNumber;
    private MoreTextView _morePraisetv;
    private WebView _webView;
    private WebMediaReplacelHelper webRepHelper;
    private Subscription shareSubscription;
    private IsLoginCheck mloginCheck;
    private WebUrlCache mWebUrlChe;
    NetChecker mNetChecker;
    private TextView useNameTv, userTimetv, mtoppubTimeTv;
    private View bottomlayout, _swicthBtn;
    View tailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mloginCheck = _newsHelper.getMloginCheck();
        mNetChecker = new NetChecker(this, mCheckCallBack);
        webRepHelper = new WebMediaReplacelHelper(act);
        mWebUrlChe = new WebUrlCache(this);
        initWebView();
        iniRegister();
        initdata();
    }

    public void initdata() {
        emptyView.loading();
        try {
            ContentCmsInfoEntry entry = _newsHelper.readWebCacheData(this, _cmsId);
            if (entry != null && entry instanceof ContentCmsInfoEntry) {
                _gEntry = entry;
                initParams(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        myHander.postDelayed(new Runnable() {
            @Override
            public void run() {
                getContentInfo();
                getCommendData(pageoffset);
            }
        }, 50);
    }

    private void iniRegister() {
        shareSubscription = RxBus.getInstance().toObserverable(ShareCallBackEvent.class)
                .subscribe(new Action1<ShareCallBackEvent>() {
                    @Override
                    public void call(ShareCallBackEvent shareCallBackEvent) {
                        if (shareCallBackEvent != null) {
                            myHander.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    TaskManager.uploadShareNewsTask(context);
                                }
                            }, 100);
                        }
                    }
                });
    }

    public void getContentInfo() {
        _newsHelper.getCotentInfo(_cmsId, "default", new Action1<ContentCmsInfoEntry>() {
            @Override
            public void call(ContentCmsInfoEntry data) {
                if (data != null && data.getId() != -1 && data.getId() != 0) {
                    ((View) bottomlayout.getParent()).setVisibility(View.VISIBLE);
                    ((View) emptyView.getParent()).setVisibility(View.GONE);
                    _gEntry = data;
                    initParams(data);
                    emptyView.setVisibility(View.GONE);
                    myHander.post(new Runnable() {
                        @Override
                        public void run() {
                            _newsHelper.saveWebCacheData(context, _cmsId, _gEntry);
                        }
                    });
                } else {
                    if (!isConectNet) {
                        ((View) bottomlayout.getParent()).setVisibility(View.GONE);
                    }
                    if (_gEntry == null) {
                        emptyView.loadOver();
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        ((View) bottomlayout.getParent()).setVisibility(View.VISIBLE);
                        ((View) emptyView.getParent()).setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public void initParams(final ContentCmsInfoEntry data) {
        if (data == null || context == null) return;
        mPraiseNumber = data.getLike_count();
        mStrmpNumber = data.getDislike_count();
        videoPraiseTxt.setText(mPraiseNumber + "");
        videoStrampTxt.setText(mStrmpNumber + "");
        videoTitleTx.setText(data.getTitle());
        String pralistList = data.getPraiseList();
        updatePraiseUiList(_morePraisetv, pralistList);
        if (data.isAttend())
            setAttteonTextStatus(attionbtn, true, false);
        if (data.getRaletionList() != null && !data.getRaletionList().isEmpty()) {
            myHander.post(new Runnable() {
                @Override
                public void run() {
                    initRelationData(data.getRaletionList());
                }
            });
        } else {
            ((View) _videoRalationContainer.getParent()).setVisibility(View.GONE);
        }
        if (data.getBody() != null && !TextUtils.isEmpty(data.getBody())) {
            webRepHelper.set_geComtenInfo(_gEntry);
            String body = webRepHelper.getHtmlWeb(data.getBody());
            loadDataFromText(body);
        }
        userTimetv.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", data.getPublish_time() * 1000));
        UtilHelp.setTimeDate(mtoppubTimeTv, data.getPublish_time());
//        if (LanguageUtil.isTibetanLanguage(this)) {
//            mtoppubTimeTv.setText(Util.getTimeString("yyyy-MM-dd HH:mm", data.getPublish_time()));
//        } else {
//            mtoppubTimeTv.setText(UtilHelp.getTimeFormatText("yyyy年MM月dd日 HH:mm", data.getPublish_time() * 1000));
//        }
        useNameTv.setText(data.getAuthor_nickname());
        UtilHelp.setViewCount(bottomViewTxt, data.getView_count());
//        bottomViewTxt.setText(data.getView_count()  + getResources().getString(R.string.news_item_viewcountex_hit));
        setFavStatus(favorityBtn, data.isFav(), false);
        if (data.getSummary() != null && !TextUtils.isEmpty(data.getSummary().toString().trim())) {
            SpannableString apnnser = new SpannableString(getResources().getString(R.string.news_act_daodu_hit) + ": " + data.getSummary().toString().trim());
//            SpannableString apnnser = new SpannableString(getResources().getString(R.string.news_act_daodu_hit) + ": " + yu);
            StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);
            apnnser.setSpan(styleSpan_B, 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            summaryTxt.setText(apnnser.toString());
        } else {
            View parent = (View) summaryTxt.getParent();
            if (parent != null)
                parent.setVisibility(View.GONE);
        }
    }

    public void loadDataFromText(String body) {
        if (_webView == null) return;
        _webView.resumeTimers();
        _webView.loadDataWithBaseURL("file:///android_asset/", body, "text/html", "utf-8", null);
    }

    private void getCommendData(int pageOffset) {
        _newsHelper.getCommendList(_cmsId, pageOffset, new DataRequest.DataCallback<List<CommendCmsEntry>>() {
            @Override
            public void onSuccess(boolean isAppend, List<CommendCmsEntry> data) {
                if (isAppend)
                    tailView.setVisibility(View.VISIBLE);
                if (mReplayAdapter != null) {
                    mReplayAdapter.update(data, isAppend);
                }
                if (data != null && data.size() > 0) {
                    _commnedNoplayLayout.setVisibility(View.GONE);
                } else {
                    if (!isAppend)
                        tailView.setVisibility(View.GONE);
                    if (!mReplayAdapter.isHas() && isAppend)
                        tailView.setVisibility(View.GONE);
                    if (data.isEmpty() && !isAppend)
                        tailView.setVisibility(View.GONE);
                }
                pullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                pullToRefreshListView.onRefreshComplete();
            }
        });
    }

//    protected PullToRefreshBase.Mode getListViewMode() {
//        return PullToRefreshBase.Mode.BOTH;
//    }

    public void setListAdapter(ListView listView) {
        mReplayAdapter = new NewsReplayListAdapter(act);
        listView.setAdapter(mReplayAdapter);
        mReplayAdapter.setCallback(new IButtonClickListenr<CommendCmsEntry>() {
            @Override
            public void onLbtClick(int type, IButtonClickData<CommendCmsEntry> data) {
                CommendCmsEntry entry = data.getObject();
                if (entry == null) return;
                switch (type) {
                    case IButtonClickType.ITEM_CLICK:
                        if (entry.getSub_comment_count() == 0) {
                            ToastUtils.toastNoCommendFunction(act);
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putLong("itemId", _cmsId);
                        bundle.putLong("subId", entry.getId());
                        bundle.putLong("praiseNumer", entry.getLike_count());
                        String title = entry.getSub_comment_count() + "个回复";
//                        WhiteTopBarActivity.startAct(context, CommendPageFragment.class.getName(), title, "", bundle);
                        DefaultFragmentActivity.start(context, CommendPageFragment.class.getName(), bundle);
                        break;
                    case IButtonClickType.COMMEND_CLICK:
                        View v = data.getTag();
                        onCommendBtn(v, entry.getId());
                        break;
                    case IButtonClickType.PRAISE_CLICK:
                        View view = data.getTag();
                        _newsHelper.praiseforCmsCommend(view, entry.getId(), new DataRequest.DataCallbackTag() {
                            @Override
                            public void onSuccess(Object object, boolean isAppend, Object data) {
                                if ((boolean) data) {
                                    getCommendData(pageoffset);
                                }
                            }

                            @Override
                            public void onSuccess(boolean isAppend, Object data) {

                            }

                            @Override
                            public void onFail(ApiException e) {
                                e.printStackTrace();
                                ToastUtils.toastApiexceFunction(context, e);
                            }
                        });
                        break;
                }
            }
        });
        View headView = initHeadTop();
        headView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.MATCH_PARENT));
        listView.addHeaderView(headView);
        tailView = initTailVIew();
        listView.addFooterView(tailView);
        listView.setSelectionFromTop(0, 1);
    }

    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        View eV = LayoutInflater.from(context).
                inflate(R.layout.no_default_frg_layout, null);
        emptyView.setLoadOverView(eV);
        emptyView.loading();
        container.addView(emptyView);
        emptyView.setOnClickListener(null);
        ImageView btnStart = (ImageView) eV.findViewById(R.id.retyr_btn);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initdata();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (_webView != null) {
            _webView.loadUrl("javascript:stopvideo()");
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageoffset = 1;
        getCommendData(pageoffset);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        tailView.setVisibility(View.GONE);
        pageoffset++;
        getCommendData(pageoffset);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //do something...
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                //                InputMethodManager imm = (InputMethodManager) CvideoPlayAct.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm.isActive()) {
//                    imm.hideSoftInputFromWindow(mReplyContent.getWindowToken(), 0);
//                }
                if (customView != null) {
                    hideCustomView();
                }
//                else if (mWebView.canGoBack()) {
//                    mWebView.goBack();
//                }
                else {
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public View initHeadTop() {
        View headLayout = getLayoutInflater().from(context).inflate(R.layout.news_imagetxt_header, null);
        _videoRalationContainer = (LinearLayout) headLayout.findViewById(R.id.video_ralation_container);
        useNameTv = (TextView) headLayout.findViewById(R.id.author_user_name);
        userTimetv = (TextView) headLayout.findViewById(R.id.author_time_tx);
        mtoppubTimeTv = (TextView) headLayout.findViewById(R.id.news_pubtime_tv);
        videoTitleTx = (TextView) headLayout.findViewById(R.id.newsDetailss_title);
        summaryTxt = (MyCollapseeView) headLayout.findViewById(R.id.news_summary_text);
        summaryTxt.setCollapsedLines(3);
        summaryTxt.setCollapsedText(getResources().getString(R.string.news_act_shou_hit));
        summaryTxt.setExpandedText(getResources().getString(R.string.news_act_expend_hit));
        summaryTxt.setTipsColor(0xff5193EA);
        _webView = (WebView) headLayout.findViewById(R.id.webView_content);
        _webView.setOnLongClickListener(this);
        videoPraiseTxt = (TextView) headLayout.findViewById(R.id.cvideo_praise_tx);
        videoPraiseTxt.setOnClickListener(this);
        videoStrampTxt = (TextView) headLayout.findViewById(R.id.cvideo_stamp_tx);
        videoStrampTxt.setOnClickListener(this);
        attionbtn = (ImageView) headLayout.findViewById(R.id.cvideo_addatt_img);
        attionbtn.setOnClickListener(this);
        praiseMark = (ImageView) headLayout.findViewById(R.id.praise_mark);
        _morePraisetv = (MoreTextView) headLayout.findViewById(R.id.mulite_line_txt);
        _commnedNoplayLayout = (View) headLayout.findViewById(R.id.comnend_noplay_layout);
        bottomViewTxt = (TextView) headLayout.findViewById(R.id.communtiry_brower_count_tx);
        _swicthBtn = (View) headLayout.findViewById(R.id.switch_btn);
        _swicthBtn.setOnClickListener(this);
        return headLayout;
    }

    public View initTailVIew() {
        View tailView = getLayoutInflater().from(context).inflate(R.layout.cms_tail_bar, null);
        return tailView;
    }

    protected void setBottomView(FrameLayout bottomListViewContainer) {
        bottomlayout = getLayoutInflater().from(context).inflate(R.layout.news_commend_bottom_custom, null);
        commendBtn = bottomlayout.findViewById(R.id.cvideo_bottom_commend);
        commendBtn.setOnClickListener(this);
        favorityBtn = (ImageView) bottomlayout.findViewById(R.id.cvideo_isfav_img);
        favorityBtn.setOnClickListener(this);
        shareBtn = (ImageView) bottomlayout.findViewById(R.id.cvido_share_img);
        shareBtn.setOnClickListener(this);
        bottomListViewContainer.addView(bottomlayout);
    }

    protected void setFullView(FrameLayout fullContainerViewContainer) {
        fullScreenContainer = new FrameLayout(act);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        fullScreenContainer.setLayoutParams(lp);
        fullContainerViewContainer.addView(fullScreenContainer);
    }

    protected void setTopView(FrameLayout topListViewContainer) {
        View top = getLayoutInflater().from(context).inflate(R.layout.common_setting_bar, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.dp2px(act, 40));
        backBtn = (ImageView) top.findViewById(R.id.backer_finish);
        backBtn.setOnClickListener(this);
        topListViewContainer.addView(top, lp);
    }

    private View createRelationItem(ContentCmsEntry entry) {
//        if (isFinishing()) return
        View childView = getLayoutInflater().from(context).inflate(R.layout.news_news_list_item, null);
        TextView titleTextView = (TextView) childView.findViewById(R.id.news_list_item_title);
        ImageView thumbnailImageView = (ImageView) childView.findViewById(R.id.news_news_list_item_image);
        TextView ctimeTextView = (TextView) childView.findViewById(R.id.news_list_item_time);
        TextView commentNumberTextView = (TextView) childView.findViewById(R.id.news_list_item_command_tx);
        if (entry != null) {
            String title = SearchUtil.getShowTitleHtmlString(entry.getTitle(),
                    entry.getSearchItemInfo());
            titleTextView.setText(Html.fromHtml(title));
            UtilHelp.setViewCount(commentNumberTextView, entry.getView_count());
            UtilHelp.setTimeDate(ctimeTextView, entry.getPublish_time());
            String imageUrl = "";
            if (entry.getThumbnail_urls() != null && entry.getThumbnail_urls().size() > 0) {
                imageUrl = entry.getThumbnail_urls().get(0).toString();
            }
            Glide.with(act)
                    .load(imageUrl)
                    .asBitmap()
                    .error(R.drawable.glide_default_image)
                    .into(thumbnailImageView);
//            Util.LoadThumebImage(thumbnailImageView, imageUrl, null);
            childView.setTag(entry);
        }
        childView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null) {
                    ContentCmsEntry entry = (ContentCmsEntry) v.getTag();
                    _newsHelper.goDetail(entry);
                }
            }
        });
        return childView;
    }

    NetChecker.CheckCallBack mCheckCallBack = new NetChecker.CheckCallBack() {
        @Override
        public void callBack(boolean isCouldPlay, Object tag) {
            if (isCouldPlay) {
                myHander.post(new Runnable() {
                    public void run() {
                        _webView.loadUrl("javascript:startvideo()");
                    }
                });
            }
        }
    };

    public void initWebView() {
        WebSettings webSettings = _webView.getSettings();
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
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
        _webView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
        _webView.setHorizontalScrollBarEnabled(false);
        _webView.setVerticalScrollBarEnabled(false);
        DvWebViewClient webClient = new DvWebViewClient();
        _webView.setWebViewClient(webClient);
        _webView.setWebChromeClient(new WebChromeClient() {
            private View xprogressvideo;

            //视频加载时进程loading
            @Override
            public View getVideoLoadingProgressView() {
//                FrameLayout frameLayout = new FrameLayout(CvideoPlayAct.this);
//                frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
//                return frameLayout;
                if (xprogressvideo == null) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    xprogressvideo = inflater.inflate(R.layout.video_loading_progress, null);
                }
                return xprogressvideo;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }

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

    /**
     * 隐藏视频全屏
     */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setStatusBarVisibility(true);
        customView.setVisibility(View.GONE);
        fullScreenContainer.removeView(customView);
//        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
//        decor.removeView(video_fullView);
        customView = null;
        fullScreenContainer.setVisibility(View.GONE);
        super.setPortLayoutContainer(false);
        customViewCallback.onCustomViewHidden();
        _webView.setVisibility(View.VISIBLE);
//        mListView.smoothScrollToPosition(0);
    }

    //长按获取图中二维码
    @Override
    public boolean onLongClick(View v) {
        final WebView.HitTestResult htr = _webView.getHitTestResult();//获取所点击的内容
        if (htr.getType() == WebView.HitTestResult.IMAGE_TYPE) {//判断被点击的类型为图片
            Log.e("onLongClick: ", " " + htr.getExtra());
            showDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    decodeRQCode(htr.getExtra());
                }
            });
        }
        return false;
    }

    private void showDialog(DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(this).setItems(new String[]{"识别图中二维码"}, listener).create().show();
    }

    private void decodeRQCode(String url){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("识别中...");
        dialog.show();
        DecodeUtils.getInstance().decodeUrl(url, new ProcessDataTask.DecodeListener() {
            @Override
            public void decodeResult(String result) {
                dialog.dismiss();
                if (TextUtils.isEmpty(result)){
                    ToastUtils.toastMsgFunction(CmsImageTextActivity.this,"未识别出内容...");
                }else {
                    Intent intent = new Intent(CmsImageTextActivity.this, ResultsActivity.class);
                    intent.putExtra(CaptureActivity.EXTRA_STRING, result);
                    startActivity(intent);
                }
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

        //for api17+
        @android.webkit.JavascriptInterface
        public boolean isWifiStatus() {
            //           Toast.makeText(CvideoPlayAct.this, "js 调用java", Toast.LENGTH_SHORT).show();
            return isWifi;
        }

        @android.webkit.JavascriptInterface
        public void showPlayDialog() {
            if (mNetChecker != null) {
                mNetChecker.checkNet(null);
            }
            //          Toast.makeText(CvideoPlayAct.this, "js 调用showPlayDialog", Toast.LENGTH_SHORT).show();
        }

        @android.webkit.JavascriptInterface
        public String version() {
            return App.getInstance().getBaseUrl();
        }

        @android.webkit.JavascriptInterface
        public boolean isLogon() {
            return App.getInstance().getUser() != null ? true : false;
        }

        @android.webkit.JavascriptInterface
        public String getToken() {
            return App.getInstance().getCurrentToken();
        }

        @android.webkit.JavascriptInterface
        public void gotoLogon() {
            if (!mloginCheck.checkLogin()) return;
        }

    }

    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one

        _webView.setVisibility(View.INVISIBLE);

        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

//        CvideoPlayAct.this.getWindow().getDecorView();
//        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
//        video_fullView = new FullscreenHolder(CvideoPlayAct.this);
        fullScreenContainer.addView(view);
//        decor.addView(video_fullView, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
        super.setPortLayoutContainer(true);
        fullScreenContainer.setVisibility(View.VISIBLE);
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void cleareWebView() {
        if (_webView == null) return;
        _webView.clearCache(true);
        _webView.clearHistory();
        _webView.removeAllViews();
        final ViewGroup viewGroup = (ViewGroup) _webView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(_webView);
        }
        _webView.destroy();
        _webView = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareSubscription != null)
            shareSubscription.unsubscribe();
//        NetworkChangeManager.getInstance().
//                removeOnNetworkChangeListener(_onNetworkChangeListener);
        cleareWebView();
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.image_click));
        if (v == videoPraiseTxt) {
            onPraisBtn();
        } else if (v == videoStrampTxt) {
            onStrampBtn();
        } else if (v == _swicthBtn) {
            _newsHelper.getRelationCotentList(_cmsId, "default", new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
                @Override
                public void onSuccess(boolean isAppend, final ArrayList<ContentCmsEntry> data) {
                    myHander.post(new Runnable() {
                        @Override
                        public void run() {
                            initRelationData(data);
                        }
                    });
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                }
            });
        } else if (v == backBtn) {
            finish();
        } else if (v == commendBtn) {
            onCommendBtn(v, -1);
        } else if (v == favorityBtn) {
            onFavorityBtn();
        } else if (v == shareBtn) {
            onShareWnd(v, _gEntry);
        } else if (v == attionbtn) {
            onFollowMe();
        }
    }

    public void onFollowMe() {
        if (_gEntry == null) return;
        addFollowed(_gEntry.getAuthor_id(), _gEntry.isAttend(), new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                if ((boolean) data) {

                    RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
                    boolean isAttionAuthor = !_gEntry.isAttend();
                    _gEntry.setAttend(isAttionAuthor);
                    RXBusUtil.sendConcernChangeMessage(isAttionAuthor, 1);
                    setAttteonTextStatus(attionbtn, isAttionAuthor, true);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                ToastUtils.toastApiexceFunction(act, e);
            }
        });
    }

    public void onCommendBtn(View v, long ref_id) {
        if (_gEntry == null) return;
        writeCommendbtn(v, _cmsId, ref_id, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                ToastUtils.toastReplayOk(context);
//                LSUtils.toastMsgFunction(context, "发表评论成功");
                getCommendData(1);
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                ToastUtils.toastMsgFunction(act, JsonCreater.getErrorMsgFromApi(e.toString()));
            }
        });
    }

    public void onFavorityBtn() {
        if (_gEntry == null) return;
        final boolean isFav = _gEntry.isFav();
        addFavoritybtn(_cmsId, !isFav, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                if ((boolean) data) {
                    _gEntry.setIsFav(!isFav);
                    setFavStatus(favorityBtn, !isFav, true);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                ToastUtils.toastMsgFunction(act, JsonCreater.getErrorMsgFromApi(e.toString()));
            }
        });
    }

    public void onPraisBtn() {
        if (_gEntry == null) return;
        addPraisebtn(_cmsId, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                mPraiseNumber++;
                lbtnClickAnimal(videoPraiseTxt, mPraiseNumber);
//                mIGetPraistmp.updateValuse(mIndex, true, false, false);
                updatePraiseList(_cmsId, new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String data) {
                        updatePraiseUiList(_morePraisetv, data);
                    }
                });
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                ToastUtils.toastMsgFunction(act, JsonCreater.getErrorMsgFromApi(e.toString()));
            }
        });
    }

    public void onStrampBtn() {
        addStrampbtn(_cmsId, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                mStrmpNumber++;
                lbtnClickAnimal(videoStrampTxt, mStrmpNumber);
//                mIGetPraistmp.updateValuse(mIndex, false, true, false);
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                ToastUtils.toastMsgFunction(act, JsonCreater.getErrorMsgFromApi(e.toString()));
            }
        });
    }

    private void initRelationData(List<ContentCmsEntry> data) {
        if (!(data == null || data.isEmpty()))
            if (data != null && !data.isEmpty()) {
                ((View) _videoRalationContainer.getParent()).setVisibility(View.VISIBLE);
                if (_videoRalationContainer != null && _videoRalationContainer.getChildCount() > 0)
                    _videoRalationContainer.removeAllViews();
                int i = 0;
                for (; i < data.size(); i++) {
                    ContentCmsEntry enytry = data.get(i);
                    View child = createRelationItem(enytry);
                    _videoRalationContainer.addView(child);
                }
            }
    }

    @Override
    public void addVideoPlayerToContainer(VideoPlayView videoPlayer) {

    }

    @Override
    public void addVideoPlayerToFullScreenContainer(VideoPlayView videoPlayer) {

    }

    /**
     * 重写
     */
    public class DvWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl("javascript:imgResize()");
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//            return super.shouldInterceptRequest(view, request);
            //获取本地的URL主域名
            String curDomain = request.getUrl().getHost();

            //这行LOG可以不看
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "curDomain " + curDomain + " request headers " + request.getRequestHeaders());
                for (Map.Entry<String, String> entry : request.getRequestHeaders().entrySet()) {
                    Log.d(TAG, "key=" + entry.getKey() + " #####value=" + entry.getValue() + "\n");
                }
            }
//取不到domain就直接返回，把接下俩的动作交给webview自己处理
//            if (curDomain == null || !isPicUrl(curDomain)) {
//                return null;
//            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "shouldInterceptRequest url " + request.getUrl().toString());
            }
            //读取当前webview正准备加载URL资源
            String url = request.getUrl().toString();

            //取不到domain就直接返回，把接下俩的动作交给webview自己处理
            if (curDomain == null || !mWebUrlChe.isPicUrl(url)) {
                return null;
            }
            try {
                //根据资源url获取一个你要缓存到本地的文件名，一般是URL的MD5
                String resFileName = getResSourceName(url);
                if (resFileName == null || "".equals(resFileName)) {
                    return null;
                }
                //这里是处理本地缓存的URL，缓存到本地，或者已经缓存过的就直接返回而不去网络进行加载
                mWebUrlChe.register(url, getResSourceName(url),
                        request.getRequestHeaders().get("Accept"), "UTF-8", mWebUrlChe.ONE_MONTH);
                return mWebUrlChe.load(url);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
            return null;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("TAG", "web request url == " + url);
            if (!TextUtils.isEmpty(url) && (url.startsWith("http") ||
                    url.startsWith("https"))) {
                try {
                    Bundle bundles = new Bundle();
                    bundles.putString(BaseAndroidWebFragment.PARAMS_URL, url);
                    WhiteTopBarActivity.startAct(context, NewsWebVoteFragment.class.getName(), "", "",
                            bundles);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    public String getResSourceName(String url) {
        String cach_name = "";
        if (url != null && !TextUtils.isEmpty(url)) {
            int index = url.lastIndexOf("/");
            int endixn = url.lastIndexOf(".");
            if (index != -1 && endixn != -1) {
                cach_name = url.substring(index + 1, endixn);
            }
        }
        return cach_name;
    }

}
