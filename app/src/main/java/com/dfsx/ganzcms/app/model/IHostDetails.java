package com.dfsx.ganzcms.app.model;

import java.util.List;

public interface IHostDetails {

    long getHostDetailsId();

    /**
     * 用户Id
     *
     * @return
     */
    long getHostUserId();

    /**
     * 用户名
     *
     * @return
     */
    String getHostUserName();

    String getHostNickName();

    String getHostIntroduce();

    List<String> getHostImageList();

    String getHostAudio();

    String getHostAudioTitle();

    String getHostAudioImage();

    String getHostAudioIntro();
}
