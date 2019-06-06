package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import com.dfsx.core.common.Util.*;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.BuildConfig;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.fragment.CommendPageFragment;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.EditTextEx;
import com.dfsx.ganzcms.app.view.MoreTextView;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.thirdloginandshare.ShareCallBackEvent;
import com.dfsx.thirdloginandshare.share.*;
import com.dfsx.videoijkplayer.NetChecker;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.dfsx.videoijkplayer.util.NetworkChangeManager;
import com.dfsx.videoijkplayer.util.NetworkChangeReceiver;
import com.dfsx.videoijkplayer.util.NetworkUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by heyang on 2016/12/31
 */

public class CvideoPlayAct extends AbsVideoActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, View.OnTouchListener,
        GestureDetector.OnGestureListener, View.OnClickListener, EditChangedLister.EditeTextStatuimpl {
    private static final String TAG = "CvideoPlayAct";
    private static final String NET_CNNECY_TIME = "com.dfsx.core.exception.ApiException: java.net.SocketTimeoutException: connect timed out";
    private Context context;
    private WebView mWebView;
    private ImageView mShareBtn, mFavorityBtn;
    private long mIndex = -1;
    private final int NETWORK_BUSY = 13;
    private CustomeProgressDialog progressDialog;
    private String connectUrl;
    GestureDetector mGestureDetector;
    //网络请求连接
    private DisplayMetrics dm;
    private static final int HTMLWEB_TYPE = 1;
    private static final int VIDEOENABLEWEB_TYPE = 0;
    private int meWebTYPE = 0;
    private long mAver_id = -1;
    int nScreenheight = 0, nScreenWidth = 0;
    private ViewGroup mAnchor;
    RelativeLayout mloadFailurelay, mHeandRalayout, mTitleLayout, mWebLayout, mVideoLayout;
    private android.view.animation.Animation animation;
    String mPosterPath, mVideoPath;
    boolean isGetDateflag = false;
    PullToRefreshListView mPullRefreshList;
    ReplayAdapter mReplayAdapter = null;
    ListView mListView;
    boolean isComparehtml = false;
    private View porLayout, headLayout;
    private FrameLayout fullScreenContainer, video_fullView;
    private NewsDatailHelper newsDatailHelper;
    TopicalApi mTopicalApi = null;
    ContentCmsApi mContentCmApi = null;
    LinearLayout mBodyImgs;
    TextView mAuthorNameTx, mCommendNumberTx, mBrowerNumberTx;
    View commnedNoplayLayout;
    private int offset = 1;
    EditText mReplyContent;
    PopupWindow mMorePopupWindow;
    TextView mTxthtmlTitle, mPubauthoreTx, mTxtViewcount;
    ImageButton mBtnback, mBtnSend, mReLoadBtn;
    CircleButton mTopHeader, mCirbtnpub, mHeadImgView;
    ImageView mBtnAddAttion, mPlayVide0btn;
    ContentCmsInfoEntry mCotentInfoeny;
    boolean isAttionAuthor = false;
    View mViewbotmcommmed;
    SharePopupwindow sharePopupwindow;
    View rootView, mViewTopLayout, mVideoOperView, mOtherView;
    TextView mTopAuthor, mTopAutime;
    ImageView videoPraiseBtn, videoStrampBtn, mPosterImagView;
    TextView newsPrsieBtn, newsStrampBtn, videoPraiseTxt, videoStrampTxt, videoTitleTx;
    long mPraiseNumber, mStrmpNumber;
    IGetPraistmp mIGetPraistmp = null;
    NetChecker mNetChecker;
    boolean isWifi = true;
    private IsLoginCheck mloginCheck;
    private Subscription shareSubscription;
    private WebUrlCache mWebUrlChe;
    MoreTextView _moreLineText;
    private TextView _newSummarytxt;


    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (CvideoPlayAct.this == null)
                return false;
            if (message.what == NETWORK_BUSY) {
                if (CvideoPlayAct.this != null) {
                    UtilHelp.getConfirmDialog(CvideoPlayAct.this, "网络繁忙，是否重新加载数据....?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LoadDataFormURl();
                        }
                    }).show();
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
//                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private DataFileCacheManager<ArrayList<CommendCmsEntry>> dataCommendManager = new
            DataFileCacheManager<ArrayList<CommendCmsEntry>>
                    (App.getInstance().getApplicationContext(),
                            mIndex + "", App.getInstance().getPackageName() + "ccvi_commend.txt") {
                @Override
                public ArrayList<CommendCmsEntry> jsonToBean(JSONObject jsonObject) {
                    return mContentCmApi.getRootCommendListFromJson(jsonObject);
                }
            };

    private DataRequest.DataCallback<ArrayList<CommendCmsEntry>>
            callback = new DataRequest.DataCallback<ArrayList<CommendCmsEntry>>() {
        @Override
        public void onSuccess(final boolean isAppend, ArrayList<CommendCmsEntry> data) {
            if (isAppend && (data == null || data.isEmpty())) {

            } else {
                if (data != null && !data.isEmpty()) {
                    Observable.from(data)
                            .subscribeOn(Schedulers.io())
                            .map(new Func1<CommendCmsEntry, CommendCmsEntry>() {
                                @Override
                                public CommendCmsEntry call(CommendCmsEntry topicalEntry) {
                                    if (topicalEntry.getSub_comment_count() > 0) {
//                                        List<CommendCmsEntry.SubCommentsBean>  list=mContentCmApi.getSubCommendListFromJson();c
                                    }
                                    return topicalEntry;
                                }
                            })
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<List<CommendCmsEntry>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(List<CommendCmsEntry> data) {
                                    mReplayAdapter.update((ArrayList<CommendCmsEntry>) data, isAppend);
                                }
                            });
                }
                mReplayAdapter.update(data, isAppend);
            }
            if (data != null && data.size() > 0) {
                commnedNoplayLayout.setVisibility(View.GONE);
            } else {
                commnedNoplayLayout.setVisibility(View.VISIBLE);
            }
            mPullRefreshList.onRefreshComplete();
        }

        @Override
        public void onFail(ApiException e) {
            e.printStackTrace();
            mPullRefreshList.onRefreshComplete();
        }
    };

    private void getData(int offset) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/contents/" + mIndex + "/root-comments?";
        url += "page=" + offset + "&size20&sub_comment_count=3";
        dataCommendManager.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url).setToken(App.getInstance().getCurrentToken())
                .build(), offset > 1).setCallback(callback);
    }

    private void showWebviewData(ContentCmsInfoEntry webData) {
        if (CvideoPlayAct.this != null) {
            if (!isComparehtml) {
                mListView.setVisibility(View.VISIBLE);
                isGetDateflag = false;
                if (!TextUtils.equals(webData.getType(), "video")) mHeandRalayout.setVisibility(View.VISIBLE);
                mloadFailurelay.setVisibility(View.GONE);
                mTxthtmlTitle.setText(webData.getTitle());
                mTopAutime.setText(UtilHelp.getTimeString("yyyy/MM/dd hh:mm", webData.getPublish_time()));
                mPraiseNumber = webData.getLike_count();
                mStrmpNumber = webData.getDislike_count();
                mVideoPath = webData.getUrl();
                if (isNewsType()) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    newsPrsieBtn.setText(mPraiseNumber + "");
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    videoTitleTx.setText(webData.getTitle());
                    videoPraiseTxt.setText(mPraiseNumber + "");
                }
                String praiseList = webData.getPraiseList();
                if (praiseList != null && !TextUtils.isEmpty(praiseList)) {
                    _moreLineText.setText(webData.getPraiseList());
                } else
                    _moreLineText.setVisibility(View.GONE);
                mTxtViewcount.setText(webData.getView_count() + "次播放");
                mBrowerNumberTx.setText(webData.getView_count() + "次浏览");
                if (isNewsType()) {
                    newsStrampBtn.setText(mStrmpNumber + "");
                    if (webData.getSummary() != null &&
                            !TextUtils.isEmpty(webData.getSummary())) {
                        View parent = (View) _newSummarytxt.getParent();
                        if (parent != null) {
                            parent.setVisibility(View.VISIBLE);
                            _newSummarytxt.setText("\b\b\b\b" + webData.getSummary());
                        }
                    }
                } else {
                    videoStrampTxt.setText(mStrmpNumber + "");
                }
                String avatal_url = webData.getAuthor_avatar_url();
                LSLiveUtils.showUserLogoImage(context, mCirbtnpub, avatal_url);
                LSLiveUtils.showUserLogoImage(context, mTopHeader, avatal_url);
                mCirbtnpub.setTag(R.id.cirbutton_user_id, webData.getAuthor_id());
                mPubauthoreTx.setText(webData.getAuthor_nickname());
                mTopAuthor.setText(webData.getAuthor_nickname());
                if (!("").equals(mVideoPath) && mVideoPath != null) {
                    String videoThumb = webData.getVideoThumb();
                    if (videoThumb != null)
                        mPosterPath = videoThumb;
                    mViewTopLayout.setVisibility(View.GONE);
                    mVideoLayout.setVisibility(View.VISIBLE);
                    mPlayVide0btn.setVisibility(View.VISIBLE);
                    int  height=Util.dp2px(this,211);
                    mPosterPath+="?&h=" + height + "&s=1";
                    Util.LoadThumebImage(mPosterImagView, mPosterPath, null);
                    mVideoOperView.setVisibility(View.VISIBLE);
                    mOtherView.setVisibility(View.GONE);
                } else {
                    mVideoLayout.setVisibility(View.GONE);
                }
                setFavStatus(webData.isFav(), false);
                if (mWebView != null && !TextUtils.isEmpty(webData.getBody())) {
                    connectUrl = getHtmlWeb(webData.getBody());
                    loadDataFromText();
                }
                isLoadDialog();
            }
        }
    }

    public String pearseImageString(long id) {
        String newLine = "";
        if (mCotentInfoeny.getGroupimgs() != null && mCotentInfoeny.getGroupimgs().size() > 0) {
            for (ContentCmsInfoEntry.GroupImgsBean bean : mCotentInfoeny.getGroupimgs()) {
                if (id == bean.getId()) {
                    String path = bean.getUrl();
//                    path += "?w=" + nScreenWidth + "&s=1";
                    int dpWidth = Util.dp2px(context, bean.getWidth());
                    int dpHeight = Util.dp2px(context, bean.getHeight());
//                    int dpWidth = bean.getWidth();
                    if (dpWidth >= nScreenWidth) {
                        path += "?w=" + nScreenWidth + "&s=0";
                        newLine += "<p><img src=\"" + path + "\" height=\"auto\" width=\"100%\" /></p>";
                    } else {
                        path += "?w=" + dpWidth + "&h=" + dpHeight;
                        newLine += "<p><img src=\"" + path + "\" height=\"" + bean.getHeight() + "\" width=\"" + bean.getWidth() + "\" /></p>";
                    }
//                    path += "?w=" + nScreenWidth + "&s=0";
//                    newLine += "<p><img src=\"" + path + "\" height=\"auto\" width=\"100%\" /></p>";
                    break;
                }
            }
        }
        return newLine;
    }

    public String pearseVideoString(long id, String width, String height) {
        String newLine = "";
        if (mCotentInfoeny.getVideoGroups() != null && mCotentInfoeny.getVideoGroups().size() > 0) {
            for (ContentCmsInfoEntry.VideosBean bean : mCotentInfoeny.getVideoGroups()) {
                if (id == bean.getId()) {
                    if (bean.getVersions() == null) break;
//                    newLine += "<p><video src=\"" + bean.getVersions().get(0).getUrl()+ "\"  poster=\"" + bean.getCoverUrl()+ "\"  height=auto  width=100%  controls /></p>";
//                    width:100%;height:auto;
                    //                newLine += "<div class=\"playvideo\" >";
                    newLine += "<div style=\"position:relative;overflow: hidden;\"><div><video  poster=\"" + bean.getCoverUrl() + "\" />";
                    newLine += "<source src=\"" + bean.getVersions().getUrl() + "\" type=\"video/mp4\" >";
                    newLine += "您的浏览器不支持HTML5视频";
                    newLine += "</video></div>";
                    newLine += "<span class=\"play-video-btn\"></span></div>";
//                    newLine += "<div id=\"modelView\">&nbsp;</div>\n" +
//                            "</div>";

//                    newLine += "<script>\n" +
//                            "//document.documentElement.style.overflow='hidden';\n" +
//                            "document.body.style.overflow='hidden';\n" +
//                            "zymedia('video',{autoplay: true});\n" +
//                            "var screenheight = window.screen.height/2;\n" +
//                            "$(\"#modelView\").width(window.screen.width);\n" +
//                            "$(\"#modelView\").height(window.screen.height);\n" +
//                            "var videoheight = $(\".zy_media\").height()/2;\n" +
//                            "var padding_top = screenheight-videoheight;\n" +
//                            "$(\".playvideo\").css({\"top\":padding_top});\n" +
//                            "$(\"#modelView\").css({\"margin-top\":-1*(padding_top+$(\".zy_media\").height())});\n" +
//                            "</script>";
                    break;
                }
            }
        }
        return newLine;
    }

    public String pearseAudioString(long id, String width, String height) {
        String newLine = "";
        if (mCotentInfoeny.getAduioGroups() != null && mCotentInfoeny.getAduioGroups().size() > 0) {
            for (ContentCmsInfoEntry.VideosBean bean : mCotentInfoeny.getAduioGroups()) {
                if (id == bean.getId()) {
                    if (bean.getVersions() != null)
                        newLine += "<p><audio  src=\"" + bean.getVersions().getUrl() + "\" height=auto  width=100%  controls /></p>";
//                        newLine += "<p><audio  src=\"http://223.255.28.42:8003/2017-06-14/317/1238c0ef6d345011cdda8d5604d6333e.mp3\" height=auto  width=100%  controls /></p>";
                    break;
                }
            }
        }
        return newLine;
    }

    public static final String KEYWORDSTART = "<!--";
    public static final String KEYWORDEND = "->";

    public String findReplaceString(String str) {
        if (str == null || str.length() == 0) {
            Log.e("TAG", "getLrcRows str null or empty");
            return null;
        }
        StringReader reader = new StringReader(str);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        StringBuffer stringBuffer = new StringBuffer();
        String lineHeader = "";
        try {
            //循环地读取歌词的每一行
            do {
                line = br.readLine();
                System.out.println("line == " + line);
                //                Log.d("TAG", "str line: " + line);
                if (!"".equals(line) && line != null) {
                    String tempLine = lineHeader + line;
                    String[] tempArr = findAndReplace(tempLine);
                    stringBuffer.append(tempArr[0]);
                    lineHeader = tempArr[1];
                }
            } while (line != null);

            return stringBuffer.toString();
        } catch (Exception e) {
            //            Log.e("TAG", "parse exceptioned:" + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader.close();
        }
    }

    public String[] findAndReplace(String lineStr) {
        String[] backStrArr = new String[2];
        String str = lineStr;
        String tempStr = "";
        while (isFind(str)) {
            int start = str.indexOf(KEYWORDSTART);
            int end = str.indexOf(KEYWORDEND, start);
//            System.out.println("start == " + start);
//            System.out.println("end == " + end);
            if (start < end) {
                String selectedStr = str.substring(start, end + KEYWORDEND.length());
//                System.out.println("selectedStr == " + selectedStr);
//                String replaceStr = getReplaceText(selectedStr);
//                if (!TextUtils.isEmpty(replaceStr))
                str = str.replace(selectedStr, getReplaceText(selectedStr));
            } else if (end == -1) {
                tempStr = str.substring(start, str.length());
                str = str.substring(0, start);
            }
        }
//        System.out.println("back line == " + str);
//        System.out.println("back temp == " + tempStr);
        backStrArr[0] = str == null ? "" : str;
        backStrArr[1] = tempStr;
        return backStrArr;
    }

    private boolean isFind(String lineStr) {
        return lineStr.contains(KEYWORDSTART);
    }

    public String getReplaceText(String str) {
        String[] pst = str.split(",");
        String result = "";
        if (pst.length != 3 || TextUtils.isEmpty(pst[0]))
            return "";
        int index = pst[0].toString().indexOf("#");
        int tag = pst[0].toString().lastIndexOf("-");
        String markTag = "";
        if (index > tag) {
            markTag = pst[0].substring(tag + 1, index);
        }
        pst[0] = pst[0].toString().substring(index + 1);
        index = pst[2].toString().indexOf("-");
        pst[2] = pst[2].toString().substring(0, index);
        if (TextUtils.equals("PICTURE", markTag)) {
            result = pearseImageString(Long.parseLong(pst[0]));
        } else if (TextUtils.equals("VIDEO", markTag)) {
            result = pearseVideoString(Long.parseLong(pst[0]), pst[1], pst[2]);
        } else if (TextUtils.equals("AUDIO", markTag)) {
            result = pearseAudioString(Long.parseLong(pst[0]), pst[1], pst[2]);
        }
        return result;
    }

    public String getHtmlWeb(String body) {
        body = findReplaceString(body);

        String txtWeb = "<html>\n" +
                "<meta charset=\"utf-8\" />\n" +
//                "<meta name=\"viewport\" content=\"width=device-width\"/>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no\">\n" +
//                  "<link rel=\"stylesheet\" type=\"text/css\" href=\"main.css\" />\n" +

                "<link rel=\"stylesheet\" type=\"text/css\" href=\"zy.media.min.css\" />\n" +
//                "<script  src=\"zy.media.min.js\" />\n" +   这儿格式错误很重，导致其他都js没有效果
                "<script src=\"zy.media.min.js\"></script>\n" +
                "<script src=\"jquery-1.8.3.min.js\"></script>" +
                "<style>.zy_media{min-height: 56vw;}" +
                "  video{\n" +
                "            background: #000;\n" +
                "        }\n" +
                ".play-video-btn{\n" +
                "            display:block;\n" +
                "            position:absolute;\n" +
                "            top:0;\n" +
                "            bottom:0;\n" +
                "            width:100%;\n" +
                "            cursor: pointer;\n" +
                "            background: rgba(0,0,0,0.5) url('play.png') no-repeat center;\n" +
                "            background-size: 60px;\n" +
                "        }</style>\n" +
                "<style>video{height: auto;width:100%;width:100vw;}\n" +
                "img{max-width:100%}\n" +
                "body * {\n" +
                "    max-width: 100vw!important;\n" +
                "}</style>\n" +
//                "rgba(255,255,255,0.3)"
//                "<style>\n" +
//                "a{poorfish:expression(this.onclick=function kill(){return false})}\n" +
//                "</style>\n"+
//                "<style type=\"text/css\">\n" +
//                "#modelView{background-color:#DDDDDD;z-index:0;opacity:0.7;height: 100%;width: 100%;position: relative;}\n" +
//                ".playvideo{padding-top: auto;z-index: 9999;position: relative;}\n" +
//                ".zy_media{z-index: 999999999}\n" +
//                "</style>"+

                "<script type=\"text/javascript\"  charset=\"GB2312\">\n" +

                "var canPlay = false;\n" +
//                "$(function(){\n" +
//                "    $('video').on('play', function(){\n" +
//                "if(canPlay){\n" +
//                "return;\n" +
//                "}\n" +
//                "  this.pause();   \n" +
//                "if(window.imagelistner.isWifiStatus()){\n" +
//                "canPlay = true;\n" +
//                "this.play();\n" +
//                "}else{\n" +
//                "if(confirm('当前使用的是移动流量,确认继续播放？') ){\n" +
////                "if(window.imagelistner.showPlayDialog()){\n" +
//                "canPlay = true;\n" +
//                "this.play()\n" +
//                "}\n" +
//                "}\n" +
//                "\t});\n" +
//                "});\n" +
                "var playvideo=null;\n" +
                "var postimg=null;\n" +
                "$(function(){\n" +
                "    $('.play-video-btn').on('click', function(){\n" +
                "playvideo=$(this).closest('div').find('video')[0];\n" +
                "postimg=$(this);\n" +
                //     "alert(1);\n" +
                "if(canPlay){\n" +
                "playvideo.play();$(this).hide();\n" +
                "return;\n" +
                "}\n" +
                "if(window.imagelistner.isWifiStatus()){\n" +
//                "canPlay = true;\n" +
                "playvideo.play();$(this).hide();\n" +
                "}else{\n" +
//                "if(confirm('当前使用的是移动流量,确认继续播放？') ){\n" +
//                "if(window.imagelistner.showPlayDialog()){\n" +
                "window.imagelistner.showPlayDialog()\n" +
//                "canPlay = true;\n" +
//                "this.play()\n" +
//                "}\n" +
                "}\n" +
                "});\n" +
                "});\n" +

                "$(function(){\n" +
                "    $('body').on('click', 'a', function(e){\n" +
//                "        e.preventDefault();  alert($(this).attr('href'));\n" +
                "        e.preventDefault();\n" +
                "        return false;\n" +
                "    });\n" +
                "});\n" +

                "function startvideo() {\n" +
                "if(playvideo!=null){\n" +
//                " canPlay = true;\n" +
                "playvideo.play();\n" +
                "}\n" +
                "if(postimg!=null){\n" +
                "postimg.hide();\n" +
                "}\n" +
                "}\n" +

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
//        txtWeb+="<h3>"+mCotentInfoeny.getTitle()+"</h3>";
        ///      String  time=UtilHelp.getTimeString("yyyy-MM-dd",mCotentInfoeny.getPublish_time());
//        txtWeb+="<p><img  src=\""+mCotentInfoeny.getPublisher_avatar_url()+"\" height=\""+25+"\" width=\""+25+"\" />"+time+"</p>";
        //      txtWeb+="<p>"+mCotentInfoeny.getPublisher_name()+"    "+time+"</p>";
//                    txtWeb += "<p style=\"word-break:break-all;padding:5px\">";
        //     txtWeb += "<p style=\"text-align:justify;font-size:17px;line-height:180%;text-indent:2em;\">";
//                    txtWeb += "<font style=\"line-height:180%;font-size:17px\">";

        if (body != null) {
            Log.e("TAG", body.toString());
            txtWeb += body;
        }
//        txtWeb += "</font>";
        //       txtWeb += "</p>";
//        txtWeb += "<script>zymedia('video', {autoplay: true, preload: 'metadata'});</script>";
        //       txtWeb += "<script>zymedia('video', {autoplay: true,hideVideoControlsOnLoad:true, preload: 'metadata'});</script>";
//        txtWeb += "<script>zymedia('video', {autoplay: true, preload: 'none'});</script>";
        if (meWebTYPE == HTMLWEB_TYPE) {
            txtWeb += "<script>zymedia('video', {autoplay: true,hideVideoControlsOnLoad:true, preload: 'metadata'});</script>";
        } else {
            txtWeb += "<script>zymedia('video', {autoplay: true,hideVideoControlsOnLoad:true, preload: 'none'});</script>";
        }
        txtWeb += "</body></html>";

        return txtWeb;
    }

    private void InitCustomVideoView() {
        Activity act = CvideoPlayAct.this;
        mAnchor = (ViewGroup) act.findViewById(R.id.videoSurfaceContainer);
        ViewGroup.LayoutParams lp = mAnchor.getLayoutParams();
//        lp.height = nScreenheight;
        lp.height = Util.dp2px(this, 211);
        mAnchor.setLayoutParams(lp);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        offset = 1;
        getData(offset);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        offset++;
        getData(offset);
    }

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if (isHas) {
            mBtnSend.setImageDrawable(getResources().getDrawable(R.drawable.video_send_select));
        } else {
            mBtnSend.setImageDrawable(getResources().getDrawable(R.drawable.video_send_normal));
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        position = position - mListView.getHeaderViewsCount();
//        try {
//            boolean isLeagle = position >= 0 && position < mReplayAdapter.getCount();
//            if (isLeagle) {
//                CommendCmsEntry channel = mReplayAdapter.getData().get(position);
////                goDetail(channel, position);
//                int a = 0;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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

    NetChecker.CheckCallBack mCheckCallBack = new NetChecker.CheckCallBack() {
        @Override
        public void callBack(boolean isCouldPlay, Object tag) {
            if (isCouldPlay) {
                myHander.post(new Runnable() {
                    public void run() {
                        mWebView.loadUrl("javascript:startvideo()");
                    }
                });
            }
        }
    };

    private NetworkChangeReceiver.OnNetworkChangeListener _onNetworkChangeListener = new NetworkChangeReceiver.OnNetworkChangeListener() {
        @Override
        public void onChange(int networkType) {
            if (networkType != NetworkUtil.TYPE_WIFI) {
                //不是wifi
                Log.e("TAG", "Cvideo=====不是wifi+===");
                isWifi = false;
            } else {
                Log.e("TAG", "Cvideo=====是wifi=====");
                isWifi = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIndex = bundle.getLong("index");
            mAver_id = bundle.getLong("aver_id");
//            postGetUrl = bundle.getString("get_url");
//            boolean isport = bundle.getBoolean("isPort");
//            if (isport) {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            } else
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        int type = NetworkUtil.getConnectivityStatus(context);
        isWifi = type != NetworkUtil.TYPE_WIFI ? false : true;
        newsDatailHelper = new NewsDatailHelper(context);
        mWebUrlChe = new WebUrlCache(this);
        mNetChecker = new NetChecker(this, mCheckCallBack);
        mloginCheck = new IsLoginCheck(this);
        NetworkChangeManager.getInstance().addOnNetworkChangeListener(_onNetworkChangeListener);
        mIGetPraistmp = IGetPriseManager.getInstance().getProseToken(PraistmpType.PRISE_NEWS);
        rootView = getLayoutInflater().inflate(R.layout.cvideo_detail_act, null);
        setContentView(rootView);
        mViewbotmcommmed = (View) findViewById(R.id.cvideo_bottom_commend);
        mViewbotmcommmed.setOnClickListener(this);
        animation = AnimationUtils.loadAnimation(context, R.anim.nn);
        porLayout = findViewById(R.id.poriant_layout);
        fullScreenContainer = (FrameLayout) findViewById(R.id.full_screen_video_container);
        video_fullView = (FrameLayout) findViewById(R.id.full_screen_video_container);
        mPullRefreshList = (PullToRefreshListView) findViewById(R.id.relati_list);
        mReplayAdapter = new ReplayAdapter();
//        mPullRefreshList.setAdapter(mReplayAdapter);
        mPullRefreshList.setOnRefreshListener(this);
        mPullRefreshList.setMode(PullToRefreshBase.Mode.DISABLED);  //上拉刷新
        mListView = (ListView) mPullRefreshList.getRefreshableView();
        headLayout = LayoutInflater.from(context).inflate(R.layout.video_detail_header, null);
        mListView.addHeaderView(headLayout);
        mListView.setAdapter(mReplayAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int  a=0;
//                int  b=0;
//            }
//        });
        _moreLineText = (MoreTextView) headLayout.findViewById(R.id.mulite_line_txt);
        _newSummarytxt = (TextView) headLayout.findViewById(R.id.news_summary_text);
        mHeadImgView = (CircleButton) headLayout.findViewById(R.id.head_img);
        mShareBtn = (ImageView) findViewById(R.id.cvido_share_img);
        mShareBtn.setOnClickListener(this);
        mTxthtmlTitle = (TextView) headLayout.findViewById(R.id.newsDetailss_title);
        mPubauthoreTx = (TextView) headLayout.findViewById(R.id.cvideo_pubauthor_name);
        commnedNoplayLayout = (View) headLayout.findViewById(R.id.comnend_noplay_layout);
        mloadFailurelay = (RelativeLayout) findViewById(R.id.load__news_fail_layout);
        mReLoadBtn = (ImageButton) findViewById(R.id.reload_news_btn);
        mReLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadDataFormURl();
            }
        });
        mViewTopLayout = (View) headLayout.findViewById(R.id.new_detail_title_layout);
        mVideoOperView = (View) headLayout.findViewById(R.id.cvide_play_view);
        mOtherView = (View) headLayout.findViewById(R.id.bottom_oper_view);
        ImageButton headBackbtn = (ImageButton) findViewById(R.id.news_image_news_back_btn);
        headBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBtnback = (ImageButton) headLayout.findViewById(R.id.cvideo_back_btn);
        mBtnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        newsPrsieBtn = (TextView) headLayout.findViewById(R.id.cvideo_praise_tx);
        newsStrampBtn = (TextView) headLayout.findViewById(R.id.cvideo_stamp_tx);
        newsPrsieBtn.setOnClickListener(this);
        newsStrampBtn.setOnClickListener(this);
        videoPraiseBtn = (ImageView) headLayout.findViewById(R.id.cvideo_play_parise_btn);
        videoStrampBtn = (ImageView) headLayout.findViewById(R.id.cvideo_play_stramp_btn);
        videoPraiseBtn.setOnClickListener(this);
        videoStrampBtn.setOnClickListener(this);
        videoPraiseTxt = (TextView) headLayout.findViewById(R.id.cvideo_play_parise_txt);
        videoStrampTxt = (TextView) headLayout.findViewById(R.id.cvideo_play_stramp_txt);
        videoTitleTx = (TextView) headLayout.findViewById(R.id.cvideo_title_tx);
        mBtnAddAttion = (ImageView) headLayout.findViewById(R.id.cvideo_addatt_img);
        mBtnAddAttion.setOnClickListener(this);
        mCirbtnpub = (CircleButton) headLayout.findViewById(R.id.replay_user_img);
        mTopHeader = (CircleButton) headLayout.findViewById(R.id.author_head_img);
        mTopAuthor = (TextView) headLayout.findViewById(R.id.author_user_name);
        mTopAutime = (TextView) headLayout.findViewById(R.id.author_time_tx);
        mTxtViewcount = (TextView) headLayout.findViewById(R.id.cvideo_view_txt);
        mPosterImagView = (ImageView) headLayout.findViewById(R.id.psoter_imagveo);
        mFavorityBtn = (ImageView) findViewById(R.id.cvideo_isfav_img);
        mFavorityBtn.setOnClickListener(this);
        mVideoPath = "";
        mPlayVide0btn = (ImageView) headLayout.findViewById(R.id.player_imagveo);
        mPlayVide0btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!("").equals(mVideoPath) && mVideoPath != null) {
                    try {
                        Uri uril = Uri.parse(mVideoPath);
                        videoPlayer.start(mVideoPath);
                        videoPlayer.setCompletionListener(new VideoPlayView.CompletionListener() {
                            @Override
                            public void completion(IMediaPlayer mp) {
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
        if (android.os.Build.VERSION.SDK_INT > 20) {
            meWebTYPE = HTMLWEB_TYPE;
        } else {
            meWebTYPE = VIDEOENABLEWEB_TYPE;
        }
        mCommendNumberTx = (TextView) headLayout.findViewById(R.id.communtiry_count_tx);
        mBrowerNumberTx = (TextView) headLayout.findViewById(R.id.communtiry_brower_count_tx);
        mBodyImgs = (LinearLayout) headLayout.findViewById(R.id.commomtiy_imgs);
        mAuthorNameTx = (TextView) headLayout.findViewById(R.id.replay_user_name);
        mWebView = (WebView) headLayout.findViewById(R.id.webView_content);
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mHeandRalayout = (RelativeLayout) findViewById(R.id.new_detail_video_return_layout);
        mTitleLayout = (RelativeLayout) findViewById(R.id.new_detail_title_layout);
        mWebLayout = (RelativeLayout) findViewById(R.id.news_detail_web);
        mVideoLayout = (RelativeLayout) findViewById(R.id.news_detail_centernss);
        mGestureDetector = new GestureDetector(context, (GestureDetector.OnGestureListener) this);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.newsdtauel_smain);
        relativeLayout.setOnTouchListener(this);
        relativeLayout.setLongClickable(true);
        initView();
        dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
//        nPixWidth = ((dm.widthPixels - 30) / 2);
        nScreenWidth = dm.widthPixels;
        nScreenheight = ((dm.widthPixels - 20) * 3) / 4;
        mTopicalApi = new TopicalApi(this);
        mContentCmApi = new ContentCmsApi(this);
//        mTopicalApi.addViewCount(mIndex);
        InitCustomVideoView();
        LoadDataFormURl();
        iniRegister();
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

    public void setAttteonTextStatus(boolean falg, boolean isShowMsg) {
        String msg = "关注成功";
        if (falg) {
            mBtnAddAttion.setImageResource(R.drawable.cvideo_att_select);
        } else {
            msg = "已取消关注";
            mBtnAddAttion.setImageResource(R.drawable.cviddeo_info_att);
        }
        if (isShowMsg) {
            LSUtils.toastMsgFunction(context, msg);
        }
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
        }
        if (mReplayAdapter != null && !mReplayAdapter.isInit()) {
            offset = 1;
            getData(offset);
        }
    }

    public void LoadDataFormURl() {
        if (!isGetDateflag) {
            isGetDateflag = true;
            mloadFailurelay.setVisibility(View.GONE);
            if (!isComparehtml)
                progressDialog = CustomeProgressDialog.show(context, "正在加载中...");
            getDataMainThread();
        }
    }

    private void showMore(View moreBtnView, long resf_cid) {
        if (mMorePopupWindow == null) {
            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = li.inflate(R.layout.layout_more, null, false);
            mMorePopupWindow = new PopupWindow(content, MATCH_PARENT,
                    WRAP_CONTENT);
            mMorePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mMorePopupWindow.setFocusable(true);
//            mMorePopupWindow.setAnimationStyle(R.style.LiveVideoPopupStyle);
            mMorePopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            mMorePopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //     mMorePopupWindow.setOutsideTouchable(true);
            //      mMorePopupWindow.setTouchable(true);
            //    content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            //     mShowMorePopupWindowWidth = UtilHelp.getScreenWidth(this);
            //      mShowMorePopupWindowHeight = content.getMeasuredHeight();
            View parent = mMorePopupWindow.getContentView();
            mReplyContent = (EditText) parent.findViewById(R.id.commentEdit_replay_edt);
            mBtnSend = (ImageButton) parent.findViewById(R.id.commentButton);
            mReplyContent.addTextChangedListener(new EditChangedLister(this));
//            final TextView comment = (TextView) parent.findViewById(R.id.commentButton);
            mBtnSend.setTag(resf_cid);
            mBtnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (isUserNull()) return;
                    if (!mloginCheck.checkLogin()) return;
                    String contnt = mReplyContent.getText().toString();
                    long refId = -1;
                    if (view.getTag() != null) refId = (long) view.getTag();
                    mContentCmApi.createCmsCommend(mIndex, refId, contnt, new DataRequest.DataCallback() {
                        @Override
                        public void onSuccess(boolean isAppend, Object data) {
                            LSUtils.toastMsgFunction(context, "发表评论成功");
                            mMorePopupWindow.dismiss();
                            onFocusChange(false, mReplyContent);
                            mReplyContent.setText("");
                            getData(1);
                        }

                        @Override
                        public void onFail(ApiException e) {
                            LSUtils.toastMsgFunction(context, "发表评论失败:" + e.toString());
                            e.printStackTrace();
                        }
                    });
                }
            });
        }
        if (mMorePopupWindow.isShowing()) {
            mMorePopupWindow.dismiss();
        } else {
            mBtnSend.setTag(resf_cid);
            mMorePopupWindow.showAtLocation(moreBtnView, Gravity.BOTTOM,
                    0, 0);
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
        final ContentCmsInfoEntry entry = newsDatailHelper.readWebCacheData(this, mIndex);
        if (entry != null) {
            isLoadDialog();
            mCotentInfoeny = entry;
            isAttionAuthor = entry.isAttend();
            showWebviewData(entry);
            setAttteonTextStatus(isAttionAuthor, false);
        }
        String url = App.getInstance().getmSession().getContentcmsServerUrl() +
                "/public/contents/" + mIndex;
        DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().setRequestType(DataReuqestType.GET).
                setUrl(url).setToken(App.getInstance().getCurrentToken()).build();

//        DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().setRequestType(DataReuqestType.GET).
//                setUrl(url).setToken(App.getInstance().getCurrentToken()).build();
        new DataRequest<Long>(this) {
            @Override
            public Long jsonToBean(JSONObject json) {
                long id = json.optLong("id");
                return id;
            }
        }.getData(params, false).
                setCallback(new DataRequest.DataCallback<Long>() {
                    @Override
                    public void onSuccess(final boolean isAppend, final Long data) {
//                        if (data == null || data.isEmpty()) return;
                        if (data == -1) return;
                        Observable.just(data)
                                .subscribeOn(Schedulers.io())
                                .map(new Func1<Long, ContentCmsInfoEntry>() {
                                    @Override
                                    public ContentCmsInfoEntry call(Long json) {
                                        ContentCmsInfoEntry topicalEntry = null;
                                        topicalEntry = mContentCmApi.getEnteyFromJson(json);
                                        //                                        getExtentions(topicalEntry);
                                        boolean flag = mContentCmApi.isFav(topicalEntry.getId());
                                        topicalEntry.setIsFav(flag);
                                        String praiseList = mContentCmApi.getPraiseNumberList(topicalEntry.getId());
                                        topicalEntry.setPraiseList(praiseList);
                                        if (App.getInstance().getUser() != null) {
                                            if (topicalEntry.getAuthor_id() != App.getInstance().getUser().getUser().getId()) {
                                                int res = mTopicalApi.isAttentionOther(topicalEntry.getAuthor_id());
                                                isAttionAuthor = res == 1 ? true : false;
                                                topicalEntry.setAttend(isAttionAuthor);
                                            }
                                        }
                                        return topicalEntry;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<ContentCmsInfoEntry>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        isLoadDialog();
                                    }

                                    @Override
                                    public void onNext(ContentCmsInfoEntry data) {
                                        isLoadDialog();
                                        if (data != null) {
                                            mCotentInfoeny = data;
                                            showWebviewData(data);
                                            setAttteonTextStatus(isAttionAuthor, false);
                                            myHander.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    newsDatailHelper.saveWebCacheData(context, mIndex, mCotentInfoeny);
                                                }
                                            });
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFail(ApiException e) {
                        if (TextUtils.equals(e.toString(), NET_CNNECY_TIME)) {
                            myHander.sendEmptyMessage(NETWORK_BUSY);
                        }
                        e.printStackTrace();
                        isLoadDialog();
                    }
                });
    }

    public void isLoadDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void initView() {
        WebSettings webSettings = mWebView.getSettings();

        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
//            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }

//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSettings.setMediaPlaybackRequiresUserGesture(false);

        webSettings.setJavaScriptEnabled(true);

        //这方法可以让你的页面适应手机屏幕的分辨率，完整的显示在屏幕上，可以放大缩小(推荐使用)。
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);

        // 添加js交互接口类，并起别名 imagelistner
        mWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");

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

        /***
         mWebView.setWebViewClient(new WebViewClient() {
        @Override public void onPageFinished(WebView view, String url) {
        //                super.onPageFinished(view, url);
        if (!view.getSettings().getLoadsImagesAutomatically()) {
        view.getSettings().setLoadsImagesAutomatically(true);
        }
        view.loadUrl("javascript:imgResize()");
        //                myHander.sendEmptyMessage(LOAD_COMPLATE);
        }

        @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
        }

        @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        }
        });   **/

        DvWebViewClient webClient = new DvWebViewClient();
        mWebView.setWebViewClient(webClient);

        mWebView.setWebChromeClient(new WebChromeClient() {
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

    /**
     * 视频全屏参数
     */
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View customView;
//    private FrameLayout video_fullView;

    private WebChromeClient.CustomViewCallback customViewCallback;

    /**
     * 视频播放全屏
     **/
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one

        mWebView.setVisibility(View.INVISIBLE);

        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

//        CvideoPlayAct.this.getWindow().getDecorView();
//        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
//        video_fullView = new FullscreenHolder(CvideoPlayAct.this);
        video_fullView.addView(view);
//        decor.addView(video_fullView, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
        video_fullView.setVisibility(View.VISIBLE);

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
        video_fullView.removeView(customView);
//        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
//        decor.removeView(video_fullView);
        customView = null;
        video_fullView.setVisibility(View.GONE);
        customViewCallback.onCustomViewHidden();
        mWebView.setVisibility(View.VISIBLE);
//        mListView.smoothScrollToPosition(0);
    }

    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void loadDataFromText() {
        mWebView.setVisibility(View.VISIBLE);
        mWebView.resumeTimers();
        mWebView.loadDataWithBaseURL("file:///android_asset/", connectUrl, "text/html", "utf-8", null);
//        Log.e("TAG", "html == " + connectUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLoadDialog();
        if (shareSubscription != null)
            shareSubscription.unsubscribe();
        NetworkChangeManager.getInstance().
                removeOnNetworkChangeListener(_onNetworkChangeListener);
        cleareWebView();
    }

    public void cleareWebView() {
        if (mWebView == null) return;

        mWebView.clearCache(true);
        mWebView.clearHistory();

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
            float x_limit = dm.widthPixels / 2;
            float y_limit = dm.heightPixels / 2;
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

    public void setFavStatus(boolean flag, boolean isShowMsg) {
        String msg = "收藏成功";
        if (flag) {
            mFavorityBtn.setImageDrawable(getResources().getDrawable(R.drawable.communtiy_item_fal_sel));
        } else {
            msg = "已取消收藏";
            mFavorityBtn.setImageDrawable(getResources().getDrawable(R.drawable.cvidoe_favirty_normal));
        }
        if (isShowMsg) {
            LSUtils.toastMsgFunction(context, msg);
            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_MSG));
        }
    }

    public void setAttionAutor() {
//        if (isUserNull()) return;
        if (!mloginCheck.checkLogin()) return;
        if (mCotentInfoeny == null) return;
        int fouc = isAttionAuthor ? 1 : 0;
        mTopicalApi.attentionAuthor(mCotentInfoeny.getAuthor_id(), fouc, new DataRequest.DataCallback() {
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
                                           setAttteonTextStatus(isAttionAuthor, true);
                                       }
                                   }
                        );
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                LSUtils.toastMsgFunction(context, JsonCreater.getErrorMsgFromApi(e.toString()));
            }
        });
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_click));
        if (view == mBtnAddAttion) {
            setAttionAutor();
        } else if (view == mViewbotmcommmed) {
            showMore(view, -1);
        } else if (view == newsPrsieBtn || view == videoPraiseBtn) {
            pubContenPraise();
        } else if (view == newsStrampBtn || view == videoStrampBtn) {
            pubContenStamp();
        } else if (view == mFavorityBtn) {
            if (mCotentInfoeny != null) {
                pubFavirty(!mCotentInfoeny.isFav());
            }
        } else if (view == mShareBtn) {
            shareWnd();
        }
    }

    public void pubFavirty(final boolean flag) {
//        if (isUserNull()) return;
        if (!mloginCheck.checkLogin()) return;
        mContentCmApi.farityToptic(mIndex, flag, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                if ((boolean) data) {
                    mCotentInfoeny.setIsFav(flag);
                    setFavStatus(flag, true);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                LSUtils.toastMsgFunction(context, JsonCreater.getErrorMsgFromApi(e.toString()));
            }
        });
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

    public void finish() {
        cleareWebView();
        super.finish();
    }

    public void onSharePlatfrom(SharePlatform platform) {
        if (mCotentInfoeny == null) return;
        ShareContent content = new ShareContent();
        content.title = mCotentInfoeny.getTitle();
        content.disc = mCotentInfoeny.getSummary();
        if (mCotentInfoeny.getThumbnail_urls() != null && mCotentInfoeny.getThumbnail_urls().size() > 0)
            content.thumb = mCotentInfoeny.getThumbnail_urls().get(0);
        content.type = ShareContent.UrlType.WebPage;
        content.url = App.getInstance().getContentShareUrl() + mCotentInfoeny.getId();
        AbsShare share = ShareFactory.createShare(context, platform);
        share.share(content);
    }

    //更新点赞列表
    public void updatePraiseList() {
        Observable.just("").
                subscribeOn(Schedulers.io()).
                observeOn(Schedulers.io()).
                map(new Func1<String, String>() {
                    @Override
                    public String call(String id) {
                        String appendStr = mContentCmApi.getPraiseNumberList(mIndex);
                        return appendStr;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                               @Override
                               public void onCompleted() {
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                               }

                               @Override
                               public void onNext(String strformat) {
                                   if (strformat != null && !TextUtils.isEmpty(strformat)) {
                                       _moreLineText.setText(strformat);
                                   }
                               }
                           }
                );
    }

    public void pubContenPraise() {
        if (!mloginCheck.checkLogin()) return;
        boolean flag = mIGetPraistmp.isPriseFlag(mIndex);
        if (flag) {
            LSUtils.toastMsgFunction(context, "已经点赞过了");
//            mContentCmApi.cancelCmsPraise(mIndex, new DataRequest.DataCallback() {
//                @Override
//                public void onSuccess(boolean isAppend, Object data) {
//                    LSUtils.toastMsgFunction(context, "已取消点赞");
//                    mPraiseNumber--;
//                    if (isNewsType()) {
//                        newsPrsieBtn.startAnimation(animation);
//                        newsPrsieBtn.setText("-1");
//                        new Handler().postDelayed(new Runnable() {
//                            public void run() {
//                                newsPrsieBtn.setText(mPraiseNumber + "");
//                            }
//                        }, 50);
//                    } else {
//                        videoPraiseTxt.startAnimation(animation);
//                        videoPraiseTxt.setText("-1");
//                        new Handler().postDelayed(new Runnable() {
//                            public void run() {
//                                videoPraiseTxt.setText(mPraiseNumber + "");
//                            }
//                        }, 50);
//                    }
//                    mIGetPraistmp.updateValuse(mIndex, false, false, false);
//                    updatePraiseList();
//                }
//
//                @Override
//                public void onFail(ApiException e) {
//                    e.printStackTrace();
//                }
//            });
            return;
        }
        mContentCmApi.pubContentPraise(mIndex, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                mPraiseNumber++;
                if (isNewsType()) {
                    lbtnClickAnimal(newsPrsieBtn, mPraiseNumber);
                } else {
                    lbtnClickAnimal(videoPraiseTxt, mPraiseNumber);
                }
                mIGetPraistmp.updateValuse(mIndex, true, false, false);
                updatePraiseList();
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                LSUtils.toastMsgFunction(context, JsonCreater.getErrorMsgFromApi(e.toString()));
            }
        });
    }

    public void lbtnClickAnimal(final TextView view, final long number) {
        view.startAnimation(animation);
        view.setText("+1");
        new Handler().postDelayed(new Runnable() {
            public void run() {
                view.setText(number + "");
            }
        }, 50);
    }

    public boolean isNewsType() {
        boolean bflag = true;
        if (mVideoPath != null && !TextUtils.isEmpty(mVideoPath)) bflag = false;
        return bflag;
    }

    public void pubContenStamp() {
//        if (isUserNull()) return;
        if (!mloginCheck.checkLogin()) return;
        boolean flag = mIGetPraistmp.isStrmpFlag(mIndex);
        if (flag) {
            LSUtils.toastMsgFunction(context, "已经踩过了");
            return;
        }
        mContentCmApi.pubContentStamp(mIndex, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                mStrmpNumber++;
                if (isNewsType()) {
                    lbtnClickAnimal(newsStrampBtn, mStrmpNumber);
                } else {
                    lbtnClickAnimal(videoStrampTxt, mStrmpNumber);
                }
                mIGetPraistmp.updateValuse(mIndex, false, true, false);
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                LSUtils.toastMsgFunction(context, JsonCreater.getErrorMsgFromApi(e.toString()));
            }
        });
    }

    public class ReplayAdapter extends BaseAdapter {
        private ArrayList<CommendCmsEntry> list;

        public boolean isInit() {
            return init;
        }

        private boolean init = false;

        public ReplayAdapter() {
        }

        public void update(ArrayList<CommendCmsEntry> data, boolean isAdd) {
            if (list == null || !isAdd) {
                if (data != null && !data.isEmpty())
                    Collections.reverse(data);
//                Collections.sort(data,new Comparator(){
//                    public int compare(CommendCmsEntry arg0, CommendCmsEntry arg1) {
//                        int  n1=(int)arg1.getCreation_time();
//                        return n1.compareTo((int)arg0.getCreation_time());
//                    }
//                });
                list = data;
            } else {
                list.addAll(data);
            }
            init = true;
            notifyDataSetChanged();
        }

        public ArrayList<CommendCmsEntry> getData() {
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
            View bodyView = hodler.getView(R.id.replay_item_hor);
            CircleButton logo = hodler.getView(R.id.replay_user_logo);
            TextView nameText = hodler.getView(R.id.replay_user_name);
            TextView titleText = hodler.getView(R.id.replay_title_value);
            TextView timeText = hodler.getView(R.id.replay_time_value);
            TextView numberText = hodler.getView(R.id.replay_count_text);
            ImageView thumb = hodler.getView(R.id.replay_thumb);
            ImageButton replypub = hodler.getView(R.id.disclosure_replay_btn);
            replypub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long tId = (long) view.getTag(R.id.tag_replay_cid);
                    showMore(view, tId);
                }
            });
            LinearLayout extendlay = (LinearLayout) hodler.getView(R.id.replya_exlist_layout);
