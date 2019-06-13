package com.dfsx.ganzcms.app.act;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.ShortVideoAdapter;
import com.dfsx.ganzcms.app.adapter.ShortVideoDetailAdapter;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.HeadlineListManager;
import com.dfsx.ganzcms.app.fragment.ShortVideoFragment;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsInfoEntry;
import com.dfsx.ganzcms.app.model.ShortVideoBean;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;
import java.util.List;

public class ShortVideoDetailActivity extends AbsVideoActivity implements BaseQuickAdapter.OnItemChildClickListener {

    private SystemBarTintManager systemBarTintManager;
    private SharePopupwindow sharePopupwindow;
    private RecyclerView videoRecycler;
    private LinearLayoutManager mLayoutManager;
    private ShortVideoDetailAdapter adapter;
    private List<ShortVideoBean> mVideoData = new ArrayList<>();
    //将要播放位置
    private int newPosition = 0;
    //播放状态
    private boolean isPlay = false;
    private int playPostion = 0;
    private FrameLayout videoFullLyout;
    private ImageView ivBack;
    private View rootView;
    private long videoId = -1;
    private ContentCmsApi mContentCmsApi = null;
    private PullToRefreshRecyclerView mPullToRefreshRecyclerView;
    private ColumnCmsEntry pagerEntry;
    private HeadlineListManager pagerDataRequester = null;
    private int mPager = 1;
    private TextView mLoding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_short_video_detail, null);
        setContentView(rootView);
        systemBarTintManager = Util.applyKitKatTranslucency(this, ContextCompat.getColor(this, R.color.transparent));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            videoId = bundle.getLong("index", -1);
        }
        mContentCmsApi = new ContentCmsApi(this);
        initView();
        initData();
    }


    private void initView() {
        mPullToRefreshRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.rl_video_detail_recycler);
        mLoding = (TextView) findViewById(R.id.tv_video_detail_loding);
        mLoding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoding();
                initData();
            }
        });
        showLoding();
        mPullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                mPager = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (videoId == ShortVideoFragment.FEATURED_VIDEO && pagerDataRequester != null) {
                    pagerDataRequester.start(true, false, mPager);
                } else {
                    mPullToRefreshRecyclerView.onRefreshComplete();
                }

            }
        });
        videoRecycler = mPullToRefreshRecyclerView.getRefreshableView();
        videoFullLyout = (FrameLayout) findViewById(R.id.fl_video_detail_full);
        ivBack = (ImageView) findViewById(R.id.iv_video_detail_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置recycler
        mLayoutManager = new LinearLayoutManager(this);
        videoRecycler.setLayoutManager(mLayoutManager);

        adapter = new ShortVideoDetailAdapter(mVideoData, videoPlayer,videoId);
        videoRecycler.setAdapter(adapter);
        initListener();
    }

    /**
     * 这么写牛不牛，要怪就怪XX的后台写的接口，还好他妈有RxJava
     */
    private void initData() {
        if (videoId == -1) {
            if (mPullToRefreshRecyclerView != null)
                mPullToRefreshRecyclerView.onRefreshComplete();
            showEmpty();
            return;
        }
        if (videoId == ShortVideoFragment.FEATURED_VIDEO) {
            pagerEntry = ColumnBasicListManager.getInstance().findColumnByName("jxsp");
            if (pagerEntry != null && pagerDataRequester == null) {
                pagerDataRequester = new HeadlineListManager(this, pagerEntry.getId() + "", pagerEntry.getId(), pagerEntry.getMachine_code());
                pagerDataRequester.setCallback(new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
                    @Override
                    public void onSuccess(final boolean isAppend, ArrayList<ContentCmsEntry> data) {
                        if (!(data == null || data.isEmpty())) {
                            Observable.from(data)
                                    .subscribeOn(Schedulers.io())
                                    .flatMap(new Func1<ContentCmsEntry, Observable<ContentCmsInfoEntry>>() {
                                        @Override
                                        public Observable<ContentCmsInfoEntry> call(ContentCmsEntry topicalEntry) {
                                            //视频查询详情
                                            if (!TextUtils.equals(topicalEntry.getType(), "video")) return null;
                                            ContentCmsInfoEntry cmsInfoEntry = mContentCmsApi.getEnteyFromJson(topicalEntry.getId());
                                            return Observable.just(cmsInfoEntry);
                                        }
                                    })
                                    .map(new Func1<ContentCmsInfoEntry, ShortVideoBean>() {
                                             private List<ContentCmsInfoEntry.VideosBean> videoGroups;

                                             @Override
                                             public ShortVideoBean call(ContentCmsInfoEntry cmsInfoEntry) {
                                                 //获取时间
                                                 videoGroups = cmsInfoEntry.getVideoGroups();
                                                 if (videoGroups != null && !videoGroups.isEmpty()) {
                                                     ContentCmsInfoEntry.VideosBean videosBean = mContentCmsApi.getWebVideoBeanById(videoGroups.get(0).getId());
                                                     if (videosBean != null) {
                                                         cmsInfoEntry.setVideoDuration(videosBean.getDuration());
                                                     }
                                                 }
                                                 //数据转换
                                                 ShortVideoBean shortVideoBean = new ShortVideoBean();
                                                 shortVideoBean.setContentCmsInfoEntry(cmsInfoEntry);
                                                 return shortVideoBean;
                                             }
                                         }
                                    )
                                    .toList()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<List<ShortVideoBean>>() {
                                        @Override
                                        public void onCompleted() {
                                            if (mPullToRefreshRecyclerView != null)
                                                mPullToRefreshRecyclerView.onRefreshComplete();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            showError();
                                            if (mPullToRefreshRecyclerView != null)
                                                mPullToRefreshRecyclerView.onRefreshComplete();
                                            e.printStackTrace();
                                            Log.e("onError", e.getMessage());
                                        }

                                        @Override
                                        public void onNext(List<ShortVideoBean> data) {
                                            Log.e("mPager", "  " + mPager);
                                            if (!isAppend) {
//                                            stopPVideo();
                                                mVideoData.clear();
                                                mVideoData.addAll(data);
                                                mVideoData.get(0).setVideo_state(ShortVideoAdapter.VIDEO_PLAY);
                                                adapter.notifyDataSetChanged();
                                                stopPVideo();
                                                playVideo();
                                            } else {
                                                int p = mVideoData.size();
                                                mVideoData.addAll(data);
                                                adapter.notifyItemRangeChanged(p,data.size());
                                            }
                                            if (!data.isEmpty()){
                                                showChontent();
                                                ++mPager;
                                            }
                                            if (mVideoData.isEmpty()) {
                                                stopPVideo();
                                                showEmpty();
                                            }
//                                            if (!addVideoToLayout(videoPlayer)) {
//                                                stopPVideo();
//                                                playVideo();
//                                            }
                                        }
                                    });
                        } else {
                            if (mPullToRefreshRecyclerView != null)
                                mPullToRefreshRecyclerView.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        showError();
                        if (mPullToRefreshRecyclerView != null)
                            mPullToRefreshRecyclerView.onRefreshComplete();
                        Log.e("pagerEntry", e.getMessage());
                    }
                });
            }
            pagerDataRequester.start(false, false, mPager);
            return;
        }
        Observable.just(videoId)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Long, Observable<ContentCmsInfoEntry>>() {
                    @Override
                    public Observable<ContentCmsInfoEntry> call(Long id) {
                        //获取详情
                        List<ContentCmsInfoEntry> data = new ArrayList<>();
                        ContentCmsInfoEntry entey = mContentCmsApi.getEnteyFromJson(id);
                        if (entey != null) {
                            data.add(entey);
                        }
                        //获取相关推荐
                        List<ContentCmsEntry> dlist = null;
                        dlist = mContentCmsApi.getRelationContenList(entey.getId(), "video", 8);
                        if (dlist != null && !dlist.isEmpty()) {
                            for (ContentCmsEntry relationConten : dlist) {
                                ContentCmsInfoEntry relationContenInfo = mContentCmsApi.getEnteyFromJson(relationConten.getId());
                                if (relationContenInfo != null) {
                                    data.add(relationContenInfo);
                                }
                            }
                        }
                        return Observable.from(data);
                    }
                })
                .map(new Func1<ContentCmsInfoEntry, ContentCmsInfoEntry>() {
                    private List<ContentCmsInfoEntry.VideosBean> videoGroups;

                    @Override
                    public ContentCmsInfoEntry call(ContentCmsInfoEntry cmsInfoEntry) {
                        //获取时间
                        if (cmsInfoEntry == null) return null;
                        videoGroups = cmsInfoEntry.getVideoGroups();
                        if (videoGroups != null && !videoGroups.isEmpty()) {
                            ContentCmsInfoEntry.VideosBean videosBean = mContentCmsApi.getWebVideoBeanById(videoGroups.get(0).getId());
                            if (videosBean != null) {
                                cmsInfoEntry.setVideoDuration(videosBean.getDuration());
                            }
                        }
                        return cmsInfoEntry;
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ContentCmsInfoEntry>>() {
                    @Override
                    public void onCompleted() {
                        if (mPullToRefreshRecyclerView != null)
                            mPullToRefreshRecyclerView.onRefreshComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mPullToRefreshRecyclerView != null)
                            mPullToRefreshRecyclerView.onRefreshComplete();
                        Log.e("onError: ", e.getMessage());
                    }

                    @Override
                    public void onNext(List<ContentCmsInfoEntry> contentCmsInfoEntry) {
                        if (contentCmsInfoEntry.size() > 0) {
                            mVideoData.clear();
                        }else {
                            showEmpty();
                            return;
                        }
                        for (int i = 0; i < contentCmsInfoEntry.size(); i++) {
                            ShortVideoBean shortVideoBean = new ShortVideoBean();
                            shortVideoBean.setContentCmsInfoEntry(contentCmsInfoEntry.get(i));
                            if (i == 0) {
                                shortVideoBean.setVideo_state(ShortVideoAdapter.VIDEO_PLAY);
                            }
                            mVideoData.add(shortVideoBean);
                            if (i == 0 && videoId != -1) {
                                ShortVideoBean shortVideoBean2 = new ShortVideoBean();
                                shortVideoBean2.setContentCmsInfoEntry(contentCmsInfoEntry.get(i));
                                shortVideoBean2.setVitemType(ShortVideoBean.TYPE_SHARE);
                                mVideoData.add(shortVideoBean2);
                            }
                        }
                        showChontent();
                        adapter.notifyDataSetChanged();

//                        Log.e("onNext: ", contentCmsInfoEntry.toString() );
                    }
                });

//        for (int i = 0; i < 15; i++) {
//            ShortVideoBean shortVideoBean = new ShortVideoBean();
//            if (i == 0) shortVideoBean.setVideo_state(ShortVideoAdapter.VIDEO_PLAY);
//            if (i == 1) shortVideoBean.setVitemType(ShortVideoBean.TYPE_SHARE);
//            shortVideoBean.setTitle(i + " video item");
//            mVideoData.add(shortVideoBean);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playVideo();
            }
        }, 500);
    }

    private void initListener() {
        videoPlayer.setCompletionListener(new VideoPlayView.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                endPVideo();
            }
        });
        adapter.setOnItemChildClickListener(this);
        videoRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //静止的时候
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playVideo();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager == null) {
                    return;
                }
                //得到显示屏内的第一个list的位置数position
                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                View firstView = mLayoutManager.findViewByPosition(firstVisibleItem);
                //数量小于二
                if (null == firstView || mLayoutManager.getChildCount() < 2) {
                    return;
                }
                //向上滑动
                if (dy > 0) {
                    if (firstView.getHeight() + firstView.getTop() <= firstView.getHeight() * 2 / 3) {
                        newPosition = firstVisibleItem + 1;
                        if (newPosition == playPostion) return;
                        if (mVideoData.get(newPosition).getItemType() == ShortVideoBean.TYPE_SHARE) {
                            ++newPosition;
                            return;
                        }
                        stopPVideo();
                    }
                }
                //向下滑动
                if (dy < 0) {
                    if (firstView.getHeight() + firstView.getTop() >= firstView.getHeight() * 2 / 3) {
                        newPosition = firstVisibleItem;
                        if (newPosition == playPostion) return;
                        if (mVideoData.get(newPosition).getItemType() == ShortVideoBean.TYPE_SHARE) {
                            ++newPosition;
                            return;
                        }
                        stopPVideo();
                    }
                }
            }
        });
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.tv_short_video_play:
                playAgain(position);
                break;
            case R.id.tv_short_video_comment:
                Log.e("play", "setPlay: tv_short_video_comment：" + newPosition);
                break;
            case R.id.tv_short_video_share:
                shareWnd(position);
