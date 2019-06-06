package com.dfsx.lzcms.liveroom.view;

import android.view.View;
import com.dfsx.videoijkplayer.media.IMediaController;

/**
 * Created by liuwb on 2017/5/10.
 */
public interface ILiveVideoController extends IMediaController {

    /**
     * 生成控制界面
     *
     * @return
     */
    View getControllerView();

    void setLiveVideoPlayerView(LiveFullRoomVideoPlayer videoPlayerView);
}
