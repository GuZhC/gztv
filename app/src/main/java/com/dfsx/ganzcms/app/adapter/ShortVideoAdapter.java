package com.dfsx.ganzcms.app.adapter;

import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsInfoEntry;
import com.dfsx.ganzcms.app.model.ShortVideoBean;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.videoijkplayer.VideoPlayView;
import rx.Observer;

import java.util.List;

/**
 * @author : GuZhC
 * @date : 2019/5/30 16:53
 * @description : 短视频页面recycleViews适配器，添加头布局
 */
public class ShortVideoAdapter extends BaseQuickAdapter<ContentCmsInfoEntry, BaseViewHolder> {

    protected VideoPlayView videoPlayer;

    private FrameLayout portraintContainer;
    //不加载视频控件
    public static final int VIDEO_NULL = -1;
    //播放
    public static final int VIDEO_PLAY = 0;
    //停止
    public static final int VIDEO_STOP = 1;
    //暂停
    public static final int VIDEO_PUSE = 2;
    //结束
    public static final int VIDEO_END = 3;

    public ShortVideoAdapter(@Nullable List<ContentCmsInfoEntry> data, VideoPlayView videoPlayer) {
        super(R.layout.item_short_vider, data);
        this.videoPlayer = videoPlayer;
    }


    @Override
    protected void convert(BaseViewHolder helper, ContentCmsInfoEntry item) {
        portraintContainer = helper.getView(R.id.fl_short_video_video);

        Glide.with(mContext).load(item.getVideoThumb()).into((ImageView) helper.getView(R.id.img_short_video_bg));
//        commView = helper.getView(R.id.tv_short_video_test);
        helper.setText(R.id.tv_short_video_title, item.getTitle())
                .setText(R.id.tv_short_video_time, UtilHelp.getFormatMinute(item.getVideoDuration()))
                .setText(R.id.tv_short_video_praise, String.valueOf(item.getLike_count()))
                .setText(R.id.tv_short_video_look_count, String.valueOf(item.getView_count()))
                .setText(R.id.tv_short_video_comment, String.valueOf(item.getComment_count()))
                .setChecked(R.id.tv_short_video_praise, item.isLike())
                .setGone(R.id.rl_short_video_share, false)
                .setGone(R.id.tv_short_video_play, true)
                .addOnClickListener(R.id.tv_short_video_play)
                .addOnClickListener(R.id.tv_short_video_comment)
                .addOnClickListener(R.id.tv_short_video_share)
                .addOnClickListener(R.id.tv_short_video_share_friends)
                .addOnClickListener(R.id.tv_short_video_share_wx)
                .addOnClickListener(R.id.cb_short_video_sound)
                .addOnClickListener(R.id.tv_short_video_praise)
                .addOnClickListener(R.id.tv_short_video_go_detail)
                .addOnClickListener(R.id.btn_short_video_look_again);
        int videoState = item.getVideo_state();
        switch (videoState) {
            case VIDEO_NULL:
                break;
            case VIDEO_PLAY:
                helper.setGone(R.id.tv_short_video_play, false)
                        .setGone(R.id.fl_short_video_video, true);
                stopPlay();
                if (portraintContainer == null) return;
                portraintContainer.addView(videoPlayer, 0);
                if (videoPlayer != null) {
                    videoPlayer.start(item.getUrl());
                }
                break;
            case VIDEO_STOP:
                helper.setGone(R.id.tv_short_video_play, true)
                        .setGone(R.id.fl_short_video_video, false);
//                stopPlay();
                break;
            case VIDEO_PUSE:
                break;
            case VIDEO_END:
                stopPlay();
                helper.setGone(R.id.rl_short_video_share, true)
                        .setGone(R.id.tv_short_video_play, false)
                        .setGone(R.id.fl_short_video_video, false);
                break;
        }
    }


    public void startPlay() {

    }

    public void stopPlay() {
//        videoPlayer.stop();
        if (videoPlayer != null) {
            ViewGroup view = (ViewGroup) videoPlayer.getParent();
            if (view != null) {
                view.removeView(videoPlayer);
            }
        }
    }
}
