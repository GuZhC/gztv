package com.dfsx.ganzcms.app.adapter;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ShortVideoBean;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.dfsx.videoijkplayer.media.VideoVoiceManager;

import java.util.List;

/**
 * @author : GuZhC
 * @date : 2019/6/5 17:53
 * @description : ShortVideoDetailAdapter 短视频详情
 */
public class ShortVideoDetailAdapter extends BaseMultiItemQuickAdapter<ShortVideoBean, BaseViewHolder> {
    private final VideoPlayView videoPlayer;
    private FrameLayout portraintContainer;
    public ShortVideoDetailAdapter(List<ShortVideoBean> data, VideoPlayView videoPlayer) {
        super(data);
        addItemType(ShortVideoBean.TYPE_SHARE, R.layout.layout_short_video_detail_top);
        addItemType(ShortVideoBean.TYPE_VIDEO, R.layout.item_short_vider);
        this.videoPlayer =videoPlayer;
    }


    @Override
    protected void convert(BaseViewHolder helper, ShortVideoBean item) {
        switch (helper.getItemViewType()) {
            case ShortVideoBean.TYPE_SHARE:
                setShareItem(helper, item);
                break;
            case ShortVideoBean.TYPE_VIDEO:
                setVideoItem(helper, item);
                break;
        }
    }

    private void setShareItem(BaseViewHolder helper, ShortVideoBean item) {
        helper.setText(R.id.tv_short_video_detail_title, item.getTitle())
                .setText(R.id.tv_short_video_detail_data, mContext.getResources()
                        .getString(R.string.tv_short_video_detail_data,"04.16-15:25","1458","226"))
                .addOnClickListener(R.id.iv_short_video_detail_praise)
                .addOnClickListener(R.id.iv_short_video_detail_comment)
                .addOnClickListener(R.id.iv_short_video_detail_share_wx)
                .addOnClickListener(R.id.iv_short_video_detail_share_pyq)
                .addOnClickListener(R.id.iv_short_video_detail_share_wb)
                .addOnClickListener(R.id.iv_short_video_detail_share_qq);
    }

    private void setVideoItem(BaseViewHolder helper, ShortVideoBean item) {
        portraintContainer = helper.getView(R.id.fl_short_video_video);
        if (helper.getLayoutPosition() == 0) {
            helper.setGone(R.id.ll_short_video_bottom, false);
        } else {
            helper.setGone(R.id.ll_short_video_bottom, true);
        }
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
            case ShortVideoAdapter.VIDEO_NULL:
                break;
            case ShortVideoAdapter.VIDEO_PLAY:
                helper.setGone(R.id.tv_short_video_play, false);
//                startPlay();
                break;
            case ShortVideoAdapter.VIDEO_STOP:
                helper.setGone(R.id.tv_short_video_play, true);
//                stopPlay();
                break;
            case ShortVideoAdapter.VIDEO_PUSE:
                break;
            case ShortVideoAdapter.VIDEO_END:
//                stopPlay();
                helper.setGone(R.id.rl_short_video_share, true)
                        .setGone(R.id.tv_short_video_play, false);
                break;
        }
    }

    public void startPlay() {
        stopPlay();
        if (videoPlayer != null) {
            ViewGroup view = (ViewGroup) videoPlayer.getParent();
            if (view != null) {
                view.removeView(videoPlayer);
            }
        }
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

}