//            setCountText(itemCountText, position);
            CommendCmsEntry info = list.get(position);
//            GlideImgManager.getInstance().showImg(context, logo, info.getAuthor_avatar_url());
            Util.LoadImageErrorUrl(logo, info.getAuthor_avatar_url(), null, R.drawable.user_default_commend);

            nameText.setText(info.getAuthor_nickname());
            titleText.setText(info.getText());
            replypub.setTag(R.id.tag_replay_cid, info.getId());
            timeText.setText(UtilHelp.getTimeFormatText("yyyy-MM-dd", info.getCreation_time() * 1000));

            bodyView.setTag(R.id.tag_replay_item_cid, info);
            bodyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommendCmsEntry entry = (CommendCmsEntry) v.getTag(R.id.tag_replay_item_cid);
//                    String title = entry.getSub_comment_count() + "个回复";
                    if (entry.getSub_comment_count() == 0) {
                        ToastUtils.toastNoCommendFunction(v.getContext());
                        return;
                    }
                    String title = entry.getSub_comment_count() + "个回复";
                    Bundle   bundle=new Bundle();
                    bundle.putLong("itemId",mIndex);
                    bundle.putLong("subId",entry.getId());
                    bundle.putLong("praiseNumer", entry.getLike_count());
                    WhiteTopBarActivity.startAct(context, CommendPageFragment.class.getName(),title,"", bundle);
                }
            });

            thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = (String) view.getTag(R.id.tag_replay_thumb);
                    if (path != null && !TextUtils.isEmpty(path)) {
                        OpenImageUtils.openImage(view.getContext(), path, 0);
                    }
                }
            });
            List<CommendCmsEntry.SubCommentsBean> reppls = info.getmSubCommendList();
            long nSubCommendNum = info.getSub_comment_count();
            if (reppls != null && !reppls.isEmpty()) {
                if (extendlay.getChildCount() > 0) extendlay.removeAllViews();
                extendlay.setVisibility(View.VISIBLE);
                int i = 0;
                int count = reppls.size() >= 3 ? 3 : reppls.size();
                do {
                    CommendCmsEntry.SubCommentsBean bean = reppls.get(i);
                    View view = newsDatailHelper.createSubReplay(bean.getAuthor_nickname(), bean.getText());
                    extendlay.addView(view);
                    i++;
                } while (i < count);
            } else {
                extendlay.removeAllViews();
                extendlay.setVisibility(View.GONE);
            }
            if (nSubCommendNum > 0) {
                numberText.setText(nSubCommendNum + "回复");
                numberText.setBackground(getResources().getDrawable(R.drawable.shape_news_commend_circle));
            } else {
                numberText.setText("回复");
                numberText.setBackground(null);
            }
        }
    }

}
