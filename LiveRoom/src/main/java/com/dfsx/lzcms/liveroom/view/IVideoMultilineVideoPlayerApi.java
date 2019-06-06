package com.dfsx.lzcms.liveroom.view;

import java.util.List;

/**
 * Created by liuwb on 2017/7/6.
 */
public interface IVideoMultilineVideoPlayerApi extends IVideoPlayerApi {

    void selectedMultiline(IMultilineVideo multilineVideo);

    /**
     * 设置播放器的线路
     *
     * @param multilineVideoList
     */
    void setMultilineList(List<IMultilineVideo> multilineVideoList);
}
