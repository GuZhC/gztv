package com.dfsx.ganzcms.app.act;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.business.IButtonClickData;
import com.dfsx.core.common.business.IButtonClickListenr;
import com.dfsx.core.common.business.IButtonClickType;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.NewsReplayListAdapter;
import com.dfsx.ganzcms.app.business.TaskManager;
import com.dfsx.ganzcms.app.fragment.CommendPageFragment;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsInfoEntry;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.ImagTopView;
import com.dfsx.ganzcms.app.view.MoreTextView;
import com.dfsx.ganzcms.app.view.MyCollapseeView;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.thirdloginandshare.ShareCallBackEvent;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by heyang on 2017/12/5.
 */
public class CmsVideoActivity extends AbsCmsActivity {
    private final static String TAG = "CmsVideoActivity";
    private NewsReplayListAdapter mReplayAdapter = null;
    private EmptyView emptyView;
    private ViewGroup mAnchor;
    private FrameLayout fullScreenContainer;
    private ImageView videoPraiseBtn, videoStrampBtn;
    private ImageButton backBtn;
    private TextView videoPraiseTxt, videoStrampTxt, videoTitleTx, viewCountTxt, pubTimeTxt,
            bottomViewTxt;
    private MyCollapseeView summaryTxt;
    private int pageoffset = 1;
    protected ImageView _playBtn, _thumbImage, favorityBtn, shareBtn, praiseMark;
    private String _videoPath;
    private LinearLayout _videoRalationContainer;
    protected View _commnedNoplayLayout, commendBtn;
    protected ContentCmsInfoEntry _gEntry;
    private long mPraiseNumber, mStrmpNumber;
    private MoreTextView _morePraisetv;
    protected ProgressBar _videolenBar;
    private Timer mTimer;
    private TimerTask mTimerTask;
    protected boolean isPlaying = false;
    private boolean isConectNet = true;
    private View bottomlayout, tailView, _swicthBtn;
    private Subscription shareSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniitTimeer();
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

    public void getContentInfo() {
        getContentInfo("video");
    }