//                Log.e("play", "setPlay: tv_short_video_share：" + newPosition);
                break;
            case R.id.tv_short_video_share_friends:
                onSharePlatfrom(SharePlatform.Wechat_FRIENDS, position);
//                Log.e("play", "setPlay: tv_short_video_share_friends：" + newPosition);
                break;
            case R.id.tv_short_video_share_wx:
                onSharePlatfrom(SharePlatform.Wechat, position);
//                Log.e("play", "setPlay: tv_short_video_share_wx：" + newPosition);
                break;
            case R.id.cb_short_video_sound:
                Log.e("play", "setPlay: cb_short_video_sound：" + newPosition);
                break;
            case R.id.tv_short_video_go_detail:

//                videoId = mVideoData.get(position).getContentCmsInfoEntry().getId();
//                initData();
                break;
            case R.id.tv_short_video_praise:
                Log.e("play", "setPlay: tv_short_video_praise：" + newPosition);
                break;
            case R.id.btn_short_video_look_again:
                playAgain(position);
                break;
            case R.id.iv_short_video_detail_praise:
                Log.e("play", "setPlay: iv_short_video_detail_praise：" + newPosition);
                break;
            case R.id.iv_short_video_detail_comment:
                Log.e("play", "setPlay: iv_short_video_detail_comment：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_wx:
                onSharePlatfrom(SharePlatform.Wechat, position);
//                Log.e("play", "setPlay: iv_short_video_detail_share_wx：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_pyq:
                onSharePlatfrom(SharePlatform.Wechat_FRIENDS, position);
//                Log.e("play", "setPlay: iv_short_video_detail_share_pyq：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_wb:
                onSharePlatfrom(SharePlatform.WeiBo, position);
//                Log.e("play", "setPlay: iv_short_video_detail_share_wb：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_qq:
                onSharePlatfrom(SharePlatform.QQ, position);
//                Log.e("play", "setPlay: iv_short_video_detail_share_qq：" + newPosition);
                break;
        }
    }

    private void playAgain(int position) {
        if (playPostion == position && isPlay) return;
        stopPVideo();
        newPosition = position;
        View nowView = mLayoutManager.findViewByPosition(position);
        videoRecycler.smoothScrollBy(0, nowView.getTop());
        playVideo();
    }

    private void stopPVideo() {
        if (mVideoData.isEmpty()) return;
        if (isPlay) {
            if (videoPlayer != null) {
                ViewGroup view = (ViewGroup) videoPlayer.getParent();
                if (view != null) {
                    view.removeView(videoPlayer);
                }
            }
            videoPlayer.pause();
            isPlay = false;
            mVideoData.get(playPostion).setVideo_state(ShortVideoAdapter.VIDEO_STOP);
            adapter.notifyItemChanged(playPostion);
            Log.e("play", "setPlay: 暂停：" + playPostion);
        }
    }
    private void endPVideo() {
        if (mVideoData.isEmpty()) return;
        if (isPlay) {
            if (videoPlayer != null) {
                ViewGroup view = (ViewGroup) videoPlayer.getParent();
                if (view != null) {
                    view.removeView(videoPlayer);
                }
            }
            videoPlayer.pause();
            isPlay = false;
            mVideoData.get(playPostion).setVideo_state(ShortVideoAdapter.VIDEO_END);
            adapter.notifyItemChanged(playPostion);
//            Log.e("play", "setPlay: 暂停：" + playPostion);
        }
    }

    private void playVideo() {
        if (mVideoData.isEmpty()) return;
        if (!isPlay) {
            if (videoPlayer != null) {
                ViewGroup view = (ViewGroup) videoPlayer.getParent();
                if (view != null) {
                    view.removeView(videoPlayer);
                }
            }
            if (videoPlayer == null) {
                return;
            }
            mVideoData.get(newPosition).setVideo_state(ShortVideoAdapter.VIDEO_PLAY);
            playPostion = newPosition;
//            adapter.notifyItemChanged(newPosition);
            isPlay = true;
            if (addVideoToLayout(videoPlayer)) {
                videoPlayer.start(mVideoData.get(playPostion).getContentCmsInfoEntry().getUrl());
                Log.e("play", "setPlay: 播放：" + newPosition);
            } else {
                stopPVideo();
            }
        }

    }

    public void shareWnd(final int pos) {
        if (sharePopupwindow == null) {
            sharePopupwindow = new SharePopupwindow(this);
            sharePopupwindow.setOnShareClickListener(new SharePopupwindow.OnShareClickListener() {
                @Override
                public void onShareClick(View v) {
                    int vId = v.getId();
                    if (vId == com.dfsx.lzcms.liveroom.R.id.share_qq) {
                        onSharePlatfrom(SharePlatform.QQ, pos);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wb) {
                        onSharePlatfrom(SharePlatform.WeiBo, pos);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wx) {
                        onSharePlatfrom(SharePlatform.Wechat, pos);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wxfriends) {
                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS, pos);
                    }
                }
            });
        }
        sharePopupwindow.show(rootView);
    }

    public void onSharePlatfrom(SharePlatform platform, int pos) {
        ContentCmsInfoEntry mCotentInfoeny = mVideoData.get(pos).getContentCmsInfoEntry();
        if (mCotentInfoeny == null) return;
        ShareContent content = new ShareContent();
        content.title = mCotentInfoeny.getTitle();
        content.disc = mCotentInfoeny.getSummary();
        if (mCotentInfoeny.getThumbnail_urls() != null && mCotentInfoeny.getThumbnail_urls().size() > 0)
            content.thumb = mCotentInfoeny.getThumbnail_urls().get(0);
        content.type = ShareContent.UrlType.WebPage;
        content.url = App.getInstance().getContentShareUrl() + mCotentInfoeny.getId();
        AbsShare share = ShareFactory.createShare(this, platform);
        share.share(content);
    }

    @Override
    public void addVideoPlayerToContainer(VideoPlayView videoPlayer) {
        addVideoToLayout(videoPlayer);
    }

    private boolean addVideoToLayout(VideoPlayView videoPlayer) {
        FrameLayout videoLyout = (FrameLayout) adapter.getViewByPosition(videoRecycler, newPosition, R.id.fl_short_video_video);
        if (videoLyout != null) {
            videoLyout.removeAllViews();
            videoLyout.addView(videoPlayer, 0);
            return true;
        }
        return false;
    }

    @Override
    public void addVideoPlayerToFullScreenContainer(VideoPlayView videoPlayer) {
        if (videoFullLyout != null) {
            if (videoFullLyout.getChildAt(0) == null ||
                    !(videoFullLyout.getChildAt(0)
                            instanceof VideoPlayView)) {
                videoFullLyout.addView(videoPlayer, 0);
            }
        }
    }

    public void showLoding(){
        mLoding.setVisibility(View.VISIBLE);
        mLoding.setEnabled(false);
        mPullToRefreshRecyclerView.setVisibility(View.GONE);
        mLoding.setText("数据加载中...");
    }
    public void showEmpty(){
        mLoding.setEnabled(true);
        mLoding.setVisibility(View.VISIBLE);
        mPullToRefreshRecyclerView.setVisibility(View.GONE);
        mLoding.setText("暂无数据");
    }
    public void showError(){
        mLoding.setEnabled(true);
        mLoding.setVisibility(View.VISIBLE);
        mPullToRefreshRecyclerView.setVisibility(View.GONE);
        mLoding.setText("数据加错误，点击重试");
    }
    public void  showChontent(){
        mLoding.setEnabled(false);
        mLoding.setVisibility(View.GONE);
        mPullToRefreshRecyclerView.setVisibility(View.VISIBLE);
    }
}
