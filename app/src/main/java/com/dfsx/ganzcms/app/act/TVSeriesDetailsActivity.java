package com.dfsx.ganzcms.app.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.adapter.BaseRecyclerViewDataAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.AbsAction1;
import com.dfsx.ganzcms.app.business.ObjectUtil;
import com.dfsx.ganzcms.app.fragment.TVSeriesVideoItemListFragment;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.view.NoScrollGridView;
import com.dfsx.videoijkplayer.VideoPlayView;
import org.json.JSONArray;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 电视剧详情页面
 */
public class TVSeriesDetailsActivity extends CmsVideoActivity {

    public static final String TAG_VIDEO_ITEM_LIST_FRAG = "TVSeriesDetailsActivity_TVSeriesVideoItemListFragment";

    private BaseViewHodler headerHolder;

    private OtherVideoSeriesAdapter otherVideoSeriesAdapter;
    private TVSeriesRecyclerAdapter seriesRecyclerAdapter;

    private int currentPlayIndex;
    /**
     * 所有可以播放的视频总数
     */
    private int allCouldPlayIndex;

    private List<IItemVideo> itemPlayVideoList;

    private TVSeriesVideoItemListFragment videoItemListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View initHeadTop() {
        headerHolder = new BaseViewHodler(listView, R.layout.header_tv_series_layout, 0);
        _commnedNoplayLayout = headerHolder.getView(R.id.comnend_noplay_layout);
        commendBtn = headerHolder.getView(R.id.video_comment_num_text);
        favorityBtn = headerHolder.getView(R.id.tv_video_store);
        shareBtn = headerHolder.getView(R.id.tv_video_share);
        return headerHolder.getConvertView();
    }

    @Override
    public void getContentInfo() {
        super.getContentInfo("dianshiju");
    }