    public void getContentInfo(String type) {
        _newsHelper.getCotentInfo(_cmsId, type, new Action1<ContentCmsInfoEntry>() {
            @Override
            public void call(ContentCmsInfoEntry data) {
                if (data != null && data.getId() != -1 &&
                        data.getId() != 0) {
                    bottomListViewContainer.setVisibility(View.VISIBLE);
                    ((View) emptyView.getParent()).setVisibility(View.GONE);
                    _gEntry = data;
                    initParams(data);
                    emptyView.setVisibility(View.GONE);
                    mTimer.schedule(mTimerTask, 0, 1000);
                    myHander.post(new Runnable() {
                        @Override
                        public void run() {
                            _newsHelper.saveWebCacheData(context, _cmsId, _gEntry);
                        }
                    });
                } else {
                    if (!isConectNet) {
                        bottomListViewContainer.setVisibility(View.GONE);
                    }
                    if (_gEntry == null) {
                        emptyView.loadOver();
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        bottomListViewContainer.setVisibility(View.VISIBLE);
                        ((View) emptyView.getParent()).setVisibility(View.GONE);
                    }
                }
            }
        });
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

    public void initParams(final ContentCmsInfoEntry data) {
        if (data == null || context == null) return;
        mPraiseNumber = data.getLike_count();
        mStrmpNumber = data.getDislike_count();
        videoPraiseTxt.setText(mPraiseNumber + "");
        videoStrampTxt.setText(mStrmpNumber + "");
        videoTitleTx.setText(data.getTitle());
        String pralistList = data.getPraiseList();
        updatePraiseUiList(_morePraisetv, pralistList);
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
//        viewCountTxt.setText(data.getView_count() + "次播放");
        viewCountTxt.setText(data.getView_count() + act.getResources().getString(R.string.news_item_viewplay_hit));
        UtilHelp.setViewCount(bottomViewTxt, data.getView_count());
        setFavStatus(favorityBtn, data.isFav(), false);
        UtilHelp.setTimeDate(pubTimeTxt, data.getPublish_time());
//        pubTimeTxt.setText(UtilHelp.getTimeFormatText("yyyy年MM月dd HH:mm", data.getPublish_time() * 1000));
        if (data.getSummary() != null && !TextUtils.isEmpty(data.getSummary().toString().trim())) {
//            SpannableString apnnser = new SpannableString("导读: " + data.getSummary().toString());
            SpannableString apnnser = new SpannableString(act.getResources().getString(R.string.news_act_daodu_hit) + ": " + data.getSummary().toString());
            StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);
            apnnser.setSpan(styleSpan_B, 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            summaryTxt.setText(apnnser);
        } else {
            View parent = (View) summaryTxt.getParent();
            if (parent != null)
                parent.setVisibility(View.GONE);
        }
        _videoPath = data.getUrl();
        if (data.getThumbnail_urls() != null && data.getThumbnail_urls().size() > 0)
            LoadImagee(_thumbImage, data.getThumbnail_urls().get(0).toString());
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

    public View initTailVIew() {
        View tailView = getLayoutInflater().from(context).inflate(R.layout.cms_tail_bar, null);
        return tailView;
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
                AbsListView.LayoutParams.WRAP_CONTENT));
        listView.addHeaderView(headView);
        tailView = initTailVIew();
        listView.addFooterView(tailView);
        //        listView.smoothScrollToPosition(0, 0);
        listView.setSelectionFromTop(0, 1);
    }

    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        View eV = LayoutInflater.from(context).
                inflate(R.layout.no_default_frg_layout, null);
        emptyView.setLoadOverView(eV);
        emptyView.loading();
        container.addView(emptyView);
        ImageView btnStart = (ImageView) eV.findViewById(R.id.retyr_btn);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initdata();
            }
        });
    }

    protected void setTopView(FrameLayout topListViewContainer) {
        View top = getLayoutInflater().from(context).inflate(R.layout.cms_video_header, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.dp2px(act, 214));
        topListViewContainer.addView(top, lp);
        mAnchor = (ViewGroup) top.findViewById(R.id.videoSurfaceContainer);
        backBtn = (ImageButton) top.findViewById(R.id.cvideo_back_btn);
        backBtn.setOnClickListener(this);
        _videolenBar = (ProgressBar) top.findViewById(R.id.video_length_bar);
        _thumbImage = (ImageView) top.findViewById(R.id.psoter_imagveo);
        _playBtn = (ImageView) top.findViewById(R.id.player_imagveo);
        _playBtn.setVisibility(View.VISIBLE);
        _playBtn.setOnClickListener(this);
    }

    @Override
    public void addVideoPlayerToContainer(VideoPlayView videoPlayer) {
        if (mAnchor != null) {
            if (!(mAnchor.getChildAt(0) instanceof VideoPlayView)) {
                mAnchor.addView(videoPlayer, 0);
            }
        }
        super.setPortLayoutContainer(false);
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
        super.setPortLayoutContainer(true);
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
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public View initHeadTop() {
        View headLayout = getLayoutInflater().from(context).inflate(R.layout.news_video_header, null);
        _videoRalationContainer = (LinearLayout) headLayout.findViewById(R.id.video_ralation_container);
        _commnedNoplayLayout = (View) headLayout.findViewById(R.id.comnend_noplay_layout);
        viewCountTxt = (TextView) headLayout.findViewById(R.id.cvideo_view_txt);
        bottomViewTxt = (TextView) headLayout.findViewById(R.id.communtiry_brower_count_tx);
        pubTimeTxt = (TextView) headLayout.findViewById(R.id.cvideo_view_time);
        summaryTxt = (MyCollapseeView) headLayout.findViewById(R.id.cvideo_sumamry_text);
        summaryTxt.setCollapsedLines(3);
        summaryTxt.setCollapsedText(act.getResources().getString(R.string.news_act_shou_hit));
        summaryTxt.setExpandedText(act.getResources().getString(R.string.news_act_expend_hit));
        summaryTxt.setTipsColor(0xff5193EA);
        videoPraiseBtn = (ImageView) headLayout.findViewById(R.id.cvideo_play_parise_btn);
        videoStrampBtn = (ImageView) headLayout.findViewById(R.id.cvideo_play_stramp_btn);
        videoPraiseBtn.setOnClickListener(this);
        videoStrampBtn.setOnClickListener(this);
        _morePraisetv = (MoreTextView) headLayout.findViewById(R.id.mulite_line_txt);
        praiseMark = (ImageView) headLayout.findViewById(R.id.praise_mark);
        videoPraiseTxt = (TextView) headLayout.findViewById(R.id.cvideo_play_parise_txt);
        videoStrampTxt = (TextView) headLayout.findViewById(R.id.cvideo_play_stramp_txt);
        videoTitleTx = (TextView) headLayout.findViewById(R.id.cvideo_title_tx);
        _swicthBtn = (View) headLayout.findViewById(R.id.switch_btn);
        _swicthBtn.setOnClickListener(this);
        return headLayout;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareSubscription != null)
            shareSubscription.unsubscribe();
        stopTimer();
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.image_click));
        if (v == _playBtn) {
            if (_videoPath != null && !TextUtils.isEmpty(_videoPath)) {
                try {
                    videoPlayer.start(_videoPath);
                    videoPlayer.setPreparedListener(new VideoPlayView.OnPreparedListener() {
                        @Override
                        public void preparedlistener(IMediaPlayer mp) {
                            long duration = videoPlayer.getVideoDuration();
                            _videolenBar.setMax((int) duration);
                            isPlaying = true;
                            //                            mTimer.schedule(mTimerTask, 0, 1000);
                        }
                    });

                    videoPlayer.setCompletionListener(new VideoPlayView.CompletionListener() {
                        @Override
                        public void completion(IMediaPlayer mp) {
                            isPlaying = false;
                            _playBtn.setVisibility(View.VISIBLE);
                            _thumbImage.setVisibility(View.VISIBLE);
                            //                            mTimerTask.cancel();
                        }
                    });
                    _playBtn.setVisibility(View.GONE);
                    _thumbImage.setVisibility(View.GONE);
                } catch (Exception e) {
                    isPlaying = false;
                    e.printStackTrace();
                }
            } else {
                ToastUtils.toastMsgFunction(act, getResources().getString(R.string.news_act_no_video_hit));
            }
        } else if (v == backBtn) {
            finish();
        } else if (v == videoPraiseBtn) {
            onPraisBtn();
        } else if (v == videoStrampBtn) {
            onStrampBtn();
        } else if (v == _swicthBtn) {
            _newsHelper.getRelationCotentList(_cmsId, "video", new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
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
        } else if (v == commendBtn) {
            onCommendBtn(v, -1);
        } else if (v == favorityBtn) {
            onFavorityBtn();
        } else if (v == shareBtn) {
            onShareWnd(v, _gEntry);
        }
    }

    public void iniitTimeer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isPlaying) {
                    return;
                }
                int len = videoPlayer.getVideoCurrentPostion();
                Log.e(TAG, "len=====" + len);
                _videolenBar.setProgress(videoPlayer.getVideoCurrentPostion());
            }
        };
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public void onCommendBtn(View v, long ref_id) {
        writeCommendbtn(v, _cmsId, ref_id, new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {
                ToastUtils.toastReplayOk(context);
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
        if (data != null && !data.isEmpty()) {
            ((View) _videoRalationContainer.getParent()).setVisibility(View.VISIBLE);
            if (_videoRalationContainer != null && _videoRalationContainer.getChildCount() > 0)
                _videoRalationContainer.removeAllViews();
            int i = 1;
            for (; i <= data.size(); i++) {
                if (i > data.size()) break;
                ContentCmsEntry enytry = data.get(i - 1);
                if (i % 2 != 0) {
                    LinearLayout v = createHorirtyView();
                    createItemView(v, enytry, true);
                    _videoRalationContainer.addView(v);
                } else {
                    int count = _videoRalationContainer.getChildCount();
                    LinearLayout v = (LinearLayout) _videoRalationContainer.getChildAt(count >= 0 ? count - 1 : 0);
                    createItemView(v, enytry, false);
                }
            }
            if ((i - 1) % 2 != 0) {
                int count = _videoRalationContainer.getChildCount();
                LinearLayout v = (LinearLayout) _videoRalationContainer.getChildAt(count >= 0 ? count - 1 : 0);
                createItemView(v, null, true);
            }
        }
    }

    public LinearLayout createHorirtyView() {
        LinearLayout container = new LinearLayout(this);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int top = Util.dp2px(this, 23);
        lp1.setMargins(0, 23, 0, 0);
        container.setLayoutParams(lp1);
        container.setOrientation(0);
        return container;
    }

    public View createItemView(LinearLayout container, ContentCmsEntry fr, boolean isFirst) {
        LinearLayout layput = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        layput.setOrientation(1);
        layput.setLayoutParams(params);

        RelativeLayout lay = new RelativeLayout(this);
        lay.setLayoutParams(params);

        RelativeLayout childlay = new RelativeLayout(this);
        LinearLayout.LayoutParams lppp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.dp2px(this, 109));
        childlay.setLayoutParams(lppp);

        ImageView videoMark = new ImageView(this);
        int width = Util.dp2px(this, 31);
        int height = Util.dp2px(this, 35);
        RelativeLayout.LayoutParams marklp = new RelativeLayout.LayoutParams(width,
                height);
        videoMark.setImageResource(R.drawable.video_play_mark);
        marklp.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoMark.setLayoutParams(marklp);
        childlay.addView(videoMark);

        ImagTopView imag = new ImagTopView(this);
        //        LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (isFirst) {
            params.setMargins(0, 0, 5, 0);
        }
        //        imag.setLayoutParams(lpp);

        //        LinearLayout.LayoutParams lppp = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.dp2px(this,109));
        imag.getFirstView().setLayoutParams(lppp);
        imag.getFirstView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        imag.getSecondView().setTextColor(R.color.black);
        //        imag.getSecondView().setText(item.getTitle());
        imag.getSecondView().setTextSize(15);
        imag.getSecondView().setMaxLines(2);
        imag.getSecondView().setEllipsize(TextUtils.TruncateAt.END);
        imag.getSecondView().setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams ltpp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //        childlay.setLayoutParams(lppp);
        ltpp.setMargins(5, 0, 5, 0);
        imag.getSecondView().setLayoutParams(ltpp);

        imag.getFirstView().setLayoutParams(lppp);
        //        imag.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (fr == null) {
            imag.setVisibility(View.INVISIBLE);
            videoMark.setVisibility(View.GONE);
        } else {
            //            Util.LoadThumebImage(imag.getFirstView(), "http://cms-bucket.nosdn.127.net/bf2bce51be704afca2ff892723ae8f1320171206092245.jpeg?imageView&thumbnail=185y116&quality=85", null);
            String thumb = "";
            if (fr.getThumbnail_urls() != null && fr.getThumbnail_urls().size() > 0)
                thumb = fr.getThumbnail_urls().get(0);
            Glide.with(act)
                    .load(thumb)
                    .asBitmap()
                    .error(R.drawable.glide_default_image)
                    .into(imag.getFirstView());
//            Util.LoadThumebImage(imag.getFirstView(), thumb, null);
            imag.setTag(fr);
            imag.getSecondView().setText(fr.getTitle());
        }
        imag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentCmsEntry entry = (ContentCmsEntry) v.getTag();
                if (entry != null) {
                    _newsHelper.goDetail(entry);
                }
            }
        });

        lay.addView(imag);
        lay.addView(childlay);
        layput.addView(lay);
        container.addView(layput);
        return container;
    }

}
