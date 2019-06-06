package com.dfsx.ganzcms.app.adapter;

import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ShortVideoBean;
import com.dfsx.videoijkplayer.VideoPlayView;

import java.util.List;

/**
 * @author : GuZhC
 * @date : 2019/5/30 16:53
 * @description : 短视频页面recycleViews适配器，添加头布局
 */
public class ShortVideoAdapter extends BaseQuickAdapter<ShortVideoBean, BaseViewHolder> {

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

    public ShortVideoAdapter(@Nullable List<ShortVideoBean> data, VideoPlayView videoPlayer) {
        super(R.layout.item_short_vider, data);
        this.videoPlayer = videoPlayer;
    }


    @Override
    protected void convert(BaseViewHolder helper, ShortVideoBean item) {
        portraintContainer = helper.getView(R.id.fl_short_video_video);
//        commView = helper.getView(R.id.tv_short_video_test);
        helper.setText(R.id.tv_short_video_title, item.getTitle())
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
                helper.setGone(R.id.tv_short_video_play, false);
                startPlay();
                break;
            case VIDEO_STOP:
                helper.setGone(R.id.tv_short_video_play, true);
                stopPlay();
                break;
            case VIDEO_PUSE:
                break;
            case VIDEO_END:
                stopPlay();
                helper.setGone(R.id.rl_short_video_share, true)
                        .setGone(R.id.tv_short_video_play, false);
                break;
        }
    }


    public void startPlay() {
        stopPlay();
        if (portraintContainer == null) return;
        if (portraintContainer.getChildCount() <= 0 || !(portraintContainer.getChildAt(0) instanceof VideoPlayView)) {
            portraintContainer.addView(videoPlayer, 0);
        }
        if (videoPlayer != null) {
            videoPlayer.start("http://file.yatv.tv/cms/videos/nmip-media/2019-05-29/4370046821-v0-mp4/8F58BC10D49511829BD82C260052FDEB.mp4");
        }
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

    public FrameLayout getPortraintContainer() {
        if (portraintContainer != null) {
            return portraintContainer;
        } else {
            return null;
        }
    }
}