    @Override
    public void initParams(ContentCmsInfoEntry data) {
        if (data != null) {

            TextView tvSeriesName = headerHolder.getView(R.id.tv_video_name);

            TextView tvCommentNum = headerHolder.getView(R.id.video_comment_num_text);
            ImageView imageShare = headerHolder.getView(R.id.tv_video_share);
            ImageView imageStore = headerHolder.getView(R.id.tv_video_store);
            View otherVideoInfoView = headerHolder.getView(R.id.other_video_info_view);
            RecyclerView videoSizeRecycler = headerHolder.getView(R.id.recycler_video_list);
            NoScrollGridView otherVideoGride = headerHolder.getView(R.id.other_video_grid);
            View goSeeAllVideo = headerHolder.getView(R.id.btn_go_all_video_list);

            setFavStatus(imageStore, data.isFav(), false);

            tvSeriesName.setText(data.getTitle());
            updateVideoPlayDesc();
            String commentStr = getResources().getString(R.string.tv_series_comment_desc);
            tvCommentNum.setText(String.format(commentStr, data.getComment_count()));

            if (seriesRecyclerAdapter == null) {
                seriesRecyclerAdapter = new TVSeriesRecyclerAdapter();
                videoSizeRecycler.setAdapter(seriesRecyclerAdapter);
                videoSizeRecycler.setLayoutManager(new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false));
            }

            itemPlayVideoList = createJujiListData(data);
            seriesRecyclerAdapter.update(itemPlayVideoList, false);

            if (otherVideoSeriesAdapter == null) {
                otherVideoSeriesAdapter = new OtherVideoSeriesAdapter(context);
                otherVideoGride.setAdapter(otherVideoSeriesAdapter);
            }
            if (data.getRaletionList() != null && data.getRaletionList().size() > 0) {
                otherVideoInfoView.setVisibility(View.VISIBLE);
                otherVideoSeriesAdapter.update(data.getRaletionList(), false);
            } else {
                otherVideoInfoView.setVisibility(View.GONE);
            }

            goSeeAllVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showVideoItemListFrag();
                }
            });

            imageShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShareWnd(v, _gEntry);
                }
            });

            imageStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavorityBtn();
                }
            });
            tvCommentNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommendBtn(v, -1);
                }
            });
            //do super
            if (data.getThumbnail_urls() != null && data.getThumbnail_urls().size() > 0)
                LoadImagee(_thumbImage, data.getThumbnail_urls().get(0).toString());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == _playBtn) {
            setItemVideoPlay(0);
        } else {
            super.onClick(v);
        }
    }

    private List<IItemVideo> createJujiListData(@Nullable ContentCmsInfoEntry data) {
        List<IItemVideo> list = new ArrayList<>();
        try {
            String jujiStr = data.getFieldsMap().get("juji").toString();
            JSONArray jsonArray = new JSONArray(jujiStr);
            allCouldPlayIndex = jsonArray.length() - 1;
            for (int i = 0; i < jsonArray.length(); i++) {
                long id = jsonArray.optLong(i);
                IItemVideo video = new ItemTVSeriesVideo(id, i);
                list.add(video);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void updateVideoPlayDesc() {
        TextView tvSeriesDesc = headerHolder.getView(R.id.tv_video_desc_info_text);
        int jushu = 0;
        try {
            jushu = ObjectUtil.objectToInt(_gEntry.getFieldsMap().get("jishu"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String descStr = getResources().getString(R.string.tv_series_play_desc);
        String desc = String.format(descStr, currentPlayIndex + 1, jushu);
        tvSeriesDesc.setText(desc);
    }

    @Override
    protected void setBottomView(FrameLayout bottomListViewContainer) {
        //        super.setBottomView(bottomListViewContainer);
    }

    @Override
    public void setFavStatus(ImageView image, boolean flag, boolean isShowMsg) {
        String msg = "收藏成功";
        if (flag) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.icon_tv_video_stored));
        } else {
            msg = "已取消收藏";
            image.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_wish));
        }
        if (isShowMsg) {
            LSUtils.toastMsgFunction(context, msg);
            RxBus.getInstance().post(new Intent(IntentUtil.UPDATE_FAVIRITY_MSG));
        }
    }

    public void showVideoItemListFrag() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_VIDEO_ITEM_LIST_FRAG);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment == null) {
            fragment = new TVSeriesVideoItemListFragment();
        }
        if (fragment instanceof TVSeriesVideoItemListFragment) {
            videoItemListFragment = (TVSeriesVideoItemListFragment) fragment;
        }
        transaction.setCustomAnimations(R.anim.frag_bottom_in, R.anim.frag_bottom_out);
        transaction.add(R.id.bottom_top_view, fragment, TAG_VIDEO_ITEM_LIST_FRAG);
        transaction.commitAllowingStateLoss();

        videoItemListFragment.update(itemPlayVideoList);
    }

    private void notifyVideoListUI() {
        if (seriesRecyclerAdapter != null) {
            seriesRecyclerAdapter.notifyDataSetChanged();
        }
        if (videoItemListFragment != null) {
            videoItemListFragment.update(itemPlayVideoList);
        }
    }

    private void setVideoPlay(String path, final IItemVideo itemVideo) {
        try {
            videoPlayer.start(path);
            videoPlayer.setPreparedListener(new VideoPlayView.OnPreparedListener() {
                @Override
                public void preparedlistener(IMediaPlayer mp) {
                    long duration = videoPlayer.getVideoDuration();
                    _videolenBar.setMax((int) duration);
                    itemVideo.setPlaying(true);
                    notifyVideoListUI();
                    isPlaying = true;
                    //                                                mTimer.schedule(mTimerTask, 0, 1000);
                }
            });
            videoPlayer.setErrorListener(new VideoPlayView.OnErrorListener() {
                @Override
                public void errorListener(IMediaPlayer mp) {
                    itemVideo.setPlaying(false);
                    isPlaying = false;
                    notifyVideoListUI();
                }
            });
            videoPlayer.setCompletionListener(new VideoPlayView.CompletionListener() {
                @Override
                public void completion(IMediaPlayer mp) {
                    if (currentPlayIndex < allCouldPlayIndex) {
                        setItemVideoPlay(++currentPlayIndex);
                    } else {
                        _playBtn.setVisibility(View.VISIBLE);
                        _thumbImage.setVisibility(View.VISIBLE);
                    }
                    itemVideo.setPlaying(false);
                    isPlaying = false;
                    notifyVideoListUI();
                }
            });
            _playBtn.setVisibility(View.GONE);
            _thumbImage.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setItemVideoPlay(int index) {
        try {
            onItemSeriesVideoClick(itemPlayVideoList.get(index), index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * recycleView 选集点击事件
     *
     * @param itemVideo
     */
    public void onItemSeriesVideoClick(IItemVideo itemVideo, int pos) {
        //更新过去的状态
        try {
            itemPlayVideoList.get(currentPlayIndex).setPlaying(false);
            notifyVideoListUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.currentPlayIndex = pos;
        updateVideoPlayDesc();
        Observable.just(itemVideo)
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .flatMap(new Func1<IItemVideo, Observable<VideoContent>>() {
                    @Override
                    public Observable<VideoContent> call(IItemVideo itemVideo) {
                        return itemVideo.getVideoInfo();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AbsAction1<IItemVideo, VideoContent>(itemVideo) {
                    @Override
                    public void call(IItemVideo itemVideo, VideoContent videoContent) {
                        try {
                            List<VideoContent.VideoVersion> versions = videoContent.getVersions();
                            String videoPath = "";
                            for (VideoContent.VideoVersion videoVersion : versions) {
                                videoPath = videoVersion.getUrl();
                                if (!TextUtils.isEmpty(videoPath)) {
                                    break;
                                }
                            }
                            if (TextUtils.isEmpty(videoPath)) {
                                throw new ApiException("没有找到播放地址");
                            }
                            setVideoPlay(videoPath, itemVideo);
                            itemVideo.setPlaying(true);
                            notifyVideoListUI();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "获取视频信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    class OtherVideoSeriesAdapter extends BaseListViewAdapter<ContentCmsEntry> {

        public OtherVideoSeriesAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_other_video_series_layout;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            ContentCmsEntry entry = list.get(position);
            ImageView videoImage = holder.getView(R.id.item_video_image);
            TextView videoTitle = holder.getView(R.id.item_video_title);
            String image = "";
            try {
                image = entry.getThumbnail_urls().get(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            GlideImgManager.getInstance().showImg(context, videoImage, image);
            videoTitle.setText(entry.getTitle());
        }
    }

    class TVSeriesRecyclerAdapter extends BaseRecyclerViewDataAdapter<IItemVideo> {

        @Override
        public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(R.layout.adapter_recyler_tv_series_layout,
                    parent, viewType);
            return holder;
        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
            TextView videoTx = holder.getView(R.id.item_video_text);
            View vieoSquareView = holder.getView(R.id.video_square_view);
            IItemVideo video = list.get(position);
            vieoSquareView.setTag(position);
            if (video.isPlaying()) {
                videoTx.setText("");
                videoTx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_tv_series_play, 0, 0, 0);
            } else {
                videoTx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                videoTx.setText(video.getTitle());
            }
            vieoSquareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    onItemSeriesVideoClick(list.get(pos), pos);
                }
            });
        }
    }
}
